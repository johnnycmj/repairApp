package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repair.activity.PushMessageListActivity;
import com.wutong.repair.data.bean.PushCategoryBean;
import com.wutong.repair.data.bean.PushMessageBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.TimeUtil;

/**
 * 520活动列表
 * @author Jolly
 * 创建时间：2014年5月4日下午4:14:45
 *
 */
public class PushMessageCategoryListFragment extends BaseFragment {

	public final static int MESSAGE_LIST_ENTER_REQUEST_CODE = 9;

	private TextView titlebarTitleView;
	private ImageView titlebarBackView;

	private PullToRefreshView mContributePullToRefreshView;
	private ListView mContributesView;
	private BaseAdapter loveYouAdapter;
	private List<PushCategoryBean> loveYouList;
	private int complaintCurrentStart = 0;


	private View footerView;
	private View mFooterNoMoreView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_push_message_category_list);
	}

	@Override
	public void onResume() {
		super.onResume();
		setupData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_push_message_category_list, container,false);
		setFragmentView(fragmentView);
		//
		commonInit();
		titleBarInit();
		viewInit();
		callInit();
		return fragmentView;
	}

	private void commonInit(){
		mTitle = getString(R.string.title_fragment_push_message_list);

		loveYouList= new ArrayList<PushCategoryBean>();
	}

	private void titleBarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(mTitle);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		boolean isTitlebarBackShow = this.getActivity().getIntent().getBooleanExtra("titlebar_back_is_show", false);
		if(isTitlebarBackShow){
			titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			titlebarBackView.setVisibility(ViewGroup.GONE);
		}
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PushMessageCategoryListFragment.this.getActivity().onBackPressed();

			}
		});
	}




	private void viewInit(){
		mContributePullToRefreshView = (PullToRefreshView) findViewById(R.id.complaint_index_pull_refresh_view);
		mContributesView = (ListView)  findViewById(R.id.complaint_list);
		footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_push_category_no_more, null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		mContributesView.addFooterView(footerView);

		mContributePullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMoreContribute(true);

			}
		});
		mContributePullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMoreContribute(false);
			}
		});
		View emptyView = findViewById(R.id.complaint_index_empty);
		mContributesView.setEmptyView(emptyView);

		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMoreContribute(true);

			}
		});

		mContributesView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position < loveYouList.size()){
					PushCategoryBean messageBean = loveYouList.get(position);
					Intent intent = new Intent(mContext, PushMessageListActivity.class);
					intent.putExtra("title",messageBean.getName() );
					intent.putExtra("modularValue",messageBean.getValue() );
					startActivity(intent);
				}
			}
		});

		loveYouAdapter = new LoveYouBaseAdapter();
		mContributesView.setAdapter(loveYouAdapter);
		complaintCurrentStart = 0;
		//

	}
	
	private void setupData(){
		loadMoreContribute(true);
	}

	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_push_category_fragment_name), new CallFragmentListener() {

			@Override
			public void onCall(int callCode) {
				if(!PushMessageCategoryListFragment.this.isAdded()){
					return;
				}
				switch (callCode) {
				case MESSAGE_LIST_ENTER_REQUEST_CODE:
					loadMoreContribute(true);
					break;

				default:
					break;
				}

			}
		});
	}




	private void loadMoreContribute(boolean isRefresh){
		if(isRefresh){
			complaintCurrentStart = 0;
		}
		else{
			complaintCurrentStart += application.getPagingSize();
		}
		loadCurrentActivityListAsyncHttp();
	}

	static class CompliantViewHolder{
		ImageView categoryImgView;
		ImageView categoryNewView;
		TextView categoryNameView;
		TextView contentView;
	}



	private class LoveYouBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return loveYouList.size();
		}

		@Override
		public Object getItem(int position) {
			return loveYouList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			CompliantViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_push_message_category_list, null);
				holder = new CompliantViewHolder();

				holder.categoryImgView = (ImageView) convertView.findViewById(R.id.push_msg_category_img);
				holder.categoryNewView = (ImageView) convertView.findViewById(R.id.push_msg_category_new);
				holder.contentView = (TextView) convertView.findViewById(R.id.push_msg_category_last_content);
				holder.categoryNameView = (TextView) convertView.findViewById(R.id.push_msg_category_name);
				convertView.setTag(holder);
			}

			PushCategoryBean complaintBean = loveYouList.get(position);
			holder = (CompliantViewHolder) convertView.getTag();
			holder.categoryImgView.setImageResource(R.drawable.msg_category_microlife);
			holder.categoryNameView.setText(complaintBean.getName());
			holder.categoryNameView.getPaint().setFakeBoldText(true);
			holder.contentView.setText(complaintBean.lastContent());
			if(!complaintBean.hasNew()){
				holder.categoryNewView.setVisibility(ViewGroup.GONE);
			}
			else{
				holder.categoryNewView.setVisibility(ViewGroup.VISIBLE);
			}
			return convertView;
		}

	}

	/**
	 * 获取表白列表
	 * @param complaintPagerBean
	 * @param isRefresh
	 */
	private void loadCurrentActivityListAsyncHttp(){
		String url = getString(R.string.http_url_load_push_category_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
				if(!PushMessageCategoryListFragment.this.isAdded() ||! PushMessageCategoryListFragment.this.isVisible()){
					return;
				}
				com.wutong.androidprojectlibary.http.util.HttpResponseUtil.justToast(errorNo, t, mContext);
			}

			@Override
			public void onStart() {
				super.onStart();
				showProgressDialog();
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				hideProgressDialog();
				ResponseBean<List<PushCategoryBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<PushCategoryBean>>>(){}.getType());
				List<PushCategoryBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					loveYouList.clear();
					loveYouList.addAll(responseList);
					mContributePullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					mContributePullToRefreshView.onFooterRefreshComplete();


					loveYouAdapter.notifyDataSetChanged();
				}

			}

		});

	}



	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			ImageView footerView = (ImageView) findViewById(R.id.footer_no_more_icon);
			if(footerView != null){
				footerView.setImageResource(R.drawable.spring_horse_icon_logo_mask);
			}
			break;

		default:
			break;
		}
	}




}
