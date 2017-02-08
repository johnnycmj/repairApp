package com.wutong.common.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class UnableScrollViewPager extends ViewPager{

	public UnableScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UnableScrollViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}

}
