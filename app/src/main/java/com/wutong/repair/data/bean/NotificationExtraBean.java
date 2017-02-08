package com.wutong.repair.data.bean;

public class NotificationExtraBean {
	
	private String messageId;
	private String formType;//（报修类型CONTENT_TYPE 1：1宿舍报修，2公共室内，3公共室外,4我捡到了，5推送消息），(公告类型CONTENT_TYPE 2：通知公告,formType:6.打开连接,7微分享)
	private String url;
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
