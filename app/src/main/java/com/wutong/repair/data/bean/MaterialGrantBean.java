package com.wutong.repair.data.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class MaterialGrantBean implements Serializable{
	
	@SerializedName(value="applyAssetId")
	private String id;
	@SerializedName(value="applyTime")
	private String grantTime;
	@SerializedName(value="status")
	private String status;
	private String statusName;
	@SerializedName(value="applyNum")
	private String grantCode;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGrantTime() {
		return grantTime;
	}
	public void setGrantTime(String grantTime) {
		this.grantTime = grantTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getGrantCode() {
		return grantCode;
	}
	public void setGrantCode(String grantCode) {
		this.grantCode = grantCode;
	}

}
