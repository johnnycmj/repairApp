package com.wutong.androidprojectlibary.widget.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wutong.androidprojectlibary.R;

public class ToastUtil {


	private static Drawable iconLeft;

	private static Toast mToastView;
	private static View toastContentView ;
	private static ImageView toastIconView;
	private static TextView toastMessageView;//吐司的文字控件
	private static Map<String, Boolean> singleMap;
	static{
		//吐司Toast效果
		singleMap = new HashMap<String, Boolean>();
	}
	
	public static void applicationInit(Context context, int leftResId){
		iconLeft = context.getResources().getDrawable(leftResId);
	}
	
	
	public static void showToast(Context context,String message){
		mToastView = new Toast(context);
		toastContentView = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
		toastMessageView = (TextView) toastContentView.findViewById(R.id.toast_message);
		toastIconView = (ImageView) toastContentView.findViewById(R.id.toast_icon);
		if(iconLeft != null){
			toastIconView.setImageDrawable(iconLeft);
		}
		
		mToastView.setDuration(Toast.LENGTH_SHORT);
		mToastView.setView(toastContentView);
		mToastView.setGravity(Gravity.CENTER, 0, 100);
		toastMessageView.setText(message);
		mToastView.show();
	}
	
	public static void showToast(Context context,int resId){
		showToast(context, context.getString(resId));
	}
	
	public static void showToast(Context context,String message,String key){
		synchronized (singleMap) {
			Boolean hasShow = singleMap.get(key);
			if(hasShow == null || !hasShow.booleanValue()){
				showToast(context, message);
				singleMap.put(key, true);
			}
		}
		
	}
	public static void showToast(Context context,int resId,String key){
		showToast(context, context.getString(resId),key);
		
	}
	
	public static void cancelToast() {
		if (mToastView != null) {
			mToastView.cancel();
		}
	}
}
