package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.OfficeEmploymentBean;
import com.wutong.repair.data.bean.OfficeMaterialBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.TimeUtil;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * DivisionMaterialUseStatisticActivity 的衍生品
 * @author Jolly
 * 创建时间：2014年2月25日下午3:43:02
 *
 */
public class EmploymentMaterialUseStatisticActivity extends BaseActivity {

	public final static int MONTH_APPLYED = 1;
	public final static int MONTH_USED = 2;

	private final static int VIEW_CODE_DIVISION_LIST = 98;
	private final static int VIEW_CODE_FOOTER = 99;
	
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private Integer currentStart = 0;


	private PullToRefreshView mOfficeMaterialPullToRefreshView;
	private ListView mOfficeMaterialLv;
	private List<OfficeMaterialBean> mOfficeMaterialUseBeanList;
	private OfficeMaterialUseListBaseAdapter officeMaterialUseListBaseAdapter;
	private String outDivisionId;
	private int outNumberType =0;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;
	private String outDivisionName;
	private String title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_division_material_use_statistic);
		intentInit();
		titlebarInit();
		viewInit();
		setupData();
	}

	private void intentInit(){
		outDivisionId = getIntent().getStringExtra("divisionId");
		outNumberType = getIntent().getIntExtra("type", 0);
		outDivisionName = getIntent().getStringExtra("divisionName");
		Logger.i("intent receive type:" + outNumberType);
		
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setVisibility(ViewGroup.VISIBLE);
		titlebarTitleView.setText(title);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EmploymentMaterialUseStatisticActivity.this.onBackPressed();

			}
		});
	}

	private void viewInit(){
		mOfficeMaterialLv = (ListView) findViewById(R.id.division_material_used_list);
		footerView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_FOOTER), null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		mOfficeMaterialLv.addFooterView(footerView);
		View emptyView = findViewById(R.id.division_material_used_empty);
		mOfficeMaterialLv.setEmptyView(emptyView);
		mOfficeMaterialUseBeanList = new ArrayList<OfficeMaterialBean>();
		officeMaterialUseListBaseAdapter = new OfficeMaterialUseListBaseAdapter();
		mOfficeMaterialLv.setAdapter(officeMaterialUseListBaseAdapter);
		mOfficeMaterialPullToRefreshView = (PullToRefreshView) findViewById(R.id.division_material_used_pull_refresh_view);

		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadDivisionMaterial(true);
			}
		});

		mOfficeMaterialPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadDivisionMaterial(true);
			}
		});
		mOfficeMaterialPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadDivisionMaterial(false);
			}
		});
	}

	private void setupData(){
		loadDivisionMaterial(true);
	}

	static class OfficeMaterialUsedViewHolder{
		TextView nameView;
		TextView monthUsedNumberView;
		TextView monthUsedNumberLabelView;
		TextView brandView;
		TextView specificationView;
		TextView storeNumberView;

		TextView monthAppliedNumberView;
		TextView monthAppliedNumberLabelView;
	}
	private class OfficeMaterialUseListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mOfficeMaterialUseBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return mOfficeMaterialUseBeanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OfficeMaterialUsedViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_DIVISION_LIST), null);
				holder = new OfficeMaterialUsedViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.material_page_material_name);
				holder.brandView = (TextView) convertView.findViewById(R.id.material_page_brand);
				holder.monthUsedNumberView = (TextView) convertView.findViewById(R.id.material_page_month_used_number);
				holder.monthUsedNumberLabelView = (TextView) convertView.findViewById(R.id.material_page_month_used_number_label);
				holder.specificationView = (TextView) convertView.findViewById(R.id.material_page_specification);
				holder.storeNumberView = (TextView) convertView.findViewById(R.id.material_page_store_number);
				holder.monthAppliedNumberView = (TextView) convertView.findViewById(R.id.material_page_month_apply_number);
				holder.monthAppliedNumberLabelView = (TextView) convertView.findViewById(R.id.material_page_month_apply_number_label);
				convertView.setTag(holder);
			}
			OfficeMaterialBean officeMaterialBean = mOfficeMaterialUseBeanList.get(position);
			holder = (OfficeMaterialUsedViewHolder) convertView.getTag();
			holder.nameView.setText(outNumberType==MONTH_USED?officeMaterialBean.getAssetName():officeMaterialBean.getName());
			holder.brandView.setText(officeMaterialBean.getBrand());
			holder.monthUsedNumberView.setText(officeMaterialBean.getMonthUsedNumber().toString()+officeMaterialBean.getUnit());
			holder.monthAppliedNumberView.setText(officeMaterialBean.getMonthUsedNumber().toString()+officeMaterialBean.getUnit());

			holder.monthUsedNumberView.setVisibility(outNumberType==MONTH_USED?ViewGroup.VISIBLE:ViewGroup.GONE);
			holder.monthUsedNumberLabelView.setVisibility(outNumberType==MONTH_USED?ViewGroup.VISIBLE:ViewGroup.GONE);

			holder.monthAppliedNumberView.setVisibility(outNumberType==MONTH_APPLYED?ViewGroup.VISIBLE:ViewGroup.GONE);
			holder.monthAppliedNumberLabelView.setVisibility(outNumberType==MONTH_APPLYED?ViewGroup.VISIBLE:ViewGroup.GONE);
			//Fix分支
				if(outNumberType == MONTH_USED){
					((TextView) convertView.findViewById(R.id.material_page_specification_label)).setText(R.string.workbench_material_page_stat_spec_label);
				}
				else{
					((TextView) convertView.findViewById(R.id.material_page_specification_label)).setText(R.string.workbench_material_page_stat_spec_to_code_index_label);
				}
				
				holder.specificationView.setText(outNumberType==MONTH_USED?officeMaterialBean.getSpec():officeMaterialBean.getCodeIndex());
				
			
			
			
			return convertView;
		}

	}
	
	private void loadDivisionMaterial(boolean isRefresh){
		if(outNumberType == MONTH_APPLYED){
			loadDivisionMaterialUseAsyncHttp(isRefresh);
		}
		else if(outNumberType == MONTH_USED){
			loadDivisionMaterialAppliedAsyncHttp(true);
		}
	}

	private void loadDivisionMaterialUseAsyncHttp(final boolean isRefresh){
		String url = application.getCommonHttpUrlActionManager().getOfficeEmploymentMaterialUsedLoadList();
		if(isRefresh){
			currentStart = 0;
		}
		else{
			currentStart += application.getPagingSize();
		}
		AjaxParams params = new AjaxParams();
		params.put("start", currentStart.toString());
		params.put("limit", String.valueOf(application.getPagingSize()));
		params.put("userId", outDivisionId);

		Logger.i("[params:" + params);
		Logger.i("[url:" + url);
		new FinalHttp().post(url, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
			}

			@Override
			public void onStart() {
				showProgressDialog();
			}

			@Override
			public void onSuccess(Object t) {
				Logger.i("[result:" + t.toString());
				hideProgressDialog();
				ResponseBean<OfficeEmploymentBean> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<OfficeEmploymentBean>>(){}.getType());
				OfficeEmploymentBean result = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(result == null || result.getUsedMaterialList() == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
				}
				else{
					if(isRefresh){
						mOfficeMaterialUseBeanList.clear();
						mOfficeMaterialPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						mOfficeMaterialPullToRefreshView.onFooterRefreshComplete();
					}
					mOfficeMaterialUseBeanList.addAll(result.getUsedMaterialList());
					officeMaterialUseListBaseAdapter.notifyDataSetChanged();
					titlebarTitleView.setText(getString(R.string.format_division_material_applied_title,result.getName()));
				}
			}

		});
	}
	
	private void loadDivisionMaterialAppliedAsyncHttp(final boolean isRefresh){
		String url = application.getCommonHttpUrlActionManager().getOfficeDivisionMaterialAppliedLoadList();
		if(isRefresh){
			currentStart = 0;
		}
		else{
			currentStart += application.getPagingSize();
		}
		AjaxParams params = new AjaxParams();
		params.put("start", currentStart.toString());
		params.put("limit", String.valueOf(application.getPagingSize()));
		params.put("departmentId", outDivisionId);

		Logger.i("[params:" + params);
		Logger.i("[url:" + url);
		new FinalHttp().post(url, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
			}

			@Override
			public void onStart() {
				showProgressDialog();
			}

			@Override
			public void onSuccess(Object t) {
				Logger.i("[result:" + t.toString());
				hideProgressDialog();
				ResponseBean<OfficeEmploymentBean> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<OfficeEmploymentBean>>(){}.getType());
				OfficeEmploymentBean result = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(result == null || result.getAppliedMaterialList() == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
				}
				else{
					if(isRefresh){
						mOfficeMaterialUseBeanList.clear();
						mOfficeMaterialPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						mOfficeMaterialPullToRefreshView.onFooterRefreshComplete();
					}
					mOfficeMaterialUseBeanList.addAll(result.getAppliedMaterialList());
					officeMaterialUseListBaseAdapter.notifyDataSetChanged();
					
					
					if(result.getAppliedMaterialList().size() < application.getPagingSize()){
						//
						if(!isFooterAdded){
							mOfficeMaterialLv.addFooterView(footerView);
							isFooterAdded = true;
						}
					}
					else{
						if(isFooterAdded){
							mOfficeMaterialLv.removeFooterView(footerView);
							isFooterAdded = false;
						}
					}
					officeMaterialUseListBaseAdapter.notifyDataSetChanged();
					
					titlebarTitleView.setText(getString(R.string.format_division_material_used_title,result.getName()));
				}
			}

		});
	}
	
	private int selectedItemLayout(int viewCode){
		switch (application.getSkinType()) {
		case 1:
			switch (viewCode) {
			case VIEW_CODE_DIVISION_LIST:
				return R.layout.spring_horse_list_item_workbench_division_material_used;
			case VIEW_CODE_FOOTER:
				return R.layout.spring_horse_footer_common_no_more;
			default:
				break;
			}

		default:
			switch (viewCode) {
			case VIEW_CODE_DIVISION_LIST:
				return R.layout.list_item_workbench_division_material_used;
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
			View emptyView = findViewById(R.id.division_material_used_empty);
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
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_division_material_use_statistic;

		default:
			return R.layout.activity_division_material_use_statistic;
		}
	}
}
