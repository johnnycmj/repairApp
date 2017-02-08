package com.wutong.repair.data.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OfficeMaterialBean  implements Serializable{
	@SerializedName(value="applyCount")
	private String applyNumber;//申领数量
	@SerializedName(value="grantCount")
	private String stockOutNumber;//实领数量
	
	@SerializedName(value="brand")
	private String brand;
	
	@SerializedName(value="materialName")
	private String name;
	@SerializedName(value="assetsName")
	private String assetsName;
	@SerializedName(value="materialNum")
	private String codeIndex;//编号
	
	@SerializedName(value="assetName")
	private String assetName;
	
	@SerializedName(value="specification")
	private String spec;
	@SerializedName(value="unit")
	private String unit;
	
	@SerializedName(value="officeMaterialId")
	private String id;
	
	@SerializedName(value="monthUsedCount")
	private String monthUsedNumber;
	
	@SerializedName(value="stockCount")
	private String stockNumber;
	@SerializedName(value="idleCount")
	private String idleCount;//fjcc 总库存量
	
	@SerializedName(value="deparmentUsedCount")
	private List<DivisionBean> divisionBeanList;
	
	
	@SerializedName(value="managerUseCount")
	private List<OfficeEmploymentBean> employmentBeanList;
	
	/**/
	private boolean isChecked;
	private Integer applyingNumber;//申请中的数量
	/**/
	
	public String getApplyNumber() {
		return applyNumber;
	}
	public String getIdleCount() {
		return idleCount;
	}
	public void setIdleCount(String idleCount) {
		this.idleCount = idleCount;
	}
	public String getMonthUsedNumber() {
		return monthUsedNumber;
	}
	public void setMonthUsedNumber(String monthUsedNumber) {
		this.monthUsedNumber = monthUsedNumber;
	}
	public void setApplyNumber(String applyNumber) {
		this.applyNumber = applyNumber;
	}
	public String getStockOutNumber() {
		return stockOutNumber;
	}
	public void setStockOutNumber(String stockOutNumber) {
		this.stockOutNumber = stockOutNumber;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public Integer getApplyingNumber() {
		return applyingNumber;
	}
	public void setApplyingNumber(Integer applyingNumber) {
		this.applyingNumber = applyingNumber;
	}
	public String getStockNumber() {
		return stockNumber;
	}
	public void setStockNumber(String stockNumber) {
		this.stockNumber = stockNumber;
	}
	public List<DivisionBean> getDivisionBeanList() {
		return divisionBeanList;
	}
	public void setDivisionBeanList(List<DivisionBean> divisionBeanList) {
		this.divisionBeanList = divisionBeanList;
	}
	public OfficeMaterialBean() {
		super();
		//初始化参数
		applyingNumber = 0;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OfficeMaterialBean other = (OfficeMaterialBean) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public String getCodeIndex() {
		return codeIndex;
	}
	public void setCodeIndex(String codeIndex) {
		this.codeIndex = codeIndex;
	}
	@Override
	public String toString() {
		return "OfficeMaterialBean [applyNumber=" + applyNumber
				+ ", stockOutNumber=" + stockOutNumber + ", brand=" + brand
				+ ", name=" + name + ", codeIndex=" + codeIndex
				+ ", assetName=" + assetName + ", spec=" + spec + ", unit="
				+ unit + ", id=" + id + ", monthUsedNumber=" + monthUsedNumber
				+ ", stockNumber=" + stockNumber + ", divisionBeanList="
				+ divisionBeanList + ", isChecked=" + isChecked
				+ ", applyingNumber=" + applyingNumber + "]";
	}
	public List<OfficeEmploymentBean> getEmploymentBeanList() {
		return employmentBeanList;
	}
	public void setEmploymentBeanList(List<OfficeEmploymentBean> employmentBeanList) {
		this.employmentBeanList = employmentBeanList;
	}
	public String getAssetsName() {
		return assetsName;
	}
	public void setAssetsName(String assetsName) {
		this.assetsName = assetsName;
	}
	
	

}
