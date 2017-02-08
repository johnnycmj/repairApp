package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.WorkbenchStatEmploymentBean;
import com.wutong.repair.data.bean.WorkbenchStatMaterialBean;
import com.wutong.repair.data.bean.WorkbenchStatRepairBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.TimeUtil;
import com.wutong.repairfjnu.R;

public class WorkbenchFragment extends BaseFragment {
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private final static int REPAIR_PAGE_INDEX = 0;
	private final static int MATERIAL_PAGE_INDEX = 1;
	private final static int EMPLOYMENT_PAGE_INDEX = 2;

	private ViewPager workbenchViewPager;
	private WorkbenchPagerAdapter workbenchPagerAdapter;

	private TextView repairSwitcherView;
	private TextView materialSwitcherView;
	private TextView employmentSwitcherView;
	private TextView divistionSwitcherView;

	private View repairPageContentView;
	private View materialPageContentView;
	private View employmentPageContentView;
	private int viewPageSize = 3;
	//repair


	private TextView currentMonthNotAcceptView;
	private TextView currentMonthRepairingView;
	private TextView currentMonthToatalView;
	private TextView currentMonthMonthNameView;

	private TextView todayTimeView;
	private TextView todayTotalView;
	private TextView todayDealingView;
	private TextView todayCompleteView;

	private TextView currentWeekTimeView;
	private TextView currentWeekTotalView;
	private TextView currentWeekDealingView;
	private TextView currentWeekCompleteView;

	private TextView currentYearTimeView;
	private TextView currentYearTotalView;
	private TextView currentYearDealingView;
	private TextView currentYearCompleteView;
	//

	//material


	private EditText searchContentView;
	private ImageView searchCleanView;
	private ImageView searchSubmitView;

	private View usedNumberLayoutView;
	private View storeNumberLayoutView;

	private TextView usedNumberView;
	private TextView storeNumberView;

	private CheckBox usedNumberToggleView;
	private CheckBox storeNumberToggleView;

	private PullToRefreshView materialPullToRefreshView;
	private ListView materialListView;
	private MaterialListBaseAdapter materialListBaseAdapter;
	private List<WorkbenchStatMaterialBean> materialBeanList;
	private Integer materialCurrentStart = 0;
	private String materialOrderType;
	private String materialSort;
	private String materialSearch;

	private final static String MATERIAL_ORDER_TYPE_MONTH_USED = "2";//使用量
	private final static String MATERIAL_ORDER_TYPE_STORE = "1";//库存量

	private final static String MATERIAL_SORT_DESC = "1";
	private final static String MATERIAL_SORT_ASC = "2";

	//人员

	private PullToRefreshView employmentPullToRefreshView;

	private ListView employmentListView;
	private EmploymentListBaseAdapter employmentListBaseAdapter;
	private List<WorkbenchStatEmploymentBean> employmentBeanList;
	private Integer employmentCurrentStart = 0;

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_workbench);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.activity_workbench, container,false);
		setFragmentView(fragmentView);
		//
		dataInit();
		titlebarInit();
		viewInit();
		setupData();
		return fragmentView;
	}
	private void dataInit(){
		materialOrderType = MATERIAL_ORDER_TYPE_MONTH_USED;
		materialSort = MATERIAL_SORT_DESC;
		materialSearch = "";
	}

	private void setupData(){
		switcherChange(REPAIR_PAGE_INDEX);
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_simple_stat);
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
				WorkbenchFragment.this.getActivity().onBackPressed();

			}
		});

	}

	private void viewInit(){

		workbenchViewPager = (ViewPager) findViewById(R.id.viewpager_workbench);
		workbenchViewPager.setOffscreenPageLimit(3);
		repairSwitcherView = (TextView) findViewById(R.id.tab_switcher_repair);
		materialSwitcherView = (TextView) findViewById(R.id.tab_switcher_material);
		employmentSwitcherView = (TextView) findViewById(R.id.tab_switcher_employment);
		divistionSwitcherView = (TextView) findViewById(R.id.tab_switcher_division);

		repairPageContentView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_workbench_repair, null);
		materialPageContentView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_workbench_material, null);
		employmentPageContentView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_workbench_employment, null);

		workbenchViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				//
				switcherChange(position);
				//

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		repairSwitcherView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switcherChange(REPAIR_PAGE_INDEX);

			}
		});
		materialSwitcherView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switcherChange(MATERIAL_PAGE_INDEX);
			}
		});
		employmentSwitcherView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switcherChange(EMPLOYMENT_PAGE_INDEX);
			}
		});


		workbenchPagerAdapter = new WorkbenchPagerAdapter();
		workbenchViewPager.setAdapter(workbenchPagerAdapter);
		
		//
		
		
		//
		tabRepairViewInit();
		tabMaterialViewInit();
		tabEmploymentViewInit();
	}

	private void switcherChange(int position){
		repairSwitcherView.setEnabled(true);
		materialSwitcherView.setEnabled(true);
		employmentSwitcherView.setEnabled(true);
		divistionSwitcherView.setEnabled(true);
		switch (position) {
		case REPAIR_PAGE_INDEX:
			repairSwitcherView.setEnabled(false);
			repairSetData();
			break;
		case MATERIAL_PAGE_INDEX:
			materialSwitcherView.setEnabled(false);
			materialSetData();
			break;
		case EMPLOYMENT_PAGE_INDEX:
			employmentSwitcherView.setEnabled(false);
			employmentSetData();
			break;
		default:
			break;
		}
		workbenchViewPager.setCurrentItem(position);
	}

	private void tabRepairViewInit(){


		currentMonthNotAcceptView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_month_not_accept);
		currentMonthRepairingView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_month_repairing);
		currentMonthToatalView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_month_total);
		currentMonthMonthNameView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_month_month_name);

		todayTimeView = (TextView) repairPageContentView.findViewById(R.id.repair_page_today_time);
		todayTotalView = (TextView) repairPageContentView.findViewById(R.id.repair_page_today_total);
		todayDealingView = (TextView) repairPageContentView.findViewById(R.id.repair_page_today_dealt);
		todayCompleteView = (TextView) repairPageContentView.findViewById(R.id.repair_page_today_complete);

		currentWeekTimeView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_week_time);
		currentWeekTotalView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_week_total);
		currentWeekDealingView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_week_dealt);
		currentWeekCompleteView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_week_complete);

		currentYearTimeView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_year_time);
		currentYearTotalView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_year_total);
		currentYearDealingView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_year_dealt);
		currentYearCompleteView = (TextView) repairPageContentView.findViewById(R.id.repair_page_current_year_complete);
	}

	private void tabMaterialViewInit(){
		//
		searchContentView = (EditText) materialPageContentView.findViewById(R.id.material_page_search_input);
		searchCleanView = (ImageView) materialPageContentView.findViewById(R.id.material_page_search_clean);;
		searchSubmitView = (ImageView) materialPageContentView.findViewById(R.id.material_page_search_submit);
		usedNumberLayoutView = materialPageContentView.findViewById(R.id.material_page_used_number_layout);
		storeNumberLayoutView = materialPageContentView.findViewById(R.id.material_page_store_number_layout);
		usedNumberView = (TextView) materialPageContentView.findViewById(R.id.material_page_used_number_tv);
		storeNumberView = (TextView) materialPageContentView.findViewById(R.id.material_page_store_number_tv);

		usedNumberToggleView = (CheckBox) materialPageContentView.findViewById(R.id.material_page_used_number_toggle);
		storeNumberToggleView = (CheckBox) materialPageContentView.findViewById(R.id.material_page_store_number_toggle);
		materialListView =  (ListView) materialPageContentView.findViewById(R.id.material_page_material_list);
		materialPullToRefreshView = (PullToRefreshView) materialPageContentView.findViewById(R.id.workbench_material_pull_refresh_view);
		
		View emptyView = materialPageContentView.findViewById(R.id.workbench_material_empty);
		materialListView.setEmptyView(emptyView);

		materialBeanList = new ArrayList<WorkbenchStatMaterialBean>();
		materialListBaseAdapter = new MaterialListBaseAdapter();
		materialListView.setAdapter(materialListBaseAdapter);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);

			}
		});
		searchSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);

			}
		});
		materialPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);

			}
		});
		materialPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMaterialAsyncHttp(false, materialOrderType, materialSort, materialSearch);

			}
		});

		usedNumberLayoutView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				usedNumberToggleView.performClick();
				usedNumberLayoutView.setSelected(true);
				usedNumberToggleView.setSelected(true);
				storeNumberLayoutView.setSelected(false);
				storeNumberToggleView.setSelected(false);
				usedNumberView.setSelected(true);
				storeNumberView.setSelected(false);
			}
		});
		storeNumberLayoutView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				storeNumberToggleView.performClick();
				storeNumberLayoutView.setSelected(true);
				storeNumberToggleView.setSelected(true);
				usedNumberLayoutView.setSelected(false);
				usedNumberToggleView.setSelected(false);
				storeNumberView.setSelected(true);
				usedNumberView.setSelected(false);
			}
		});

		usedNumberToggleView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				materialOrderType = MATERIAL_ORDER_TYPE_MONTH_USED;
				materialSort = isChecked ?MATERIAL_SORT_ASC:MATERIAL_SORT_DESC;
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
			}
		});
		storeNumberToggleView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				materialOrderType = MATERIAL_ORDER_TYPE_STORE;
				materialSort = isChecked ?MATERIAL_SORT_ASC:MATERIAL_SORT_DESC;
				loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
			}
		});

		searchContentView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch(actionId){  
				case EditorInfo.IME_NULL:  
					Logger.i("null for default_content: " + v.getText() );  
					break; 
				case EditorInfo.IME_ACTION_NEXT:  
					loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
					break;
				case EditorInfo.IME_ACTION_SEND:  
					loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
					break;  
				case EditorInfo.IME_ACTION_DONE:  
					loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch); 
					break;  
				case EditorInfo.IME_ACTION_SEARCH:
					loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
					break;
				}  
				return true;
			}
		});
		searchContentView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				materialSearch = s.toString();

			}
		});

		searchCleanView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchContentView.setText("");
				//执行 刷新
			}
		});
	}

	private void tabEmploymentViewInit(){
		employmentPullToRefreshView = (PullToRefreshView) employmentPageContentView.findViewById(R.id.workbench_material_pull_refresh_view);
		employmentListView = (ListView) employmentPageContentView.findViewById(R.id.employment_page_employment_list);
		employmentBeanList = new ArrayList<WorkbenchStatEmploymentBean>();
		employmentListBaseAdapter = new EmploymentListBaseAdapter();
		employmentListView.setAdapter(employmentListBaseAdapter);

		View emptyView = employmentPageContentView.findViewById(R.id.workbench_employment_empty);
		employmentListView.setEmptyView(emptyView);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadEmploymentAsyncHttp(true);

			}
		});

		employmentPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadEmploymentAsyncHttp(true);

			}
		});
		employmentPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadEmploymentAsyncHttp(false);

			}
		});
		employmentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long id) {
				Intent intent = new Intent();
				intent.putExtra("repairmanId", String.valueOf(id));
				intent.putExtra("repairmanName", employmentBeanList.get(position).getName());
				intent.setClass(mContext, EmploymentRepairGroupListActivity.class);
				startActivity(intent);
			}

		});
	}
	

	private void repairSetData(){
		loadRepairStatAsyncHttp();
	}

	private void materialSetData(){
		loadMaterialAsyncHttp(true, materialOrderType, materialSort, materialSearch);
		usedNumberLayoutView.setSelected(true);
		storeNumberLayoutView.setSelected(false);
		usedNumberView.setSelected(true);
		storeNumberView.setSelected(false);//默认
	}

	private void employmentSetData(){
		loadEmploymentAsyncHttp(true);
	}
	

	private class WorkbenchPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return viewPageSize;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int position,
				Object object) {
			((ViewPager)container).removeView((View)object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View viewPagerView = null;
			switch (position) {
			case REPAIR_PAGE_INDEX:
				viewPagerView = repairPageContentView;
				break;
			case MATERIAL_PAGE_INDEX:
				viewPagerView = materialPageContentView;
				break;
			case EMPLOYMENT_PAGE_INDEX:
				viewPagerView = employmentPageContentView;
				break;
			}
			((ViewPager)container).addView(viewPagerView);
			return viewPagerView;
		}

	}

	/**
	 * 获取报修单统计
	 */
	private void loadRepairStatAsyncHttp(){
		String url = application.getCommonHttpUrlActionManager().getStatRepairLoadUrl();
		AjaxParams params = new AjaxParams();
		if(application.getAppFlag().equals("fjnu")){
			//师大独有common版本不去掉
			params.put("userId", application.getLoginInfoBean().getUserId().toString());
			
		}
		new FinalHttp().post(url,params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				HttpResponseUtil.justToast(new Exception(t), mContext);
			}

			@Override
			public void onStart() {
				showProgressDialog("获取报修单统计中……");
			}

			@Override
			public void onSuccess(Object t) {
				hideProgressDialog();
				ResponseBean<Map<String, WorkbenchStatRepairBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<Map<String, WorkbenchStatRepairBean>>>(){}.getType());
				Map<String, WorkbenchStatRepairBean> map = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(map != null && !map.isEmpty()){
					WorkbenchStatRepairBean yearRepairBean = map.get("yearRepair");
					WorkbenchStatRepairBean monthRepairBean = map.get("monthRepair");
					WorkbenchStatRepairBean todayRepairBean = map.get("todayRepair");
					WorkbenchStatRepairBean weekRepairBean = map.get("weekRepair");
					if(yearRepairBean != null){
						currentYearTimeView.setText(yearRepairBean.getTime());
						currentYearTotalView.setText(yearRepairBean.getTotalNumber());
						currentYearDealingView.setText(yearRepairBean.getRepairingNumber());
						currentYearCompleteView.setText(yearRepairBean.getCompleteNumber());
					}
					if(monthRepairBean != null){
						currentMonthNotAcceptView.setText(monthRepairBean.getNotAcceptNumber());
						currentMonthRepairingView.setText(monthRepairBean.getDealtNumber());
						currentMonthToatalView.setText(monthRepairBean.getTotalNumber());
						currentMonthMonthNameView.setText(monthRepairBean.getTime());
					}
					if(weekRepairBean != null){
						currentWeekTimeView.setText(weekRepairBean.getTime());
						currentWeekTotalView.setText(weekRepairBean.getTotalNumber());
						currentWeekDealingView.setText(weekRepairBean.getRepairingNumber());
						currentWeekCompleteView.setText(weekRepairBean.getCompleteNumber());
					}
					if(todayRepairBean != null){
						todayTimeView.setText(todayRepairBean.getTime());
						todayTotalView.setText(todayRepairBean.getTotalNumber());
						todayDealingView.setText(todayRepairBean.getRepairingNumber());
						todayCompleteView.setText(todayRepairBean.getCompleteNumber());
					}
				}
				else{
					ToastUtil.showToast(mContext, "没有报修数据");
				}
			}

		});
	}

	static class WorkbenchMaterialViewHolder{
		TextView nameView;
		TextView monthUsedNumberView;
		TextView brandView;
		TextView specificationView;
		TextView storeNumberView;
	}

	private class MaterialListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return materialBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return materialBeanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return materialBeanList.get(position).getId().longValue();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			WorkbenchMaterialViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_workbench_material, null);
				holder = new WorkbenchMaterialViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.material_page_material_name);
				holder.brandView = (TextView) convertView.findViewById(R.id.material_page_brand);
				holder.monthUsedNumberView = (TextView) convertView.findViewById(R.id.material_page_month_used_number);
				holder.specificationView = (TextView) convertView.findViewById(R.id.material_page_specification);
				holder.storeNumberView = (TextView) convertView.findViewById(R.id.material_page_store_number);
				convertView.setTag(holder);
			}
			WorkbenchStatMaterialBean workbenchStatMaterialBean = materialBeanList.get(position);
			holder = (WorkbenchMaterialViewHolder) convertView.getTag();
			holder.nameView.setText(workbenchStatMaterialBean.getName());
			holder.brandView.setText(workbenchStatMaterialBean.getBrand());
			holder.monthUsedNumberView.setText(workbenchStatMaterialBean.getMonthUsedNumber().toString()+workbenchStatMaterialBean.getUnit());
			holder.storeNumberView.setText(workbenchStatMaterialBean.getStoreNumber().toString()+workbenchStatMaterialBean.getUnit());
			holder.specificationView.setText(workbenchStatMaterialBean.getSpecification());
			return convertView;
		}

	}

	private void loadMaterialAsyncHttp( final boolean isRefresh,String orderType,String sort,String search){
		if(isRefresh){
			materialCurrentStart = 0;
		}
		else{
			materialCurrentStart += application.getPagingSize();
		}
		String url = application.getCommonHttpUrlActionManager().getStatMaterialListLoadUrl();
		AjaxParams params = new AjaxParams();
		
		if(application.getAppFlag().equals("fjnu")){
			//师大独有common版本不去掉
			params.put("userId", application.getLoginInfoBean().getUserId().toString());
			
		}
		params.put("start", materialCurrentStart.toString());
		params.put("limit", String.valueOf(application.getPagingSize()));
		params.put("order", orderType);
		params.put("dir", sort);
		params.put("condition", search);
		Logger.i("[params:" + params.getParamString() + "]");
		Logger.i("[URL:" + url +"]");

		new FinalHttp().post(url, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				HttpResponseUtil.justToast(new Exception(t), mContext);
			}

			@Override
			public void onStart() {
				showProgressDialog("材料信息获取中");
			}

			@Override
			public void onSuccess(Object t) {
				Logger.i("[result:" + t +"]");
				hideProgressDialog();
				ResponseBean<List<WorkbenchStatMaterialBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<WorkbenchStatMaterialBean>>>(){}.getType());
				List<WorkbenchStatMaterialBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
				}
				else{
					if(isRefresh){
						materialBeanList.clear();
						materialPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						materialPullToRefreshView.onFooterRefreshComplete();
					}
					materialBeanList.addAll(resultList);
					materialListBaseAdapter.notifyDataSetChanged();

				}
			}

		});
	}

	static class WorkbenchEmploymentViewHolder{
		TextView nameView;
		TextView notAcceptView;
		TextView repairingView;
		TextView monthTotalView;
	}

	private class EmploymentListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return employmentBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return employmentBeanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return employmentBeanList.get(position).getEmploymentId().longValue();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			WorkbenchEmploymentViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_workbench_employment, null);
				holder = new WorkbenchEmploymentViewHolder();
				holder.monthTotalView = (TextView) convertView.findViewById(R.id.employment_page_emplyment_month_total);
				holder.nameView = (TextView) convertView.findViewById(R.id.employment_page_emplyment_name);
				holder.notAcceptView = (TextView) convertView.findViewById(R.id.employment_page_emplyment_not_accept);
				holder.repairingView = (TextView) convertView.findViewById(R.id.employment_page_emplyment_repairing);
				convertView.setTag(holder);
			}
			WorkbenchStatEmploymentBean workbenchStatEmploymentBean = (WorkbenchStatEmploymentBean) getItem(position);
			holder = (WorkbenchEmploymentViewHolder) convertView.getTag();
			holder.monthTotalView.setText(workbenchStatEmploymentBean.getMonthTotal().toString());
			holder.nameView.setText(workbenchStatEmploymentBean.getName());
			holder.notAcceptView.setText(workbenchStatEmploymentBean.getNotAcceptNumber().toString());
			holder.repairingView.setText(workbenchStatEmploymentBean.getRepairingNumber().toString());
			return convertView;
		}

	}

	private void loadEmploymentAsyncHttp(final boolean isRefresh){
		String url = application.getCommonHttpUrlActionManager().getStatEmploymentListLoadUrl();
		if(isRefresh){
			employmentCurrentStart = 0;
		}
		else{
			employmentCurrentStart += application.getPagingSize();
		}
		AjaxParams params = new AjaxParams();
		if(application.getAppFlag().equals("fjnu")){
			//师大独有，common版本不去掉
			params.put("userId", application.getLoginInfoBean().getUserId().toString());
			
		}
		params.put("start", employmentCurrentStart.toString());
		params.put("limit", String.valueOf(application.getPagingSize()));
		new FinalHttp().post(url, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
			}

			@Override
			public void onStart() {
				showProgressDialog("获取人员统计中……");
			}

			@Override
			public void onSuccess(Object t) {
				hideProgressDialog();
				ResponseBean<List<WorkbenchStatEmploymentBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<WorkbenchStatEmploymentBean>>>(){}.getType());
				List<WorkbenchStatEmploymentBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
				}
				else{
					if(isRefresh){
						employmentBeanList.clear();
						employmentPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						employmentPullToRefreshView.onFooterRefreshComplete();
					}
					employmentBeanList.addAll(resultList);
					employmentListBaseAdapter.notifyDataSetChanged();
				}
			}

		});
	}
	
	
	static class WorkbenchDivisionViewHolder{
		TextView nameView;
	}

}
