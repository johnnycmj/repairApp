package com.wutong.repair.activity;

import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class IntroductionGuideActivity extends BaseActivity {
	private ViewPager introductionVp;
	private GuidePagerAdapter guidePagerAdapter;
	private View firstPageGuideView;
	private View lastPageGuideView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isNeedLogin = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduction_guide);
		setStatPageName(mContext, R.string.page_name_activity_introduction_guide);
		viewInit();
	}

	private void viewInit(){
		firstPageGuideView = LinearLayout.inflate(mContext, R.layout.listview_introduction_guide, null);
		lastPageGuideView = LinearLayout.inflate(mContext, R.layout.listview_introduction_guide, null);

		((ImageView)firstPageGuideView.findViewById(R.id.guide_img)).setImageResource(R.drawable.intruduction_guide_college_service_page);
		((ImageView)lastPageGuideView.findViewById(R.id.guide_img)).setImageResource(R.drawable.intruduction_guide_micro_share_page);



		introductionVp = (ViewPager) findViewById(R.id.viewpager_guide_introduction);
		guidePagerAdapter = new GuidePagerAdapter();
		introductionVp.setAdapter(guidePagerAdapter);

		lastPageGuideView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();

			}
		});
	}

	private class GuidePagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return 2;
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
				viewPagerView = firstPageGuideView;
				break;
			case 1:
				viewPagerView = lastPageGuideView;
				break;

			default:
				break;
			}

			((ViewPager)container).addView(viewPagerView);
			return viewPagerView;
		}
	}

}
