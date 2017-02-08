package com.wutong.repair.data.bean;

import java.util.List;

public class RepairOrderGroupBean {
	
	private String groupName;
	private String childrenStatus;
	private List<RepairOrderBean> children; 
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getChildrenStatus() {
		return childrenStatus;
	}
	public void setChildrenStatus(String childrenStatus) {
		this.childrenStatus = childrenStatus;
	}
	public List<RepairOrderBean> getChildren() {
		return children;
	}
	public void setChildren(List<RepairOrderBean> children) {
		this.children = children;
	}
	

}
