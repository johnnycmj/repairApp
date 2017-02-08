package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Jolly
 * 创建时间：2014年7月8日下午2:47:27
 *
 */
public class DormChangeBuildingBean {

	@SerializedName(value="hotelFloorId")
	private String id;
	@SerializedName(value="name")
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
