package com.wutong.repair.data.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;
/**
 * 办公用品方面的人员bean
 * @author Jolly
 * 创建时间：2014年2月25日下午2:34:53
 *
 */
public class OfficeEmploymentBean {
	@SerializedName(value="realName")
	private String name;
	@SerializedName(value="userId")
	private String id;
	
	@SerializedName(value="useCount")
	private String monthAppliedNumber;//使用数量
	
	@SerializedName(value="materialUseCount")
	private List<OfficeMaterialBean> usedMaterialList;
	@SerializedName(value="assetUseCount")
	private List<OfficeMaterialBean> appliedMaterialList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMonthAppliedNumber() {
		return monthAppliedNumber;
	}
	public void setMonthAppliedNumber(String monthAppliedNumber) {
		this.monthAppliedNumber = monthAppliedNumber;
	}
	public List<OfficeMaterialBean> getUsedMaterialList() {
		return usedMaterialList;
	}
	public void setUsedMaterialList(List<OfficeMaterialBean> usedMaterialList) {
		this.usedMaterialList = usedMaterialList;
	}
	public List<OfficeMaterialBean> getAppliedMaterialList() {
		return appliedMaterialList;
	}
	public void setAppliedMaterialList(List<OfficeMaterialBean> appliedMaterialList) {
		this.appliedMaterialList = appliedMaterialList;
	}

}
