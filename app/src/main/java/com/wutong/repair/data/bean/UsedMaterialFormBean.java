package com.wutong.repair.data.bean;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class UsedMaterialFormBean {
	
	@SerializedName(value="asset")
	private List<Map<String, Integer>> materialUsed;
	@SerializedName(value="userid")
	private String userId;
	@SerializedName(value="formid")
	private String repairOrderId;
	public List<Map<String, Integer>> getMaterialUsed() {
		return materialUsed;
	}
	public void setMaterialUsed(List<Map<String, Integer>> materialUsed) {
		this.materialUsed = materialUsed;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRepairOrderId() {
		return repairOrderId;
	}
	public void setRepairOrderId(String repairOrderId) {
		this.repairOrderId = repairOrderId;
	}
	

}
