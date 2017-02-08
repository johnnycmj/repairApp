package com.wutong.repair.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnUtil {
	
	private static NetworkInfo info;
	private static ConnectivityManager connectivityManager;
	
	/**
	 * 获取当前的联网信息
	 * @param context
	 * @return
	 */
	private static NetworkInfo getNetworkInfo(Context context){
		if(connectivityManager==null){
			connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return connectivityManager.getActiveNetworkInfo();
	}
	/**
	 * 判断是否联网，无视联网方式
	 * @param context
	 * @return
	 */
	public static boolean isHaveInternet(Context context){
		info = getNetworkInfo(context);
		if (info!=null&&info.isConnected()) {
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 判断是否是wifi方式联网
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnect(Context context){
		info = getNetworkInfo(context);
		if (info!=null&&info.getType() == ConnectivityManager.TYPE_WIFI&&info.isConnected()) {
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 判断是否为手机的联网方式（GPRS,UMTS等）
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnect(Context context){
		info = getNetworkInfo(context);
		if (info!=null&&info.getType() == ConnectivityManager.TYPE_MOBILE&&info.isConnected()) {
			return true;
		}
		else{
			return false;
		}
	}

}
