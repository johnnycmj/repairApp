package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.http.util.HttpResponseUtil;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.activity.HelpFoundDetailActivity;
import com.wutong.repair.activity.HelpLostDetailActivity;
import com.wutong.repair.data.bean.HelpFoundBean;
import com.wutong.repair.data.bean.ResponseBean;

public class MyPublishLostFoundStatusFragment extends BaseFragment {

	private final static String[] statusArray = new String[]{HelpFoundListFragment.TYPE_LOST,HelpFoundListFragment.TYPE_FOUND};
	private int outIndex;

	private PullToRefreshView mRepairOrderPullView;
	private ListView mRepairOrderListView;
	private List<HelpFoundBean> mOrderBeanList;
	private StatusOrderAdapter mStatusRepairOrderAdapter;
	private View emptyView;
	private String emptyTip;

	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;
	
	private int currentStart = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_my_publish_lost_found_status, container,false);
		setFragmentView(fragmentView);
		//
		commonInitFirst();
		intentInit();
		findViewInit();
		titlebarInit();
		viewInit();
		listenerInit();
		setupData();
		return fragmentView;
	}

	private void commonInitFirst(){
		mOrderBeanList = new ArrayList<HelpFoundBean>();
	}

	private void findViewInit(){
		mRepairOrderPullView = (PullToRefreshView) findViewById(R.id.repair_order_status_pull_fresh);
		mRepairOrderListView = (ListView) findViewById(R.id.repair_order_status_list);
		emptyView = findViewById(R.id.empty_layout);
		((TextView)emptyView.findViewById(R.id.empty_tip)).setText(emptyTip);
	}

	private void intentInit(){
		Bundle arguments = getArguments();
		if(arguments != null){
			outIndex = arguments.getInt("index");
			if(statusArray[outIndex].equals(HelpFoundListFragment.TYPE_LOST)){
				emptyTip = getString(R.string.label_empty_my_lost_zero_tip);
			}
			else if(statusArray[outIndex].equals(HelpFoundListFragment.TYPE_FOUND)){				
				emptyTip = getString(R.string.label_empty_my_found_zero_tip);
			}
			else{
				emptyTip = getString(R.string.label_empty_lost_found_zero_tip);
			}
		}
		else{
			emptyTip = getString(R.string.label_empty_lost_found_zero_tip);
		}
		
	}
	private void titlebarInit(){
	}

	private void viewInit(){
		footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_common_no_more, null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		mRepairOrderListView.addFooterView(footerView);
		mStatusRepairOrderAdapter = new StatusOrderAdapter();
		mRepairOrderListView.setEmptyView(emptyView);
		mRepairOrderListView.setAdapter(mStatusRepairOrderAdapter);
	}

	private void listenerInit(){
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadStatusOrderMore(true);
			}
		});
		mRepairOrderPullView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadStatusOrderMore(true);
			}
		});
		mRepairOrderPullView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadStatusOrderMore(false);
			}
		});
		mRepairOrderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position < mOrderBeanList.size()){
					if(statusArray[outIndex].equals(HelpFoundListFragment.TYPE_FOUND)){
						Intent intent = new Intent(mContext,HelpFoundDetailActivity.class);
						HelpFoundBean contributeBean = mOrderBeanList.get(position);
						intent.putExtra("contributeId", contributeBean.getId());
						intent.putExtra("isMyPublish", true);
						MyPublishLostFoundStatusFragment.this.getActivity().startActivityForResult(intent, 0);
					}
					else if(statusArray[outIndex].equals(HelpFoundListFragment.TYPE_LOST)){
						Intent intent = new Intent(mContext,HelpLostDetailActivity.class);
						HelpFoundBean contributeBean = mOrderBeanList.get(position);
						intent.putExtra("contributeId", contributeBean.getId());
						intent.putExtra("isMyPublish", true);
						MyPublishLostFoundStatusFragment.this.getActivity().startActivityForResult(intent, 0);
					}
					else{
						ToastUtil.showToast(mContext, "类型未知，无法进入");
					}
				}
			}
		});
		
	}

	private void setupData(){
		loadStatusOrderMore(true);
	}

	public void refresh(){
		loadStatusOrderMore(true);
	}


	static class StatusOrderViewHolder{
		TextView itemTypeView;
		TextView timeView;
		TextView remarkView;
		
		
	}

	private class StatusOrderAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mOrderBeanList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			StatusOrderViewHolder holder;
			HelpFoundBean orderBean = mOrderBeanList.get(position);
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_my_publish_lost_found_status, null);
				holder = new StatusOrderViewHolder();
				holder.itemTypeView = (TextView) convertView.findViewById(R.id.my_publish_lost_found_name);
				holder.timeView = (TextView) convertView.findViewById(R.id.my_publish_lost_found_time);
				holder.remarkView = (TextView) convertView.findViewById(R.id.my_publish_lost_found_remark);
				convertView.setTag(holder);
			}
			else{
				holder = (StatusOrderViewHolder) convertView.getTag();
			}
			holder.itemTypeView.setText(orderBean.getItemTypeName());
			holder.timeView.setText(orderBean.getCreateTime());
			holder.remarkView.setText(orderBean.getContent());
			return convertView;
		}

	}
	private void loadStatusOrderMore(boolean isRefresh){
		if(isRefresh){
			currentStart = 0;
		}
		else{
			currentStart += application.getPagingSize();
		}
		loadStatusOrderHttp(isRefresh,String.valueOf(currentStart),String.valueOf(application.getPagingSize()));
	}
	private void loadStatusOrderHttp(final boolean isRefresh,String start,String limit){
		String url = getString(R.string.http_url_load_lost_or_found_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("apartmentId", "0");//地点
		params.put("status", "1");
		params.put("type", statusArray[outIndex]);//失物或招领
		params.put("itemType", "0");//物品类别
		params.put("pastDate", "0");
		params.put("start", start);
		params.put("limit", limit);
		//
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				hideProgressDialog();
				HttpResponseUtil.justToast(errorNo, t, mContext);
				mRepairOrderPullView.onHeaderRefreshComplete();
				mRepairOrderPullView.onFooterRefreshComplete();
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
				List<HelpFoundBean> resultList = responseBean.getData();
				if(responseBean.isSuccess()){
					if(resultList != null){
						if(isRefresh){
							mOrderBeanList.clear();
						}
						mOrderBeanList.addAll(resultList);
						mStatusRepairOrderAdapter.notifyDataSetChanged();
					}
					else{
						ToastUtil.showToast(mContext, "数据异常");
					}
					if(resultList.size() < application.getPagingSize()){
						//
						if(!isFooterAdded){
							mRepairOrderListView.addFooterView(footerView);
							isFooterAdded = true;
						}
					}
					else{
						if(isFooterAdded){
							mRepairOrderListView.removeFooterView(footerView);
							isFooterAdded = false;
						}
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取失败，" + responseBean.getMessage());
				}
				mRepairOrderPullView.onHeaderRefreshComplete();
				mRepairOrderPullView.onFooterRefreshComplete();

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
