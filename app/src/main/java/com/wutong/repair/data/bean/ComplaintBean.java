package com.wutong.repair.data.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * PC留言板Message Board
 * @author Jolly
 *
 */
@SuppressWarnings("serial")
public class ComplaintBean implements Serializable{
	@SerializedName(value="messageId")
	private String id;
	@SerializedName(value="title")
	private String title;
	@SerializedName(value="content")
	private String content;
	@SerializedName(value="count")
	private String receiveNumber;
	@SerializedName(value="createName")
	private String writter;
	@SerializedName(value="createDate")
	private String createTime;
	@SerializedName(value="messagePId")
	private String pid;
	@SerializedName(value="createUserId")
	private String writterUid;
	@SerializedName(value="typeName")
	private String categoryName;
	@SerializedName(value="dormitoryName")
	private String dormitoryName;//宿舍名称
	@SerializedName(value="repairManNameList")
	private List<RepairmanBean> repairmanList;//该报修人所在宿舍的维修人列表
	@SerializedName(value="phone")
	private String phone;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getReceiveNumber() {
		return receiveNumber;
	}
	public void setReceiveNumber(String receiveNumber) {
		this.receiveNumber = receiveNumber;
	}
	public String getWritter() {
		return writter;
	}
	public void setWritter(String writter) {
		this.writter = writter;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getWritterUid() {
		return writterUid;
	}
	public void setWritterUid(String writterUid) {
		this.writterUid = writterUid;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getDormitoryName() {
		return dormitoryName;
	}
	public void setDormitoryName(String dormitoryName) {
		this.dormitoryName = dormitoryName;
	}
	public List<RepairmanBean> getRepairmanList() {
		return repairmanList;
	}
	public void setRepairmanList(List<RepairmanBean> repairmanList) {
		this.repairmanList = repairmanList;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
