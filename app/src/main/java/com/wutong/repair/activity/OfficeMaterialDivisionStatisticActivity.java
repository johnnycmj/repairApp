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
import com.wutong.repair.data.bean.DivisionBean;
import com.wutong.repair.data.bean.OfficeMaterialBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.TimeUtil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class OfficeMaterialDivisionStatisticActivity extends BaseActivity {
	public final static int TYPE_ORDER = 1;
	public final static int TYPE_MATERIAL = 2;
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private PullToRefreshView divisionPullToRefreshView;

	private ListView divisionListView;
	private DivisionListBaseAdapter divisionListBaseAdapter;
	private List<DivisionBean> divisionBeanList;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;
	private Integer divisionCurrentStart = 0;

	private String outOfficeMaterialId;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_office_material_division_statistic);
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
		titlebarTitleView.setText(R.string.title_activity_office_material_division_statistic);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OfficeMaterialDivisionStatisticActivity.this.onBackPressed();

			}
		});
	}


	private void viewInit(){
		divisionPullToRefreshView = (PullToRefreshView) findViewById(R.id.office_material_division_statistic_pull_refresh_view);
		divisionListView = (ListView) findViewById(R.id.office_material_division_statistic_list);

		footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_common_no_more, null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		divisionListView.addFooterView(footerView);

		divisionBeanList = new ArrayList<DivisionBean>();
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
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_office_material_division_statistic, null);
				holder = new WorkbenchDivisionViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.division_page_division_name);
				holder.appliedView = (TextView) convertView.findViewById(R.id.material_page_month_apply_number);
				convertView.setTag(holder);
			}
			DivisionBean divisionBean = (DivisionBean) getItem(position);
			holder = (WorkbenchDivisionViewHolder) convertView.getTag();
			holder.nameView.setText(divisionBean.getName());
			holder.appliedView.setText(divisionBean.getMonthAppliedNumber());
			return convertView;
		}

	}


	private void loadMaterialDivisionStatisticAsyncHttp(final boolean isRefresh){
		String url = application.getCommonHttpUrlActionManager().getOfficeOfficeMaterialStatisticDivisionList();
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
				hideProgressDialog();
				Logger.i("result="+t);
				ResponseBean<OfficeMaterialBean> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<OfficeMaterialBean>>(){}.getType());
				OfficeMaterialBean result = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(result == null || result.getDivisionBeanList() == null){
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
					divisionBeanList.addAll(result.getDivisionBeanList());
					
					if(result.getDivisionBeanList().size() < application.getPagingSize()){
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
}
