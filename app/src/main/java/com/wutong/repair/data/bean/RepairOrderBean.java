package com.wutong.repair.data.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class RepairOrderBean implements Serializable{
	@SerializedName(value="repairFormId")
	private String id;
	@SerializedName(value="repairNum")
	private String repairCode;
	@SerializedName(value="repairmanName")
	private String repairmanName;
	@SerializedName(value="userRepairmanId")
	private String repairmanId;

	private String userId;
	@SerializedName(value="repaircontact_tel")
	private String repairmanContactTel;
	@SerializedName(value="userName")
	private String userName;
	@SerializedName(value="applyPhone")
	private String userContactTel;
	@SerializedName(value="roomName")
	private String roomName;
	@SerializedName(value="hotelRoomId")
	private Integer roomId;
	@SerializedName(value="itemPname")
	private String assetName;
	@SerializedName(value="itemName")
	private String troubleName;
	@SerializedName(value="pictureCount")
	private Integer photoNumber;
	@SerializedName(value="status")
	private String status;
	@SerializedName(value="statusName")
	private String statusName;
	@SerializedName(value="startTime")
	private String startTime;
	@SerializedName(value="acceptTime")
	private String acceptTime;
	@SerializedName(value="endTime")
	private String endTime;
	@SerializedName(value="detail")
	private String other;
	@SerializedName(value="feedbackCount")
	private Integer feedbackCount;
	@SerializedName(value="assetsCount")
	private String assetCount;
	@SerializedName(value="repairAssetsCount")
	private String materialCount;
	@SerializedName(value="formType")
	private String formType;
	private String groupName;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRepairCode() {
		return repairCode;
	}
	public void setRepairCode(String repairCode) {
		this.repairCode = repairCode;
	}
	public String getRepairmanName() {
		return repairmanName;
	}
	public void setRepairmanName(String repairmanName) {
		this.repairmanName = repairmanName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public String getTroubleName() {
		return troubleName;
	}
	public void setTroubleName(String troubleName) {
		this.troubleName = troubleName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getUserContactTel() {
		return userContactTel;
	}
	public void setUserContactTel(String userContactTel) {
		this.userContactTel = userContactTel;
	}
	public String getRepairmanContactTel() {
		return repairmanContactTel;
	}
	public void setRepairmanContactTel(String repairmanContactTel) {
		this.repairmanContactTel = repairmanContactTel;
	}


	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public Integer getRoomId() {
		return roomId;
	}
	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}
	public Integer getFeedbackCount() {
		return feedbackCount;
	}
	public void setFeedbackCount(Integer feedbackCount) {
		this.feedbackCount = feedbackCount;
	}
	public String getAssetCount() {
		return assetCount;
	}
	public void setAssetCount(String assetCount) {
		this.assetCount = assetCount;
	}
	public String getMaterialCount() {
		return materialCount;
	}
	public void setMaterialCount(String materialCount) {
		this.materialCount = materialCount;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getAcceptTime() {
		return acceptTime;
	}
	public void setAcceptTime(String acceptTime) {
		this.acceptTime = acceptTime;
	}
	public String getRepairmanId() {
		return repairmanId;
	}
	public void setRepairmanId(String repairmanId) {
		this.repairmanId = repairmanId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getPhotoNumber() {
		return photoNumber;
	}
	public void setPhotoNumber(Integer photoNumber) {
		this.photoNumber = photoNumber;
	}


}
