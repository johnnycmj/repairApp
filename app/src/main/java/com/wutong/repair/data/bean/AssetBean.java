package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;

public class AssetBean implements SingleSelectable{
	@SerializedName(value="itemId")
	private String id;
	@SerializedName(value="itemName")
	private String assetName;

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AssetBean(String id, String assetName) {
		super();
		this.id = id;
		this.assetName = assetName;
	}

	@Override
	public String getTextName() {
		return this.assetName;
	}

	@Override
	public void setTextName(String textName) {
		this.assetName = textName;
		
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
