package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class MicroShareBean {

	@SerializedName(value="createDate")
	private String fullDate;
	@SerializedName(value="monthYear")
	private String yearMonthOfDate;
	@SerializedName(value="day")
	private String dayOfDate;
	@SerializedName(value="path")
	private String pictureUrl;
	@SerializedName(value="content")
	private String content;
	@SerializedName(value="onePictureId")
	private String pictrueId;
	@SerializedName(value="agreeCount")
	private String likeNumber;
	public String getFullDate() {
		return fullDate;
	}
	public void setFullDate(String fullDate) {
		this.fullDate = fullDate;
	}
	public String getYearMonthOfDate() {
		return yearMonthOfDate;
	}
	public void setYearMonthOfDate(String yearMonthOfDate) {
		this.yearMonthOfDate = yearMonthOfDate;
	}
	public String getDayOfDate() {
		return dayOfDate;
	}
	public void setDayOfDate(String dayOfDate) {
		this.dayOfDate = dayOfDate;
	}
	public String getPictureUrl() {
		return pictureUrl;
	}
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPictrueId() {
		return pictrueId;
	}
	public void setPictrueId(String pictrueId) {
		this.pictrueId = pictrueId;
	}
	public String getLikeNumber() {
		return likeNumber;
	}
	public void setLikeNumber(String likeNumber) {
		this.likeNumber = likeNumber;
	}
}
