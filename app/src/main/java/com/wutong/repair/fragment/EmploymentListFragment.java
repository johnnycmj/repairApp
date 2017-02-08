package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.activity.DivisionMaterialApplyActivity;
import com.wutong.repair.activity.EmploymentMaterialUseStatisticActivity;
import com.wutong.repair.activity.OfficeMaterialApplyDetailActivity;
import com.wutong.repair.data.bean.OfficeEmploymentBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.TimeUtil;

/**
 * 人员列表（申领管理-申领、申领管理-人员）
 * @author Jolly
 * 创建时间：2014年2月25日下午2:26:48
 *
 */
public class EmploymentListFragment extends BaseFragment{

	public final static int TYPE_ORDER = 1;
	public final static int TYPE_MATERIAL = 2;
	
	private final static int VIEW_CODE_DIVISION_LIST = 98;
	private final static int VIEW_CODE_FOOTER = 99;

	private PullToRefreshView divisionPullToRefreshView;

	private ListView divisionListView;
	private DivisionListBaseAdapter divisionListBaseAdapter;
	private List<OfficeEmploymentBean> divisionBeanList;
	private Integer divisionCurrentStart = 0;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;
	private int outType = 0;
	
	private OnItemClickListener itemClickListener;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.viewpager_workbench_division, container,false);
		setFragmentView(fragmentView);
		argumentInit();
		//
		viewInit();
		setupData();
		return fragmentView;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isSubFragment = true;
	}
	private void argumentInit(){
		Bundle argument = getArguments();
		if(argument != null){
			outType = argument.getInt("type");
		}
	}

	private void viewInit(){
		divisionPullToRefreshView = (PullToRefreshView) findViewById(R.id.workbench_material_pull_refresh_view);
		divisionListView = (ListView) findViewById(R.id.division_page_division_list);
		footerView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_FOOTER), null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		divisionListView.addFooterView(footerView);
		divisionBeanList = new ArrayList<OfficeEmploymentBean>();
		divisionListBaseAdapter = new DivisionListBaseAdapter();
		divisionListView.setAdapter(divisionListBaseAdapter);
		View emptyView = findViewById(R.id.workbench_division_empty);
		divisionListView.setEmptyView(emptyView);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadDivisionAsyncHttp(true);

			}
		});

		divisionPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadDivisionAsyncHttp(true);

			}
		});
		divisionPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadDivisionAsyncHttp(false);

			}
		});
		itemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long id) {
				if(position < divisionBeanList.size()){
					Intent intent = new Intent();
					switch (outType) {
					case TYPE_MATERIAL:
						intent.putExtra("divisionId", divisionBeanList.get(position).getId());
						intent.putExtra("type", EmploymentMaterialUseStatisticActivity.MONTH_APPLYED);
						intent.setClass(mContext, EmploymentMaterialUseStatisticActivity.class);
						startActivity(intent);
						break;
					case TYPE_ORDER:
						intent.putExtra("divisionId", divisionBeanList.get(position).getId());
						intent.putExtra("titlebar_back_is_show", true);
						intent.putExtra("hasAdd", true);
						intent.putExtra("divisionName", divisionBeanList.get(position).getName());
						intent.putExtra("detailPermissionType", OfficeMaterialApplyDetailActivity.PERMISSION_MANAGER);
						intent.setClass(mContext, DivisionMaterialApplyActivity.class);
						startActivity(intent);
						break;
					default:
						ToastUtil.showToast(mContext, "类型没有申明，无法进入");
						break;
					}
				}
			}

		};
		divisionListView.setOnItemClickListener(itemClickListener);
	}

	private void setupData(){
		loadDivisionAsyncHttp(true);
	}

	static class WorkbenchDivisionViewHolder{
		TextView nameView;
	}

	private class DivisionListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return divisionBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return divisionBeanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			WorkbenchDivisionViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_DIVISION_LIST), null);
				holder = new WorkbenchDivisionViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.division_page_division_name);
				convertView.setTag(holder);
			}
			OfficeEmploymentBean divisionBean = (OfficeEmploymentBean) getItem(position);
			holder = (WorkbenchDivisionViewHolder) convertView.getTag();
			holder.nameView.setText(divisionBean.getName());
			return convertView;
		}

	}


	/**
	 * 获取人员列表（仿科室）
	 * @param isRefresh
	 */
	private void loadDivisionAsyncHttp(final boolean isRefresh){
		String url = application.getCommonHttpUrlActionManager().getOfficeEmploymentLoadList();
		if(isRefresh){
			divisionCurrentStart = 0;
		}
		else{
			divisionCurrentStart += application.getPagingSize();
		}
		AjaxParams params = new AjaxParams();
		params.put("start", divisionCurrentStart.toString());
		params.put("limit", String.valueOf(application.getPagingSize()));
		Logger.i("[params:" + params.getParamString() + "]");
		Logger.i("[URL:" + url +"]"); 
		new FinalHttp().post(url, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				HttpResponseUtil.justToast(new Exception(t), mContext);
				hideProgressDialog();
			}

			@Override
			public void onStart() {
				showProgressDialog();
			}

			@Override
			public void onSuccess(Object t) {
				Logger.i("result:" + t.toString());
				hideProgressDialog();
				ResponseBean<List<OfficeEmploymentBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<OfficeEmploymentBean>>>(){}.getType());
				List<OfficeEmploymentBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
				}
				else{
					if(isRefresh){
						divisionBeanList.clear();
						divisionPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						divisionPullToRefreshView.onFooterRefreshComplete();
					}
					divisionBeanList.addAll(resultList);
					if(resultList.size() < application.getPagingSize()){
						//
						if(!isFooterAdded){
							divisionListView.addFooterView(footerView);
							isFooterAdded = true;
						}
					}
					else{
						if(isFooterAdded){
							divisionListView.removeFooterView(footerView);
							isFooterAdded = false;
						}
					}
					divisionListBaseAdapter.notifyDataSetChanged();
				}
			}

		});
	}
	
	private int selectedItemLayout(int viewCode){
		switch (application.getSkinType()) {
		case 1:
			switch (viewCode) {
			case VIEW_CODE_DIVISION_LIST:
				return R.layout.spring_horse_list_item_workbench_division;
			case VIEW_CODE_FOOTER:
				return R.layout.spring_horse_footer_common_no_more;
			default:
				break;
			}

		default:
			switch (viewCode) {
			case VIEW_CODE_DIVISION_LIST:
				return R.layout.list_item_workbench_division;
			case VIEW_CODE_FOOTER:
				return R.layout.footer_common_no_more;
			default:
				break;
			}
		}
		return 0;
	}
	
	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			Drawable emptyTopDraw = getResources().getDrawable(R.drawable.spring_horse_empty_icon_repair_order);
			View emptyView = findViewById(R.id.workbench_division_empty);
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
