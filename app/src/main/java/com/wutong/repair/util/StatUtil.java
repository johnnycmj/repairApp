package com.wutong.repair.util;

import com.baidu.mobstat.StatService;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * 统计工具
 * @author Jolly
 * 创建时间：2013-7-24下午4:40:53
 *
 */
public class StatUtil {
	public static void onResume(Context paramContext){
		StatService.onResume(paramContext);
	}
	public static void onPause(Context paramContext){
		StatService.onPause(paramContext);
	}
	
	public static void onResume(Fragment fragment){
		StatService.onResume(fragment);
	}
	public static void onPause(Fragment fragment){
		StatService.onPause(fragment);
	}
	
	public static void onPageStart(Context paramContext,String pageName){
		StatService.onPageStart(paramContext, pageName);
	}
	
	public static void onPageEnd(Context paramContext,String pageName){
		StatService.onPageEnd(paramContext, pageName);
	}
	
}
