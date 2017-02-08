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
import com.wutong.repair.activity.LoveYouSubmitActivity;
import com.wutong.repair.data.bean.ContributePagerBean;
import com.wutong.repair.data.bean.MicroActivityLoveYouBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.TimeUtil;

/**
 * 520活动列表
 * @author Jolly
 * 创建时间：2014年5月4日下午4:14:45
 *
 */
public class MicroActivityListFragment extends BaseFragment {

	public final static int MICRO_ACTIVITY_SUBMIT_REQUEST_CODE = 9;

	private TextView titlebarTitleView;
	private ImageView titlebarAddView;
	private ImageView titlebarBackView;

	private PullToRefreshView mContributePullToRefreshView;
	private ListView mContributesView;
	private BaseAdapter loveYouAdapter;
	private List<MicroActivityLoveYouBean> loveYouList;
	
	private int complaintCurrentStart = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_contribute_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_micro_activity_list, container,false);
		setFragmentView(fragmentView);
		//
		commonInit();
		titleBarInit();
		viewInit();
		callInit();
		return fragmentView;
	}

	private void commonInit(){
		mTitle = getString(R.string.title_fragment_micro_activity_list);

		loveYouList= new ArrayList<MicroActivityLoveYouBean>();
	}

	private void titleBarInit(){
		titlebarAddView = (ImageView) findViewById(R.id.titlebar_add);
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(mTitle);
		titlebarAddView.setVisibility(ViewGroup.VISIBLE);
		titlebarAddView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, LoveYouSubmitActivity.class);
				intent.putExtra("isFromIndex", true);
				startActivityForResult(intent, MICRO_ACTIVITY_SUBMIT_REQUEST_CODE);

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
				MicroActivityListFragment.this.getActivity().onBackPressed();

			}
		});
	}




	private void viewInit(){
		mContributePullToRefreshView = (PullToRefreshView) findViewById(R.id.complaint_index_pull_refresh_view);
		mContributesView = (ListView)  findViewById(R.id.complaint_list);
		
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
		loadMoreContribute(true);
		//

	}

	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_love_you_list_fragment_name), new CallFragmentListener() {

			@Override
			public void onCall(int callCode) {
				if(!MicroActivityListFragment.this.isAdded()){
					return;
				}
				switch (callCode) {
				case MICRO_ACTIVITY_SUBMIT_REQUEST_CODE:
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
		loadCurrentActivityListAsyncHttp(complaintPagerBean,isRefresh);
	}

	static class CompliantViewHolder{
		TextView fromNameView;
		TextView toNameView;
		TextView contentView;
		TextView timeView;
		TextView likeItView;
		TextView unLikeView;
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
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_love_you_list, null);
				holder = new CompliantViewHolder();

				holder.fromNameView = (TextView) convertView.findViewById(R.id.love_you_item_from_name);
				holder.toNameView = (TextView) convertView.findViewById(R.id.love_you_item_to_name);
				holder.contentView = (TextView) convertView.findViewById(R.id.love_you_item_content);
				holder.timeView = (TextView) convertView.findViewById(R.id.love_you_item_time);
				holder.likeItView = (TextView) convertView.findViewById(R.id.love_you_item_like_it);
				holder.unLikeView = (TextView) convertView.findViewById(R.id.love_you_item_unlike_it);
				convertView.setTag(holder);
			}

			MicroActivityLoveYouBean complaintBean = loveYouList.get(position);
			holder = (CompliantViewHolder) convertView.getTag();

			holder.fromNameView.setText(complaintBean.getFromName());
			holder.toNameView.setText(complaintBean.getToName());
			holder.contentView.setText(complaintBean.getContent());
			holder.timeView.setText(complaintBean.getCreateTime());
			holder.likeItView.setText(complaintBean.getLikeNumber().toString());
			holder.unLikeView.setText(complaintBean.getLikeNumber().toString());
			if(complaintBean.isLike()){
				holder.unLikeView.setVisibility(ViewGroup.VISIBLE);
				holder.likeItView.setVisibility(ViewGroup.GONE);
			}
			else{
				holder.likeItView.setVisibility(ViewGroup.VISIBLE);
				holder.unLikeView.setVisibility(ViewGroup.GONE);
			}

			holder.unLikeView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					submitUnLikeItAsyncHttp(position);
				}
			});

			holder.likeItView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					submitLikeItAsyncHttp(position);
				}
			});

			return convertView;
		}

	}

	/**
	 * 获取表白列表
	 * @param complaintPagerBean
	 * @param isRefresh
	 */
	private void loadCurrentActivityListAsyncHttp(ContributePagerBean complaintPagerBean,final boolean isRefresh){
		String url = getString(R.string.http_url_load_micro_activity_current_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("start", complaintPagerBean.getStart());
		params.put("limit", complaintPagerBean.getLimit());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
				if(!MicroActivityListFragment.this.isAdded() ||! MicroActivityListFragment.this.isVisible()){
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
				ResponseBean<List<MicroActivityLoveYouBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<MicroActivityLoveYouBean>>>(){}.getType());
				List<MicroActivityLoveYouBean> responseList = responseBean.getData();
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

					
					loveYouAdapter.notifyDataSetChanged();
				}

			}

		});

	}

	private void submitLikeItAsyncHttp(final int position){
		final MicroActivityLoveYouBean loveYouBean = loveYouList.get(position);
		String url = getString(R.string.http_url_load_love_you_like_it,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("welifeExpressId", loveYouBean.getId());
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
					//
					loveYouBean.setLike(true);
					loveYouBean.setLikeNumber(loveYouBean.getLikeNumber()+1);
					loveYouList.set(position, loveYouBean);
					loveYouAdapter.notifyDataSetChanged();
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}


			}

		});


	}

	private void submitUnLikeItAsyncHttp(final int position){
		final MicroActivityLoveYouBean loveYouBean = loveYouList.get(position);
		String url = getString(R.string.http_url_load_love_you_unlike_it,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("welifeExpressId", loveYouBean.getId());
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
					//
					loveYouBean.setLike(false);
					loveYouBean.setLikeNumber(loveYouBean.getLikeNumber()-1);
					loveYouList.set(position, loveYouBean);
					loveYouAdapter.notifyDataSetChanged();
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
