package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class NoticeBean {
	@SerializedName(value="noticeId")
	private String id;
	@SerializedName(value="noticeTitle")
	private String title;
	@SerializedName(value="noticeContent")
	private String content;
	private String author;
	private String typeId;
	@SerializedName(value="noticeTypeName")
	private String typeName;
	private String date;
	private String isRead;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}

}
