package com.wutong.repair.data.bean;

public class FeedbackPagerBean {
	
	private String id;
	private String start;
	private String limit;
	public FeedbackPagerBean(int start, int limit) {
		this.id = "0";
		this.start = String.valueOf(start);
		this.limit = String.valueOf(limit);
	}
	
	public FeedbackPagerBean(String id, int start, int limit) {
		super();
		this.id = id;
		this.start = String.valueOf(start);
		this.limit = String.valueOf(limit);
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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

}
