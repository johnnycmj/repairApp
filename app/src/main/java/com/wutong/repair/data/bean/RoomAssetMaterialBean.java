package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class RoomAssetMaterialBean {
	
	@SerializedName(value="assetsId")
	private String assetsId;
	@SerializedName(value="assetsName")
	private String assetsName;
	
	@SerializedName(value="brand")
	private String brand;
	@SerializedName(value="specification")
	private String spec;
	@SerializedName(value="remark")
	private String remark;
	@SerializedName(value="unit")
	private String unit;
	@SerializedName(value="count")
	private String grantNumber;
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getGrantNumber() {
		return grantNumber;
	}
	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}
	public String getAssetsId() {
		return assetsId;
	}
	public void setAssetsId(String assetsId) {
		this.assetsId = assetsId;
	}
	public String getAssetsName() {
		return assetsName;
	}
	public void setAssetsName(String assetsName) {
		this.assetsName = assetsName;
	}
	
	
}
