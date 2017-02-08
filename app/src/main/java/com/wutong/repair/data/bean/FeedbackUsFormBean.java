package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class FeedbackUsFormBean {
	@SerializedName(value="content")
	private String content;
	@SerializedName(value="email")
	private String email;
	@SerializedName(value="brand")
	private String brand;
	@SerializedName(value="phoneModel")
	private String device;
	@SerializedName(value="version")
	private String applicationVersionName;
	@SerializedName(value="operatingSystem")
	private String systemVersionName;
	@SerializedName(value="netState")
	private String netTypeName;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getApplicationVersionName() {
		return applicationVersionName;
	}
	public void setApplicationVersionName(String applicationVersionName) {
		this.applicationVersionName = applicationVersionName;
	}
	public String getSystemVersionName() {
		return systemVersionName;
	}
	public void setSystemVersionName(String systemVersionName) {
		this.systemVersionName = systemVersionName;
	}
	public String getNetTypeName() {
		return netTypeName;
	}
	public void setNetTypeName(String netTypeName) {
		this.netTypeName = netTypeName;
	}
	

}
