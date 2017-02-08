package com.wutong.androidprojectlibary.http.util;

import com.wutong.androidprojectlibary.log.util.Logger;

import net.tsz.afinal.http.AjaxCallBack;

public class AjaxCallBackPlus<T> extends AjaxCallBack<T> {

	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
		Logger.v("http failed:errorNo"+ errorNo +",strMsg:" + strMsg +",ex:" +new Exception(t).getMessage());
		super.onFailure(t, errorNo, strMsg);
	}

	@Override
	public void onStart() {
		
		super.onStart();
	}

	@Override
	public void onSuccess(T t) {
		Logger.v("http result:" + t.toString());
		super.onSuccess(t);
	}

}
