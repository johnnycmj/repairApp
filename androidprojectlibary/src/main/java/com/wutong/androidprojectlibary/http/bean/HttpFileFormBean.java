package com.wutong.androidprojectlibary.http.bean;

import java.util.Map;


public class HttpFileFormBean {
	private String url;
	private Map<String,String> FileMap;//键为名称，值为本地路径
	private Map<String, String> params;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Map<String, String> getFileMap() {
		return FileMap;
	}
	public void setFileMap(Map<String, String> fileMap) {
		FileMap = fileMap;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	
	
}
