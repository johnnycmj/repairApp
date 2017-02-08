package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class WorkbenchStatRepairBean {
	@SerializedName(value="totalRepair")
	private String totalNumber;
	@SerializedName(value="dealRepair")
	private String repairingNumber;
	@SerializedName(value="finishCount")
	private String completeNumber;
	@SerializedName(value="time")
	private String time;
	
	private String overTimeCount;
	
	@SerializedName(value="beRepair")
	private String dealtNumber;//当月的维修中
	@SerializedName(value="noAccept")
	private String notAcceptNumber;//当月的未受理
	
	public String getTotalNumber() {
		return totalNumber;
	}
	public String getRepairingNumber() {
		return repairingNumber;
	}
	public String getCompleteNumber() {
		return completeNumber;
	}
	public String getTime() {
		return time;
	}
	public String getDealtNumber() {
		return dealtNumber;
	}
	public void setDealtNumber(String dealtNumber) {
		this.dealtNumber = dealtNumber;
	}
	public String getNotAcceptNumber() {
		return notAcceptNumber;
	}
	public void setNotAcceptNumber(String notAcceptNumber) {
		this.notAcceptNumber = notAcceptNumber;
	}
	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}
	public void setRepairingNumber(String repairingNumber) {
		this.repairingNumber = repairingNumber;
	}
	public void setCompleteNumber(String completeNumber) {
		this.completeNumber = completeNumber;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getOverTimeCount() {
		return overTimeCount;
	}
	public void setOverTimeCount(String overTimeCount) {
		this.overTimeCount = overTimeCount;
	}

}
