package com.wutong.repair.data.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 推送消息分类
 * @author Jolly
 * 创建时间：2014年5月6日下午3:27:55
 *
 */
public class PushCategoryBean {

	@SerializedName(value="categoryName")
	private String name;
	@SerializedName(value="categoryValues")
	private String value;
	@SerializedName(value="count")
	private Integer unReadNumber;//未读数
	@SerializedName(value="sysNoticeList")
	private List<PushMessageBean> messageList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public Integer getUnReadNumber() {
		return unReadNumber;
	}
	public void setUnReadNumber(Integer unReadNumber) {
		this.unReadNumber = unReadNumber;
	}
	public List<PushMessageBean> getMessageList() {
		return messageList;
	}
	public void setMessageList(List<PushMessageBean> messageList) {
		this.messageList = messageList;
	}
	
	public boolean hasNew(){
		return this.unReadNumber != null && this.unReadNumber.intValue() > 0?true:false;
	}
	
	public String lastContent(){
		if(this.messageList == null || this.messageList.isEmpty()){
			return null;
		}
		else{
			return this.messageList.get(0).getContent();
		}
	}
}
