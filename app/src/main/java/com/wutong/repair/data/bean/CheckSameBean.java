package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class CheckSameBean {
	
	@SerializedName(value="itemPId")
	private String assetId;
	@SerializedName(value="itemId")
	private String troubleId;
	
	private String count;
	@SerializedName(value="hotelRoomId")
	private String dormId;
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
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getDormId() {
		return dormId;
	}
	public void setDormId(String dormId) {
		this.dormId = dormId;
	}
	

}
