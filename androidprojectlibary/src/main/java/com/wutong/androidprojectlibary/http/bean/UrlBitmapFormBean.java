package com.wutong.androidprojectlibary.http.bean;


public class UrlBitmapFormBean {
	private String url;
	private boolean isScale;
	private int maxSize;//最大像素

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isScale() {
		return isScale;
	}

	public void setScale(boolean isScale) {
		this.isScale = isScale;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	

}
