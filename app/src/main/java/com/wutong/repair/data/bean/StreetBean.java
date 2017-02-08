package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;

public class StreetBean implements SingleSelectable{
	@SerializedName(value="buildingId")
	private String id;
	@SerializedName(value="buildingName")
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
	@Override
	public String getTextName() {
		return this.name;
	}
	@Override
	public void setTextName(String textName) {
		this.name = textName;
	}
	@Override
	public String getValue() {
		return this.id;
	}
	@Override
	public void setValue(String value) {
		this.id = value;
	}
	@Override
	public String getExtra() {
		return null;
	}
	@Override
	public void setExtra(String extra) {
		
	}

}
