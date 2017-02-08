package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class MicroActivityLoveYouBean {

	@SerializedName(value="welifeExpressId")
	private String id;
	@SerializedName(value="phone")
	private String phone;
	@SerializedName(value="createDate")
	private String createTime;
	@SerializedName(value="author")
	private String fromName;
	@SerializedName(value="agree")
	private boolean isLike;
	@SerializedName(value="agreeCount")
	private Integer likeNumber;
	@SerializedName(value="receiver")
	private String toName;
	@SerializedName(value="isBooking")
	private String isOrder;
	@SerializedName(value="content")
	private String content;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public boolean isLike() {
		return isLike;
	}
	public void setLike(boolean isLike) {
		this.isLike = isLike;
	}
	public Integer getLikeNumber() {
		return likeNumber;
	}
	public void setLikeNumber(Integer likeNumber) {
		this.likeNumber = likeNumber;
	}
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
	public String getIsOrder() {
		return isOrder;
	}
	public void setIsOrder(String isOrder) {
		this.isOrder = isOrder;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
