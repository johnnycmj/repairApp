package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class StudentDormBean {

	@SerializedName(value = "floorName")
	private String floorName;
	@SerializedName(value = "roomName")
	private String roomName;
	@SerializedName(value = "roomFloor")
	private String roomFloor;
	@SerializedName(value = "bedNode")
	private String bedNode;
	@SerializedName(value = "studentName")
	private String studentName;
	@SerializedName(value = "phone")
	private String phone;
	@SerializedName(value = "dormtoryId")
	private Integer dormtoryId;
	public String getFloorName() {
		return floorName;
	}
	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getRoomFloor() {
		return roomFloor;
	}
	public void setRoomFloor(String roomFloor) {
		this.roomFloor = roomFloor;
	}
	public String getBedNode() {
		return bedNode;
	}
	public void setBedNode(String bedNode) {
		this.bedNode = bedNode;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getDormtoryId() {
		return dormtoryId;
	}
	public void setDormtoryId(Integer dormtoryId) {
		this.dormtoryId = dormtoryId;
	}
	
	
	
}
