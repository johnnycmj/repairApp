package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseActivity;
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.RepairOrderBean;
import com.wutong.repair.data.bean.RepairOrderPagerBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.TimeUtil;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EmploymentRepairGroupListActivity extends BaseActivity {
	private final static int VIEW_CODE_NOT_ACCEPT = 91;
	private final static int VIEW_CODE_REPAIRING = 92;
	private final static int VIEW_CODE_FINISH = 93;
	
	public final static int CALL_CODE_NOT_ACCEPT_REFRESH = 0;
	public final static int CALL_CODE_REPAIRING_REFRESH = 1;
	public final static int CALL_CODE_FINISH_REFRESH = 2;
	public final static int CALL_CODE_REPAIRING_AND_FINISH_REFRESH = 3;
	public final static int CALL_CODE_NOT_ACCEPT_AND_REPAIRING_REFRESH = 4;

	private final static int PAGE_REPAIR_SWITCHER_NOT_ACCEPT = 0;
	private final static int PAGE_REPAIR_SWITCHER_REPAIRING = 1;
	private final static int PAGE_REPAIR_SWITCHER_FINISH = 2;

	private String loadSingleTostKey;

	private String nullToastKey ;
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private ViewPager mStatusViewPagerView;
	private PagerAdapter pagerAdapter;

	private TextView notAcceptSwitcher;
	private TextView repairingSwitcher;
	private TextView finishSwitcher;



	private int currentStart = 0;

	private int currentRepairSubPagePosition;

	private String repairmanId;
	private String repairmanName;

	private View notAcceptPagerContentView;
	private View repairingPagerContentView;
	private View finnishPagerContentView;
	//
	private PullToRefreshView notAcceptPullToRefreshView;
	private GridView notAcceptGridView;
	private RepairOrderNotAcceptBaseAdapter repairNotAcceptBaseAdapter;

	private PullToRefreshView repairingPullToRefreshView;
	private GridView repairingGridView;
	private RepairOrderRepairingBaseAdapter repairRepairingBaseAdapter;

	private PullToRefreshView finishPullToRefreshView;
	private GridView finishGridView;
	private RepairOrderFinishBaseAdapter repairFinishBaseAdapter;

	private List<RepairOrderBean> notAcceptList;//未受理
	private List<RepairOrderBean> repairingList;//维修中
	private List<RepairOrderBean> finishList;//已完成
	private boolean startInit;
	private boolean ingInit;
	private boolean endInit;


	protected Map<String, CallFragmentListener> callFragmentListenerMap;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.page_name_activity_employment_repair_group_list);
		loadSingleTostKey = UUID.randomUUID().toString();
		nullToastKey = UUID.randomUUID().toString();
		intentInit();
		titlebarInit();
		viewInit();
		callInit();
		setupData();
	}

	private void intentInit(){
		repairmanId = getIntent().getStringExtra("repairmanId");
		repairmanName = getIntent().getStringExtra("repairmanName");
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		//某某某的维修单
		titlebarTitleView.setText(getString(R.string.format_title_employment_repair_group,repairmanName));
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EmploymentRepairGroupListActivity.this.onBackPressed();

			}
		});
	}

	private void callInit(){
		callFragmentListenerMap.put(getString(R.string.call_fragment_repair_fragment_name), new CallFragmentListener() {

			@Override
			public void onCall(int callCode) {
				switch (callCode) {
				case CALL_CODE_NOT_ACCEPT_REFRESH:
					loadMoreRepairOrder(true, "1");
					break;
				case CALL_CODE_REPAIRING_REFRESH:
					loadMoreRepairOrder(true, "2");
					break;
				case CALL_CODE_FINISH_REFRESH:
					loadMoreRepairOrder(true, "3");
					break;
				case CALL_CODE_REPAIRING_AND_FINISH_REFRESH:
					loadMoreRepairOrder(true, "2");
					loadMoreRepairOrder(true, "3");
					break;
				case CALL_CODE_NOT_ACCEPT_AND_REPAIRING_REFRESH:
					loadMoreRepairOrder(true, "1");
					loadMoreRepairOrder(true, "2");
					break;
				default:
					break;
				}

			}
		});
	}

	private void viewInit(){

		callFragmentListenerMap = new HashMap<String, CallFragmentListener>();
		mStatusViewPagerView = (ViewPager) findViewById(R.id.viewpager_repair_order);
		//
		notAcceptPagerContentView = LayoutInflater.from(mContext).inflate(R.layout.vp_page_repair_repair_order_not_accept, null);
		repairingPagerContentView = LayoutInflater.from(mContext).inflate(R.layout.vp_page_repair_repair_order_repairing, null);
		finnishPagerContentView = LayoutInflater.from(mContext).inflate(R.layout.vp_page_repair_repair_order_finish, null);

		notAcceptSwitcher = (TextView) findViewById(R.id.employment_repair_group_list_switcher_not_accept);
		repairingSwitcher = (TextView) findViewById(R.id.employment_repair_group_list_switcher_repairing);
		finishSwitcher = (TextView) findViewById(R.id.employment_repair_group_list_switcher_finish);



		notAcceptSwitcher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageRepairSwitcherChange(PAGE_REPAIR_SWITCHER_NOT_ACCEPT);

			}
		});
		repairingSwitcher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageRepairSwitcherChange(PAGE_REPAIR_SWITCHER_REPAIRING);

			}
		});
		finishSwitcher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageRepairSwitcherChange(PAGE_REPAIR_SWITCHER_FINISH);

			}
		});

		notAcceptList = new ArrayList<RepairOrderBean>();
		repairingList = new ArrayList<RepairOrderBean>();
		finishList = new ArrayList<RepairOrderBean>();

		notAcceptGridView = (GridView) notAcceptPagerContentView.findViewById(R.id.grid_repair_order);
		repairingGridView = (GridView) repairingPagerContentView.findViewById(R.id.grid_repair_order);
		finishGridView = (GridView) finnishPagerContentView.findViewById(R.id.grid_repair_order);
		notAcceptPullToRefreshView = (PullToRefreshView) notAcceptPagerContentView.findViewById(R.id.grid_pull_refresh_view);
		repairingPullToRefreshView = (PullToRefreshView) repairingPagerContentView.findViewById(R.id.grid_pull_refresh_view);
		finishPullToRefreshView = (PullToRefreshView) finnishPagerContentView.findViewById(R.id.grid_pull_refresh_view);

		View notAcceptEmptyView = notAcceptPagerContentView.findViewById(R.id.grid_empty);
		TextView notAcceptTipView = (TextView) notAcceptEmptyView.findViewById(R.id.empty_tip);
		notAcceptTipView.setText(getString(R.string.label_empty_repair_order_collection_zero_tip_not_accept));
		notAcceptPagerContentView.findViewById(R.id.footer_no_more_tip).setVisibility(ViewGroup.GONE);
		notAcceptGridView.setEmptyView(notAcceptEmptyView);
		notAcceptEmptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				showProgressDialog();
				loadMoreRepairOrder(true, "1");

			}
		});
		View repairingEmptyView = repairingPagerContentView.findViewById(R.id.grid_empty);
		TextView repairingTipView = (TextView) repairingEmptyView.findViewById(R.id.empty_tip);
		repairingTipView.setText(getString(R.string.label_empty_repair_order_collection_zero_tip_repairing));
		repairingPagerContentView.findViewById(R.id.footer_no_more_tip).setVisibility(ViewGroup.GONE);
		repairingGridView.setEmptyView(repairingEmptyView);
		repairingEmptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				showProgressDialog();
				loadMoreRepairOrder(true, "2");

			}
		});
		View finishEmptyView = finnishPagerContentView.findViewById(R.id.grid_empty);
		TextView finishTipView = (TextView) finishEmptyView.findViewById(R.id.empty_tip);
		finishTipView.setText(getString(R.string.label_empty_repair_order_collection_zero_tip_finish));
		finnishPagerContentView.findViewById(R.id.footer_no_more_tip).setVisibility(ViewGroup.GONE);
		finishGridView.setEmptyView(finishEmptyView);
		finishEmptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				showProgressDialog();
				loadMoreRepairOrder(true, "3");

			}
		});

		notAcceptPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMoreRepairOrder(true, "1");

			}
		});
		notAcceptPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMoreRepairOrder(false, "1");

			}
		});

		repairingPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMoreRepairOrder(true, "2");

			}
		});
		repairingPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMoreRepairOrder(false, "2");

			}
		});

		finishPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMoreRepairOrder(true, "3");

			}
		});
		finishPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMoreRepairOrder(false, "3");

			}
		});

		repairNotAcceptBaseAdapter = new RepairOrderNotAcceptBaseAdapter(notAcceptList);
		repairRepairingBaseAdapter = new RepairOrderRepairingBaseAdapter(repairingList);
		repairFinishBaseAdapter = new RepairOrderFinishBaseAdapter(finishList);

		notAcceptGridView.setAdapter(repairNotAcceptBaseAdapter);
		repairingGridView.setAdapter(repairRepairingBaseAdapter);
		finishGridView.setAdapter(repairFinishBaseAdapter);

		notAcceptGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View convertView,
					int position, long id) {
				if(position < notAcceptList.size()){
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					RepairOrderBean repairOrderBean = notAcceptList.get(position);
					intent.putExtra("repairOrderId", repairOrderBean.getId());
					intent.putExtra("repairType", repairOrderBean.getFormType());
					intent.setClass(mContext, RepairOrderDetailActivity.class);
					intent.putExtra("lastFragmentOrActivity", EmploymentRepairGroupListActivity.this.getClass().getName());
					intent.putExtra("isFromIndex", true);
					intent.putExtras(bundle);
					startActivityForResult(intent,0);
				}
			}
		});

		repairingGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View convertView,
					int position, long id) {
				if(position < repairingList.size()){
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					RepairOrderBean repairOrderBean = repairingList.get(position);
					intent.putExtra("repairOrderId", repairOrderBean.getId());
					intent.putExtra("repairType", repairOrderBean.getFormType());
					intent.setClass(mContext, RepairOrderDetailActivity.class);
					intent.putExtra("lastFragmentOrActivity", EmploymentRepairGroupListActivity.this.getClass().getName());
					intent.putExtra("isFromIndex", true);
					intent.putExtras(bundle);
					startActivityForResult(intent,0);
				}
			}
		});

		finishGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View convertView,
					int position, long id) {
				if(position < finishList.size()){
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					RepairOrderBean repairOrderBean = finishList.get(position);
					intent.putExtra("repairOrderId", repairOrderBean.getId());
					intent.putExtra("repairType", repairOrderBean.getFormType());
					intent.setClass(mContext, RepairOrderDetailActivity.class);
					intent.putExtra("lastFragmentOrActivity", EmploymentRepairGroupListActivity.this.getClass().getName());
					intent.putExtra("isFromIndex", true);
					intent.putExtras(bundle);
					startActivityForResult(intent,0);
				}
			}
		});
		pagerAdapter = new RepairOrderPagePagerAdapter();
		mStatusViewPagerView.setAdapter(pagerAdapter);
		mStatusViewPagerView.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				pageRepairSwitcherChange(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

				Logger.i("onPageScrollStateChanged:" + state);
				if(state == ViewPager.SCROLL_STATE_IDLE){
					switch (currentRepairSubPagePosition) {
					case 0:
						if(!startInit){
							Logger.i("startInit:"+startInit);
							showProgressDialog();
							loadMoreRepairOrder(true, "1");

						}
						break;
					case 1:
						if(!ingInit){
							Logger.i("ingInit:"+ingInit);
							showProgressDialog();
							loadMoreRepairOrder(true, "2");
						}
						break;
					case 2:
						if(!endInit){
							Logger.i("endInit:"+endInit);
							showProgressDialog();
							loadMoreRepairOrder(true, "3");
						}
						break;

					}
				}

			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void setupData(){
		startInit = false;
		ingInit = false;
		endInit = false;
		pageRepairSwitcherChange(PAGE_REPAIR_SWITCHER_NOT_ACCEPT);
		loadMoreRepairOrder(true, "1");
	}


	static class RepairOrderViewHolder{
		TextView assetView;
		TextView troubleView;
		TextView timeView;
		TextView statusView;
		TextView addressView;
	}

	private class RepairOrderBaseAdapter extends BaseAdapter{

		private List<RepairOrderBean> list;
		public RepairOrderBaseAdapter(List<RepairOrderBean> list) {
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
			RepairOrderViewHolder holder;
			RepairOrderBean repairOrderBean = (RepairOrderBean)getItem(position);
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_repair_type_pager, null);
				holder = new RepairOrderViewHolder();
				holder.assetView = (TextView) convertView.findViewById(R.id.repair_order_pager_asset_name);
				holder.troubleView = (TextView) convertView.findViewById(R.id.repair_order_pager_name);
				holder.timeView = (TextView) convertView.findViewById(R.id.repair_order_pager_time);
				holder.statusView = (TextView) convertView.findViewById(R.id.repair_order_pager_status);
				holder.addressView = (TextView) convertView.findViewById(R.id.repair_order_pager_address);
				List<Object> tags = new ArrayList<Object>();
				tags.add(repairOrderBean);
				tags.add(holder);
				convertView.setTag(tags);
			}
			List<Object> tags = (List<Object>) convertView.getTag();
			holder = (RepairOrderViewHolder) tags.get(1);
			holder.addressView.setText(repairOrderBean.getRoomName());
			holder.assetView.setText(repairOrderBean.getAssetName());
			holder.troubleView.setText(repairOrderBean.getTroubleName());
			holder.timeView.setText(repairOrderBean.getStartTime());
			holder.statusView.setText(repairOrderBean.getStatusName());
			return convertView;
		}

	}



	private class RepairOrderPagePagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return 3;
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
			case 0:
				viewPagerView = notAcceptPagerContentView;
				break;
			case 1:
				viewPagerView = repairingPagerContentView;
				break;
			case 2:
				viewPagerView = finnishPagerContentView;
				break;
			}
			((ViewPager)container).addView(viewPagerView);
			return viewPagerView;
		}


	}



	/**
	 * 获取更多或刷新报修单
	 * @param isRefresh 是否刷新
	 * @param groupCode 分组code，未受理1、维修中2、已完成3
	 */
	private void loadMoreRepairOrder(boolean isRefresh, String groupCode){
		if(isRefresh){
			currentStart = 0;
		}
		else{
			currentStart += application.getPagingSize();
		}
		RepairOrderPagerBean repairOrderPagerBean = new RepairOrderPagerBean("","",groupCode,currentStart,application.getPagingSize());
		asyncLoadRepairOrderHttp(repairOrderPagerBean,isRefresh);
	}


	/**
	 * 异步获取报修单 替代RepairOrderPagerAsycTask
	 * @param repairOrderPagerBean
	 * @param isRefresh
	 */
	private void asyncLoadRepairOrderHttp(RepairOrderPagerBean repairOrderPagerBean,final boolean isRefresh){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		//Fix分支
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getStatEmploymentRepairOrderGroupListUrl());

		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair userIdPair = new BasicNameValuePair("userId", repairmanId);
		params.add(userIdPair);
		NameValuePair formTypePair = new BasicNameValuePair("formType", "0");
		params.add(formTypePair);
		NameValuePair groupCodePair = new BasicNameValuePair("group", repairOrderPagerBean.getGroupCode());
		params.add(groupCodePair);
		NameValuePair startPair = new BasicNameValuePair("start", repairOrderPagerBean.getStart());
		params.add(startPair);
		NameValuePair limitPair = new BasicNameValuePair("limit", repairOrderPagerBean.getLimit());
		params.add(limitPair);
		httpFormBean.setParams(params);
		final String groupCode = repairOrderPagerBean.getGroupCode();
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<RepairOrderBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<RepairOrderBean>>>(){}.getType());
				List<RepairOrderBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				if(groupCode.equals("1")){
					if(isRefresh){
						notAcceptList.clear();
						notAcceptList.addAll(resultList);
						repairNotAcceptBaseAdapter.notifyDataSetChanged();
						notAcceptPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						notAcceptList.addAll(resultList);
						repairNotAcceptBaseAdapter.notifyDataSetChanged();
						notAcceptPullToRefreshView.onFooterRefreshComplete();
					}
					//					notAcceptSwitcher.setText(getString(R.string.format_page_repair_switcher_not_accept,notAcceptList.size()));
					if(resultList.size() < application.getPagingSize()){
						//footer
						notAcceptPagerContentView.findViewById(R.id.grid_repair_order_footer).setVisibility(ViewGroup.VISIBLE);
					}
					else{
						notAcceptPagerContentView.findViewById(R.id.grid_repair_order_footer).setVisibility(ViewGroup.GONE);
					}
					startInit = true;
				}
				else if(groupCode.equals("2")){
					if(isRefresh){
						repairingList.clear();
						repairingList.addAll(resultList);
						repairRepairingBaseAdapter.notifyDataSetChanged();
						repairingPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						repairingList.addAll(resultList);
						repairRepairingBaseAdapter.notifyDataSetChanged();
						repairingPullToRefreshView.onFooterRefreshComplete();
					}
					//					repairingSwitcher.setText(getString(R.string.format_page_repair_switcher_reparing,repairingList.size()));
					if(resultList.size() < application.getPagingSize()){
						//footer
						repairingPagerContentView.findViewById(R.id.grid_repair_order_footer).setVisibility(ViewGroup.VISIBLE);
					}
					else{
						repairingPagerContentView.findViewById(R.id.grid_repair_order_footer).setVisibility(ViewGroup.GONE);
					}
					ingInit = true;
				}
				else if(groupCode.equals("3")){
					if(isRefresh){
						finishList.clear();
						finishList.addAll(resultList);
						repairFinishBaseAdapter.notifyDataSetChanged();
						finishPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						finishList.addAll(resultList);
						repairFinishBaseAdapter.notifyDataSetChanged();
						finishPullToRefreshView.onFooterRefreshComplete();
					}
					//					finishSwitcher.setText(getString(R.string.format_page_repair_switcher_finish,finishList.size()));
					if(resultList.size() < application.getPagingSize()){
						//footer
						finnishPagerContentView.findViewById(R.id.grid_repair_order_footer).setVisibility(ViewGroup.VISIBLE);
					}
					else{
						finnishPagerContentView.findViewById(R.id.grid_repair_order_footer).setVisibility(ViewGroup.GONE);
					}
					endInit = true;
				}
				else {

				}
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
				hideProgressDialog();

			}

			@Override
			public void beforeDealt() {
				showProgressDialog();

			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	/**
	 * xml中调用该方法
	 * <pre>
	 * 来自{@link expandable_list_view_repair_order_child}
	 * </pre>
	 * @param view
	 */
	public void childOnClick(View view){
		List<Object> tags = (List<Object>) view.getTag();
		Object obj = tags.get(0);
		if(obj != null){
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			RepairOrderBean repairOrderBean = (RepairOrderBean)obj;
			intent.putExtra("repairOrderId", repairOrderBean.getId());
			intent.putExtra("repairType", repairOrderBean.getFormType());
			intent.setClass(mContext, RepairOrderDetailActivity.class);
			intent.putExtra("lastFragmentOrActivity", EmploymentRepairGroupListActivity.this.getClass().getName());
			intent.putExtra("isFromIndex", true);
			intent.putExtras(bundle);
			startActivityForResult(intent,0);
		}
	}

	/**
	 * 报修page下的切换
	 * @param switcherIndex
	 */
	private void pageRepairSwitcherChange(int switcherIndex){
		notAcceptSwitcher.setEnabled(true);
		repairingSwitcher.setEnabled(true);
		finishSwitcher.setEnabled(true);
		currentRepairSubPagePosition = switcherIndex;
		switch (switcherIndex) {
		case PAGE_REPAIR_SWITCHER_NOT_ACCEPT:
			notAcceptSwitcher.setEnabled(false);
			mStatusViewPagerView.setCurrentItem(PAGE_REPAIR_SWITCHER_NOT_ACCEPT);
			break;
		case PAGE_REPAIR_SWITCHER_REPAIRING:
			repairingSwitcher.setEnabled(false);
			mStatusViewPagerView.setCurrentItem(PAGE_REPAIR_SWITCHER_REPAIRING);
			break;
		case PAGE_REPAIR_SWITCHER_FINISH:
			finishSwitcher.setEnabled(false);
			mStatusViewPagerView.setCurrentItem(PAGE_REPAIR_SWITCHER_FINISH);
			break;
		}
	}

	static class RepairNotAcceptViewHolder{
		TextView assetAndTroubleView;
		TextView placeView;
		TextView startTimeView;
		ImageView hasPhotoView;
		TextView feedbackNumberView;//反馈数
	}


	private class RepairOrderNotAcceptBaseAdapter extends BaseAdapter{
		private List<RepairOrderBean> repairList;
		public RepairOrderNotAcceptBaseAdapter(List<RepairOrderBean> repairList) {
			super();
			this.repairList = repairList;
		}

		@Override
		public int getCount() {
			return repairList.size();
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
			RepairNotAcceptViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_NOT_ACCEPT), null);
				holder = new RepairNotAcceptViewHolder();
				holder.assetAndTroubleView = (TextView) convertView.findViewById(R.id.repair_order_item_asset_and_trouble);
				holder.placeView = (TextView) convertView.findViewById(R.id.repair_order_item_place);
				holder.startTimeView = (TextView) convertView.findViewById(R.id.repair_order_item_start_time);
				holder.hasPhotoView = (ImageView) convertView.findViewById(R.id.repair_order_item_has_photo);
				holder.feedbackNumberView = (TextView) convertView.findViewById(R.id.repair_order_item_feedback_number);
				convertView.setTag(holder);
			}
			holder = (RepairNotAcceptViewHolder) convertView.getTag();
			switch (application.getSkinType()) {
			case 1:
				convertView.setBackgroundResource(R.drawable.spring_horse_grid_repair_order);
				break;

			default:
				int residue = position % 6;
				switch (residue) {
				case 0:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_one);
					break;
				case 1:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_two);
					break;
				case 2:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_three);
					break;
				case 3:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_four);
					break;
				case 4:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_five);
					break;
				case 5:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_six);
					break;
				}
				break;
			}
			RepairOrderBean repairOrderBean = repairList.get(position);
			holder.assetAndTroubleView.getPaint().setFakeBoldText(true);
			holder.assetAndTroubleView.setText(getString(R.string.format_repair_order_asset_and_trouble, repairOrderBean.getAssetName(),repairOrderBean.getTroubleName()));
			holder.startTimeView.setText(repairOrderBean.getStartTime());
			holder.placeView.setText(repairOrderBean.getRoomName());
			//反馈数量决定显示与否
			if(repairOrderBean.getFeedbackCount() != null && repairOrderBean.getFeedbackCount().intValue() >0){
				holder.feedbackNumberView.setVisibility(ViewGroup.VISIBLE);
				holder.feedbackNumberView.setText(repairOrderBean.getFeedbackCount().toString());
			}
			else{
				holder.feedbackNumberView.setVisibility(ViewGroup.GONE);
			}
			if(repairOrderBean.getPhotoNumber() != null && repairOrderBean.getPhotoNumber().intValue() >0){
				holder.hasPhotoView.setVisibility(ViewGroup.VISIBLE);
			}
			else{
				holder.hasPhotoView.setVisibility(ViewGroup.GONE);
			}
			return convertView;
		}

	}

	static class RepairRepairingViewHolder{
		TextView assetAndTroubleView;//标题（报修大项和小项）
		ImageView statusIconView;//转发、返修、维修的标签图
		TextView placeView;//报修地点
		TextView startTimeView;//开始时间
		TextView repairmanView;//维修人员
		TextView acceptTimeView;//受理时间
		TextView feedbackNumberView;//反馈数
		ImageView hasPhotoView;//是否有图片
		View studentRoleView;
		View repairmanRoleView;
	}


	private class RepairOrderRepairingBaseAdapter extends BaseAdapter{
		private List<RepairOrderBean> repairList;
		public RepairOrderRepairingBaseAdapter(List<RepairOrderBean> repairList) {
			super();
			this.repairList = repairList;
		}

		@Override
		public int getCount() {
			return repairList.size();
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
			RepairRepairingViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_REPAIRING), null);
				holder = new RepairRepairingViewHolder();
				holder.assetAndTroubleView = (TextView) convertView.findViewById(R.id.repair_order_item_asset_and_trouble);
				holder.statusIconView = (ImageView) convertView.findViewById(R.id.repair_order_item_status_icon);
				holder.placeView = (TextView) convertView.findViewById(R.id.repair_order_item_place);
				holder.startTimeView = (TextView) convertView.findViewById(R.id.repair_order_item_start_time);
				holder.repairmanView = (TextView) convertView.findViewById(R.id.repair_order_item_repairman_name);
				holder.acceptTimeView = (TextView) convertView.findViewById(R.id.repair_order_item_accept_time);
				holder.hasPhotoView = (ImageView) convertView.findViewById(R.id.repair_order_item_has_photo);
				holder.feedbackNumberView = (TextView) convertView.findViewById(R.id.repair_order_item_feedback_number);
				holder.studentRoleView = convertView.findViewById(R.id.repair_order_student_role_show);
				holder.repairmanRoleView = convertView.findViewById(R.id.repair_order_repairman_role_show);
				convertView.setTag(holder);
			}
			holder = (RepairRepairingViewHolder) convertView.getTag();
			switch (application.getSkinType()) {
			case 1:
				convertView.setBackgroundResource(R.drawable.spring_horse_grid_repair_order);
				break;

			default:
				int residue = position % 6;
				switch (residue) {
				case 0:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_one);
					break;
				case 1:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_two);
					break;
				case 2:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_three);
					break;
				case 3:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_four);
					break;
				case 4:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_five);
					break;
				case 5:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_six);
					break;
				}
				break;
			}
			RepairOrderBean repairOrderBean = repairList.get(position);
			holder.assetAndTroubleView.getPaint().setFakeBoldText(true);
			holder.assetAndTroubleView.setText(getString(R.string.format_repair_order_asset_and_trouble, repairOrderBean.getAssetName(),repairOrderBean.getTroubleName()));
			if(application.hasPermission(getString(R.string.roles_permissions_function_applicant_repair_all))){
				holder.repairmanView.setText(repairOrderBean.getRepairmanName());
				holder.acceptTimeView.setText(repairOrderBean.getAcceptTime());
				holder.studentRoleView.setVisibility(ViewGroup.VISIBLE);
				holder.repairmanRoleView.setVisibility(ViewGroup.GONE);

			}
			else{
				holder.startTimeView.setText(repairOrderBean.getStartTime());
				holder.placeView.setText(repairOrderBean.getRoomName());
				holder.repairmanRoleView.setVisibility(ViewGroup.VISIBLE);
				holder.studentRoleView.setVisibility(ViewGroup.GONE);

			}
			//图片数量决定显示与否
			if(repairOrderBean.getPhotoNumber() != null && repairOrderBean.getPhotoNumber().intValue() >0){
				holder.hasPhotoView.setVisibility(ViewGroup.VISIBLE);
			}
			else{
				holder.hasPhotoView.setVisibility(ViewGroup.GONE);
			}
			//反馈数量决定显示与否
			if(repairOrderBean.getFeedbackCount() != null && repairOrderBean.getFeedbackCount().intValue() >0){
				holder.feedbackNumberView.setVisibility(ViewGroup.VISIBLE);
				holder.feedbackNumberView.setText(repairOrderBean.getFeedbackCount().toString());
			}
			else{
				holder.feedbackNumberView.setVisibility(ViewGroup.GONE);
			}
			if(repairOrderBean.getStatus() == null){
				holder.statusIconView.setImageDrawable(null);
				ToastUtil.showToast(mContext, "部分报修单的状态异常，无法正确显示",nullToastKey);
			}
			else if(repairOrderBean.getStatus().equals("-1")){
				//返修
				holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_redo);
			}
			else if(repairOrderBean.getStatus().equals("6")){
				//返修完成
				holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_redo_finish);
			}
			else if(repairOrderBean.getStatus().equals("8")){
				//已转发
				holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_has_forwarded);
			}
			else if(repairOrderBean.getStatus().equals("3")){
				//已维修
				holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_has_repaired);
			}
			else{
				holder.statusIconView.setImageDrawable(null);
			}

			return convertView;
		}

	}

	static class RepairFinishViewHolder{
		TextView assetAndTroubleView;//标题（报修大项和小项）
		ImageView statusIconView;//转发、返修、维修的标签图
		TextView repairmanView;//维修人员
		TextView finishTimeView;//受理时间
		TextView feedbackNumberView;//反馈数
		ImageView hasPhotoView;//是否有图片
	}


	private class RepairOrderFinishBaseAdapter extends BaseAdapter{
		private List<RepairOrderBean> repairList;
		public RepairOrderFinishBaseAdapter(List<RepairOrderBean> repairList) {
			super();
			this.repairList = repairList;
		}

		@Override
		public int getCount() {
			return repairList.size();
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
			RepairFinishViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_FINISH), null);
				holder = new RepairFinishViewHolder();
				holder.assetAndTroubleView = (TextView) convertView.findViewById(R.id.repair_order_item_asset_and_trouble);
				holder.statusIconView = (ImageView) convertView.findViewById(R.id.repair_order_item_status_icon);
				holder.repairmanView = (TextView) convertView.findViewById(R.id.repair_order_item_repairman_name);
				holder.finishTimeView = (TextView) convertView.findViewById(R.id.repair_order_item_finish_time);
				holder.hasPhotoView = (ImageView) convertView.findViewById(R.id.repair_order_item_has_photo);
				holder.feedbackNumberView = (TextView) convertView.findViewById(R.id.repair_order_item_feedback_number);
				convertView.setTag(holder);
			}
			holder = (RepairFinishViewHolder) convertView.getTag();
			switch (application.getSkinType()) {
			case 1:
				convertView.setBackgroundResource(R.drawable.spring_horse_grid_repair_order);
				break;

			default:
				int residue = position % 6;
				switch (residue) {
				case 0:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_one);
					break;
				case 1:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_two);
					break;
				case 2:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_three);
					break;
				case 3:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_four);
					break;
				case 4:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_five);
					break;
				case 5:
					convertView.setBackgroundResource(R.drawable.grid_repair_order_bg_six);
					break;
				}
				break;
			}
			RepairOrderBean repairOrderBean = repairList.get(position);
			holder.assetAndTroubleView.getPaint().setFakeBoldText(true);
			holder.assetAndTroubleView.setText(getString(R.string.format_repair_order_asset_and_trouble, repairOrderBean.getAssetName(),repairOrderBean.getTroubleName()));
			holder.repairmanView.setText(repairOrderBean.getRepairmanName());
			holder.finishTimeView.setText(repairOrderBean.getEndTime());
			//图片数量决定显示与否
			if(repairOrderBean.getPhotoNumber() != null && repairOrderBean.getPhotoNumber().intValue() >0){
				holder.hasPhotoView.setVisibility(ViewGroup.VISIBLE);
			}
			else{
				holder.hasPhotoView.setVisibility(ViewGroup.GONE);
			}
			//反馈数量决定显示与否
			if(repairOrderBean.getFeedbackCount() != null && repairOrderBean.getFeedbackCount().intValue() >0){
				holder.feedbackNumberView.setVisibility(ViewGroup.VISIBLE);
				holder.feedbackNumberView.setText(repairOrderBean.getFeedbackCount().toString());
			}
			else{
				holder.feedbackNumberView.setVisibility(ViewGroup.GONE);
			}
			if(repairOrderBean.getStatus() == null){
				holder.statusIconView.setImageDrawable(null);
				ToastUtil.showToast(mContext, "部分报修单的状态异常，无法正确显示",nullToastKey);
			}
			else if(repairOrderBean.getStatus().equals("-1")){
				//返修
				holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_redo);
			}
			else if(repairOrderBean.getStatus().equals("6")){
				//返修完成
				holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_redo_finish);
			}
			else if(repairOrderBean.getStatus().equals("8")){
				//已转发
				holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_has_forwarded);
			}
			else if(repairOrderBean.getStatus().equals("3")){
				//已维修
				holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_has_repaired);
			}
			else{
				holder.statusIconView.setImageDrawable(null);
			}

			return convertView;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.i("requestCode:" +requestCode +",resultCode:"+resultCode);
		if (resultCode == Activity.RESULT_OK ) {//正确返回
			String callFunctionName = data.getStringExtra(getString(R.string.call_fragment_key_name));
			int callCode = data.getIntExtra(getString(R.string.call_fragment_key_code),0);
			CallFragmentListener callFragmentListener = callFragmentListenerMap.get(callFunctionName);
			if(callFragmentListener != null){
				callFragmentListener.onCall(callCode);
			}
			else{
				Logger.i("意外的call fragment");
			}
		}
		else if(resultCode == Activity.RESULT_CANCELED ){//取消返回

		}
		else{
			Logger.i("其他返回code");
		}
	}
	
	
	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			notAcceptSwitcher.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			notAcceptSwitcher.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			repairingSwitcher.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			repairingSwitcher.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			finishSwitcher.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			finishSwitcher.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			View pageRepairSwitcherLayout = findViewById(R.id.page_repair_switcher_layout);
			if(pageRepairSwitcherLayout != null){
				pageRepairSwitcherLayout.setBackgroundResource(R.drawable.spring_horse_modular_repair_page_switchers_bg);
			}
			
			//
			Drawable emptyTopDraw = getResources().getDrawable(R.drawable.spring_horse_empty_icon_repair_order);
			View notAcceptEmptyView = notAcceptPagerContentView.findViewById(R.id.grid_empty);
			TextView notAcceptTipView = (TextView) notAcceptEmptyView.findViewById(R.id.empty_tip);
			TextView notAcceptClickTipView = (TextView) notAcceptEmptyView.findViewById(R.id.empty_click_tip);
			notAcceptTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			notAcceptClickTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			notAcceptTipView.setCompoundDrawablesWithIntrinsicBounds(null, emptyTopDraw, null, null);
			ImageView notAcceptFooterView = (ImageView) notAcceptPagerContentView.findViewById(R.id.footer_no_more_icon);
			notAcceptFooterView.setImageResource(R.drawable.spring_horse_icon_logo_mask);
			TextView notAcceptFooterTipView = (TextView) notAcceptPagerContentView.findViewById(R.id.footer_no_more_tip);
			notAcceptFooterTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			
			View repairingEmptyView = repairingPagerContentView.findViewById(R.id.grid_empty);
			TextView repairingTipView = (TextView) repairingEmptyView.findViewById(R.id.empty_tip);
			TextView repairingClickTipView = (TextView) repairingEmptyView.findViewById(R.id.empty_click_tip);
			repairingTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			repairingClickTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			repairingTipView.setCompoundDrawablesWithIntrinsicBounds(null, emptyTopDraw, null, null);
			ImageView repairingFooterView = (ImageView) repairingPagerContentView.findViewById(R.id.footer_no_more_icon);
			repairingFooterView.setImageResource(R.drawable.spring_horse_icon_logo_mask);
			TextView repairingFooterTipView = (TextView) repairingPagerContentView.findViewById(R.id.footer_no_more_tip);
			repairingFooterTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			
			View finishEmptyView = finnishPagerContentView.findViewById(R.id.grid_empty);
			TextView finishTipView = (TextView) finishEmptyView.findViewById(R.id.empty_tip);
			TextView finishClickTipView = (TextView) finishEmptyView.findViewById(R.id.empty_click_tip);
			finishTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			finishClickTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			finishTipView.setCompoundDrawablesWithIntrinsicBounds(null, emptyTopDraw, null, null);
			ImageView finishFooterView = (ImageView) finnishPagerContentView.findViewById(R.id.footer_no_more_icon);
			finishFooterView.setImageResource(R.drawable.spring_horse_icon_logo_mask);
			TextView finishFooterTipView = (TextView) finnishPagerContentView.findViewById(R.id.footer_no_more_tip);
			finishFooterTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
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
			case VIEW_CODE_NOT_ACCEPT:
				return R.layout.spring_horse_list_item_grid_repair_order_not_accept;
			case VIEW_CODE_REPAIRING:
				return R.layout.spring_horse_list_item_grid_repair_order_repairing;
			case VIEW_CODE_FINISH:
				return R.layout.spring_horse_list_item_grid_repair_order_finish;
			default:
				break;
			}
			break;

		default:
			switch (viewCode) {
			case VIEW_CODE_NOT_ACCEPT:
				return R.layout.list_item_grid_repair_order_not_accept;
			case VIEW_CODE_REPAIRING:
				return R.layout.list_item_grid_repair_order_repairing;
			case VIEW_CODE_FINISH:
				return R.layout.list_item_grid_repair_order_finish;
			default:
				break;
			}
			break;
		}
		return 0;
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_employment_repair_group_list;

		default:
			return R.layout.activity_employment_repair_group_list;
		}
	}
}
