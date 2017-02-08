package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 推送信息 for sysy_notice
 * @author Jolly
 * 创建时间：2014年5月6日下午2:08:39
 *
 */
public class PushMessageBean {
	@SerializedName(value="sysNoticeId")
	private String id;
	@SerializedName(value="fromId")
	private String fromId;//推送来源
	@SerializedName(value="toId")
	private String toId;//目标
	@SerializedName(value="createDate")
	private String createTime;
	@SerializedName(value="title")
	private String title;
	@SerializedName(value="content")
	private String content;
	@SerializedName(value="module")
	private String modular;
	@SerializedName(value="status")
	private String status;//1为未读；2为已读

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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

	public String getModular() {
		return modular;
	}

	public void setModular(String modular) {
		this.modular = modular;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean isRead(){
		return this.status != null && this.status.equals("2")?true:false;
	}
}
