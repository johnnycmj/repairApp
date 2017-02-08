package com.wutong.androidprojectlibary.http.util;

import net.tsz.afinal.FinalHttp;

public class HttpClientManager {
	
	private CloudHttpClient cloudHttpClient;
	private FinalHttp finalHttp;

	public CloudHttpClient getCloudHttpClient() {
		return cloudHttpClient;
	}


	public HttpClientManager(CloudHttpClient cloudHttpClient) {
		super();
		this.cloudHttpClient = cloudHttpClient;
		finalHttp = new FinalHttp();
	}


	public FinalHttp getFinalHttp() {
		return finalHttp;
	}
	

}
