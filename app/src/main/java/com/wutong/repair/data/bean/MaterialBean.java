package com.wutong.repair.data.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


/**
 * 用于维修材料
 * 当前材料中为assets（带有s）
 * @author Jolly
 * 创建时间：2013年12月23日下午1:09:16
 *
 *2014年01月20日下午3:46:16师大的报修单下使用材料情况的数量改成放在count上，有歧义。暂不修改，等用上了，或煤矿上服务端统一了再修改
 */
@SuppressWarnings("serial")
public class MaterialBean implements Serializable{

	@SerializedName(value="assetsId")
	private String id;
	@SerializedName(value="assetsName")
	private String materialName;
	@SerializedName(value="count")
	private Integer totalNumber;
	
	private String brand;
	
	private String specification;
	
	private String unit;
	
	private String type;
	
	
	private Boolean checked;
	@SerializedName(value="usedCount")
	private Integer selectedNumber;
	private String usedNumber;
	
	private Integer imcrementNumber;
	
	private Integer currentPlusedNumber;//当前增加的数量
	private Integer  currentRecoveryNumber;//当前回收的数量
	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public Integer getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}

	public Integer getSelectedNumber() {
		return selectedNumber;
	}

	public void setSelectedNumber(Integer selectedNumber) {
		this.selectedNumber = selectedNumber;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public MaterialBean() {
		super();
		this.totalNumber = 0;
		this.selectedNumber = 0;
		this.imcrementNumber = 0;
		this.currentPlusedNumber = 0 ;
		this.currentRecoveryNumber = 0;
		this.checked = false;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaterialBean other = (MaterialBean) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsedNumber() {
		return usedNumber;
	}

	public void setUsedNumber(String usedNumber) {
		this.usedNumber = usedNumber;
	}

	public Integer getImcrementNumber() {
		return imcrementNumber;
	}

	public void setImcrementNumber(Integer imcrementNumber) {
		this.imcrementNumber = imcrementNumber;
	}

	public Integer getCurrentPlusedNumber() {
		return currentPlusedNumber;
	}

	public void setCurrentPlusedNumber(Integer currentPlusedNumber) {
		this.currentPlusedNumber = currentPlusedNumber;
	}

	public Integer getCurrentRecoveryNumber() {
		return currentRecoveryNumber;
	}

	public void setCurrentRecoveryNumber(Integer currentRecoveryNumber) {
		this.currentRecoveryNumber = currentRecoveryNumber;
	}

	
}
