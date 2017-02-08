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
 * OfficeMaterialDivisionStatisticActivity 的衍生品(办公材料按人员统计月申领量)
 * @author Jolly
 * 创建时间：2014年2月25日下午4:39:14
 *
 */
public class OfficeMaterialEmploymentStatisticActivity extends BaseActivity {
	public final static int TYPE_ORDER = 1;
	public final static int TYPE_MATERIAL = 2;
	
	private final static int VIEW_CODE_DIVISION_LIST = 98;
	private final static int VIEW_CODE_FOOTER = 99;
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private PullToRefreshView divisionPullToRefreshView;

	private ListView divisionListView;
	private DivisionListBaseAdapter divisionListBaseAdapter;
	private List<OfficeEmploymentBean> divisionBeanList;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;
	private Integer divisionCurrentStart = 0;

	private String outOfficeMaterialId;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext,R.string.title_activity_office_material_division_statistic);
		intentInit();
		titlebarInit();
		viewInit();
		setupData();
	}

	private void intentInit(){
		outOfficeMaterialId = getIntent().getStringExtra("officeMaterialId");
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
//		titlebarTitleView.setText(R.string.title_activity_office_material_division_statistic);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OfficeMaterialEmploymentStatisticActivity.this.onBackPressed();

			}
		});
	}


	private void viewInit(){
		divisionPullToRefreshView = (PullToRefreshView) findViewById(R.id.office_material_division_statistic_pull_refresh_view);
		divisionListView = (ListView) findViewById(R.id.office_material_division_statistic_list);

		footerView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_FOOTER), null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		divisionListView.addFooterView(footerView);

		divisionBeanList = new ArrayList<OfficeEmploymentBean>();
		divisionListBaseAdapter = new DivisionListBaseAdapter();
		divisionListView.setAdapter(divisionListBaseAdapter);

		View emptyView = findViewById(R.id.office_material_apply_list_empty);
		divisionListView.setEmptyView(emptyView);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMaterialDivisionStatisticAsyncHttp(true);

			}
		});

		divisionPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMaterialDivisionStatisticAsyncHttp(true);

			}
		});
		divisionPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMaterialDivisionStatisticAsyncHttp(false);

			}
		});

	}

	private void setupData(){
		loadMaterialDivisionStatisticAsyncHttp(true);
	}

	static class WorkbenchDivisionViewHolder{
		TextView nameView;
		TextView appliedView;
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
				holder.appliedView = (TextView) convertView.findViewById(R.id.material_page_month_apply_number);
				convertView.setTag(holder);
			}
			OfficeEmploymentBean divisionBean = (OfficeEmploymentBean) getItem(position);
			holder = (WorkbenchDivisionViewHolder) convertView.getTag();
			holder.nameView.setText(divisionBean.getName());
			holder.appliedView.setText(divisionBean.getMonthAppliedNumber());
			return convertView;
		}

	}


	private void loadMaterialDivisionStatisticAsyncHttp(final boolean isRefresh){
		String url = application.getCommonHttpUrlActionManager().getOfficeOfficeMaterialStatisticEmploymentList();
		if(isRefresh){
			divisionCurrentStart = 0;
		}
		else{
			divisionCurrentStart += application.getPagingSize();
		}
		AjaxParams params = new AjaxParams();
		params.put("start", divisionCurrentStart.toString());
		params.put("limit", String.valueOf(application.getPagingSize()));
		params.put("offciceMaterialId", outOfficeMaterialId);
		application.getFinalHttp().post(url, params, new AjaxCallBack<Object>() {

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
				hideProgressDialog();
				Logger.i("result="+t);
				ResponseBean<OfficeMaterialBean> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<OfficeMaterialBean>>(){}.getType());
				OfficeMaterialBean result = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(result == null || result.getEmploymentBeanList() == null){
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
					divisionBeanList.addAll(result.getEmploymentBeanList());
					
					if(result.getEmploymentBeanList().size() < application.getPagingSize()){
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
					titlebarTitleView.setText(getString(R.string.format_division_material_applied_title, result.getName()));
				}
			}

		});
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
	
	
	private int selectedItemLayout(int viewCode){
		switch (application.getSkinType()) {
		case 1:
			switch (viewCode) {
			case VIEW_CODE_DIVISION_LIST:
				return R.layout.spring_horse_list_item_office_material_division_statistic;
			case VIEW_CODE_FOOTER:
				return R.layout.spring_horse_footer_common_no_more;
			default:
				break;
			}

		default:
			switch (viewCode) {
			case VIEW_CODE_DIVISION_LIST:
				return R.layout.list_item_office_material_division_statistic;
			case VIEW_CODE_FOOTER:
				return R.layout.footer_common_no_more;
			default:
				break;
			}
		}
		return 0;
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_office_material_division_statistic;

		default:
			return R.layout.activity_office_material_division_statistic;
		}
	}

}
