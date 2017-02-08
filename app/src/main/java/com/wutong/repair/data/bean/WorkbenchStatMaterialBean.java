package com.wutong.repair.data.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;
/**
 * 用于维修材料的统计
 * 当前材料中为asset（没有s）
 * @author Jolly
 * 创建时间：2013年12月23日下午1:14:17
 *
 * 2014年01月11日 修改师大上全加s
 */
public class WorkbenchStatMaterialBean {

	@SerializedName(value="monthUsedCount")
	private Integer monthUsedNumber;
	
	@SerializedName(value="stockCount")
	private Integer storeNumber;
	
	@SerializedName(value="unit")
	private String unit;
	
	@SerializedName(value="assetsName")
	private String name;
	
	@SerializedName(value="brand")
	private String brand;
	
	@SerializedName(value="assetsId")
	private Integer id;
	
	private String specification;
	
	@SerializedName(value="deparmentUsedCount")
	private List<DivisionBean> divisionUseCountList;
	
	/*煤矿修改前的方案是，临时变量进行承接，get方法判断null时返回临时变量*/
	@SerializedName(value="assestId")
	private Integer tempId;
	@SerializedName(value="assetName")
	private String tempName;

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public Integer getMonthUsedNumber() {
		return monthUsedNumber;
	}

	public void setMonthUsedNumber(Integer monthUsedNumber) {
		this.monthUsedNumber = monthUsedNumber;
	}

	public Integer getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(Integer storeNumber) {
		this.storeNumber = storeNumber;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getName() {
		return name!=null?name:tempName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Integer getId() {
		return id!=null?id:tempId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<DivisionBean> getDivisionUseCountList() {
		return divisionUseCountList;
	}

	public void setDivisionUseCountList(List<DivisionBean> divisionUseCountList) {
		this.divisionUseCountList = divisionUseCountList;
	}

	public Integer getTempId() {
		return tempId;
	}

	public void setTempId(Integer tempId) {
		this.tempId = tempId;
	}

	public String getTempName() {
		return tempName;
	}

	public void setTempName(String tempName) {
		this.tempName = tempName;
	}
	
	
}
