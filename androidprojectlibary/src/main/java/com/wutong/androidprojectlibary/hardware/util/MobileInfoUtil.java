package com.wutong.androidprojectlibary.hardware.util;


import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

public class MobileInfoUtil {
	
	private static TelephonyManager telephonyManager;
	
	private static TelephonyManager getTelephonyManager(Context context){
		if(telephonyManager==null){
			telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		}
		return telephonyManager;
	}

	/**
	 * 获取当前使用手机号码
	 * @return
	 */
	public static String getcurrentNumber(Context context){
		return getTelephonyManager(context).getLine1Number();
	}
	/**
	 * 获取国家长途码
	 * @param context
	 * @return
	 */
	public static String getNetworkCountryIso(Context context){
		return getTelephonyManager(context).getNetworkCountryIso();
	}
	/**
	 * 获取IMSI
	 * @param context
	 * @return
	 */
	public static String getSubscriberId(Context context){
		return getTelephonyManager(context).getSubscriberId();
	}
	
	/**
	 * 获取IMEI
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context){
		return getTelephonyManager(context).getDeviceId();
	}
	
	/**
	 * <pre>
	 * 必须在AndroidManifest加上
	 *   supports-screens
     *      android:smallScreens="true"
     *      android:normalScreens="true"
     *      android:largeScreens="true"
     *      android:resizeable="true"
     *      android:anyDensity="true"/>
	 * </pre>
	 * @return
	 */
	public static int getDisplayWidth(Activity activity){
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
		return width;
	}
	
	public static int getDisplayHeight(Activity activity){
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;  // 屏幕高度（像素）
		return height;
	}
	
	public static float getDisplayDensitiy(Activity activity){
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
		return density;
	}
	public static int getDisplayDensityDpi(Activity activity){
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
		return densityDpi;
	}
	
	/**
	  * 操作系统的版本名称
	  * @return
	  */
	 public static String getSystemVersionName(){
		 return android.os.Build.VERSION.RELEASE;
	 }
	 /**
	  * 获取手机品牌
	  * @return
	  */
	 public static String getMobileBrand(){
		 return android.os.Build.BRAND;
	 }
	 
	 /**
	  * 获取手机型号
	  * @return
	  */
	 public static String getMobileDevice(){
		 return android.os.Build.DEVICE;
	 }
}
