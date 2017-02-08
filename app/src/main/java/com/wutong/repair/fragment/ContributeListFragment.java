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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repair.activity.ContributeDetailActivity;
import com.wutong.repair.activity.ContributeSubmitActivity;
import com.wutong.repair.data.bean.ContributePagerBean;
import com.wutong.repair.data.bean.ContributeBean;
import com.wutong.repair.data.bean.NoticeBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.TimeUtil;

/**
 * 投稿列表
 * @author Jolly
 * 创建时间：2014年3月28日下午2:39:28
 *
 */
public class ContributeListFragment extends BaseFragment {

	public final static int CONTRIBUTE_SUBMIT_REQUEST_CODE = 9;

	private TextView titlebarTitleView;
	private ImageView titlebarAddView;
	private ImageView titlebarBackView;
	private String complaintTitle = "投稿箱";

	private PullToRefreshView mContributePullToRefreshView;
	private ListView mContributesView;
	private BaseAdapter complaintAdapter;
	private List<ContributeBean> complaintList;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;
	private int complaintCurrentStart = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_contribute_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		//
		titleBarInit();
		viewInit();
		callInit();
		return fragmentView;
	}

	private void titleBarInit(){
		titlebarAddView = (ImageView) findViewById(R.id.titlebar_add);
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(complaintTitle);
		titlebarAddView.setVisibility(ViewGroup.VISIBLE);
		titlebarAddView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, ContributeSubmitActivity.class);
				intent.putExtra("isFromIndex", true);
				startActivityForResult(intent, CONTRIBUTE_SUBMIT_REQUEST_CODE);

			}
		});
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
				ContributeListFragment.this.getActivity().onBackPressed();

			}
		});
	}

	


	private void viewInit(){
		mContributePullToRefreshView = (PullToRefreshView) findViewById(R.id.complaint_index_pull_refresh_view);
		mContributesView = (ListView)  findViewById(R.id.complaint_list);
		footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_common_no_more, null);
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

		//
		//
		mContributesView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View convertView, int position,
					long id) {
				if(position < complaintList.size()){
					Intent intent = new Intent(mContext,ContributeDetailActivity.class);
					ContributeBean contributeBean = complaintList.get(position);
					intent.putExtra("contributeId", contributeBean.getId());
					ContributeListFragment.this.getActivity().startActivityForResult(intent, 0);
				}
			}
		});

		complaintList= new ArrayList<ContributeBean>();
		complaintAdapter = new CompliantBaseAdapter(complaintList);
		mContributesView.setAdapter(complaintAdapter);
		complaintCurrentStart = 0;
		loadMoreContribute(true);
		//

	}
	
	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_contribute_list_fragment_name), new CallFragmentListener() {

			@Override
			public void onCall(int callCode) {
				if(!ContributeListFragment.this.isAdded()){
					return;
				}
				switch (callCode) {
				case CONTRIBUTE_SUBMIT_REQUEST_CODE:
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
		ContributePagerBean complaintPagerBean = new ContributePagerBean(complaintCurrentStart, application.getPagingSize());
		loadContributeListAsyncHttp(complaintPagerBean,isRefresh);
	}

	static class CompliantViewHolder{
		TextView likeNumberView;
		TextView contentView;
		ImageView coverView;
	}



	private class CompliantBaseAdapter extends BaseAdapter{
		private List<ContributeBean> list;
		public CompliantBaseAdapter(List<ContributeBean> list) {
			super();
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return Long.valueOf(list.get(position).getId());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CompliantViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
				holder = new CompliantViewHolder();

				holder.likeNumberView = (TextView) convertView.findViewById(R.id.contribute_item_like_number);
				holder.contentView = (TextView) convertView.findViewById(R.id.contribute_item_content);
				holder.coverView = (ImageView) convertView.findViewById(R.id.contribute_item_cover);
				convertView.setTag(holder);
			}

			ContributeBean complaintBean = list.get(position);
			holder = (CompliantViewHolder) convertView.getTag();

			holder.likeNumberView.setText(complaintBean.getLikeNumber());
			holder.contentView.setText(complaintBean.getContent());
			if(complaintBean.getCoverImage() !=null){
				holder.coverView.setVisibility(ViewGroup.VISIBLE);
				application.getImageLoader().displayImage(complaintBean.getCoverImage().getUrl(), holder.coverView,application.getSimpleDisplayImageOptions());
				
			}
			else{
				holder.coverView.setVisibility(ViewGroup.GONE);
			}
			return convertView;
		}

	}

	/**
	 * 获取投诉列表
	 * @param complaintPagerBean
	 * @param isRefresh
	 */
	private void loadContributeListAsyncHttp(ContributePagerBean complaintPagerBean,final boolean isRefresh){
		String url = getString(R.string.http_url_load_contribute_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("contributionPId", "0");
		params.put("start", complaintPagerBean.getStart());
		params.put("limit", complaintPagerBean.getLimit());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
				if(!ContributeListFragment.this.isAdded() ||! ContributeListFragment.this.isVisible()){
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
				ResponseBean<List<ContributeBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<ContributeBean>>>(){}.getType());
				List<ContributeBean> responseList = responseBean.getData();
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
						complaintList.clear();
						complaintList.addAll(responseList);
						mContributePullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						complaintList.addAll(responseList);
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
					complaintAdapter.notifyDataSetChanged();
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
	
	private int selectedItemLayout(){
		switch (application.getSkinType()) {
		case 1:
			/*暂时不用*/
			return R.layout.list_item_contribute_list;

		default:
			return R.layout.list_item_contribute_list;
		}
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_fragment_contribute;

		default:
			/*暂时不用*/
//			return R.layout.fragment_contribute;
			
			return R.layout.spring_horse_fragment_contribute;
		}
	}
	
	
}
