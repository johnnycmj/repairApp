package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 抄表记录
 * @author Jolly
 * 创建时间：2014年5月8日下午2:13:06
 *
 */
public class RoomMeterRecordBean {
	@SerializedName(value="realName")
	private String realName;//户主名字或者楼名称
	@SerializedName(value="sdRecordId")
	private String id;
	@SerializedName(value="meterId")
	private String meterId;
	@SerializedName(value="price")
	private String price;//单价
	@SerializedName(value="money")
	private String money;//金额
	@SerializedName(value="meterNum")
	private String meterNum;//水电表的编号
	@SerializedName(value="meterName")
	private String meterName;//水电表的名称
	@SerializedName(value="checkDate")
	private String checkDate;
	@SerializedName(value="householderNum")
	private String householderNum;//楼编号
	@SerializedName(value="realityValue")
	private String realityValue;//实际数
	@SerializedName(value="newValue")
	private String newValue;//本期数
	@SerializedName(value="oldValue")
	private String oldValue;//上期数
	@SerializedName(value="multiplier")
	private String multiplier;//倍数
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMeterId() {
		return meterId;
	}
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getMeterNum() {
		return meterNum;
	}
	public void setMeterNum(String meterNum) {
		this.meterNum = meterNum;
	}
	public String getMeterName() {
		return meterName;
	}
	public void setMeterName(String meterName) {
		this.meterName = meterName;
	}
	public String getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}
	public String getHouseholderNum() {
		return householderNum;
	}
	public void setHouseholderNum(String householderNum) {
		this.householderNum = householderNum;
	}
	public String getRealityValue() {
		return realityValue;
	}
	public void setRealityValue(String realityValue) {
		this.realityValue = realityValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(String multiplier) {
		this.multiplier = multiplier;
	}
	
	
	
}
