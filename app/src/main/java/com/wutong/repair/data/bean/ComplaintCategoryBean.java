package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class ComplaintCategoryBean {
	@SerializedName(value="typeName")
	private String typeName;
	@SerializedName(value="typeCode")
	private String typeValue;
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getTypeValue() {
		return typeValue;
	}
	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}
}
