package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class DormChangeRoomBean {
	@SerializedName(value="hotelRoomId")
	private String id;
	@SerializedName(value="name")
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
