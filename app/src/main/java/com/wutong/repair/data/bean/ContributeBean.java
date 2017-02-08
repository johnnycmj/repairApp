package com.wutong.repair.data.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 投稿bean
 * @author Jolly
 * 创建时间：2014年3月28日下午1:36:28
 *
 */
public class ContributeBean {
	@SerializedName(value="contributionId")
	private String id;
	@SerializedName(value="agreeCount")
	private String likeNumber;
	@SerializedName(value="contributionPId")
	private String parentId;
	@SerializedName(value="imageList")
	private List<PhotoBean> imageList;
	@SerializedName(value="realName")
	private String writterName;
	@SerializedName(value="agree")
	private boolean agree;
	@SerializedName(value="userId")
	private String writterId;
	@SerializedName(value="createDate")
	private String createTime;
	@SerializedName(value="content")
	private String content;
	@SerializedName(value="feedBackCount")
	private String receiveNumber;
	@SerializedName(value="title")
	private String title;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLikeNumber() {
		return likeNumber;
	}
	public void setLikeNumber(String likeNumber) {
		this.likeNumber = likeNumber;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public List<PhotoBean> getImageList() {
		return imageList;
	}
	public void setImageList(List<PhotoBean> imageList) {
		this.imageList = imageList;
	}
	public String getWritterName() {
		return writterName;
	}
	public void setWritterName(String writterName) {
		this.writterName = writterName;
	}
	public boolean isAgree() {
		return agree;
	}
	public void setAgree(boolean agree) {
		this.agree = agree;
	}
	public String getWritterId() {
		return writterId;
	}
	public void setWritterId(String writterId) {
		this.writterId = writterId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getReceiveNumber() {
		return receiveNumber;
	}
	public void setReceiveNumber(String receiveNumber) {
		this.receiveNumber = receiveNumber;
	}
	public PhotoBean getCoverImage(){
		if(this.imageList == null || this.imageList.isEmpty()){
			return null;
		}
		else{
			return imageList.get(0);
		}
	}
}
