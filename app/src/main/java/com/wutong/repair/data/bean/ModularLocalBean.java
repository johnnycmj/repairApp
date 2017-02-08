package com.wutong.repair.data.bean;

import java.util.Map;

import android.graphics.drawable.Drawable;

public class ModularLocalBean implements Comparable<ModularLocalBean>{

	private String code;//模块编码
	private Drawable icon;
	private String name;
	private String action;
	private Class<?> cls;
	
	private Integer sortNumber;
	public Class<?> getCls() {
		return cls;
	}
	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
	private Map<String, String> bundle;
	
	private boolean enabled;
	private boolean callFunction;
	
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Map<String, String> getBundle() {
		return bundle;
	}
	public void setBundle(Map<String, String> bundle) {
		this.bundle = bundle;
	}
	public boolean isCallFunction() {
		return callFunction;
	}
	public void setCallFunction(boolean callFunction) {
		this.callFunction = callFunction;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getSortNumber() {
		return sortNumber;
	}
	public void setSortNumber(Integer sortNumber) {
		this.sortNumber = sortNumber;
	}
	@Override
	public int compareTo(ModularLocalBean another) {
		return this.sortNumber.compareTo(another.sortNumber);
	}
	
}
