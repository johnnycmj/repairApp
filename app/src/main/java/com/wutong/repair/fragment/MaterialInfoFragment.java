package com.wutong.repair.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repair.activity.QueryMaterialsActivity;
import com.wutong.repair.data.bean.MaterialBean;
import com.wutong.repair.data.bean.MaterialGrantBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.dictionary.QueryMaterialDict;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.StringUtil;
import com.wutong.repair.util.TimeUtil;

public class MaterialInfoFragment extends BaseFragment {
	
	private final static int VIEW_CODE_MY_MATERIAL = 71;
	private final static int VIEW_CODE_QUERY_MATERIAL = 72;
	
	public final static int CALL_CODE_GRANT_REFRESH = 0;
	private static final int SWITCHER_MY_MATERIAL = 0;
	private static final int SWITCHER_QUERY_MATERIAL = 1;

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private ViewPager mViewPager;
	private PagerAdapter pagerAdapter;

	private List<MaterialGrantBean> materialGrantBeanList;
	private MaterialGrantBaseAdapter grantAdapter;
	private ListView mMaterialGrantView;
	private PullToRefreshView queryMaterialPullToRefreshView;

	private List<MaterialBean> materialBeanList;
	private MaterialBaseAdapter materialAdapter;
	private ListView mMaterialListView;
	private PullToRefreshView myMaterialPullToRefreshView;

	private TextView myMaterialSwitcherView;
	private TextView queryMaterialSwitcherView;
	
	private View myMaterialPageContentView;
	private View queryMaterialPageContentView;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_material_info);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		//
		titlebarInit();
		viewInit();
		callInit();
		setupData();
		return fragmentView;
	}
	
	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_material_info);
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
				MaterialInfoFragment.this.getActivity().onBackPressed();

			}
		});
		


	}

	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_material_info_fragment_name), new CallFragmentListener() {
			
			@Override
			public void onCall(int callCode) {
				switch (callCode) {
				case CALL_CODE_GRANT_REFRESH:
					loadMaterialListAsyncHttp();
					loadMaterialGrantAsyncHttp();
					break;
				}
				
			}
		});
	}
	private void viewInit(){
		mViewPager = (ViewPager) findViewById(R.id.viewpager_material_info);
		myMaterialSwitcherView = (TextView) findViewById(R.id.material_info_switcher_my_materials);
		queryMaterialSwitcherView = (TextView) findViewById(R.id.material_info_switcher_query_materials);

		//
		pagerAdapter = new MaterailInfoPagerAdapter();
		mViewPager.setAdapter(pagerAdapter);
		//
		materialBeanList = new ArrayList<MaterialBean>();
		materialAdapter = new MaterialBaseAdapter(materialBeanList);
		//
		materialGrantBeanList = new ArrayList<MaterialGrantBean>();
		grantAdapter = new MaterialGrantBaseAdapter(materialGrantBeanList);
		//
		
		myMaterialPageContentView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_my_material_list, null);
		myMaterialPullToRefreshView = (PullToRefreshView) myMaterialPageContentView.findViewById(R.id.my_material_pull_refresh_view);
		mMaterialListView = (ListView) myMaterialPageContentView.findViewById(R.id.material_list);
		View emptyMyMaterial = myMaterialPageContentView.findViewById(R.id.my_material_empty);
		mMaterialListView.setEmptyView(emptyMyMaterial);
		emptyMyMaterial.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadMaterialListAsyncHttp();
				
			}
		});
		mMaterialListView.setAdapter(materialAdapter);
		queryMaterialPageContentView = LayoutInflater.from(mContext).inflate(R.layout.viewpager_material_grant_history, null);
		queryMaterialPullToRefreshView = (PullToRefreshView) queryMaterialPageContentView.findViewById(R.id.query_material_pull_refresh_view);
		mMaterialGrantView = (ListView) queryMaterialPageContentView.findViewById(R.id.grant_list);
		mMaterialGrantView.setAdapter(grantAdapter);
		
		View emptyQueryMaterial = queryMaterialPageContentView.findViewById(R.id.query_material_empty);
		mMaterialGrantView.setEmptyView(emptyQueryMaterial);
		emptyQueryMaterial.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadMaterialGrantAsyncHttp();
				
			}
		});
		myMaterialPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMaterialListAsyncHttp();
				
			}
		});
		myMaterialPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {
			
			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMaterialListAsyncHttp();
				
			}
		});
		
		queryMaterialPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMaterialGrantAsyncHttp();
				
			}
		});
		
		queryMaterialPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {
			
			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMaterialGrantAsyncHttp();
				
			}
		});
		
		
		mMaterialGrantView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long id) {
				if(position < materialGrantBeanList.size()){
					Intent intent = new Intent();
					intent.setClass(mContext, QueryMaterialsActivity.class);
					intent.putExtra("grantId", String.valueOf(id));
					//
					MaterialGrantBean materialGrantBean = materialGrantBeanList.get(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable("materialGrantBean", (Serializable)materialGrantBean);
					intent.putExtras(bundle);
					startActivityForResult(intent, 0);
				}

			}
		});
		
		myMaterialSwitcherView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(SWITCHER_MY_MATERIAL);

			}
		});
		queryMaterialSwitcherView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(SWITCHER_QUERY_MATERIAL);

			}
		});

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				switcherChange(position);

			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int status) {

			}
		});

		//
	}

	private void setupData(){
		loadMaterialListAsyncHttp();
		loadMaterialGrantAsyncHttp();

	}


	private class MaterailInfoPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}


		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager)container).removeView((View)object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View viewPagerView = null;
			switch (position) {
			case 0://我的材料
				viewPagerView = myMaterialPageContentView;
				break;
			case 1://申领材料
				viewPagerView = queryMaterialPageContentView;
				break;
			default:
				break;
			}
			((ViewPager)container).addView(viewPagerView);
			return viewPagerView;
		}

	}

	private class MaterialGrantBaseAdapter extends BaseAdapter{

		private List<MaterialGrantBean> list;
		public MaterialGrantBaseAdapter(List<MaterialGrantBean> list) {
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
			MaterialGrantBean materialGrantBean = list.get(position);
			convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_QUERY_MATERIAL), null);
			TextView grantCodeView = (TextView) convertView.findViewById(R.id.grant_code);
			TextView grantTimeView = (TextView) convertView.findViewById(R.id.grant_time);
			TextView grantStatusView = (TextView) convertView.findViewById(R.id.grant_status_name);
			grantTimeView.setText(materialGrantBean.getGrantTime());
			grantStatusView.setText(QueryMaterialDict.getStatusName(materialGrantBean.getStatus()));
			grantCodeView.setText( materialGrantBean.getGrantCode());
			if(materialGrantBean.getStatus() == null){
				ToastUtil.showToast(mContext, "状态异常，显示颜色可能不正确");
			}
			else if(materialGrantBean.getStatus().equals("2")){
				grantStatusView.setTextColor(getResources().getColor(R.color.common_status_red));
			}
			
			return convertView;
		}

	}

	private class MaterialBaseAdapter extends BaseAdapter{

		private List<MaterialBean> list;
		public MaterialBaseAdapter(List<MaterialBean> list) {
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
			MaterialBean materialBean = list.get(position);
			convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_MY_MATERIAL), null);
			TextView materialNameView = (TextView) convertView.findViewById(R.id.materail_name);
			TextView materialSpecView = (TextView) convertView.findViewById(R.id.materail_spec);
			TextView materialNumberView = (TextView) convertView.findViewById(R.id.materail_number);
			materialNameView.setText(materialBean.getMaterialName());
			materialSpecView.setText(materialBean.getSpecification());
			materialNumberView.setText(getString(R.string.format_material_select_number_unit,materialBean.getTotalNumber().toString(),materialBean.getUnit()));
			return convertView;
		}

	}

	private void loadMaterialGrantAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getMaterialGrantListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair repairManIdPair = new BasicNameValuePair("repairManId", application.getLoginInfoBean().getUserId().toString());
		params.add(repairManIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<MaterialGrantBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<MaterialGrantBean>>>(){}.getType());
				List<MaterialGrantBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				if(responseList != null && !responseList.isEmpty()){
					materialGrantBeanList.clear();
					grantAdapter.notifyDataSetChanged();
					materialGrantBeanList.addAll(responseList);
					grantAdapter.notifyDataSetChanged();
				}
				queryMaterialPullToRefreshView.onHeaderRefreshComplete();
				queryMaterialPullToRefreshView.onFooterRefreshComplete();
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
				queryMaterialPullToRefreshView.onHeaderRefreshComplete();
				queryMaterialPullToRefreshView.onFooterRefreshComplete();
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();

			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	/**
	 * 获取我的材料（维修工）
	 */
	private void loadMaterialListAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairmanMaterialListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair repairManIdPair = new BasicNameValuePair("repairManId", application.getLoginInfoBean().getUserId().toString());
		params.add(repairManIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<MaterialBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<MaterialBean>>>(){}.getType());
				List<MaterialBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				if(responseList != null && !responseList.isEmpty()){
					materialBeanList.clear();
					materialAdapter.notifyDataSetChanged();
					materialBeanList.addAll(responseList);
					materialAdapter.notifyDataSetChanged();
				}
				myMaterialPullToRefreshView.onHeaderRefreshComplete();
				myMaterialPullToRefreshView.onFooterRefreshComplete();
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
				myMaterialPullToRefreshView.onHeaderRefreshComplete();
				myMaterialPullToRefreshView.onFooterRefreshComplete();
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}


	private void switcherChange(int position){
		myMaterialSwitcherView.setEnabled(true);
		queryMaterialSwitcherView.setEnabled(true);
		switch (position) {
		case SWITCHER_MY_MATERIAL:
			myMaterialSwitcherView.setEnabled(false);
			break;
		case SWITCHER_QUERY_MATERIAL:
			queryMaterialSwitcherView.setEnabled(false);
			break;
		}
	}
	
	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			myMaterialSwitcherView.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			myMaterialSwitcherView.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			queryMaterialSwitcherView.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			queryMaterialSwitcherView.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			
			View pageRepairSwitcherLayout = findViewById(R.id.page_repair_switcher_layout);
			if(pageRepairSwitcherLayout != null){
				pageRepairSwitcherLayout.setBackgroundResource(R.drawable.spring_horse_modular_repair_page_switchers_bg);
			}
			
			//
			Drawable emptyTopDraw = getResources().getDrawable(R.drawable.spring_horse_empty_icon_repair_order);
			View emptyMyMaterial = myMaterialPageContentView.findViewById(R.id.my_material_empty);
			TextView myMaterialTipView = (TextView) emptyMyMaterial.findViewById(R.id.empty_tip);
			TextView myMaterialClickTipView = (TextView) emptyMyMaterial.findViewById(R.id.empty_click_tip);
			myMaterialTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			myMaterialClickTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			myMaterialTipView.setCompoundDrawablesWithIntrinsicBounds(null, emptyTopDraw, null, null);
			
			View emptyQueryMaterial = queryMaterialPageContentView.findViewById(R.id.query_material_empty);
			TextView queryMaterialTipView = (TextView) emptyQueryMaterial.findViewById(R.id.empty_tip);
			TextView queryMaterialClickTipView = (TextView) emptyQueryMaterial.findViewById(R.id.empty_click_tip);
			queryMaterialTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			queryMaterialClickTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			queryMaterialTipView.setCompoundDrawablesWithIntrinsicBounds(null, emptyTopDraw, null, null);
			//
			
			break;

		default:
			break;
		}
	}
	
	
	private int selectedItemLayout(int viewCode){
		switch (application.getSkinType()) {
		case 1:
			switch (viewCode) {
			case VIEW_CODE_MY_MATERIAL:
				return R.layout.spring_horse_listview_my_material;
			case VIEW_CODE_QUERY_MATERIAL:
				return R.layout.spring_horse_listview_material_grant;
			default:
				break;
			}

		default:
			switch (viewCode) {
			case VIEW_CODE_MY_MATERIAL:
				return R.layout.listview_my_material;
			case VIEW_CODE_QUERY_MATERIAL:
				return R.layout.listview_material_grant;
			default:
				break;
			}
		}
		return 0;
	}
	
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_material_info;

		default:
			return R.layout.activity_material_info;
		}
	}
}
