package com.wutong.repair.data.bean;

public class HomeProgressBean {
	
	public final static int REPAIR_INFO = 1;
	public final static int COMPLAINT_INFO = 2;
	public final static int NOTICE_INFO = 3;
	public final static int HELP_FOUND = 4;//失物招领
	public final static int PUSH_MESSAGE = 5;//我的消息
	private String content;//内容
	private String messageId;//报修id，建议id，公告id
	private String indexMessageId;
	private int type;//报修1，建议2，公告3，失物招领4，我的消息5
	private String userId;
	private String createTime;//该动态信息生成的时间
	private String title;//标题或者报修大小项
	private String formType;
	private String repairStatus;//报修单状态
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getIndexMessageId() {
		return indexMessageId;
	}
	public void setIndexMessageId(String indexMessageId) {
		this.indexMessageId = indexMessageId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public String getRepairStatus() {
		return repairStatus;
	}
	public void setRepairStatus(String repairStatus) {
		this.repairStatus = repairStatus;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
	
	
	
}
