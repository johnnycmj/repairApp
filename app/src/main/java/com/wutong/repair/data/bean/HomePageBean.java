package com.wutong.repair.data.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class HomePageBean {

	@SerializedName(value="messageList")
	private List<HomeProgressBean> homeProgressBeanList;
	@SerializedName(value="imageList")
	private List<PhotoBean> picturePerDayList;
	@SerializedName(value="contributionList")
	private List<ContributeBean> contributeList;
	@SerializedName(value="count")
	private Integer newMsgNumber;//新推送消息
	
	public List<HomeProgressBean> getHomeProgressBeanList() {
		return homeProgressBeanList;
	}
	public void setHomeProgressBeanList(List<HomeProgressBean> homeProgressBeanList) {
		this.homeProgressBeanList = homeProgressBeanList;
	}
	public List<PhotoBean> getPicturePerDayList() {
		return picturePerDayList;
	}
	public void setPicturePerDayList(List<PhotoBean> picturePerDayList) {
		this.picturePerDayList = picturePerDayList;
	}
	public List<ContributeBean> getContributeList() {
		return contributeList;
	}
	public void setContributeList(List<ContributeBean> contributeList) {
		this.contributeList = contributeList;
	}
	public Integer getNewMsgNumber() {
		return newMsgNumber;
	}
	public void setNewMsgNumber(Integer newMsgNumber) {
		this.newMsgNumber = newMsgNumber;
	}
	
	public boolean isPushHasNew(){
		return this.newMsgNumber != null && newMsgNumber > 0?true:false;
	}
}
