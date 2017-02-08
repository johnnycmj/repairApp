package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;

public class LostOrFoundPlaceBean implements SingleSelectable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5020348221204635406L;
	@SerializedName(value="name")
	private String textName;
	@SerializedName(value="lostApartmentId")
	private String value;
	private String extra;
	@Override
	public String getTextName() {
		return textName;
	}
	@Override
	public void setTextName(String textName) {
		this.textName = textName;
	}
	@Override
	public String getValue() {
		return value;
	}
	@Override
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String getExtra() {
		return extra;
	}
	@Override
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public LostOrFoundPlaceBean(String textName, String value) {
		super();
		this.textName = textName;
		this.value = value;
	}
	public LostOrFoundPlaceBean(String textName, String value, String extra) {
		super();
		this.textName = textName;
		this.value = value;
		this.extra = extra;
	}
	public LostOrFoundPlaceBean() {
		super();
	}

}
