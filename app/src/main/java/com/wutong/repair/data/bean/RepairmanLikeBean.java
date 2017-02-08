package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 劳动节赞
 * @author Jolly
 * 创建时间：2014年4月14日上午10:52:03
 *
 */
public class RepairmanLikeBean {

	@SerializedName(value="name")
	private String name;
	@SerializedName(value="campus")
	private String campusName;//校区
	@SerializedName(value="agreeCount")
	private Integer likeNumber;
	@SerializedName(value="otherRepairmanInfoId")
	private String id;
	@SerializedName(value="agree")
	private boolean isLike;//本人是否对他赞过
	@SerializedName(value="image")
	private String image;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCampusName() {
		return campusName;
	}
	public void setCampusName(String campusName) {
		this.campusName = campusName;
	}
	public Integer getLikeNumber() {
		return likeNumber;
	}
	public void setLikeNumber(Integer likeNumber) {
		this.likeNumber = likeNumber;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public boolean isLike() {
		return isLike;
	}
	public void setLike(boolean isLike) {
		this.isLike = isLike;
	}
	
	
}
