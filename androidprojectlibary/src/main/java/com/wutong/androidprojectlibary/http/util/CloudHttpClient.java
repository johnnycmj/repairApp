package com.wutong.androidprojectlibary.http.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.wutong.androidprojectlibary.http.exception.HTTPResponseStatusException;
import com.wutong.androidprojectlibary.log.util.Logger;




public class CloudHttpClient {
	private final String CHARSET = HTTP.UTF_8;
	private HttpClient cloudHttpClient;
	private String response;
	private int CONNECTION_TIME_OUT = 10000;
	private int MAX_ROUTE_CONNECTIONS = 200;

	/**
	 * 获得默认处理好的httpClient对象
	 * @return
	 */
	public synchronized HttpClient getHttpClient() {
		if (null== cloudHttpClient) {
			HttpParams params =new BasicHttpParams();
			// 设置一些基本参数 
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params,CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams.setUserAgent(params,"Mozilla/5.0(Linux;U;Android 2.3.3;en-us;Nexus One Build.FRG83) "
					+"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// 超时设置
			/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(params, 10000);
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIME_OUT);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, 40000);
			

		//  MAX_ROUTE_CONNECTIONS为要设置的每个路由最大连接数

			ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS);

			ConnManagerParams.setMaxConnectionsPerRoute(params,connPerRoute);
			
			// 设置我们的HttpClient支持HTTP和HTTPS两种模式           
			SchemeRegistry schReg =new SchemeRegistry();            
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			// 使用线程安全的连接管理来创建HttpClient            
			ClientConnectionManager conMgr =new ThreadSafeClientConnManager(params, schReg); 
			cloudHttpClient =new DefaultHttpClient(conMgr, params);
		}
		return cloudHttpClient;    
	}
	
	/**
	 * 重置client为空
	 */
	private void resetHttpClient(){
		cloudHttpClient = null;
	}


	/**
	 * GET方式提交
	 * @param url
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String get(String url) throws ClientProtocolException, IOException{
		HttpClient client = getHttpClient();
		// 创建一个GET请求
		HttpGet request =new HttpGet(url);
		// 发送GET请求，并将响应内容转换成字符串
		response = client.execute(request, new BasicResponseHandler());    
		return response;
	}



	/**
	 * POST方式提交
	 * @param url
	 * @param params 可变参数
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String post(String url, NameValuePair... params) throws ClientProtocolException, IOException {

		// 编码参数
		List<NameValuePair> formparams =new ArrayList<NameValuePair>(); 
		// 请求参数
		for (NameValuePair p : params) {
			formparams.add(p);
			Logger.v("[Params name:" + p.getName()+",value:" +p.getValue()+"]");
		}
		UrlEncodedFormEntity entity =new UrlEncodedFormEntity(formparams,CHARSET);
		// 创建POST请求
		HttpPost request =new HttpPost(url);
		request.setEntity(entity);
		// 发送请求
		Logger.v("[URI:" + request.getURI()+"]");
		HttpClient client = getHttpClient();
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		if(statusCode != HttpStatus.SC_OK) {
			request.abort();
			resetHttpClient();
			throw new HTTPResponseStatusException("http请求返回状态：" + statusCode);
		}
		HttpEntity resEntity =  response.getEntity();
		String result = (resEntity ==null) ?null : EntityUtils.toString(resEntity, CHARSET);
		Logger.v("CloudHttpClient post return:" + result);
		return result;

	}
}

