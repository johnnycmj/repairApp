package com.wutong.repair.data.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class PhotoBean implements Serializable{
	
	private String url;
	@SerializedName(value="pictureId")
	private String photoId;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPhotoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

}
