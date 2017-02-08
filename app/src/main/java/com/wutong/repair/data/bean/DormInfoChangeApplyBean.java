package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 宿舍调整申请
 * @author Jolly
 * 创建时间：2014年7月8日上午9:27:33
 *
 */
public class DormInfoChangeApplyBean {
	
	public final static String STATUS_WAITING_ACCEPT = "1";
	public final static String STATUS_HAS_ACCEPTED = "2";
	public final static String STATUS_REFUSED = "3";
	
	public final static String TYPE_CHANGE = "1";//调整为
	public final static String TYPE_OFFSET = "2";//更正为
	
	@SerializedName(value="userRoomApplyId")
	private String id;
	@SerializedName(value="oldHotelRoomName")
	private String applyFromDorm;//变更前的宿舍
	@SerializedName(value="hotelRoomName")
	private String applyToDorm;//变更后的宿舍
	@SerializedName(value="oldHotelFloorName")
	private String applyFromBuilding;//变更前的宿舍
	@SerializedName(value="hotelFloorName")
	private String applyToBuilding;//变更后的宿舍
	@SerializedName(value="oldHotelBedName")
	private String applyFromBed;
	@SerializedName(value="hotelBedName")
	private String applyToBed;
	@SerializedName(value="type")
	private String type;//申请类型
	@SerializedName(value="status")
	private String status;//申请状态
	@SerializedName(value="createDate")
	private String time;//申请时间
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getApplyFromDorm() {
		return applyFromDorm;
	}
	public void setApplyFromDorm(String applyFromDorm) {
		this.applyFromDorm = applyFromDorm;
	}
	public String getApplyToDorm() {
		return applyToDorm;
	}
	public void setApplyToDorm(String applyToDorm) {
		this.applyToDorm = applyToDorm;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getApplyFromBuilding() {
		return applyFromBuilding;
	}
	public void setApplyFromBuilding(String applyFromBuilding) {
		this.applyFromBuilding = applyFromBuilding;
	}
	public String getApplyToBuilding() {
		return applyToBuilding;
	}
	public void setApplyToBuilding(String applyToBuilding) {
		this.applyToBuilding = applyToBuilding;
	}
	public String getApplyFromBed() {
		return applyFromBed;
	}
	public void setApplyFromBed(String applyFromBed) {
		this.applyFromBed = applyFromBed;
	}
	public String getApplyToBed() {
		return applyToBed;
	}
	public void setApplyToBed(String applyToBed) {
		this.applyToBed = applyToBed;
	}
	
	
}
