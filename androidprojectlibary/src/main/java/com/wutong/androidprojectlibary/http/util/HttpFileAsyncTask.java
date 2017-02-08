package com.wutong.androidprojectlibary.http.util;




import java.io.File;
import java.util.Map;
import java.util.Set;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;

import com.wutong.androidprojectlibary.http.bean.FormFile;
import com.wutong.androidprojectlibary.http.bean.HttpFileFormBean;
import com.wutong.androidprojectlibary.log.util.Logger;
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
public class HttpFileAsyncTask extends AsyncTask<HttpFileFormBean, Integer, Integer>{

	private final String HTTP_FILE_NAME = "httpfileName";
	public static final int SUCCESS = 1;
	public static final int FAILED = 0;

	private Context mContext;
	private OnDealtListener mDealtListener;
	private OnProgressRefresh mProgressRefresh;
	private OnResponseAnaly mResponseAnaly;
	private String resultResponse;
	private Exception catchedException;
	private FinalHttp finalHttp;

	public HttpFileAsyncTask(Context mContext) {
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
	protected void onProgressUpdate(Integer... values) {
		if(mProgressRefresh != null){
			mProgressRefresh.refreshProgress(values[0]);
		}
		super.onProgressUpdate(values);
	}
	@Override
	protected Integer doInBackground(HttpFileFormBean... params) {
		HttpFileFormBean httpFileFormBean = params[0];
		try {
			Map<String,String> fileMap = httpFileFormBean.getFileMap();
			int fileIndex = 0;
			Set<String> keySet = fileMap.keySet();
			for(String key:keySet){
				String path = fileMap.get(key);
				AjaxParams ajaxParams = new AjaxParams(httpFileFormBean.getParams());
				ajaxParams.put(HTTP_FILE_NAME , new File(path));
				resultResponse = finalHttp.postSync(httpFileFormBean.getUrl(), ajaxParams).toString();
				Logger.v("response:"+resultResponse);
				if(mResponseAnaly != null){
					if(mResponseAnaly.responseAnaly(resultResponse)){
						this.publishProgress(fileIndex);
					}
					else{
						this.publishProgress(-1);
					}
					fileIndex++;
				}
				else{
					if(resultResponse.indexOf("true")!=-1){//默认的解析方式
						this.publishProgress(fileIndex);
					}
					else{
						this.publishProgress(-1);
					}
					fileIndex++;
				}
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			catchedException = e;
			return FAILED;
		}
	}
	
	

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}
	@Override
	protected void onPostExecute(Integer result) {
		switch (result.intValue()) {
		case SUCCESS:
			if(mDealtListener != null){
				mDealtListener.success(resultResponse+"");
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
	
	public interface OnProgressRefresh{
		public void refreshProgress(int index);
	}
	
	public interface OnResponseAnaly{
		public boolean responseAnaly(String responseData);
	}

	public void setOnResponseAnaly(OnResponseAnaly mResponseAnaly) {
		this.mResponseAnaly = mResponseAnaly;
	}
	public void setProgressRefresh(OnProgressRefresh mProgressRefresh) {
		this.mProgressRefresh = mProgressRefresh;
	}
	public void setOnDealtListener(OnDealtListener dealtListener) {
		this.mDealtListener = dealtListener;
	}

}
