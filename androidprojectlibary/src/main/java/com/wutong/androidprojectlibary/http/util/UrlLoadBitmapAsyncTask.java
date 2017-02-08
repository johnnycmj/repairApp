package com.wutong.androidprojectlibary.http.util;




import java.net.URL;

import com.wutong.androidprojectlibary.http.bean.UrlBitmapFormBean;
import com.wutong.androidprojectlibary.log.util.Logger;
import com.wutong.androidprojectlibary.network.util.InternetConnUtil;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.widget.Toast;

public class UrlLoadBitmapAsyncTask extends AsyncTask<UrlBitmapFormBean, Integer, Integer>{

	public static final int SUCCESS = 1;
	public static final int EMPTY  = 0;
	public static final int FAILED = -1;

	private Options options;
	private int scale = 1;
	private int maxSize = 2048;
	
	private Context mContext;
	private OnUrlLoadBitmapDealtListener mDealtListener;
	private Bitmap resultResponse;
	private Exception catchedException;

	public UrlLoadBitmapAsyncTask(Context mContext) {
		super();
		this.mContext = mContext;
	}
	@Override
	protected void onPreExecute() {
		if(!InternetConnUtil.isHaveInternet(mContext)){
			Toast.makeText(mContext, "无法连接网络，请打开wifi或者3g", Toast.LENGTH_LONG).show();
		}
		else{
			super.onPreExecute();
			if(mDealtListener != null){
				mDealtListener.beforeDealt();
			}
		}
	}
	@Override
	protected Integer doInBackground(UrlBitmapFormBean... params) {
		UrlBitmapFormBean urlBitmapFormBean = params[0];
		try {
			URL url = new URL(urlBitmapFormBean.getUrl());
			options = new Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(url.openStream(),null,options);
			
			scale = 1;//scale=1表示不缩放
			if(urlBitmapFormBean.getMaxSize() != 0){
				maxSize = urlBitmapFormBean.getMaxSize();
			}
			else{
				maxSize = 2048;
			}
			if (options.outWidth > options.outHeight && options.outWidth > maxSize) {//如果宽度大的话根据宽度固定大小缩放
				scale = options.outWidth / maxSize;
			}
			else if (options.outWidth < options.outHeight && options.outHeight > maxSize) {//如果高度高的话根据高度固定大小缩放
				scale = options.outHeight / maxSize;
			}
			if (scale <= 0){
				scale = 1;
			}
			else if(urlBitmapFormBean.isScale() ||(options.outWidth /scale)> maxSize){
				scale++;
			}
			options.inSampleSize = scale;//设置缩放比例
			//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			options.inJustDecodeBounds = false;
			
			
			resultResponse = BitmapFactory.decodeStream(url.openStream(),null,options);
			Logger.v("options.outHeight:" + options.outHeight +"options.outWidth:"+options.outWidth);
			if(resultResponse != null){
				return SUCCESS;
			}
			else{
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	public interface OnUrlLoadBitmapDealtListener{
		public void beforeDealt();
		public void success(Bitmap resultResponse);
		public void failed(Exception exception);
	}

	public void setOnUrlLoadBitmapDealtListener(OnUrlLoadBitmapDealtListener dealtListener) {
		this.mDealtListener = dealtListener;
	}

}
