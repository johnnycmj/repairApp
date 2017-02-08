package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class PersonBean {
	@SerializedName(value="userName")
	private String userName;
	@SerializedName(value="realName")
	private String realName;
	@SerializedName(value="roomName")
	private String dormName;
	@SerializedName(value="phone")
	private String contact;
	@SerializedName(value="dormtoryId")
	private Integer dormId;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getDormName() {
		return dormName;
	}
	public void setDormName(String dormName) {
		this.dormName = dormName;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public Integer getDormId() {
		return dormId;
	}
	public void setDormId(Integer dormId) {
		this.dormId = dormId;
	}
	
	

}
