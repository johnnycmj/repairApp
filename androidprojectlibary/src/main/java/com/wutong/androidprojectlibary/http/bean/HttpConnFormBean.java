package com.wutong.androidprojectlibary.http.bean;

import java.util.Map;
import java.util.Set;



public class HttpConnFormBean {
	private String url;
	private Map<String, String> params;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[http conn url="+url+" ]\n");
		Set<String> keySet = params.keySet();
		for(String key:keySet){
			stringBuilder.append("[Params name="+key+",value="+params.get(key)+"]\n");
		}
		return stringBuilder.toString();
	}
	
}
