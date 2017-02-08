package com.wutong.repair.fragment;

import java.io.Serializable;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.GridViewInnerScroll;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repair.activity.HelpFoundDetailActivity;
import com.wutong.repair.activity.HelpFoundSubmitActivity;
import com.wutong.repair.activity.HelpLostDetailActivity;
import com.wutong.repair.activity.HelpLostSubmitActivity;
import com.wutong.repair.data.bean.ContributePagerBean;
import com.wutong.repair.data.bean.FoundItemTypeBean;
import com.wutong.repair.data.bean.HelpFoundBean;
import com.wutong.repair.data.bean.LostOrFoundPlaceBean;
import com.wutong.repair.data.bean.NoticeBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.DataChangeListener;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SimpleSelect;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.TimeUtil;

/**
 * 我捡到了列表
 * @author Jolly
 * 创建时间：2014年3月28日下午2:39:28
 *
 */
public class HelpFoundListFragment extends BaseFragment {

	public final static int HELP_FOUND_SUBMIT_REQUEST_CODE = 9;

	public final static String TYPE_FOUND = "2";//招领
	public final static String TYPE_LOST = "1";//失物
	private final FoundItemTypeBean mAllItemType = new FoundItemTypeBean("不限", "0");
	private final LostOrFoundPlaceBean mAllPlaceBean = new LostOrFoundPlaceBean("不限", "0");

	private TextView titlebarTitleView;
	private ImageView titlebarAddView;
	private ImageView titlebarBackView;
	private String complaintTitle;
	private String emptyTip;

	private PullToRefreshView mContributePullToRefreshView;
	private GridViewInnerScroll mContributesView;
	private BaseAdapter complaintAdapter;
	private List<HelpFoundBean> complaintList;
	private View footerView;
	private int complaintCurrentStart = 0;

	private TextView mPastDateFilter;
	private TextView mPlaceFilter;
	private TextView mItemTypeFilter;

	private SingleSelectDialogFragment mPlaceDialogFragment;
	private List<LostOrFoundPlaceBean> mPlaceList;

	private SingleSelectDialogFragment mPastDateDialogFragment;
	private List<SimpleSelect> mPastDateList;
	private SingleSelectDialogFragment mItemTypeDialogFragment;
	private List<FoundItemTypeBean> mItemTypeList;

	private String dataStatus = "1";
	private String dataPlaceId = "0";
	private String dataType = "0";
	private String dataItemType = "0";
	private String dataPastDate = "0";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_found_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		//
		commonInit();
		intentInit();
		titleBarInit();
		viewInit();
		setUpData();
		callInit();
		return fragmentView;
	}

	private void commonInit(){
		mPlaceList = new ArrayList<LostOrFoundPlaceBean>();
	}

	private void intentInit(){
		Bundle arguments = getArguments();
		if(arguments != null){
			dataType = arguments.getString("type");
		}
		if(dataType.equals(TYPE_FOUND)){
			complaintTitle = "捡到了";
			emptyTip = getString(R.string.label_empty_found_zero_tip);
		}
		else if(dataType.equals(TYPE_LOST)){
			complaintTitle = "弄丢了";
			emptyTip = getString(R.string.label_empty_lost_zero_tip);
		}
		else{
			complaintTitle = "启事";
			emptyTip = getString(R.string.label_empty_lost_found_zero_tip);
		}
	}

	private void titleBarInit(){
		titlebarAddView = (ImageView) findViewById(R.id.titlebar_add);
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(complaintTitle);
		titlebarAddView.setVisibility(ViewGroup.VISIBLE);
		titlebarAddView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(dataType.equals(TYPE_FOUND)){
					Intent intent = new Intent();
					intent.setClass(mContext, HelpFoundSubmitActivity.class);
					intent.putExtra("isFromIndex", true);
					startActivityForResult(intent, HELP_FOUND_SUBMIT_REQUEST_CODE);
				}
				else if(dataType.equals(TYPE_LOST)){
					Intent intent = new Intent();
					intent.setClass(mContext, HelpLostSubmitActivity.class);
					intent.putExtra("isFromIndex", true);
					startActivityForResult(intent, HELP_FOUND_SUBMIT_REQUEST_CODE);
				}
				else{
					ToastUtil.showToast(mContext, "无法添加");
				}

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
				HelpFoundListFragment.this.getActivity().onBackPressed();

			}
		});
	}




	private void viewInit(){
		mItemTypeList = new ArrayList<FoundItemTypeBean>();
		mPastDateList = new ArrayList<SingleSelectDialogFragment.SimpleSelect>();
		mPastDateList.add(new SimpleSelect("不限时间", "0"));
		mPastDateList.add(new SimpleSelect("今天", "1"));
		mPastDateList.add(new SimpleSelect("三天内", "3"));
		mPastDateList.add(new SimpleSelect("一周内", "7"));
		mPastDateList.add(new SimpleSelect("一月内", "30"));
		mPastDateFilter = (TextView) findViewById(R.id.found_list_filter_past_date);
		mPlaceFilter = (TextView) findViewById(R.id.found_list_filter_place);
		mItemTypeFilter = (TextView) findViewById(R.id.found_list_filter_item_type);

		mContributePullToRefreshView = (PullToRefreshView) findViewById(R.id.complaint_index_pull_refresh_view);
		mContributesView = (GridViewInnerScroll)  findViewById(R.id.grid_lost_or_found);
		footerView = findViewById(R.id.grid_lost_or_found_footer);

		mPlaceFilter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPlaceDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) mPlaceList);
				mPlaceDialogFragment.setArguments(data);
				mPlaceDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mPlaceFilter.setText(itemBean.getTextName());
						dataPlaceId = itemBean.getValue();
						loadMoreContribute(true);
					}
				});
				mPlaceDialogFragment.show(getChildFragmentManager(), "single-select");
			}
		});

		mPastDateFilter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPastDateDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) mPastDateList);
				mPastDateDialogFragment.setArguments(data);
				mPastDateDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mPastDateFilter.setText(itemBean.getTextName());
						dataPastDate = itemBean.getValue();
						loadMoreContribute(true);
					}
				});
				mPastDateDialogFragment.show(getChildFragmentManager(), "single-select");
			}
		});

		mItemTypeFilter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mItemTypeDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) mItemTypeList);
				mItemTypeDialogFragment.setArguments(data);
				mItemTypeDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mItemTypeFilter.setText(itemBean.getTextName());
						dataItemType = itemBean.getValue();
						loadMoreContribute(true);
					}
				});
				mItemTypeDialogFragment.show(getChildFragmentManager(), "single-select");
			}
		});

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
		((TextView)emptyView.findViewById(R.id.empty_tip)).setText(emptyTip);
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
					if(dataType.equals(TYPE_FOUND)){
						Intent intent = new Intent(mContext,HelpFoundDetailActivity.class);
						HelpFoundBean contributeBean = complaintList.get(position);
						intent.putExtra("contributeId", contributeBean.getId());
						HelpFoundListFragment.this.getActivity().startActivityForResult(intent, 0);
					}
					else if(dataType.equals(TYPE_LOST)){
						Intent intent = new Intent(mContext,HelpLostDetailActivity.class);
						HelpFoundBean contributeBean = complaintList.get(position);
						intent.putExtra("contributeId", contributeBean.getId());
						HelpFoundListFragment.this.getActivity().startActivityForResult(intent, 0);
					}
					else{
						ToastUtil.showToast(mContext, "类型未知，无法进入");
					}
				}
			}
		});

		complaintList= new ArrayList<HelpFoundBean>();
		complaintAdapter = new CompliantBaseAdapter(complaintList);
		mContributesView.setAdapter(complaintAdapter);
		complaintCurrentStart = 0;
		loadMoreContribute(true);
		//

	}

	private void setUpData(){
		loadPlaceListHttp();
		loadItemTypeListHttp();
	}
	
	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_lost_or_found_list_fragment_name), new CallFragmentListener() {

			@Override
			public void onCall(int callCode) {
				if(!HelpFoundListFragment.this.isAdded()){
					return;
				}
				switch (callCode) {
				case HELP_FOUND_SUBMIT_REQUEST_CODE:
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
			complaintCurrentStart += application.getLimitForLostFound();
		}
		ContributePagerBean complaintPagerBean = new ContributePagerBean(complaintCurrentStart, application.getLimitForLostFound());
		loadContributeListAsyncHttp(complaintPagerBean,isRefresh);
	}

	static class CompliantViewHolder{
		TextView noImgPlaceView;
		TextView noImgContentView;
		TextView withImgPlaceView;
		ImageView photoView;
		View noImgLayout;
	}



	private class CompliantBaseAdapter extends BaseAdapter{
		private List<HelpFoundBean> list;
		public CompliantBaseAdapter(List<HelpFoundBean> list) {
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
				convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_found, null);
				holder = new CompliantViewHolder();

				holder.noImgLayout = convertView.findViewById(R.id.found_item_no_img_area);
				holder.noImgContentView = (TextView) convertView.findViewById(R.id.found_item_no_img_remark);
				holder.noImgPlaceView = (TextView) convertView.findViewById(R.id.found_item_no_img_place);
				holder.photoView = (ImageView) convertView.findViewById(R.id.found_item_with_img_photo);
				holder.withImgPlaceView = (TextView) convertView.findViewById(R.id.found_item_with_img_place);
				convertView.setTag(holder);
			}

			HelpFoundBean complaintBean = list.get(position);
			holder = (CompliantViewHolder) convertView.getTag();



			if(complaintBean.getCoverImage() !=null){
				holder.photoView.setVisibility(ViewGroup.VISIBLE);
				application.getImageLoader().displayImage(complaintBean.getCoverImage().getUrl(), holder.photoView,application.getSimpleDisplayImageOptions());
				holder.withImgPlaceView.setVisibility(ViewGroup.VISIBLE);
				holder.noImgLayout.setVisibility(ViewGroup.GONE);
				holder.withImgPlaceView.setText(complaintBean.getPlaceName()+complaintBean.getAddress());
			}
			else{
				holder.photoView.setVisibility(ViewGroup.GONE);
				holder.withImgPlaceView.setVisibility(ViewGroup.GONE);
				holder.noImgLayout.setVisibility(ViewGroup.VISIBLE);
				holder.noImgContentView.setText(complaintBean.getContent());
				holder.noImgPlaceView.setText(complaintBean.getPlaceName()+complaintBean.getAddress());
			}
			return convertView;
		}

	}

	private void loadItemTypeListHttp(){
		String url = getString(R.string.http_url_load_lost_or_found_item_type_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
				if(!HelpFoundListFragment.this.isAdded() ||! HelpFoundListFragment.this.isVisible()){
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
				ResponseBean<List<FoundItemTypeBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<FoundItemTypeBean>>>(){}.getType());
				List<FoundItemTypeBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					mItemTypeList.clear();
					mItemTypeList.add(mAllItemType);
					mItemTypeList.addAll(responseList);
				}

			}

		});

	}


	private void loadContributeListAsyncHttp(ContributePagerBean complaintPagerBean,final boolean isRefresh){
		String url = getString(R.string.http_url_load_lost_or_found_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", "0");
		params.put("apartmentId", dataPlaceId);//地点
		params.put("status", dataStatus);
		params.put("type", dataType);//失物或招领
		params.put("itemType", dataItemType);//物品类别
		params.put("pastDate", dataPastDate);//物品类别
		params.put("start", complaintPagerBean.getStart());
		params.put("limit", complaintPagerBean.getLimit());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
				if(!HelpFoundListFragment.this.isAdded() ||! HelpFoundListFragment.this.isVisible()){
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
				ResponseBean<List<HelpFoundBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<HelpFoundBean>>>(){}.getType());
				List<HelpFoundBean> responseList = responseBean.getData();
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

					if(responseList.size() < application.getLimitForLostFound()){
						//
						footerView.setVisibility(ViewGroup.VISIBLE);
					}
					else{
						footerView.setVisibility(ViewGroup.GONE);
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


	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.fragment_help_found_list;

		default:
			/*暂时不用*/
			//			return R.layout.fragment_contribute;

			return R.layout.fragment_help_found_list;
		}
	}

	private void loadPlaceListHttp(){
		String url = getString(R.string.http_url_load_lost_or_found_place_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
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
				ResponseBean<List<LostOrFoundPlaceBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<LostOrFoundPlaceBean>>>(){}.getType());
				List<LostOrFoundPlaceBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					mPlaceList.clear();
					mPlaceList.add(mAllPlaceBean);
					mPlaceList.addAll(responseList);
				}

			}

		});

	}
}
