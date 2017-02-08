package com.wutong.androidprojectlibary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class HorizontalSmoothScrollView extends ScrollView {

	private GestureDetector mGestureDetector;

	public HorizontalSmoothScrollView(Context context, AttributeSet attrs){
		super(context, attrs);
		if(!isInEditMode()){
			mGestureDetector = new GestureDetector(context, new YScrollDetector());
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
	}

	class YScrollDetector extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			/**
			 * if we're scrolling more closer to x direction, return false, let subview to process it
			 */
			return (Math.abs(distanceY) > Math.abs(distanceX));
		}
	}
}