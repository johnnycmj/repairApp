package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class WorkbenchStatEmploymentBean {

	@SerializedName(value="userId")
	private Integer employmentId;
	
	@SerializedName(value="repairManName")
	private String name;
	
	@SerializedName(value="monthCount")
	private Integer monthTotal;
	
	@SerializedName(value="noAcceptCount")
	private Integer notAcceptNumber;
	
	@SerializedName(value="dealCount")
	private Integer repairingNumber;

	public Integer getEmploymentId() {
		return employmentId;
	}

	public void setEmploymentId(Integer employmentId) {
		this.employmentId = employmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMonthTotal() {
		return monthTotal;
	}

	public void setMonthTotal(Integer monthTotal) {
		this.monthTotal = monthTotal;
	}

	public Integer getNotAcceptNumber() {
		return notAcceptNumber;
	}

	public void setNotAcceptNumber(Integer notAcceptNumber) {
		this.notAcceptNumber = notAcceptNumber;
	}

	public Integer getRepairingNumber() {
		return repairingNumber;
	}

	public void setRepairingNumber(Integer repairingNumber) {
		this.repairingNumber = repairingNumber;
	}
	
}
