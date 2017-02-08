package com.wutong.repair.fragment;

import java.lang.reflect.Field;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repair.activity.OfficeMaterialApplyAddActivity;

/**
 * 行政申领模块入口
 * @author Jolly
 * 创建时间：2014年2月24日下午3:01:24
 *
 */
public class OfficeMaterialApplyFragment extends BaseFragment {
	public final static int CALL_CODE_APPLYING_REFRESH = 1;
	public final static int CALL_CODE_APPLYING_AND_CANCEL_REFRESH = 2;
	public final static int CALL_CODE_QUERY_AND_STOCK_OUT_REFRESH = 3;


	private final static int INDEX_APPLY = 0;//申请中
	private final static int INDEX_STOCK_OUT = 1;//已出库
	private final static int INDEX_QUERY = 2;//已确认
	private final static int INDEX_CANCEL = 3;//已取消

	private ImageView titlebarAddView;
	private TextView titlebarTitleView;
	private ImageView titlebarBackView;


	private TextView mSwitcherApplyingTv;
	private TextView mSwitcherStockOutTv;
	private TextView mSwitcherQueryTv;
	private TextView mSwitcherCancelTv;

	private ViewPager applyByStatusVp;

	private OfficeMaterialApplyListFragment applyingSubFragment;//申请中
	private OfficeMaterialApplyListFragment stockOutSubFragment;//已出库
	private OfficeMaterialApplyListFragment querySubFragment;//已确认
	private OfficeMaterialApplyListFragment cancelSubFragment;//已撤销

	private String outDivisionId;
	private boolean outHasAdd;
	private int outDetailPermissionType;
	private String outDivisionName;
	private String title;

	private StatusOfficeMaterialApplyFragmentPagerAdapter mStatusOfficeMaterialApplyFragmentPagerAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_office_material_apply, container,false);
		setFragmentView(fragmentView);
		setFragmentPageName(mContext, R.string.title_fragment_office_material_apply);
		//

		argumentInit();
		titleBarInit();
		viewInit();
		setupData();
		callInit();
		return fragmentView;
	}

	private void argumentInit(){
		Bundle argument = getArguments();
		if(argument != null){
			outDivisionId = argument.getString("divisionId");
			outHasAdd = argument.getBoolean("hasAdd");
			outDetailPermissionType = argument.getInt("detailPermissionType");
			outDivisionName = argument.getString("divisionName");
		}
		if(outDivisionId == null){
			//Fix分支
				outDivisionId = application.getLoginInfoBean().getDivisionId();
			title = getString(R.string.title_fragment_office_material_apply);
		}
		else{
			//外部传入的outDivisionId
			title = outDivisionName + "的申领单";
		}
	}

	private void titleBarInit(){
		titlebarAddView = (ImageView) findViewById(R.id.titlebar_add);
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(title);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		
		boolean isTitlebarBackShow = this.getActivity().getIntent().getBooleanExtra("titlebar_back_is_show", false);
		if(isTitlebarBackShow){
			titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			titlebarBackView.setVisibility(ViewGroup.GONE);
		}
		if(outHasAdd){
			titlebarAddView.setVisibility(ViewGroup.GONE);
		}
		else{
			titlebarAddView.setVisibility(ViewGroup.VISIBLE);
		}
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OfficeMaterialApplyFragment.this.getActivity().onBackPressed();

			}
		});

		titlebarAddView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, OfficeMaterialApplyAddActivity.class);
				OfficeMaterialApplyFragment.this.getActivity().startActivityForResult(intent,0);


			}
		});
	}

	private void viewInit(){
		applyByStatusVp = (ViewPager) findViewById(R.id.viewpager_office_material_apply_list);
		applyByStatusVp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				pageSwitch(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		mStatusOfficeMaterialApplyFragmentPagerAdapter = new StatusOfficeMaterialApplyFragmentPagerAdapter(getChildFragmentManager());
		applyByStatusVp.setAdapter(mStatusOfficeMaterialApplyFragmentPagerAdapter);
		mSwitcherApplyingTv = (TextView) findViewById(R.id.page_office_material_apply_switcher_applying);
		mSwitcherStockOutTv = (TextView) findViewById(R.id.page_office_material_apply_switcher_out_from_warehouse);
		mSwitcherQueryTv = (TextView) findViewById(R.id.page_office_material_apply_switcher_has_query);
		mSwitcherCancelTv = (TextView) findViewById(R.id.page_office_material_apply_switcher_has_cancel);

		mSwitcherApplyingTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageSwitch(INDEX_APPLY);
			}
		});
		mSwitcherStockOutTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageSwitch(INDEX_STOCK_OUT);
			}
		});
		mSwitcherQueryTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageSwitch(INDEX_QUERY);
			}
		});
		mSwitcherCancelTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageSwitch(INDEX_CANCEL);
			}
		});
	}

	private void setupData(){

	}

	private void pageSwitch(int position){
		applyByStatusVp.setCurrentItem(position);
		mSwitcherApplyingTv.setEnabled(true);
		mSwitcherStockOutTv.setEnabled(true);
		mSwitcherQueryTv.setEnabled(true);
		mSwitcherCancelTv.setEnabled(true);
		switch (position) {
		case INDEX_APPLY:
			mSwitcherApplyingTv.setEnabled(false);
			break;
		case INDEX_STOCK_OUT:
			mSwitcherStockOutTv.setEnabled(false);
			break;
		case INDEX_QUERY:
			mSwitcherQueryTv.setEnabled(false);
			break;
		case INDEX_CANCEL:
			mSwitcherCancelTv.setEnabled(false);
			break;
		default:

			break;
		}
	}

	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_office_material_apply_name), new CallFragmentListener() {

			@Override
			public void onCall(int callCode) {
				switch (callCode) {
				case CALL_CODE_APPLYING_REFRESH://申请
					if(applyingSubFragment != null && applyingSubFragment.isAdded()){
						applyingSubFragment.refreshForStatusChange();
					}
					break;
				case CALL_CODE_APPLYING_AND_CANCEL_REFRESH://撤销
					if(applyingSubFragment != null && applyingSubFragment.isAdded()){
						applyingSubFragment.refreshForStatusChange();
					}
					if(cancelSubFragment != null && cancelSubFragment.isAdded()){
						cancelSubFragment.refreshForStatusChange();
					}
					break;
				case CALL_CODE_QUERY_AND_STOCK_OUT_REFRESH://确认
					if(querySubFragment != null && querySubFragment.isAdded()){
						querySubFragment.refreshForStatusChange();
					}
					if(stockOutSubFragment != null && stockOutSubFragment.isAdded()){
						stockOutSubFragment.refreshForStatusChange();
					}
					break;
				default:
					break;
				}

			}
		});
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}

	private class StatusOfficeMaterialApplyFragmentPagerAdapter extends FragmentPagerAdapter{
		public StatusOfficeMaterialApplyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case INDEX_APPLY:
				if(applyingSubFragment == null){
					applyingSubFragment = new OfficeMaterialApplyListFragment();
					Bundle data = new Bundle();
					data.putString("status", "1");
					data.putString("divisionId", outDivisionId);
					data.putInt("detailPermissionType", outDetailPermissionType);
					data.putString("divisionName", outDivisionName);
					applyingSubFragment.setArguments(data);
				}
				fragment = applyingSubFragment;
				break;
			case INDEX_STOCK_OUT:
				if(stockOutSubFragment == null){
					stockOutSubFragment = new OfficeMaterialApplyListFragment();
					Bundle data = new Bundle();
					data.putString("status", "2");
					data.putString("divisionId", outDivisionId);
					data.putInt("detailPermissionType", outDetailPermissionType);
					data.putString("divisionName", outDivisionName);
					stockOutSubFragment.setArguments(data);
				}
				fragment = stockOutSubFragment;
				break;
			case INDEX_QUERY:
				if(querySubFragment == null){
					querySubFragment = new OfficeMaterialApplyListFragment();
					Bundle data = new Bundle();
					data.putString("status", "3");
					data.putString("divisionId", outDivisionId);
					data.putInt("detailPermissionType", outDetailPermissionType);
					data.putString("divisionName", outDivisionName);
					querySubFragment.setArguments(data);
				}
				fragment = querySubFragment;
				break;
			case INDEX_CANCEL:
				if(cancelSubFragment == null){
					cancelSubFragment = new OfficeMaterialApplyListFragment();
					Bundle data = new Bundle();
					data.putString("status", "4");
					data.putString("divisionId", outDivisionId);
					data.putInt("detailPermissionType", outDetailPermissionType);
					data.putString("divisionName", outDivisionName);
					cancelSubFragment.setArguments(data);
				}
				fragment = cancelSubFragment;
				break;
			default:

				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 4;
		}
	}
	
	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			mSwitcherApplyingTv.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			mSwitcherApplyingTv.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			mSwitcherStockOutTv.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			mSwitcherStockOutTv.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			mSwitcherQueryTv.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			mSwitcherQueryTv.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			mSwitcherCancelTv.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			mSwitcherCancelTv.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			View pageRepairSwitcherLayout = findViewById(R.id.page_repair_switcher_layout);
			if(pageRepairSwitcherLayout != null){
				pageRepairSwitcherLayout.setBackgroundResource(R.drawable.spring_horse_modular_repair_page_switchers_bg);
			}
			break;

		default:
			break;
		}
	}
}
