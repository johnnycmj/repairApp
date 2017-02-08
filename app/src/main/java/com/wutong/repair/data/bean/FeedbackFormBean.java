package com.wutong.repair.data.bean;

public class FeedbackFormBean {
	
	private String repairOrderId;
	private String userId;
	private String content;
	private String needRefix;
	public String getRepairOrderId() {
		return repairOrderId;
	}
	public void setRepairOrderId(String repairOrderId) {
		this.repairOrderId = repairOrderId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getNeedRefix() {
		return needRefix;
	}
	public void setNeedRefix(String needRefix) {
		this.needRefix = needRefix;
	}
	

}
