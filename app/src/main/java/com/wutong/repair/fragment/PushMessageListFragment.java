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
import com.wutong.repair.data.bean.ContributePagerBean;
import com.wutong.repair.data.bean.MicroActivityLoveYouBean;
import com.wutong.repair.data.bean.PushCategoryBean;
import com.wutong.repair.data.bean.PushMessageBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.TimeUtil;

/**
 * 推送消息列表
 * @author Jolly
 * 创建时间：2014年5月4日下午4:14:45
 *
 */
public class PushMessageListFragment extends BaseFragment {


	private TextView titlebarTitleView;
	private ImageView titlebarBackView;

	private PullToRefreshView mContributePullToRefreshView;
	private ListView mContributesView;
	private BaseAdapter loveYouAdapter;
	private List<PushMessageBean> loveYouList;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;
	private int complaintCurrentStart = 0;
	
	private String outModularValue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_push_message_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_push_message_list, container,false);
		setFragmentView(fragmentView);
		//
		commonInit();
		intentInit();
		titleBarInit();
		viewInit();
		setupData();
		return fragmentView;
	}

	private void commonInit(){

		loveYouList= new ArrayList<PushMessageBean>();
	}
	
	private void intentInit(){
		Bundle arguments = getArguments();
		if(arguments != null){
			String title =arguments.getString("title");
			outModularValue = arguments.getString("modularValue");
			if(title != null){
				mTitle = title;
			}
			else{
				mTitle = "短消息";
			}
		}
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
				PushMessageListFragment.this.getActivity().onBackPressed();

			}
		});
	}




	private void viewInit(){
		mContributePullToRefreshView = (PullToRefreshView) findViewById(R.id.complaint_index_pull_refresh_view);
		mContributesView = (ListView)  findViewById(R.id.complaint_list);
		footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_push_message_no_more, null);
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



		loveYouAdapter = new LoveYouBaseAdapter();
		mContributesView.setAdapter(loveYouAdapter);
		complaintCurrentStart = 0;
		//

	}
	
	private void setupData(){
		loadMoreContribute(true);
	}





	private void loadMoreContribute(boolean isRefresh){
		if(isRefresh){
			complaintCurrentStart = 0;
		}
		else{
			complaintCurrentStart += application.getPagingSize();
		}
		ContributePagerBean complaintPagerBean = new ContributePagerBean(complaintCurrentStart, application.getPagingSize());
		loadCurrentActivityListAsyncHttp(complaintPagerBean,isRefresh);
	}

	static class CompliantViewHolder{
		TextView timeView;
		TextView titleView;
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
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_push_message_list, null);
				holder = new CompliantViewHolder();

				holder.timeView = (TextView) convertView.findViewById(R.id.message_item_time);
				holder.titleView = (TextView) convertView.findViewById(R.id.message_item_title);
				holder.contentView = (TextView) convertView.findViewById(R.id.message_item_content);
				convertView.setTag(holder);
			}

			PushMessageBean complaintBean = loveYouList.get(position);
			holder = (CompliantViewHolder) convertView.getTag();

			holder.timeView.setText(complaintBean.getCreateTime());
			holder.titleView.setText(complaintBean.getTitle());
			holder.titleView.getPaint().setFakeBoldText(true);
			holder.contentView.setText(complaintBean.getContent());
			return convertView;
		}

	}

	/**
	 * 获取表白列表
	 * @param complaintPagerBean
	 * @param isRefresh
	 */
	private void loadCurrentActivityListAsyncHttp(ContributePagerBean complaintPagerBean,final boolean isRefresh){
		String url = getString(R.string.http_url_load_push_message_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("module", outModularValue);
		params.put("start", complaintPagerBean.getStart());
		params.put("limit", complaintPagerBean.getLimit());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
				if(!PushMessageListFragment.this.isAdded() ||! PushMessageListFragment.this.isVisible()){
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
				ResponseBean<List<PushMessageBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<PushMessageBean>>>(){}.getType());
				List<PushMessageBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					if(isRefresh){
						loveYouList.clear();
						loveYouList.addAll(responseList);
						mContributePullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						loveYouList.addAll(responseList);
						mContributePullToRefreshView.onFooterRefreshComplete();
					}

					if(responseList.size() < application.getPagingSize()){
						//
						if(!isFooterAdded){
							mContributesView.addFooterView(footerView);
							isFooterAdded = true;
						}
					}
					else{
						if(isFooterAdded){
							mContributesView.removeFooterView(footerView);
							isFooterAdded = false;
						}
					}
					loveYouAdapter.notifyDataSetChanged();
					submitHasReadAsyncHttp();
				}

			}

		});

	}

	private void submitHasReadAsyncHttp(){
		String url = getString(R.string.http_url_load_push_message_read,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("module", outModularValue);
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				hideProgressDialog();
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
				ResponseBean<Object> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<Object>>(){}.getType());
				if(responseBean.isSuccess()){
					Logger.i("该模块已读");
					//
					((PushMessageListActivity)getActivity()).setCallFragmentKeyName(getString(R.string.call_fragment_push_category_fragment_name));
					((PushMessageListActivity)getActivity()).setCallFragmentKeyCode(PushMessageCategoryListFragment.MESSAGE_LIST_ENTER_REQUEST_CODE);
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
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
