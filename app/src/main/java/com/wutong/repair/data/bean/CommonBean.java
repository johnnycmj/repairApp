package com.wutong.repair.data.bean;

public abstract class CommonBean {
	protected String request;
	protected String response;
	
	public abstract String getResponse();
	public abstract String getRequest();
}
