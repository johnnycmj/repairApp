package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class NoticeTypeBean {

	@SerializedName(value="noticeTypeCode")
	private String typeId;
	@SerializedName(value="noticeTypeName")
	private String typeName;
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
