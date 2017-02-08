package com.wutong.androidprojectlibary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class RotateQuarterText extends TextView{
	public RotateQuarterText(Context context) {
		super(context);
	}
	public RotateQuarterText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public RotateQuarterText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.rotate(-45, getMeasuredWidth()/2, getMeasuredHeight()/2);
		super.onDraw(canvas);
	}

}
