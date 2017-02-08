package com.wutong.androidprojectlibary.http.util;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import com.wutong.androidprojectlibary.log.util.Logger;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;


import android.content.Context;

public class HttpResponseUtil {

	
	public static void justToast(int errorCode,Throwable throwable,Context context){
		if(errorCode!=0){//+600
			ToastUtil.showToast(context, "连接错误["+(errorCode+600)+"]");
		}
		else if(throwable.getClass().equals(ConnectTimeoutException.class)){
			Logger.e("ConnectTimeoutException");
			ToastUtil.showToast(context, "连接超时[C]");
		}
		else if(throwable.getClass().equals(HttpHostConnectException.class)){
			Logger.e("HttpHostConnectException");
			ToastUtil.showToast(context, "无法连接服务器[H]");
		}
		else if(throwable.getClass().equals(ConnectException.class)){
			Logger.e("ConnectException");
			ToastUtil.showToast(context, "无法连接服务器[C]");
		}
		else if(throwable.getClass().equals(SocketException.class)){
			Logger.e("SocketException");
			ToastUtil.showToast(context, "网络出现意外的异常");
		}
		else if(throwable.getClass().equals(FileNotFoundException.class)){
			Logger.e("FileNotFoundException");
			ToastUtil.showToast(context, "文件读写异常");
		}
		else if(throwable.getClass().equals(SocketTimeoutException.class)){
			Logger.e("SocketTimeoutException");
			ToastUtil.showToast(context, "连接超时[S]");
		}
		else if(throwable.getClass().equals(NoHttpResponseException.class)){
			Logger.e("NoHttpResponseException");
			ToastUtil.showToast(context, "服务器响应失败");
		}
		else{
			Logger.e("throwable.getClass():" + throwable.getClass());
			ToastUtil.showToast(context, "未处理的异常");
		}
	}
}
