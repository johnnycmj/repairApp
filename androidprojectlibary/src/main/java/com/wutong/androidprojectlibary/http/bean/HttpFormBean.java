package com.wutong.androidprojectlibary.http.bean;

import java.util.Collection;

import org.apache.http.NameValuePair;

public class HttpFormBean {
	private String url;
	private Collection<NameValuePair> params;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Collection<NameValuePair> getParams() {
		return params;
	}
	public void setParams(Collection<NameValuePair> params) {
		this.params = params;
	}
	
	
}
