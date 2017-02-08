package com.wutong.repair.util;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import com.wutong.androidprojectlibary.http.exception.HTTPResponseStatusException;


import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repairfjnu.R;

import android.content.Context;


public class HttpResponseUtil {

	public static void justToast(Exception e,Context context){
		if(e.getClass().equals(ConnectTimeoutException.class)
				||e.getClass().equals(HttpHostConnectException.class)
				||e.getClass().equals(ConnectException.class)
				||e.getClass().equals(SocketException.class)
				||e.getClass().equals(SocketTimeoutException.class)
				||e.getClass().equals(ConnectionPoolTimeoutException.class)
				||e.getClass().equals(HTTPResponseStatusException.class)){
			ToastUtil.showToast(context, R.string.tips_for_network_bad);
		}
		else if(e.getClass().equals(FileNotFoundException.class)){
			Logger.e("FileNotFoundException");
			ToastUtil.showToast(context, R.string.tips_for_memory_bad);
		}
		else{
			ToastUtil.showToast(context, R.string.tips_for_unknown_error);
		}
		
//		else if(e.getClass().equals(ConnectTimeoutException.class)){
//			Logger.e("ConnectTimeoutException");
//			ToastUtil.showToast(context, "网络不给力，请求超时");//
//		}
//		else if(e.getClass().equals(HttpHostConnectException.class)){
//			Logger.e("HttpHostConnectException");
//			ToastUtil.showToast(context, "无法连接服务器");
//		}
//		else if(e.getClass().equals(ConnectException.class)){
//			Logger.e("ConnectException");
//			ToastUtil.showToast(context, "无法连接服务器");
//		}
//		else if(e.getClass().equals(SocketException.class)){
//			Logger.e("SocketException");
//			ToastUtil.showToast(context, "网络出现意外的异常");
//		}
//		else if(e.getClass().equals(FileNotFoundException.class)){
//			Logger.e("FileNotFoundException");
//			ToastUtil.showToast(context, "文件读写异常");
//		}
//		else if(e.getClass().equals(SocketTimeoutException.class)){
//			Logger.e("SocketTimeoutException");
//			ToastUtil.showToast(context, "网络不给力，响应超时");//出错啦，出错代码[01]
//		}
//		else if(e.getClass().equals(ConnectionPoolTimeoutException.class)){
//			Logger.e("ConnectionPoolTimeoutException");
//			ToastUtil.showToast(context, "出错啦，出错代码[03]");//出错啦，出错代码[03]
//		}
//		else if(e.getClass().equals(HTTPResponseStatusException.class)){
//			Logger.e("HTTPResponseStatusException");
//			ToastUtil.showToast(context, "http请求返回状态不正确");
//		}
//		else{
//			ToastUtil.showToast(context, "出错啦，出错代码[00]");
//		}

	}
}
