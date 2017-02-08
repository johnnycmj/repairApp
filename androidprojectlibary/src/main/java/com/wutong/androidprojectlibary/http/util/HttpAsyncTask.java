package com.wutong.androidprojectlibary.http.util;



import org.apache.http.NameValuePair;

import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.network.util.InternetConnUtil;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
/**
 * 
 * @author Jolly
 * 创建时间：2013-7-12下午3:30:21
 *
 */
public class HttpAsyncTask extends AsyncTask<HttpFormBean, Integer, Integer>{

	public static final int SUCCESS = 1;
	public static final int FAILED = 0;

	private Context mContext;
	private OnDealtListener mDealtListener;
	private HttpClientManager httpClientManager;
	private String resultResponse;
	private Exception catchedException;

	public HttpAsyncTask(Context mContext,HttpClientManager httpClientManager) {
		super();
		this.mContext = mContext;
		this.httpClientManager = httpClientManager;
	}
	@Override
	protected void onPreExecute() {
		if(!InternetConnUtil.isHaveInternet(mContext)){
			Toast.makeText(mContext, "无法连接网络，请检查网络设置", Toast.LENGTH_LONG).show();
		}
		else{
			if(mDealtListener != null){
				mDealtListener.beforeDealt();
			}
			super.onPreExecute();
		}
	}
	@Override
	protected Integer doInBackground(HttpFormBean... params) {
		HttpFormBean httpFormBean = params[0];
		try {
			resultResponse = getCloudHttpClient().post(httpFormBean.getUrl(), httpFormBean.getParams().toArray(new NameValuePair[]{}));
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			catchedException = e;
			return FAILED;
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		switch (result.intValue()) {
		case SUCCESS:
			if(mDealtListener != null){
				mDealtListener.success(resultResponse);
			}
			break;
		case FAILED:
			if(mDealtListener != null){
				mDealtListener.failed(catchedException);
			}
			break;
		}

	}

	/**
	 * 处理前，处理后的监听器
	 * @author Jolly
	 *
	 */
	public interface OnDealtListener{
		public void beforeDealt();
		public void success(String resultResponse);
		public void failed(Exception exception);
	}

	public void setOnDealtListener(OnDealtListener dealtListener) {
		this.mDealtListener = dealtListener;
	}

	private CloudHttpClient getCloudHttpClient(){
		return httpClientManager.getCloudHttpClient();
	}
}
