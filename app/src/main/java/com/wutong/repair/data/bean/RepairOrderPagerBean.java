package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class RepairOrderPagerBean {
	
	private String id;//宿舍id||学生id||维修工id
	private String formType;
	@SerializedName(value="group")
	private String groupCode;
	private String start;
	private String limit;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public RepairOrderPagerBean(String id,  String formType,
			String groupCode, int start, int limit) {
		super();
		this.id = id;
		this.formType = formType;
		this.groupCode = groupCode;
		this.start = String.valueOf(start);
		this.limit = String.valueOf(limit);
	}

}
