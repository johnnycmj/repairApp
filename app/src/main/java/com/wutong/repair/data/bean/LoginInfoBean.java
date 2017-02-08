package com.wutong.repair.data.bean;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * 登录成功保存的信息
 * @author Jolly
 *
 */
public class LoginInfoBean {
	
	@SerializedName(value="isLogin")
	private boolean isLoginSuccess;
	
	private Integer userId;
	private String realName;
	private String userName;
	@SerializedName(value="returnMsg")
	private String resultMsg;
	
	private String phone;//电话
	private String userType;
	
	@SerializedName(value="hotelRoomName")
	private String dormRoomName;//宿舍名称
	@SerializedName(value="hotelRoomId")
	private String dormRoomId;//宿舍id
	
	private Map<String, List<FunctionBean>> permission;
	@SerializedName(value="modularName")
	private List<ModularNetworkBean> modulars;
	
	private String divisionId;//科室id
	private String divisionName;//科室名称
	@SerializedName(value="departMentName")
	private String departmentName;//fjcc部门
	
	public boolean isLoginSuccess() {
		return isLoginSuccess;
	}
	public void setLoginSuccess(boolean isLoginSuccess) {
		this.isLoginSuccess = isLoginSuccess;
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public Map<String, List<FunctionBean>> getPermission() {
		return permission;
	}
	public void setPermission(Map<String, List<FunctionBean>> permission) {
		this.permission = permission;
	}
	public List<ModularNetworkBean> getModulars() {
		return modulars;
	}
	public void setModulars(List<ModularNetworkBean> modulars) {
		this.modulars = modulars;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDormRoomName() {
		return dormRoomName;
	}
	public void setDormRoomName(String dormRoomName) {
		this.dormRoomName = dormRoomName;
	}
	public String getDormRoomId() {
		return dormRoomId;
	}
	public void setDormRoomId(String dormRoomId) {
		this.dormRoomId = dormRoomId;
	}
	public String getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}
	public String getDivisionName() {
		return divisionName;
	}
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}
	
	public String liteInfo() {
		return "LiteInfo [realName=" + realName + ", userName=" + userName
				+ ", phone=" + phone + ", dormRoomName=" + dormRoomName
				+ ", divisionName=" + divisionName + "]";
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	

}
