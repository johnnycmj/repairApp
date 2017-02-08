package com.wutong.repair.data.bean;

public class RepairOrderFormBean {

	private String applyUserId;
	private String dormRoomId;
	private String assetId;
	private String troubleId;
	private String other;
	private String repairType;

	
	public void checkInit(){
		
	}
	
	public String getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}
	public String getDormRoomId() {
		return dormRoomId;
	}
	public void setDormRoomId(String dormRoomId) {
		this.dormRoomId = dormRoomId;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getTroubleId() {
		return troubleId;
	}
	public void setTroubleId(String troubleId) {
		this.troubleId = troubleId;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public String getRepairType() {
		return repairType;
	}
	public void setRepairType(String repairType) {
		this.repairType = repairType;
	}

	@Override
	public String toString() {
		return "RepairOrderFormBean [applyUserId=" + applyUserId
				+ ", dormRoomId=" + dormRoomId + ", assetId=" + assetId
				+ ", troubleId=" + troubleId + ", other=" + other
				+ ", repairType=" + repairType + "]";
	}

	
	
}
