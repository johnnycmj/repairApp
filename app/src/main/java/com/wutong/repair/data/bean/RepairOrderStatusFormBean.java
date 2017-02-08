package com.wutong.repair.data.bean;

public class RepairOrderStatusFormBean {
	private String repairOrderId;
	private String userId;
	private String howRepairContent;
	private int operate;
	private String operateName;
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
	public int getOperate() {
		return operate;
	}
	public void setOperate(int operate) {
		this.operate = operate;
	}
	public String getOperateName() {
		return operateName;
	}
	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}
	public String getHowRepairContent() {
		return howRepairContent;
	}
	public void setHowRepairContent(String howRepairContent) {
		this.howRepairContent = howRepairContent;
	}
	
	

}
