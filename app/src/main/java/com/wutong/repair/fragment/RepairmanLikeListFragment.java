package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.wutong.repair.data.bean.ContributePagerBean;
import com.wutong.repair.data.bean.RepairmanLikeBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.SettingConfig;
import com.wutong.repair.util.TimeUtil;

/**
 * 劳动节赞
 * @author Jolly
 * 创建时间：2014年3月28日下午2:39:28
 *
 */
public class RepairmanLikeListFragment extends BaseFragment {

	private final static int CONTRIBUTE_SUBMIT_REQUEST_CODE = 9;

	private TextView titlebarTitleView;
	private ImageView titlebarBackView;
	private String complaintTitle = "为维修工献花";

	private PullToRefreshView mContributePullToRefreshView;
	private GridView mContributesView;
	private BaseAdapter complaintAdapter;
	private List<RepairmanLikeBean> complaintList;
	private View footerView;
	private int complaintCurrentStart = 0;

	private View headRepairmanView;
	private RepairmanLikeBean mHeadRepairmanLikeBean;
	private TextView headNameView;
	private TextView headCampusView;
	private TextView headLikeNumberView;
	private LinearLayout headLikeNumberLayout;
	private ImageView headImageView;

	private Drawable noLikeLeft;
	private Drawable hasLikeLeft;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_repairman_like);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		//
		commonInit();
		titleBarInit();
		viewInit();
		return fragmentView;
	}
	
	private void commonInit(){
		noLikeLeft = getResources().getDrawable(R.drawable.icon_repairman_no_like);
		hasLikeLeft = getResources().getDrawable(R.drawable.icon_repairman_has_like);
	}

	private void titleBarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(complaintTitle);
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
				RepairmanLikeListFragment.this.getActivity().onBackPressed();

			}
		});
	}




	private void viewInit(){
		mContributePullToRefreshView = (PullToRefreshView) findViewById(R.id.complaint_index_pull_refresh_view);
		mContributesView = (GridView)  findViewById(R.id.complaint_list);

		//
		headRepairmanView = findViewById(R.id.repairman_like_head_layout);
		headNameView = (TextView) headRepairmanView.findViewById(R.id.repairman_like_head_name);
		headCampusView = (TextView) headRepairmanView.findViewById(R.id.repairman_like_head_campus_name);
		headLikeNumberView = (TextView) headRepairmanView.findViewById(R.id.repairman_like_item_like_btn);
		headLikeNumberLayout = (LinearLayout) headRepairmanView.findViewById(R.id.repairman_like_item_like_layout);
		headImageView = (ImageView) headRepairmanView.findViewById(R.id.repairman_like_head_image);
		//

		footerView = findViewById(R.id.grid_repairman_like_footer);
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
		

		complaintList= new ArrayList<RepairmanLikeBean>();
		complaintAdapter = new CompliantBaseAdapter();
		mContributesView.setAdapter(complaintAdapter);
		complaintCurrentStart = 0;
		loadMoreContribute(true);
		//

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
		TextView nameView;
		TextView campusView;
		ImageView coverView;
		LinearLayout likeNumberLayout;
	}



	private class CompliantBaseAdapter extends BaseAdapter{


		@Override
		public int getCount() {
			return complaintList.size();
		}

		@Override
		public Object getItem(int position) {
			return complaintList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			CompliantViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_repairman_like, null);
				holder = new CompliantViewHolder();

				holder.likeNumberView = (TextView) convertView.findViewById(R.id.repairman_like_item_like_btn);
				holder.nameView = (TextView) convertView.findViewById(R.id.repairman_like_item_name);
				holder.coverView = (ImageView) convertView.findViewById(R.id.repairman_like_item_image);
				holder.campusView = (TextView) convertView.findViewById(R.id.repairman_like_item_campus_name);
				holder.likeNumberLayout = (LinearLayout) convertView.findViewById(R.id.repairman_like_item_like_layout);
				convertView.setTag(holder);
			}

			final RepairmanLikeBean complaintBean = complaintList.get(position);
			holder = (CompliantViewHolder) convertView.getTag();

			holder.likeNumberView.setText(complaintBean.getLikeNumber().toString());
			holder.campusView.setText(complaintBean.getCampusName());
			holder.nameView.setText(complaintBean.getName());
			if(complaintBean.getImage() !=null){
				application.getImageLoader().displayImage(complaintBean.getImage(), holder.coverView,application.getSimpleDisplayImageOptions());

			}
			else{
				holder.coverView.setImageResource(R.drawable.image_failed);
			}
			if(complaintBean.isLike()){
				holder.likeNumberView.setCompoundDrawablesWithIntrinsicBounds(hasLikeLeft, null, null, null);
			}
			else{
				holder.likeNumberView.setCompoundDrawablesWithIntrinsicBounds(noLikeLeft, null, null, null);
			}
			holder.likeNumberView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					submitLikeAsyncHttp(complaintBean);

				}
			});
			return convertView;
		}

	}

	/**
	 * 获取投诉列表
	 * @param complaintPagerBean
	 * @param isRefresh
	 */
	private void loadContributeListAsyncHttp(ContributePagerBean complaintPagerBean,final boolean isRefresh){
		String url = getString(R.string.http_url_load_repairman_like_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("start", complaintPagerBean.getStart());
		params.put("limit", complaintPagerBean.getLimit());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
				if(!RepairmanLikeListFragment.this.isAdded() ||! RepairmanLikeListFragment.this.isVisible()){
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
				mContributePullToRefreshView.onFooterRefreshComplete();
				mContributePullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
				ResponseBean<List<RepairmanLikeBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<RepairmanLikeBean>>>(){}.getType());
				List<RepairmanLikeBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else if(responseList.isEmpty()){
					if(isRefresh){
						headRepairmanView.setVisibility(ViewGroup.GONE);
					}
					else{

					}
					complaintAdapter.notifyDataSetChanged();
				}
				else{
					if(isRefresh){
						complaintList.clear();
						complaintList.addAll(responseList);
					}
					else{
						complaintList.addAll(responseList);
					}

					if(responseList.size() < application.getPagingSize()){
						//
						footerView.setVisibility(ViewGroup.VISIBLE);
					}
					else{
						footerView.setVisibility(ViewGroup.GONE);
					}
					complaintAdapter.notifyDataSetChanged();
					if(isRefresh){
						mHeadRepairmanLikeBean = complaintList.remove(0);
						setupHead();
					}
				}

			}

		});

	}
	private void submitLikeAsyncHttp(final RepairmanLikeBean repairmanLikeBean){
		submitLikeAsyncHttp(repairmanLikeBean, false);
	}

	private void submitLikeAsyncHttp(final RepairmanLikeBean repairmanLikeBean,final boolean isHead){
		String url = getString(R.string.http_url_load_repairman_like_submit,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("otherRepairmanInfoId", repairmanLikeBean.getId());
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
					ToastUtil.showToast(mContext, "献花成功");
					loadMoreContribute(true);
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}


			}

		});


	}
	
	private void setupHead(){
		headNameView.setText(mHeadRepairmanLikeBean.getName());
		headCampusView.setText(mHeadRepairmanLikeBean.getCampusName());
		headLikeNumberView.setText(mHeadRepairmanLikeBean.getLikeNumber().toString());
		if(mHeadRepairmanLikeBean.isLike()){
			headLikeNumberView.setCompoundDrawablesWithIntrinsicBounds(hasLikeLeft, null, null, null);
		}
		else{
			headLikeNumberView.setCompoundDrawablesWithIntrinsicBounds(noLikeLeft, null, null, null);
		}
		headLikeNumberLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submitLikeAsyncHttp(mHeadRepairmanLikeBean,true);
			}
		});

		if(mHeadRepairmanLikeBean.getImage() !=null){
			application.getImageLoader().displayImage(mHeadRepairmanLikeBean.getImage(), headImageView,application.getSimpleDisplayImageOptions());

		}
		else{
			headImageView.setImageResource(R.drawable.image_failed);
		}
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
			return R.layout.fragment_repairman_like;

		default:
			/*暂时不用*/
			//			return R.layout.fragment_contribute;

			return R.layout.fragment_repairman_like;
		}
	}


}
