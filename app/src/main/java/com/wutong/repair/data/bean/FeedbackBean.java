package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

public class FeedbackBean {

	
	private String id;
	@SerializedName(value="content")
	private String content;
	@SerializedName(value="realName")
	private String feedbackName;
	@SerializedName(value="creatTime")
	private String time;
	@SerializedName(value="type")
	private String contentType;
	@SerializedName(value="userId")
	private Integer feedbackUserId;//反馈人id
	@SerializedName(value="second")
	private Integer duration;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFeedbackName() {
		return feedbackName;
	}
	public void setFeedbackName(String feedbackName) {
		this.feedbackName = feedbackName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Integer getFeedbackUserId() {
		return feedbackUserId;
	}
	public void setFeedbackUserId(Integer feedbackUserId) {
		this.feedbackUserId = feedbackUserId;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
}
