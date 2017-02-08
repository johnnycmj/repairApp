package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.activity.OfficeMaterialApplyDetailActivity;
import com.wutong.repair.data.bean.OfficeMaterialApplyBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;

public class OfficeMaterialApplyListFragment extends BaseFragment {

	private final static int REQUEST_CODE_DETAIL = 98;
	
	private final static int VIEW_CODE_APPLYING = 91;
	private final static int VIEW_CODE_STOCK_OUT = 92;
	private final static int VIEW_CODE_QUERY = 93;
	private final static int VIEW_CODE_CANCEL = 94;
	private final static int VIEW_CODE_FOOTER = 95;

	private PullToRefreshView mOfficeMaterialPullRefreshView;
	private ListView mOfficeMaterialListView;
	private List<OfficeMaterialApplyBean> mOfficeMaterialApplyBeanList;
	private BaseAdapter mOfficeMaterialApplyListBaseAdapter;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;

	private String outStatus;//状态
	private String outDivisionId;
	private String outDivisionName;
	private int outDetailPermissionType;
	private int currentStart = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_office_material_apply_list, container,false);
		setFragmentView(fragmentView);
		//
		intentInit();
		dataInit();
		viewInit();
		setupData();
		return fragmentView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isSubFragment = true;
	}

	private void intentInit(){
		Bundle argument = getArguments();
		if(argument != null){
			outStatus = argument.getString("status");
			outDivisionId = argument.getString("divisionId");
			outDetailPermissionType = argument.getInt("detailPermissionType");
			outDivisionName = argument.getString("divisionName");
		}
	}

	private void dataInit(){
		mOfficeMaterialApplyBeanList = new ArrayList<OfficeMaterialApplyBean>();
		if(outStatus.equals("1")){
			mOfficeMaterialApplyListBaseAdapter = new OfficeMaterialApplyApplyingListBaseAdapter();
		}
		else if(outStatus.equals("2")){
			mOfficeMaterialApplyListBaseAdapter = new OfficeMaterialApplyStockOutListBaseAdapter();
		}
		else if(outStatus.equals("3")){
			mOfficeMaterialApplyListBaseAdapter = new OfficeMaterialApplyQueryListBaseAdapter();
		}
		else if(outStatus.equals("4")){
			mOfficeMaterialApplyListBaseAdapter = new OfficeMaterialApplyCancelListBaseAdapter();
		}
		else{
			//异常情况就申请的
			mOfficeMaterialApplyListBaseAdapter = new OfficeMaterialApplyApplyingListBaseAdapter();
		}
	}

	private void viewInit(){
		mOfficeMaterialListView = (ListView) findViewById(R.id.office_material_apply_list_list);
		footerView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_FOOTER), null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		mOfficeMaterialListView.addFooterView(footerView);
		mOfficeMaterialListView.setAdapter(mOfficeMaterialApplyListBaseAdapter);
		View emptyView = findViewById(R.id.office_material_apply_list_empty);
		mOfficeMaterialListView.setEmptyView(emptyView);
		mOfficeMaterialPullRefreshView = (PullToRefreshView) findViewById(R.id.office_material_apply_list_pull_refresh_view);
		mOfficeMaterialListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View convertView, int position,
					long id) {
				if(position < mOfficeMaterialApplyBeanList.size()){
					String applyId = mOfficeMaterialApplyBeanList.get(position).getOffciceMaterialApplyId();
					if(applyId == null || applyId.trim().length() == 0){
						ToastUtil.showToast(mContext, "数据异常，找不到该记录");
						return;
					}
					Intent intent = new Intent(mContext, OfficeMaterialApplyDetailActivity.class);
					intent.putExtra("applyId", applyId);
					intent.putExtra("permissionType", outDetailPermissionType);
					intent.putExtra("divisionName", outDivisionName);
					startActivityForResult(intent, REQUEST_CODE_DETAIL);
				}

			}
		});
		mOfficeMaterialPullRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadOfficeMaterialList(true);
			}
		});

		mOfficeMaterialPullRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadOfficeMaterialList(false);

			}
		});;

		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadOfficeMaterialList(true);

			}
		});
	}

	private void setupData(){
		loadOfficeMaterialList(true);
	}

	/**
	 * 父Fragment调用
	 */
	public void refreshForStatusChange(){
		loadOfficeMaterialList(true);
	}

	private void loadOfficeMaterialList(boolean isRefresh){
		if(isRefresh){
			currentStart = 0;
		}
		else{
			currentStart += application.getPagingSize();
		}
		//
		loadOfficeMaterialListHttp(isRefresh,String.valueOf(currentStart), String.valueOf(application.getPagingSize()));
	}

	private void loadOfficeMaterialListHttp(final boolean isRefresh,String start,String limit){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getOfficeMaterialApplyLoadList());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		//Fix分支
			//煤矿下查看本人所在科室的申请列表
			NameValuePair organizationIdPair = new BasicNameValuePair("organizationId", outDivisionId);
			params.add(organizationIdPair);
		NameValuePair statusPair = new BasicNameValuePair("status", outStatus);
		params.add(statusPair);
		NameValuePair startPair = new BasicNameValuePair("start", start);
		params.add(startPair);
		NameValuePair limitPair = new BasicNameValuePair("limit", limit);
		params.add(limitPair);
		NameValuePair dirPair = new BasicNameValuePair("dir", "1");//升序1，降序2
		params.add(dirPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<OfficeMaterialApplyBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<OfficeMaterialApplyBean>>>(){}.getType());
				if(responseBean.isSuccess()){
					List<OfficeMaterialApplyBean> resultList = responseBean.getData();
					if(resultList == null){
						ToastUtil.showToast(mContext, "服务端返回数据异常：" + responseBean.getMessage());
						return;
					}
					else{
						if(isRefresh){
							mOfficeMaterialApplyBeanList.clear();
							mOfficeMaterialApplyBeanList.addAll(resultList);
							mOfficeMaterialPullRefreshView.onHeaderRefreshComplete();
						}
						else{
							mOfficeMaterialApplyBeanList.addAll(resultList);
							mOfficeMaterialPullRefreshView.onFooterRefreshComplete();
						}


						if(resultList.size() < application.getPagingSize()){
							//
							if(!isFooterAdded){
								mOfficeMaterialListView.addFooterView(footerView);
								isFooterAdded = true;
							}
						}
						else{
							if(isFooterAdded){
								mOfficeMaterialListView.removeFooterView(footerView);
								isFooterAdded = false;
							}
						}
						mOfficeMaterialApplyListBaseAdapter.notifyDataSetChanged();
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取失败：" + responseBean.getMessage());
				}
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	static class OfficeMaterialApplyViewHolder{
		TextView applicantTv;//申请人
		TextView stockOutPersonTv;//出库人
		TextView queryPersonTv;//确认人
		TextView cancelPersonTv;//撤销人
		TextView timeLabelTv;//时间标签
		TextView timeValueTv;//时间

	}

	/**
	 * 出库
	 * @author Jolly
	 * 创建时间：2013年12月18日下午4:53:18
	 *
	 */
	private class OfficeMaterialApplyStockOutListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mOfficeMaterialApplyBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OfficeMaterialApplyBean officeMaterialApplyBean = mOfficeMaterialApplyBeanList.get(position);
			OfficeMaterialApplyViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_STOCK_OUT), null);
				holder = new OfficeMaterialApplyViewHolder();
				holder.stockOutPersonTv = (TextView) convertView.findViewById(R.id.office_material_apply_stock_out_name);
				holder.stockOutPersonTv.setVisibility(ViewGroup.VISIBLE);
				holder.applicantTv = (TextView) convertView.findViewById(R.id.office_material_apply_appliant_name);
				holder.timeLabelTv = (TextView) convertView.findViewById(R.id.office_material_apply_time_label);
				holder.timeValueTv = (TextView) convertView.findViewById(R.id.office_material_apply_time_value);
				convertView.setTag(holder);
			}
			else{
				holder = (OfficeMaterialApplyViewHolder) convertView.getTag();
			}


			holder.stockOutPersonTv.setText(officeMaterialApplyBean.getStockOutName());


			holder.applicantTv.setText(officeMaterialApplyBean.getApplicantName());
			holder.timeLabelTv.setText("出库时间");
			holder.timeValueTv.setText(officeMaterialApplyBean.getStockOutTime());
			return convertView;
		}

	}

	/**
	 * 确认
	 * @author Jolly
	 * 创建时间：2013年12月19日上午10:51:14
	 *
	 */
	private class OfficeMaterialApplyQueryListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mOfficeMaterialApplyBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OfficeMaterialApplyBean officeMaterialApplyBean = mOfficeMaterialApplyBeanList.get(position);
			OfficeMaterialApplyViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_QUERY), null);
				holder = new OfficeMaterialApplyViewHolder();
				holder.queryPersonTv = (TextView) convertView.findViewById(R.id.office_material_apply_query_name);
				holder.queryPersonTv.setVisibility(ViewGroup.VISIBLE);
				holder.applicantTv = (TextView) convertView.findViewById(R.id.office_material_apply_appliant_name);
				holder.timeLabelTv = (TextView) convertView.findViewById(R.id.office_material_apply_time_label);
				holder.timeValueTv = (TextView) convertView.findViewById(R.id.office_material_apply_time_value);
				convertView.setTag(holder);
			}
			else{
				holder = (OfficeMaterialApplyViewHolder) convertView.getTag();
			}


			holder.queryPersonTv.setText(officeMaterialApplyBean.getConfirmorName());


			holder.applicantTv.setText(officeMaterialApplyBean.getApplicantName());
			holder.timeLabelTv.setText("确认时间");
			holder.timeValueTv.setText(officeMaterialApplyBean.getConfirmTime());
			return convertView;
		}

	}

	/**
	 * 撤销
	 * @author Jolly
	 * 创建时间：2013年12月19日上午10:53:39
	 *
	 */
	private class OfficeMaterialApplyCancelListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mOfficeMaterialApplyBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OfficeMaterialApplyBean officeMaterialApplyBean = mOfficeMaterialApplyBeanList.get(position);
			OfficeMaterialApplyViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_CANCEL), null);
				holder = new OfficeMaterialApplyViewHolder();
				holder.cancelPersonTv = (TextView) convertView.findViewById(R.id.office_material_apply_cancel_name);
				holder.cancelPersonTv.setVisibility(ViewGroup.VISIBLE);
				holder.applicantTv = (TextView) convertView.findViewById(R.id.office_material_apply_appliant_name);
				holder.timeLabelTv = (TextView) convertView.findViewById(R.id.office_material_apply_time_label);
				holder.timeValueTv = (TextView) convertView.findViewById(R.id.office_material_apply_time_value);
				convertView.setTag(holder);
			}
			else{
				holder = (OfficeMaterialApplyViewHolder) convertView.getTag();
			}


			holder.cancelPersonTv.setText(officeMaterialApplyBean.getCancelName());


			holder.applicantTv.setText(officeMaterialApplyBean.getApplicantName());
			holder.timeLabelTv.setText("撤销时间");
			holder.timeValueTv.setText(officeMaterialApplyBean.getCancelTime());
			return convertView;
		}

	}

	/**
	 * 申请
	 * @author Jolly
	 * 创建时间：2013年12月19日上午10:54:56
	 *
	 */
	private class OfficeMaterialApplyApplyingListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mOfficeMaterialApplyBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OfficeMaterialApplyBean officeMaterialApplyBean = mOfficeMaterialApplyBeanList.get(position);
			OfficeMaterialApplyViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_APPLYING), null);
				holder = new OfficeMaterialApplyViewHolder();
				holder.applicantTv = (TextView) convertView.findViewById(R.id.office_material_apply_appliant_name);
				holder.timeValueTv = (TextView) convertView.findViewById(R.id.office_material_apply_time_value);
				convertView.setTag(holder);
			}
			else{
				holder = (OfficeMaterialApplyViewHolder) convertView.getTag();
			}
			holder.applicantTv.setText(officeMaterialApplyBean.getApplicantName());
			holder.timeValueTv.setText(officeMaterialApplyBean.getApplyTime());
			return convertView;
		}

	}
	
	private int selectedItemLayout(int viewCode){
		switch (application.getSkinType()) {
		case 1:
			switch (viewCode) {
			case VIEW_CODE_APPLYING:
				return R.layout.spring_horse_listview_item_office_material_apply_applying_apply_list;
			case VIEW_CODE_STOCK_OUT:
				return R.layout.spring_horse_listview_item_office_material_apply_stock_out_apply_list;
			case VIEW_CODE_QUERY:
				return R.layout.spring_horse_listview_item_office_material_apply_stock_out_apply_list;
			case VIEW_CODE_CANCEL:
				return R.layout.spring_horse_listview_item_office_material_apply_stock_out_apply_list;
			case VIEW_CODE_FOOTER:
				return R.layout.spring_horse_footer_common_no_more;
			default:
				break;
			}
			break;

		default:
			switch (viewCode) {
			case VIEW_CODE_APPLYING:
				return R.layout.listview_item_office_material_apply_applying_apply_list;
			case VIEW_CODE_STOCK_OUT:
				return R.layout.listview_item_office_material_apply_stock_out_apply_list;
			case VIEW_CODE_QUERY:
				return R.layout.listview_item_office_material_apply_stock_out_apply_list;
			case VIEW_CODE_CANCEL:
				return R.layout.listview_item_office_material_apply_stock_out_apply_list;
			case VIEW_CODE_FOOTER:
				return R.layout.footer_common_no_more;
			default:
				break;
			}
			break;
		}
		return 0;
	}
	
	
	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			Drawable emptyTopDraw = getResources().getDrawable(R.drawable.spring_horse_empty_icon_repair_order);
			View emptyView = findViewById(R.id.office_material_apply_list_empty);
			TextView tipView = (TextView) emptyView.findViewById(R.id.empty_tip);
			TextView clickTipView = (TextView) emptyView.findViewById(R.id.empty_click_tip);
			tipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			clickTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			tipView.setCompoundDrawablesWithIntrinsicBounds(null, emptyTopDraw, null, null);

			break;

		default:
			break;
		}
	}
	
}
