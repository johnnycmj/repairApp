package com.wutong.repair.data.bean;

import com.google.gson.annotations.SerializedName;
/**
 * 抄表-楼信息
 * @author Jolly
 * 创建时间：2014年2月21日下午8:47:29
 *
 */
public class MeterBuildingBean {

	@SerializedName(value="floorName")
	private String name;
	@SerializedName(value="sdFloorId")
	private String id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
