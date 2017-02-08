package com.wutong.common.widget;


import com.wutong.repairfjnu.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class InterceptTouchImageView extends FrameLayout {
	private Context mContext;

	public InterceptTouchImageView(Context context) {
		super(context);
		mContext = context;
	}

	public InterceptTouchImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public InterceptTouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			this.setVisibility(ViewGroup.GONE);
			SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			defaultPreferences.edit().putBoolean(mContext.getString(R.string.guide_key_for_new_function_introduction_with_micro_share), true).commit();
			break;

		default:
			break;
		}
		return true;
	}


}
