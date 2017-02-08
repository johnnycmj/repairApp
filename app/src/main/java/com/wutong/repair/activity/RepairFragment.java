package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repair.data.bean.RepairOrderBean;
import com.wutong.repair.data.bean.RepairOrderPagerBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.MobileInfoUtil;
import com.wutong.repair.util.TimeUtil;
import com.wutong.repairfjnu.R;

public class RepairFragment extends BaseFragment {

	private final static String RUNTIME_PERMISSION_KEY = "REPAIR_FRAGMENT";
	public final static int CALL_CODE_NOT_ACCEPT_REFRESH = 0;
	public final static int CALL_CODE_REPAIRING_REFRESH = 1;
	public final static int CALL_CODE_FINISH_REFRESH = 2;
	public final static int CALL_CODE_REPAIRING_AND_FINISH_REFRESH = 3;
	public final static int CALL_CODE_NOT_ACCEPT_AND_REPAIRING_REFRESH = 4;

	public final static int CALL_CODE_REPAIRING_AND_FINISH_REFRESH_FORWARD_REPAIRING = 5;
	public final static int CALL_CODE_REPAIRING_AND_FINISH_REFRESH_FORWARD_FINISH = 6;
	private ImageView titlebarAddView;
	private TextView titlebarTitleView;
	private ImageView titlebarFilterView;
	private ImageView titlebarBackView;

	private String[] filterTypeArray = new String[]{"0","1","2","3"};
	private int currentTitleIndex = 0;
	private String[] repairTitleItems;
	private final static int PAGE_REPAIR_SWITCHER_NOT_ACCEPT = 0;
	private final static int PAGE_REPAIR_SWITCHER_REPAIRING = 1;
	private final static int PAGE_REPAIR_SWITCHER_FINISH = 2;

	private final static int PAGE_REPAIR_FILTER_SWITCHER_ALL = 0;
	private final static int PAGE_REPAIR_FILTER_SWITCHER_DORM = 1;
	private final static int PAGE_REPAIR_FILTER_SWITCHER_INNER = 2;
	private final static int PAGE_REPAIR_FILTER_SWITCHER_OUTTER = 3;

	private ViewPager pageRepairViewPager;
	private PageRepairPagerAdapter pageRepairPagerAdapter;

	private View notAcceptPagerContentView;
	private View repairingPagerContentView;
	private View finnishPagerContentView;

	private TextView notAcceptSwitcher;
	private TextView repairingSwitcher;
	private TextView finishSwitcher;


	private PullToRefreshView repairOrderNotAccetpSingleView;

	private PullToRefreshView notAcceptPullToRefreshView;
	private GridView notAcceptGridView;
	private RepairOrderNotAcceptBaseAdapter repairNotAcceptBaseAdapter;

	private PullToRefreshView repairingPullToRefreshView;
	private GridView repairingGridView;
	private RepairOrderRepairingBaseAdapter repairRepairingBaseAdapter;

	private PullToRefreshView finishPullToRefreshView;
	private GridView finishGridView;
	private RepairOrderFinishBaseAdapter repairFinishBaseAdapter;

	private Dialog filterDialog;

	private TextView filterAllView;
	private TextView filterDormView;
	private TextView filterInnerView;
	private TextView filterOutterView;

	private List<RepairOrderBean> notAcceptList;//未受理
	private List<RepairOrderBean> repairingList;//维修中
	private List<RepairOrderBean> finishList;//已完成
	private boolean startInit;
	private boolean ingInit;
	private boolean endInit;
	private int currentRepairSubPagePosition;

	private int repairCurrentStart = 0;
	private String nullToastKey ;

	

	private boolean outIsLargeCharacter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_repair);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.vp_av_index_tab_repair, container,false);
		setFragmentView(fragmentView);
		//
		nullToastKey = UUID.randomUUID().toString();
		argumentsInit();
		titleBarInit();
		viewInit();
		pageReapirSetupData();
		callInit();
		return fragmentView;
	}

	private void argumentsInit(){
		Bundle arguments = getArguments();
		if(arguments != null){
			outIsLargeCharacter = arguments.getBoolean("isLargeCharacter");	
		}
		Logger.i("outIsLargeCharacter="+outIsLargeCharacter);

	}

	private void titleBarInit(){
		titlebarAddView = (ImageView) findViewById(R.id.titlebar_add);
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarFilterView = (ImageView) findViewById(R.id.titlebar_filter);
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
				RepairFragment.this.getActivity().onBackPressed();

			}
		});

		titlebarFilterView.setVisibility(ViewGroup.VISIBLE);
		titlebarAddView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				intent.putExtra("repairType", currentTitleIndex == 0?"1":String.valueOf(currentTitleIndex));
				intent.putExtra("isFromIndex", true);
				intent.setClass(mContext, RepairOrderActivity.class);
				startActivityForResult(intent,0);


			}
		});
		if(application.hasPermission(getString(R.string.roles_permissions_applicant_repair))){
			titlebarAddView.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			titlebarAddView.setVisibility(ViewGroup.GONE);
		}

		repairTitleItems = getResources().getStringArray(R.array.filter_repair_items);

		titlebarFilterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(filterDialog.isShowing()){
					filterDialog.hide();
				}
				else{
					filterDialog.show();
					Window filterWindow = filterDialog.getWindow();
					LayoutParams lp = filterWindow.getAttributes();
					lp.x = 0;
					lp.y = getResources().getDimensionPixelOffset(R.dimen.common_titlebar_height);
					lp.width = MobileInfoUtil.getDisplayWidth(RepairFragment.this.getActivity());
					filterWindow.setAttributes(lp);
				}

			}
		});
	}

	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_repair_fragment_name), new CallFragmentListener() {

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
					pageRepairSwitcherChange(PAGE_REPAIR_SWITCHER_REPAIRING);
					break;
				case CALL_CODE_REPAIRING_AND_FINISH_REFRESH_FORWARD_REPAIRING:
					loadMoreRepairOrder(true, "2");
					loadMoreRepairOrder(true, "3");
					pageRepairSwitcherChange(PAGE_REPAIR_SWITCHER_REPAIRING);
					break;
				case CALL_CODE_REPAIRING_AND_FINISH_REFRESH_FORWARD_FINISH:
					loadMoreRepairOrder(true, "2");
					loadMoreRepairOrder(true, "3");
					pageRepairSwitcherChange(PAGE_REPAIR_SWITCHER_FINISH);
					break;
				default:
					break;
				}

			}
		});
	}

	private void viewInit(){
		View filterContentView = LayoutInflater.from(mContext).inflate(R.layout.filter_repair, null);
		filterAllView = (TextView) filterContentView.findViewById(R.id.filter_repair_all);
		filterDormView = (TextView) filterContentView.findViewById(R.id.filter_repair_dorm);
		filterInnerView = (TextView) filterContentView.findViewById(R.id.filter_repair_inner);
		filterOutterView = (TextView) filterContentView.findViewById(R.id.filter_repair_outter);
		filterDialog = new Dialog(mContext, R.style.filter_dialog);
		filterDialog.getWindow().setGravity(Gravity.TOP);
		filterDialog.setContentView(filterContentView);
		filterAllView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				filterDialog.dismiss();
				pageRepairFilterSwitchChange(PAGE_REPAIR_FILTER_SWITCHER_ALL);
			}
		});

		filterDormView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				filterDialog.dismiss();
				pageRepairFilterSwitchChange(PAGE_REPAIR_FILTER_SWITCHER_DORM);
			}
		});

		filterInnerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				filterDialog.dismiss();
				pageRepairFilterSwitchChange(PAGE_REPAIR_FILTER_SWITCHER_INNER);
			}
		});

		filterOutterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageRepairFilterSwitchChange(PAGE_REPAIR_FILTER_SWITCHER_OUTTER);
				filterDialog.dismiss();
			}
		});



		notAcceptSwitcher = (TextView) findViewById(R.id.page_repair_switcher_not_accept);
		repairingSwitcher = (TextView) findViewById(R.id.page_repair_switcher_repairing);
		finishSwitcher = (TextView) findViewById(R.id.page_repair_switcher_finish);

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



		pageRepairViewPager = (ViewPager) findViewById(R.id.viewpager_repair_order);
		pageRepairViewPager.setOffscreenPageLimit(3);
		pageRepairViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				pageRepairSwitcherChange(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				Logger.i("onPageScrollStateChanged:" + state);
				if(state == ViewPager.SCROLL_STATE_IDLE){
					switch (currentRepairSubPagePosition) {
					case 0:
						if(!startInit){
							Logger.i("startInit:"+startInit);
							showProgressDialog("获取未受理报修单……");
							loadMoreRepairOrder(true, "1");

						}
						break;
					case 1:
						if(!ingInit){
							Logger.i("ingInit:"+ingInit);
							showProgressDialog("获取维修中报修单……");
							loadMoreRepairOrder(true, "2");
						}
						break;
					case 2:
						if(!endInit){
							Logger.i("endInit:"+endInit);
							showProgressDialog("获取已完成报修单……");
							loadMoreRepairOrder(true, "3");
						}
						break;

					}
				}
			}
		});

		notAcceptPagerContentView = LayoutInflater.from(mContext).inflate(R.layout.vp_page_repair_repair_order_not_accept, null);
		repairingPagerContentView = LayoutInflater.from(mContext).inflate(R.layout.vp_page_repair_repair_order_repairing, null);
		finnishPagerContentView = LayoutInflater.from(mContext).inflate(R.layout.vp_page_repair_repair_order_finish, null);
		pageRepairPagerAdapter = new PageRepairPagerAdapter();
		pageRepairViewPager.setAdapter(pageRepairPagerAdapter);

		//
		notAcceptList = new ArrayList<RepairOrderBean>();
		repairingList = new ArrayList<RepairOrderBean>();
		finishList = new ArrayList<RepairOrderBean>();

		repairOrderNotAccetpSingleView = (PullToRefreshView) notAcceptPagerContentView.findViewById(R.id.repair_order_single);

		notAcceptGridView = (GridView) notAcceptPagerContentView.findViewById(R.id.grid_repair_order);
		repairingGridView = (GridView) repairingPagerContentView.findViewById(R.id.grid_repair_order);
		finishGridView = (GridView) finnishPagerContentView.findViewById(R.id.grid_repair_order);

		//
		notAcceptGridView.setNumColumns(outIsLargeCharacter?1:2);
		repairingGridView.setNumColumns(outIsLargeCharacter?1:2);
		finishGridView.setNumColumns(outIsLargeCharacter?1:2);
		//


		notAcceptPullToRefreshView = (PullToRefreshView) notAcceptPagerContentView.findViewById(R.id.grid_pull_refresh_view);
		repairingPullToRefreshView = (PullToRefreshView) repairingPagerContentView.findViewById(R.id.grid_pull_refresh_view);
		finishPullToRefreshView = (PullToRefreshView) finnishPagerContentView.findViewById(R.id.grid_pull_refresh_view);

		View notAcceptEmptyView = notAcceptPagerContentView.findViewById(R.id.grid_empty);
		TextView notAcceptTipView = (TextView) notAcceptEmptyView.findViewById(R.id.empty_tip);
		notAcceptTipView.setText(getString(R.string.label_empty_repair_order_collection_zero_tip_not_accept));
		notAcceptGridView.setEmptyView(notAcceptEmptyView);
		notAcceptEmptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				showProgressDialog("获取未受理报修单……");
				loadMoreRepairOrder(true, "1");

			}
		});
		View repairingEmptyView = repairingPagerContentView.findViewById(R.id.grid_empty);
		TextView repairingTipView = (TextView) repairingEmptyView.findViewById(R.id.empty_tip);
		repairingTipView.setText(getString(R.string.label_empty_repair_order_collection_zero_tip_repairing));
		repairingGridView.setEmptyView(repairingEmptyView);
		repairingEmptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				showProgressDialog("获取维修中报修单……");
				loadMoreRepairOrder(true, "2");

			}
		});
		View finishEmptyView = finnishPagerContentView.findViewById(R.id.grid_empty);
		TextView finishTipView = (TextView) finishEmptyView.findViewById(R.id.empty_tip);
		finishTipView.setText(getString(R.string.label_empty_repair_order_collection_zero_tip_finish));
		finishGridView.setEmptyView(finishEmptyView);
		finishEmptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				showProgressDialog("获取已完成报修单……");
				loadMoreRepairOrder(true, "3");

			}
		});

		repairOrderNotAccetpSingleView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMoreRepairOrder(true, "1");

			}
		});

		repairOrderNotAccetpSingleView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMoreRepairOrder(false, "1");

			}
		});
		notAcceptPagerContentView.findViewById(R.id.repair_order_single_layout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				singleItemClick();
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
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				RepairOrderBean repairOrderBean = notAcceptList.get(position);
				intent.putExtra("repairOrderId", repairOrderBean.getId());
				intent.putExtra("repairType", repairOrderBean.getFormType());
				intent.setClass(mContext, RepairOrderDetailActivity.class);
				intent.putExtra("isFromIndex", true);
				intent.putExtra("isLargeCharacter", outIsLargeCharacter);
				intent.putExtras(bundle);
				startActivityForResult(intent,0);
			}
		});

		repairingGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View convertView,
					int position, long id) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				RepairOrderBean repairOrderBean = repairingList.get(position);
				intent.putExtra("repairOrderId", repairOrderBean.getId());
				intent.putExtra("repairType", repairOrderBean.getFormType());
				intent.setClass(mContext, RepairOrderDetailActivity.class);
				intent.putExtra("isFromIndex", true);
				intent.putExtra("isLargeCharacter", outIsLargeCharacter);
				intent.putExtras(bundle);
				startActivityForResult(intent,0);
			}
		});

		finishGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View convertView,
					int position, long id) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				RepairOrderBean repairOrderBean = finishList.get(position);
				intent.putExtra("repairOrderId", repairOrderBean.getId());
				intent.putExtra("repairType", repairOrderBean.getFormType());
				intent.setClass(mContext, RepairOrderDetailActivity.class);
				intent.putExtra("isFromIndex", true);
				intent.putExtra("isLargeCharacter", outIsLargeCharacter);
				intent.putExtras(bundle);
				startActivityForResult(intent,0);
			}
		});

		//

	}

	private void singleItemClick(){
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		RepairOrderBean repairOrderBean = notAcceptList.get(0);
		intent.putExtra("repairOrderId", repairOrderBean.getId());
		intent.putExtra("repairType", repairOrderBean.getFormType());
		intent.setClass(mContext, RepairOrderDetailActivity.class);
		intent.putExtra("isFromIndex", true);
		intent.putExtra("isLargeCharacter", outIsLargeCharacter);
		intent.putExtras(bundle);
		startActivityForResult(intent,0);
	}

	private void pageReapirSetupData(){
		pageRepairSwitcherChange(PAGE_REPAIR_SWITCHER_NOT_ACCEPT);
		pageRepairFilterSwitchChange(PAGE_REPAIR_FILTER_SWITCHER_ALL);
	}

	



	private class PageRepairPagerAdapter extends PagerAdapter{

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
			pageRepairViewPager.setCurrentItem(PAGE_REPAIR_SWITCHER_NOT_ACCEPT);
			break;
		case PAGE_REPAIR_SWITCHER_REPAIRING:
			repairingSwitcher.setEnabled(false);
			pageRepairViewPager.setCurrentItem(PAGE_REPAIR_SWITCHER_REPAIRING);
			break;
		case PAGE_REPAIR_SWITCHER_FINISH:
			finishSwitcher.setEnabled(false);
			pageRepairViewPager.setCurrentItem(PAGE_REPAIR_SWITCHER_FINISH);
			break;
		}
	}

	static class RepairNotAcceptViewHolder{
		TextView assetAndTroubleView;//
		TextView assetView;//
		TextView troubleView;//
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
				holder = new RepairNotAcceptViewHolder();
				if(outIsLargeCharacter){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_grid_repair_order_not_accept_large, null);
					holder.assetView = (TextView) convertView.findViewById(R.id.repair_order_item_asset);
					holder.troubleView = (TextView) convertView.findViewById(R.id.repair_order_item_trouble);
				}
				else{
					convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_grid_repair_order_not_accept, null);
					holder.assetAndTroubleView = (TextView) convertView.findViewById(R.id.repair_order_item_asset_and_trouble);
				}

				holder.placeView = (TextView) convertView.findViewById(R.id.repair_order_item_place);
				holder.startTimeView = (TextView) convertView.findViewById(R.id.repair_order_item_start_time);
				holder.hasPhotoView = (ImageView) convertView.findViewById(R.id.repair_order_item_has_photo);
				holder.feedbackNumberView = (TextView) convertView.findViewById(R.id.repair_order_item_feedback_number);
				convertView.setTag(holder);
			}
			holder = (RepairNotAcceptViewHolder) convertView.getTag();
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
			RepairOrderBean repairOrderBean = repairList.get(position);
			if(outIsLargeCharacter){
				holder.assetView.getPaint().setFakeBoldText(true);
				holder.troubleView.getPaint().setFakeBoldText(true);
				holder.assetView.setText(repairOrderBean.getAssetName());
				holder.troubleView.setText(getString(R.string.format_repair_order_trouble,repairOrderBean.getTroubleName()));
			}
			else{
				holder.assetAndTroubleView.getPaint().setFakeBoldText(true);
				holder.assetAndTroubleView.setText(getString(R.string.format_repair_order_asset_and_trouble, repairOrderBean.getAssetName(),repairOrderBean.getTroubleName()));
			}
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
		TextView assetView;//
		TextView troubleView;//
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
				holder = new RepairRepairingViewHolder();
				if(outIsLargeCharacter){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_grid_repair_order_repairing_large, null);
					holder.assetView = (TextView) convertView.findViewById(R.id.repair_order_item_asset);
					holder.troubleView = (TextView) convertView.findViewById(R.id.repair_order_item_trouble);
				}
				else{
					convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_grid_repair_order_repairing, null);
					holder.assetAndTroubleView = (TextView) convertView.findViewById(R.id.repair_order_item_asset_and_trouble);
				}
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

			RepairOrderBean repairOrderBean = repairList.get(position);
			if(outIsLargeCharacter){
				holder.assetView.getPaint().setFakeBoldText(true);
				holder.troubleView.getPaint().setFakeBoldText(true);
				holder.assetView.setText(repairOrderBean.getAssetName());
				holder.troubleView.setText(getString(R.string.format_repair_order_trouble,repairOrderBean.getTroubleName()));
			}
			else{
				holder.assetAndTroubleView.getPaint().setFakeBoldText(true);
				holder.assetAndTroubleView.setText(getString(R.string.format_repair_order_asset_and_trouble, repairOrderBean.getAssetName(),repairOrderBean.getTroubleName()));
			}
			if(application.hasPermission(getString(R.string.roles_permissions_function_applicant_repair_all))){
				holder.repairmanView.setText(repairOrderBean.getRepairmanName());
				holder.acceptTimeView.setText(repairOrderBean.getAcceptTime());
				holder.studentRoleView.setVisibility(ViewGroup.VISIBLE);
				holder.repairmanRoleView.setVisibility(ViewGroup.GONE);

			}
			else if(application.hasPermission(getString(R.string.roles_permissions_function_repairman_repair_all))){
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
			if(outIsLargeCharacter){
				if(repairOrderBean.getStatus() == null){
					holder.statusIconView.setImageDrawable(null);
					ToastUtil.showToast(mContext, "部分报修单的状态异常，无法正确显示",nullToastKey);
				}
				else if(repairOrderBean.getStatus().equals("-1")){
					//返修
					holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_redo_large);
				}
				else if(repairOrderBean.getStatus().equals("6")){
					//返修完成
					holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_redo_finish_large);
				}
				else if(repairOrderBean.getStatus().equals("-8")){
					//已转发
					holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_has_forwarded_large);
				}
				else if(repairOrderBean.getStatus().equals("3")){
					//已维修
					holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_has_repaired_large);
				}
				else{
					holder.statusIconView.setImageDrawable(null);
				}
			}
			else{
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
				else if(repairOrderBean.getStatus().equals("-8")){
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
			}

			return convertView;
		}

	}

	static class RepairFinishViewHolder{
		TextView assetAndTroubleView;//标题（报修大项和小项）
		TextView assetView;//
		TextView troubleView;//
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
				holder = new RepairFinishViewHolder();
				if(outIsLargeCharacter){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_grid_repair_order_finish_large, null);
					holder.assetView = (TextView) convertView.findViewById(R.id.repair_order_item_asset);
					holder.troubleView = (TextView) convertView.findViewById(R.id.repair_order_item_trouble);
				}
				else{
					convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_grid_repair_order_finish, null);
					holder.assetAndTroubleView = (TextView) convertView.findViewById(R.id.repair_order_item_asset_and_trouble);
				}
				holder.statusIconView = (ImageView) convertView.findViewById(R.id.repair_order_item_status_icon);
				holder.repairmanView = (TextView) convertView.findViewById(R.id.repair_order_item_repairman_name);
				holder.finishTimeView = (TextView) convertView.findViewById(R.id.repair_order_item_finish_time);
				holder.hasPhotoView = (ImageView) convertView.findViewById(R.id.repair_order_item_has_photo);
				holder.feedbackNumberView = (TextView) convertView.findViewById(R.id.repair_order_item_feedback_number);
				convertView.setTag(holder);
			}
			holder = (RepairFinishViewHolder) convertView.getTag();
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
			RepairOrderBean repairOrderBean = repairList.get(position);
			if(outIsLargeCharacter){
				holder.assetView.getPaint().setFakeBoldText(true);
				holder.troubleView.getPaint().setFakeBoldText(true);
				holder.assetView.setText(repairOrderBean.getAssetName());
				holder.troubleView.setText(getString(R.string.format_repair_order_trouble,repairOrderBean.getTroubleName()));
			}
			else{
				holder.assetAndTroubleView.getPaint().setFakeBoldText(true);
				holder.assetAndTroubleView.setText(getString(R.string.format_repair_order_asset_and_trouble, repairOrderBean.getAssetName(),repairOrderBean.getTroubleName()));
			}
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

			if(outIsLargeCharacter){
				if(repairOrderBean.getStatus() == null){
					holder.statusIconView.setImageDrawable(null);
					ToastUtil.showToast(mContext, "部分报修单的状态异常，无法正确显示",nullToastKey);
				}
				else if(repairOrderBean.getStatus().equals("-1")){
					//返修
					holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_redo_large);
				}
				else if(repairOrderBean.getStatus().equals("6")){
					//返修完成
					holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_redo_finish_large);
				}
				else if(repairOrderBean.getStatus().equals("-8")){
					//已转发
					holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_has_forwarded_large);
				}
				else if(repairOrderBean.getStatus().equals("3")){
					//已维修
					holder.statusIconView.setImageResource(R.drawable.status_icon_repairing_has_repaired_large);
				}
				else{
					holder.statusIconView.setImageDrawable(null);
				}
			}
			else{
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
				else if(repairOrderBean.getStatus().equals("-8")){
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
			}

			return convertView;
		}

	}


	/**
	 * 获取更多或刷新报修单
	 * @param isRefresh 是否刷新
	 * @param groupCode 分组code，未受理1、维修中2、已完成3
	 */
	private void loadMoreRepairOrder(boolean isRefresh, String groupCode){
		if(isRefresh){
			repairCurrentStart = 0;
		}
		else{
			repairCurrentStart += application.getPagingSize();
		}
		RepairOrderPagerBean repairOrderPagerBean = new RepairOrderPagerBean("",filterTypeArray[currentTitleIndex],groupCode,repairCurrentStart,application.getPagingSize());
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
		if(application.hasPermission(getString(R.string.roles_permissions_function_applicant_repair_all))){
			String dormRoomId = application.getLoginInfoBean().getDormRoomId();
			switch (Integer.valueOf(repairOrderPagerBean.getFormType())) {
			case 0:
				if(dormRoomId.toString().trim().length()== 0){
					ToastUtil.showToast(mContext, "没有宿舍信息，无法获取报修单");
					hideProgressDialog();
					return ;
				}
				repairOrderPagerBean.setId(dormRoomId);
				httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderStudentAllLoadUrl());
				break;
			case 1:
				if(dormRoomId.toString().trim().length()== 0){
					ToastUtil.showToast(mContext, "没有宿舍信息，无法获取报修单");
					hideProgressDialog();
					return ;
				}
				repairOrderPagerBean.setId(dormRoomId);
				httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderStudentDormLoadUrl());
				break;
			case 2:
				repairOrderPagerBean.setId(application.getLoginInfoBean().getUserId().toString());
				httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderStudentPublicInnerLoadUrl());
				break;	
			case 3:
				repairOrderPagerBean.setId(application.getLoginInfoBean().getUserId().toString());
				httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderStudentPublicOutterLoadUrl());
				break;
			default:
				ToastUtil.showToast(mContext, "获取报修单列表出现意外的情况");
				hideProgressDialog();
				return;
			}

		}
		else if(application.hasPermission(getString(R.string.roles_permissions_function_repairman_repair_all))){
			repairOrderPagerBean.setId(application.getLoginInfoBean().getUserId().toString());
			switch (Integer.valueOf(repairOrderPagerBean.getFormType())){
			case 0:
				httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderRepairmanAllLoadUrl());
				break;
			case 1:
				httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderRepairmanDormAndPublicInnerLoadUrl());
				break;
			case 2:
				httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderRepairmanDormAndPublicInnerLoadUrl());
				break;
			case 3:
				httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderRepairmanPublicOutterLoadUrl());
				break;
			default:
				hideProgressDialog();
				ToastUtil.showToast(mContext, "获取报修单列表出现意外的情况");
				return;
			}
		}
		else{
			hideProgressDialog();
			return;
		}
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair hotelRoomIdPair = new BasicNameValuePair("hotelRoomId", repairOrderPagerBean.getId());
		params.add(hotelRoomIdPair);
		NameValuePair roomIdPair = new BasicNameValuePair("roomId", repairOrderPagerBean.getId());
		params.add(roomIdPair);
		NameValuePair userIdPair = new BasicNameValuePair("userId", application.getLoginInfoBean().getUserId().toString());
		params.add(userIdPair);
		NameValuePair formTypePair = new BasicNameValuePair("formType", repairOrderPagerBean.getFormType());
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
				if(!RepairFragment.this.isAdded() ||! RepairFragment.this.isVisible()){
					return;
				}
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
					//未受理

					if(isRefresh){
						notAcceptList.clear();
						notAcceptList.addAll(resultList);
						repairNotAcceptBaseAdapter.notifyDataSetChanged();
						notAcceptPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
						repairOrderNotAccetpSingleView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						notAcceptList.addAll(resultList);
						repairNotAcceptBaseAdapter.notifyDataSetChanged();
						notAcceptPullToRefreshView.onFooterRefreshComplete();
						repairOrderNotAccetpSingleView.onFooterRefreshComplete();
					}

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
				hideProgressDialog();
				if(!RepairFragment.this.isAdded() ||! RepairFragment.this.isVisible()){
					return;
				}
				if(groupCode.equals("1")){
					if(isRefresh){
						notAcceptPullToRefreshView.onHeaderRefreshComplete();
					}
					else{
						notAcceptPullToRefreshView.onFooterRefreshComplete();
					}
				}
				else if(groupCode.equals("2")){
					if(isRefresh){
						repairingPullToRefreshView.onHeaderRefreshComplete();
					}
					else{
						repairingPullToRefreshView.onFooterRefreshComplete();
					}
				}
				else if(groupCode.equals("3")){
					if(isRefresh){
						finishPullToRefreshView.onHeaderRefreshComplete();
					}
					else{
						finishPullToRefreshView.onFooterRefreshComplete();
					}
				}
				else {

				}
				HttpResponseUtil.justToast(exception, mContext);

			}

			@Override
			public void beforeDealt() {


			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	/**
	 * 筛选切换
	 */
	private void pageRepairFilterSwitchChange(int switchIndex){
		filterAllView.setEnabled(true);
		filterDormView.setEnabled(true);
		filterInnerView.setEnabled(true);
		filterOutterView.setEnabled(true);
		currentTitleIndex = switchIndex;
		switch (switchIndex) {
		case PAGE_REPAIR_FILTER_SWITCHER_ALL:
			filterAllView.setEnabled(false);
			break;
		case PAGE_REPAIR_FILTER_SWITCHER_DORM:
			filterDormView.setEnabled(false);
			break;
		case PAGE_REPAIR_FILTER_SWITCHER_INNER:
			filterInnerView.setEnabled(false);
			break;
		case PAGE_REPAIR_FILTER_SWITCHER_OUTTER:
			filterOutterView.setEnabled(false);
			break;
		}
		titlebarTitleView.setText(repairTitleItems[currentTitleIndex]);
		showProgressDialog("获取报修单……");
		startInit = false;
		ingInit = false;
		endInit = false;

		//为了让显示的数量变化，三个都刷新
		loadMoreRepairOrder(true,"1");
		loadMoreRepairOrder(true,"2");
		loadMoreRepairOrder(true,"3");
	}
}
