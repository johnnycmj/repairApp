package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;

public class BuildingBean implements SingleSelectable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName(value="buildingId")
	private String id;
	@SerializedName(value="buildingName")
	private String name;
	
	private String node;
	private String addr;
	private String floor;
	
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
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	@Override
	public String getTextName() {
		return this.name;
	}
	@Override
	public void setTextName(String textName) {
		
	}
	@Override
	public String getValue() {
		return this.id;
	}
	@Override
	public void setValue(String value) {
		
	}
	@Override
	public String getExtra() {
		return null;
	}
	@Override
	public void setExtra(String extra) {
		
	}
	public BuildingBean() {
		super();
	}
	public BuildingBean( String name,String id) {
		super();
		this.id = id;
		this.name = name;
	}
	

}
