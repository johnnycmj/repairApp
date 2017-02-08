package com.wutong.androidprojectlibary.software.util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class CommonOperateUtil {

	/**
	 * 隐藏输入法
	 * @param activity
	 */
	public static void hideIME(Activity activity){

		View currentFocusView = activity.getCurrentFocus();
		if(currentFocusView != null){
			((InputMethodManager)activity. getSystemService(Activity.INPUT_METHOD_SERVICE))
			.hideSoftInputFromWindow(currentFocusView.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
