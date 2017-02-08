package com.wutong.androidprojectlibary.http.util;

import android.content.Context;
import android.widget.Toast;

import com.wutong.androidprojectlibary.log.util.Logger;
import com.wutong.androidprojectlibary.network.util.InternetConnUtil;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class FinalHttpPlus extends FinalHttp{

	private Context mContext;
	
	public FinalHttpPlus(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public void post(String url, AjaxCallBack<? extends Object> callBack) {
		if(!InternetConnUtil.isHaveInternet(mContext)){
			Toast.makeText(mContext, "无法连接网络，请打开网络后再试", Toast.LENGTH_LONG).show();
			return;
		}
		Logger.v("+=+=Url：" +url);
		super.post(url, callBack);
	}

	@Override
	public void post(String url, AjaxParams params,
			AjaxCallBack<? extends Object> callBack) {
		if(!InternetConnUtil.isHaveInternet(mContext)){
			Toast.makeText(mContext, "无法连接网络，请打开网络后再试", Toast.LENGTH_LONG).show();
			return;
		}
		Logger.v("+=+=Params：" +params);
		Logger.v("+=+=Url：" +url);
		super.post(url, params, callBack);
	}

}
