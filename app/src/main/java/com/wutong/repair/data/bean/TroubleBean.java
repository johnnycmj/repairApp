package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;

public class TroubleBean implements SingleSelectable{
	@SerializedName(value="itemId")
	private String id;
	@SerializedName(value="itemName")
	private String troubleName;

	public String getTroubleName() {
		return troubleName;
	}

	public void setTroubleName(String troubleName) {
		this.troubleName = troubleName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TroubleBean(String id, String troubleName) {
		super();
		this.id = id;
		this.troubleName = troubleName;
	}
	@Override
	public String getTextName() {
		return this.troubleName;
	}

	@Override
	public void setTextName(String textName) {
		this.troubleName = textName;
		
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
