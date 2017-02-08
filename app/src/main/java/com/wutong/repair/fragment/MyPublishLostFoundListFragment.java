package com.wutong.repair.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;

/**
 * 我发布的
 * @author Jolly
 * 创建时间：2014年4月15日下午1:39:00
 *
 */
public class MyPublishLostFoundListFragment extends BaseFragment implements OnClickListener{

	private boolean outHasBack;

	private TextView mCenterTitleView;
	private ImageView mTitlebarBackView;


	private ViewPager mStatusViewPager;

	private AirConditionOrderFragmentPagerAdapter mMarketOrderFragmentPagerAdapter;

	public final static int INDEX_NO_QUERY = 0;
	public final static int INDEX_FINISH = 1;
	public final static int INDEX_MY_COMPLAINT = 2;
	private TextView mNotPaySwitcher;
	private TextView mPayHistorySwitcher;
	private TextView mMyComplaintSwitcher;

	private MyPublishLostFoundStatusFragment mNotfinishFragment;
	private MyPublishLostFoundStatusFragment mFinishFragment;
	private MyComplaintFragment myComplaintFragment;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_my_publish_lost_found_list, container,false);
		setFragmentView(fragmentView);
		//
		commonInitFirst();
		findViewInit();
		intentInit();
		titlebarInit();
		viewInit();
		listenerInit();
		setupData();
		return fragmentView;
	}

	private void commonInitFirst(){
		mTitle = getString(R.string.title_my_publish_lost_found);
	}

	private void findViewInit(){
		mCenterTitleView = (TextView) findViewById(R.id.titlebar_title);
		mTitlebarBackView = (ImageView) findViewById(R.id.titlebar_back);

		mStatusViewPager = (ViewPager) findViewById(R.id.pager);

		mNotPaySwitcher = (TextView) findViewById(R.id.fee_list_switcher_not_pay);
		mPayHistorySwitcher = (TextView) findViewById(R.id.fee_list_switcher_pay_history);
		mMyComplaintSwitcher = (TextView) findViewById(R.id.fee_list_switcher_complaint);
	}

	private void intentInit(){
		Bundle arguments = getArguments();
		if(arguments != null){
			outHasBack = arguments.getBoolean("hasBack");
		}
	}
	private void titlebarInit(){
		mCenterTitleView.setText(mTitle);
		boolean isTitlebarBackShow = this.getActivity().getIntent().getBooleanExtra("titlebar_back_is_show", false);
		if(isTitlebarBackShow){
			mTitlebarBackView.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			mTitlebarBackView.setVisibility(ViewGroup.GONE);
		}
	}

	private void viewInit(){
		mMarketOrderFragmentPagerAdapter = new AirConditionOrderFragmentPagerAdapter(getChildFragmentManager());
		mStatusViewPager.setAdapter(mMarketOrderFragmentPagerAdapter);
		mStatusViewPager.setOffscreenPageLimit(2);
	}

	private void listenerInit(){
		this.mTitlebarBackView.setOnClickListener(this);

		mNotPaySwitcher.setOnClickListener(this);
		mPayHistorySwitcher.setOnClickListener(this);
		mMyComplaintSwitcher.setOnClickListener(this);
		mStatusViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				pageSwitch(index);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void setupData(){
	}
	@Override
	public void onClick(View v) {
		FragmentActivity activity = this.getActivity();
		switch (v.getId()) {
		case R.id.titlebar_back:
			this.getActivity().onBackPressed();
			break;
		case R.id.fee_list_switcher_not_pay:
			pageSwitch(INDEX_NO_QUERY);
			break;
		case R.id.fee_list_switcher_pay_history:
			pageSwitch(INDEX_FINISH);
			break;
			
		case R.id.fee_list_switcher_complaint:
			pageSwitch(INDEX_MY_COMPLAINT);
			break;
		default:
			break;
		}

	}

	private class AirConditionOrderFragmentPagerAdapter extends FragmentPagerAdapter{

		public AirConditionOrderFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			Fragment fragment = null;
			switch (index) {
			case INDEX_NO_QUERY:
				mNotfinishFragment = new MyPublishLostFoundStatusFragment();
				fragment = mNotfinishFragment;

				break;
			case INDEX_FINISH:
				mFinishFragment = new MyPublishLostFoundStatusFragment();
				fragment = mFinishFragment;
				break;
			case INDEX_MY_COMPLAINT:
				myComplaintFragment = new MyComplaintFragment();
				fragment = myComplaintFragment;
				break;
			default:
				break;
			}
			Bundle data = new Bundle();
			data.putInt("index", index);
			fragment.setArguments(data);
			fragment.setTargetFragment(MyPublishLostFoundListFragment.this, 0);
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

	}

	private void pageSwitch(int position){
		mStatusViewPager.setCurrentItem(position);
		mNotPaySwitcher.setEnabled(true);
		mPayHistorySwitcher.setEnabled(true);
		mMyComplaintSwitcher.setEnabled(true);
		switch (position) {
		case INDEX_NO_QUERY:
			mNotPaySwitcher.setEnabled(false);
			break;
		case INDEX_FINISH:
			mPayHistorySwitcher.setEnabled(false);
			break;
		case INDEX_MY_COMPLAINT:
			mMyComplaintSwitcher.setEnabled(false);
			break;
		default:

			break;
		}
	}

	/**
	 * 刷新
	 */
	public void refreshStatusSub(int... indexArray){
		if(indexArray.length >0){
			for(int index:indexArray){
				switch (index) {
				case INDEX_NO_QUERY:
					mNotfinishFragment.refresh();
					break;
				case INDEX_FINISH:
					mFinishFragment.refresh();
					break;
				default:
					break;
				}
			}
		}
	}
}
