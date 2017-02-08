package com.wutong.repair.data.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class HelpFoundBean  implements Serializable{
	@SerializedName(value="realName")
	private String writterName;//失主名或者招领人姓名
	@SerializedName(value="userId")
	private String writterId;//id
	@SerializedName(value="itemType")
	private String itemTypeCode;
	@SerializedName(value="itemTypeName")
	private String itemTypeName;
	@SerializedName(value="type")
	private String typeCode;
	@SerializedName(value="typeName")
	private String typeName;
	@SerializedName(value="imageList")
	private List<PhotoBean> imageList;
	@SerializedName(value="lostFoundId")
	private String id;
	@SerializedName(value="hotelFloor")
	private String placeName;
	@SerializedName(value="apartmentId")
	private String placeId;
	@SerializedName(value="address")
	private String address;
	@SerializedName(value="createDate")
	private String createTime;
	@SerializedName(value="finishDate")
	private String finishTime;
	@SerializedName(value="findDate")
	private String foundTime;
	@SerializedName(value="content")
	private String content;
	@SerializedName(value="detail")
	private String contact;
	@SerializedName(value="isOpenCollect")
	private String isPublicContact;
	private String phone;
	private String status;
	public String getWritterName() {
		return writterName;
	}
	public void setWritterName(String writterName) {
		this.writterName = writterName;
	}
	public String getWritterId() {
		return writterId;
	}
	public void setWritterId(String writterId) {
		this.writterId = writterId;
	}
	public String getItemTypeCode() {
		return itemTypeCode;
	}
	public void setItemTypeCode(String itemTypeCode) {
		this.itemTypeCode = itemTypeCode;
	}
	public String getItemTypeName() {
		return itemTypeName;
	}
	public void setItemTypeName(String itemTypeName) {
		this.itemTypeName = itemTypeName;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public List<PhotoBean> getImageList() {
		return imageList;
	}
	public void setImageList(List<PhotoBean> imageList) {
		this.imageList = imageList;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public String getPlaceId() {
		return placeId;
	}
	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getFoundTime() {
		return foundTime;
	}
	public void setFoundTime(String foundTime) {
		this.foundTime = foundTime;
	}
	public String getIsPublicContact() {
		return isPublicContact;
	}
	public void setIsPublicContact(String isPublicContact) {
		this.isPublicContact = isPublicContact;
	}
	public boolean isPublicContact() {
		return isPublicContact!= null&&isPublicContact.equals("1")?true:false;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public PhotoBean getCoverImage(){
		if(this.imageList == null || this.imageList.isEmpty()){
			return null;
		}
		else{
			return imageList.get(0);
		}
	}
}
