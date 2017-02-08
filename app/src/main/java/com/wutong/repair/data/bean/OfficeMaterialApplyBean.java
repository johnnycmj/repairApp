package com.wutong.repair.data.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OfficeMaterialApplyBean {

	@SerializedName(value="applyNum")
	private String applyNum;//申请数量
	@SerializedName(value="remark")
	private String remark;//申请描述
	@SerializedName(value="applyMaterialList")
	private List<OfficeMaterialBean> officeMaterialBeanList;
	@SerializedName(value="officeMaterialApplyId")
	private String offciceMaterialApplyId;
	@SerializedName(value="confirmorId")
	private String confirmorId;//确认人id
	@SerializedName(value="outName")
	private String stockOutName;
	@SerializedName(value="confirmTime")
	private String confirmTime;
	@SerializedName(value="applyTime")
	private String applyTime;
	@SerializedName(value="applicantName")
	private String applicantName;
	@SerializedName(value="outId")
	private String stockOutId;
	@SerializedName(value="confirmorName")
	private String confirmorName;
	@SerializedName(value="statusName")
	private String statusName;
	@SerializedName(value="divisionName")

	private String divisionName;
	@SerializedName(value="departMentName")
	private String departmentName;
	@SerializedName(value="departmentId")
	private String departmentId;
	@SerializedName(value="applicantId")
	private String applicantId;
	@SerializedName(value="revocationId")
	private String cancelId;
	@SerializedName(value="revocationTime")
	private String cancelTime;
	@SerializedName(value="revocationName")
	private String cancelName;
	@SerializedName(value="outTime")
	private String stockOutTime;
	@SerializedName(value="status")
	private String status;
	public String getApplyNum() {
		return applyNum;
	}
	public void setApplyNum(String applyNum) {
		this.applyNum = applyNum;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<OfficeMaterialBean> getOfficeMaterialBeanList() {
		return officeMaterialBeanList;
	}
	public void setOfficeMaterialBeanList(
			List<OfficeMaterialBean> officeMaterialBeanList) {
		this.officeMaterialBeanList = officeMaterialBeanList;
	}
	public String getOffciceMaterialApplyId() {
		return offciceMaterialApplyId;
	}
	public void setOffciceMaterialApplyId(String offciceMaterialApplyId) {
		this.offciceMaterialApplyId = offciceMaterialApplyId;
	}
	public String getConfirmorId() {
		return confirmorId;
	}
	public void setConfirmorId(String confirmorId) {
		this.confirmorId = confirmorId;
	}
	public String getStockOutName() {
		return stockOutName;
	}
	public void setStockOutName(String stockOutName) {
		this.stockOutName = stockOutName;
	}
	public String getConfirmTime() {
		return confirmTime;
	}
	public void setConfirmTime(String confirmTime) {
		this.confirmTime = confirmTime;
	}
	public String getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}
	public String getStockOutId() {
		return stockOutId;
	}
	public void setStockOutId(String stockOutId) {
		this.stockOutId = stockOutId;
	}
	public String getConfirmorName() {
		return confirmorName;
	}
	public void setConfirmorName(String confirmorName) {
		this.confirmorName = confirmorName;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getDivisionName() {
		return divisionName;
	}
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getApplicantId() {
		return applicantId;
	}
	public void setApplicantId(String applicantId) {
		this.applicantId = applicantId;
	}
	public String getCancelId() {
		return cancelId;
	}
	public void setCancelId(String cancelId) {
		this.cancelId = cancelId;
	}
	public String getCancelTime() {
		return cancelTime;
	}
	public void setCancelTime(String cancelTime) {
		this.cancelTime = cancelTime;
	}
	public String getCancelName() {
		return cancelName;
	}
	public void setCancelName(String cancelName) {
		this.cancelName = cancelName;
	}
	public String getStockOutTime() {
		return stockOutTime;
	}
	public void setStockOutTime(String stockOutTime) {
		this.stockOutTime = stockOutTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	
}
