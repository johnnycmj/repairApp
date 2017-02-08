package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 电表信息
 * @author Jolly
 * 创建时间：2014年2月22日上午10:08:26
 *
 */
public class ChildMeterBean {
	@SerializedName(value="meterNum")
	private String code;
	@SerializedName(value="meterName")
	private String name;
	@SerializedName(value="presentValue")
	private String recentlyValue;
	@SerializedName(value="checkDate")
	private String recentlyWriteDate;
	@SerializedName(value="sdMeterId")
	private String id;
	
	private String dataCurrentValue;//当前抄表数
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRecentlyValue() {
		return recentlyValue;
	}
	public void setRecentlyValue(String recentlyValue) {
		this.recentlyValue = recentlyValue;
	}
	public String getRecentlyWriteDate() {
		return recentlyWriteDate;
	}
	public void setRecentlyWriteDate(String recentlyWriteDate) {
		this.recentlyWriteDate = recentlyWriteDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDataCurrentValue() {
		return dataCurrentValue;
	}
	public void setDataCurrentValue(String dataCurrentValue) {
		this.dataCurrentValue = dataCurrentValue;
	}
	
	
}
