package com.wutong.repair.data.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("serial")
public class UpdateBean implements Serializable{
	private String versionCode;
	@SerializedName(value="url")
	private String downloadUrl;
	
	private String versionName;
	@SerializedName(value="log")
	private String versionLog;
	@SerializedName(value="mustUpdateVersions")
	private String mustUpdateVersionArray;//强制更新的版本
	
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getVersionLog() {
		return versionLog;
	}
	public void setVersionLog(String versionLog) {
		this.versionLog = versionLog;
	}

	public boolean isEmpty(){
		if(versionCode.toString().trim().length() ==0|| downloadUrl.toString().trim().length() == 0){
			return true;
		}
		else {
			return false;
		}
	}
	public String getMustUpdateVersionArray() {
		return mustUpdateVersionArray;
	}
	public void setMustUpdateVersionArray(String mustUpdateVersionArray) {
		this.mustUpdateVersionArray = mustUpdateVersionArray;
	}
	
	public boolean isMustUpdate(String currentVersionName){
		if(mustUpdateVersionArray == null || mustUpdateVersionArray.toString().trim().length() == 0){
			return false;
		}
		else{
			String[] versionArray = mustUpdateVersionArray.split(",");
			for(String mustVersion:versionArray){
				if(mustVersion.equals(currentVersionName)){
					return true;
				}
			}
			return false;
		}
	}
}
