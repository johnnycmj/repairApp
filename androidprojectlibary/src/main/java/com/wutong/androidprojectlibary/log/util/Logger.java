package com.wutong.androidprojectlibary.log.util;




import com.wutong.androidprojectlibary.BuildConfig;

import android.util.Log;

public class Logger {
	private final static String commonTag = "WT_APL";
	public static int LOG_LEVEL;//0为无日志1~5为对应级别的日志
	public static int ERROR = 1;
	public static int WARN = 2;
	public static int INFO = 3;
	public static int DEBUG = 4;
	public static int VERBOS = 5;

	static{
		LOG_LEVEL = BuildConfig.DEBUG?5:0;
	}

	public static void e(String msg){
		if(LOG_LEVEL>=ERROR)
			Log.e(commonTag, msg);
	}

	public static void w(String msg){
		if(LOG_LEVEL>=WARN)
			Log.w(commonTag, msg);
	}
	public static void i(String msg){
		if(LOG_LEVEL>=INFO)
			Log.i(commonTag, msg);
	}
	public static void d(String msg){
		if(LOG_LEVEL>=DEBUG)
			Log.d(commonTag, msg);
	}
	public static void v(String msg){
		if(LOG_LEVEL>=VERBOS)
			Log.v(commonTag, msg);
	}


}