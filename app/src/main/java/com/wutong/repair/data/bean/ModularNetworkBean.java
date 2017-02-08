package com.wutong.repair.data.bean;

public class ModularNetworkBean {
	
	private String modularName;
	private String url;
	private String modularId;
	public String getModularName() {
		return modularName;
	}
	public void setModularName(String modularName) {
		this.modularName = modularName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getModularId() {
		return modularId;
	}
	public void setModularId(String modularId) {
		this.modularId = modularId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModularNetworkBean other = (ModularNetworkBean) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ModularNetworkBean [modularName=" + modularName + ", url="
				+ url + ", modularId=" + modularId + "]";
	}

}
