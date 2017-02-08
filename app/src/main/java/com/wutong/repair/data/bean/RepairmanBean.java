package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class RepairmanBean {

	@SerializedName(value="repairManName")
	private String name;
	@SerializedName(value="userId")
	private String id;
	@SerializedName(value="type")
	private String typeName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
