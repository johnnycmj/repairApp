package com.wutong.repair.fragment;

import java.lang.reflect.Field;

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

/**
 * 申领管理模块入口
 * @author Jolly
 * 创建时间：2014年2月25日上午11:11:23
 *
 */
public class OfficeMaterialStatisticFragment extends BaseFragment {

	public final static int CALL_CODE_APPLYING_REFRESH = 1;
	public final static int CALL_CODE_APPLYING_AND_CANCEL_REFRESH = 2;
	public final static int CALL_CODE_QUERY_AND_STOCK_OUT_REFRESH = 3;


	private final static int INDEX_APPLY = 0;//申请
	private final static int INDEX_MATERIAL = 1;//用品
	private final static int INDEX_DIVISION = 2;//科室

	private TextView titlebarTitleView;
	private ImageView titlebarBackView;


	private TextView mSwitcherApplyTv;
	private TextView mSwitcherMaterialTv;
	private TextView mSwitcherDivisionTv;

	private ViewPager applyByStatusVp;

	private DivisionListFragment applySubFragment;//科室模式的申领（煤矿）
	private OfficeMaterialListStatisticFragment materialListSubFragment;//用品
	private DivisionListFragment divisionSubFragment;//科室模式的科室（煤矿）

	private EmploymentListFragment applySubEmployFragment;//人员模式的申领（商专）
	private EmploymentListFragment divisionSubEmployFragment;//人员模式的科室->人员（商专）

	private OfficeMaterialStatisticFragmentPagerAdapter mStatusOfficeMaterialApplyFragmentPagerAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_office_material_apply_statistic, container,false);
		setFragmentView(fragmentView);
		setFragmentPageName(mContext,R.string.title_fragment_office_material_apply);
		//
		titleBarInit();
		viewInit();
		setupData();
		callInit();
		return fragmentView;
	}

	private void titleBarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(R.string.title_fragment_office_material_apply_static);
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
				OfficeMaterialStatisticFragment.this.getActivity().onBackPressed();

			}
		});

	}

	private void viewInit(){
		applyByStatusVp = (ViewPager) findViewById(R.id.viewpager_office_material_statistic_division_list);
		applyByStatusVp.setOffscreenPageLimit(0);
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
		mStatusOfficeMaterialApplyFragmentPagerAdapter = new OfficeMaterialStatisticFragmentPagerAdapter(getChildFragmentManager());
		applyByStatusVp.setAdapter(mStatusOfficeMaterialApplyFragmentPagerAdapter);
		mSwitcherApplyTv = (TextView) findViewById(R.id.statistic_office_apply_switcher_apply_info);
		mSwitcherMaterialTv = (TextView) findViewById(R.id.statistic_office_apply_switcher_material_info);
		mSwitcherDivisionTv = (TextView) findViewById(R.id.statistic_office_apply_switcher_division_info);

		mSwitcherApplyTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageSwitch(INDEX_APPLY);
			}
		});
		mSwitcherMaterialTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageSwitch(INDEX_MATERIAL);
			}
		});
		mSwitcherDivisionTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pageSwitch(INDEX_DIVISION);
			}
		});
	}

	private void setupData(){
		//Fix分支，代码已删除
	}

	private void pageSwitch(int position){
		applyByStatusVp.setCurrentItem(position);
		mSwitcherApplyTv.setEnabled(true);
		mSwitcherMaterialTv.setEnabled(true);
		mSwitcherDivisionTv.setEnabled(true);
		switch (position) {
		case INDEX_APPLY:
			mSwitcherApplyTv.setEnabled(false);
			break;
		case INDEX_MATERIAL:
			mSwitcherMaterialTv.setEnabled(false);
			break;
		case INDEX_DIVISION:
			mSwitcherDivisionTv.setEnabled(false);
			break;
		default:

			break;
		}
	}

	private void callInit(){

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

	private class OfficeMaterialStatisticFragmentPagerAdapter extends FragmentPagerAdapter{
		public OfficeMaterialStatisticFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case INDEX_APPLY:
				//Fix分支
					if(applySubFragment == null){
						applySubFragment = new DivisionListFragment();
						Bundle data = new Bundle();
						data.putInt("type", DivisionListFragment.TYPE_ORDER);
						applySubFragment.setArguments(data);
					}
					fragment = applySubFragment;
				break;
			case INDEX_MATERIAL:
				if(materialListSubFragment == null){
					materialListSubFragment = new OfficeMaterialListStatisticFragment();
					Bundle data = new Bundle();
					//					data.putString("status", "2");
					materialListSubFragment.setArguments(data);
				}
				fragment = materialListSubFragment;
				break;
			case INDEX_DIVISION:
				//Fix分支
					if(divisionSubFragment == null){
						divisionSubFragment = new DivisionListFragment();
						Bundle data = new Bundle();
						data.putInt("type", DivisionListFragment.TYPE_MATERIAL);
						divisionSubFragment.setArguments(data);
					}
					fragment = divisionSubFragment;
				break;
			default:

				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			mSwitcherApplyTv.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			mSwitcherApplyTv.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			mSwitcherDivisionTv.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			mSwitcherDivisionTv.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
			mSwitcherMaterialTv.setTextAppearance(mContext, R.style.spring_horse_textview_page_repair_switchers);
			mSwitcherMaterialTv.setBackgroundResource(R.drawable.spring_horse_page_repair_switcher_bg);
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
