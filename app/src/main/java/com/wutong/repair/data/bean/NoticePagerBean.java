package com.wutong.repair.data.bean;

public class NoticePagerBean {
	private String userId;
	private String start;
	private String limit;
	
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public NoticePagerBean(String userId,int start, int limit) {
		super();
		this.userId = userId;
		this.start = String.valueOf(start);
		this.limit = String.valueOf(limit);
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
