package com.wutong.repair.data.bean;
/**
 * 分页查询
 * @author Jolly
 *
 */
public class ContributePagerBean{
	private String messageId;
	private String start;
	private String limit;
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public ContributePagerBean(int start, int limit) {
		super();
		this.messageId = "0";
		this.start = String.valueOf(start);
		this.limit = String.valueOf(limit);
	}
	public ContributePagerBean(String messageId, int start, int limit) {
		super();
		this.messageId = messageId;
		this.start = String.valueOf(start);
		this.limit = String.valueOf(limit);
	}

}
