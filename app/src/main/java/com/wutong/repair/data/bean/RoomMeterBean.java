package com.wutong.repair.data.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 住宅抄表-住户信息
 * @author Jolly
 * 创建时间：2014年2月21日下午8:31:16
 *
 */
public class RoomMeterBean {
	@SerializedName(value="householderNum")
	private String roomOwnerCode;
	@SerializedName(value="realName")
	private String roomOwnerName;
	@SerializedName(value="sdFloorId")
	private String id;
	@SerializedName(value="sdHouseholderId")
	private String officeId;
	
	@SerializedName(value="meterList")
	private List<ChildMeterBean> childMeterList;
	
	public String getRoomOwnerCode() {
		return roomOwnerCode;
	}
	public void setRoomOwnerCode(String roomOwnerCode) {
		this.roomOwnerCode = roomOwnerCode;
	}
	public String getRoomOwnerName() {
		return roomOwnerName;
	}
	public void setRoomOwnerName(String roomOwnerName) {
		this.roomOwnerName = roomOwnerName;
	}
	public List<ChildMeterBean> getChildMeterList() {
		return childMeterList;
	}
	public void setChildMeterList(List<ChildMeterBean> childMeterList) {
		this.childMeterList = childMeterList;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

}
