package com.wutong.repair.receiver;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.repairfjnu.R;
import com.wutong.repair.RepairApplication;
import com.wutong.repair.activity.UpdateDownloadDialogActivity;
import com.wutong.repair.connection.ThreadHandler;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.UpdateBean;
import com.wutong.repair.util.Action2IntegerUtil;
import com.wutong.repair.util.ApplicationInfoUtil;
import com.wutong.repair.util.InternetConnUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.TimeUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;


public class CheckUpdateReceiver extends BroadcastReceiver{
	private RepairApplication application;
	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(mContext == null){
			mContext = context;
		}
		if(application == null){
			application = (RepairApplication) context.getApplicationContext();
		}
		
		if(intent.getAction().equals(Action2IntegerUtil.ACTION_CHECK_UPDATE_CHECK)){
			if(!InternetConnUtil.isHaveInternet(mContext)){
//				ToastUtil.showToast(mContext, "无法连接网络，请打开wifi或者3g");
				Logger.w("检查更新检测状态：" +"网络已断开"+ TimeUtil.currentTime());
			}
			else{
				UpdateThreadHandler handler = new UpdateThreadHandler();
				new Thread(handler).start();
			}
		}
		else if(intent.getAction().equals(Action2IntegerUtil.ACTION_CHECK_UPDATE_UPDATE)){
			Intent intentEnter = new Intent(mContext,UpdateDownloadDialogActivity.class);
			intentEnter.putExtras(intent.getExtras());
			intentEnter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intentEnter);
		}
	}


	private class UpdateThreadHandler extends ThreadHandler{
		private UpdateBean updateBean;
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				long lastCheckUpdateTime = new Date().getTime();
				application.getDefaultPreferences().edit().putLong(SettingConfig.UpdateInfo.LAST_UPDATE_TIME,lastCheckUpdateTime).commit();
				Logger.i("lastCheckUpdateTime updated:" +lastCheckUpdateTime);
				if(ApplicationInfoUtil.getVersionCode(mContext) < Integer.valueOf(updateBean.getVersionCode())){
					//有更新
					Bundle bundle = new Bundle();
					bundle.putSerializable("updateBean", (Serializable)updateBean);
					showNotifcation(mContext,mContext.getString(R.string.app_name)+"有新版本，点击查看","版本：" +updateBean.getVersionName(),false,UpdateDownloadDialogActivity.class,bundle);
				}
				else{
					//无更新
				}
				break;
			case ZERO:
				Logger.w("检查更新失败，获取数据异常");
				break;
			case CONN_THROW_EXCEPTION:
				Bundle bundle = msg.getData();
				Exception exception = (Exception) bundle.getSerializable("exception");
				HttpResponseUtil.justToast(exception, mContext);
				break;
			
			}
		}
		
		@Override
		public void run() {
			HttpFormBean httpFormBean = new HttpFormBean();
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getCheckVersionUrl());
			Collection<NameValuePair> params = new ArrayList<NameValuePair>();
			NameValuePair schoolTypePair = new BasicNameValuePair("schoolType",application.getAppFlag());
			params.add(schoolTypePair);
			NameValuePair platformPair = new BasicNameValuePair("platform", "1");
			params.add(platformPair);
			httpFormBean.setParams(params);
			try {
				String resultResponse = application.getHttpClientManager().getCloudHttpClient().post(httpFormBean.getUrl(), httpFormBean.getParams().toArray(new NameValuePair[]{}));
				ResponseBean<UpdateBean> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<UpdateBean>>(){}.getType());
				updateBean = responseBean.getData();
				if(updateBean.isEmpty()){
					this.sendEmptyMessage(ZERO);
				}
				else{
					this.sendEmptyMessage(SUCCESS);
				}
			} catch (Exception e) {
				this.dealWithIOException(e);
			}
		}
		
	}
	
	private void showNotifcation(Context context,String title,String content,boolean hasSound, Class<?> cls,Bundle bundle){
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);               
//		Notification notification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
		Intent i = new Intent(context, cls);
		if(bundle != null){
			i.putExtras(bundle);
		}
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
		//PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(context,R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.setLatestEventInfo(context,title, content,contentIntent);

		Notification notification = new Notification.Builder(mContext)
				.setContentTitle(title)
				.setContentText(content)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(contentIntent)
				.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// 添加声音提示 
		if(hasSound){
			notification.defaults=Notification.DEFAULT_ALL;
			// audioStreamType的值必须AudioManager中的值，代表着响铃的模式  
			notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER; 
		}
		else{
			notification.defaults=Notification.DEFAULT_LIGHTS;
		}

		notificationManager.notify(R.string.app_name, notification);
	}
}
