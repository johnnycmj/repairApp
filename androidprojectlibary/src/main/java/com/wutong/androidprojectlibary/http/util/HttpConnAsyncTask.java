package com.wutong.androidprojectlibary.http.util;



import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;


import com.wutong.androidprojectlibary.http.bean.HttpConnFormBean;
import com.wutong.androidprojectlibary.http.exception.ServerConnetedFailedException;
import com.wutong.androidprojectlibary.log.util.Logger;
import com.wutong.androidprojectlibary.network.util.InternetConnUtil;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
/**
 * 
 * @author Jolly
 * 创建时间：2013-8-6下午7:52:17
 *
 */
public class HttpConnAsyncTask extends AsyncTask<HttpConnFormBean, Integer, Integer>{

	public static final int SUCCESS = 1;
	public static final int FAILED = 0;
	public static final int NETWORK_CONN_FAILED = -1;

	private Context mContext;
	private OnDealtListener mDealtListener;
	private String resultResponse;
	private Exception catchedException;
	private FinalHttp finalHttp;

	public HttpConnAsyncTask(Context mContext) {
		super();
		this.mContext = mContext;
		finalHttp = new FinalHttp();
	}
	@Override
	protected void onPreExecute() {
		if(!InternetConnUtil.isHaveInternet(mContext)){
			Toast.makeText(mContext, "无法连接网络，请打开wifi或者3g", Toast.LENGTH_LONG).show();
		}
		else{
			if(mDealtListener != null){
				mDealtListener.beforeDealt();
			}
			super.onPreExecute();
		}
	}
	@Override
	protected Integer doInBackground(HttpConnFormBean... params) {
		HttpConnFormBean httpConnFormBean = params[0];
		try {
			AjaxParams ajaxParams = new AjaxParams(httpConnFormBean.getParams());
			Logger.v(httpConnFormBean.toString());
			Object responseObj = finalHttp.postSync(httpConnFormBean.getUrl(), ajaxParams);
			if(responseObj != null){
				resultResponse = responseObj.toString();
				Logger.v("[response:]"+resultResponse);
				return SUCCESS;
			}
			else{
				throw new ServerConnetedFailedException("无法连接服务器");
			}
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

}
