package com.wutong.repair.receiver;

import java.util.Calendar;

import cn.jpush.android.api.JPushInterface;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repairfjnu.R;
import com.wutong.repair.RepairApplication;
import com.wutong.repair.activity.HelpFoundDetailActivity;
import com.wutong.repair.activity.IndexModularActivity;
import com.wutong.repair.activity.NoticeDetailActivity;
import com.wutong.repair.activity.PushMessageListActivity;
import com.wutong.repair.activity.RepairOrderDetailActivity;
import com.wutong.repair.activity.WelcomeActivity;
import com.wutong.repair.data.bean.NotificationExtraBean;
import com.wutong.repair.dictionary.JPushMessageDict;
import com.wutong.repair.util.Action2IntegerUtil;
import com.wutong.repair.util.InternetConnUtil;
import com.wutong.repair.util.Logger;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;


public class RepairReceiver  extends BroadcastReceiver{
	private RepairApplication application;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(application == null){
			application = (RepairApplication) context.getApplicationContext();
		}
		Bundle bundle = intent.getExtras();
		Logger.i("onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		if(Action2IntegerUtil.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
			AlarmManager bootAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent bootIntent = new Intent(Action2IntegerUtil.ACTION_BOOT_COMPLETED_BROADCAST);
			PendingIntent bootPendIntent = PendingIntent.getBroadcast(context,  
					0, bootIntent, PendingIntent.FLAG_UPDATE_CURRENT); 
			long triggerAtTime =SystemClock.elapsedRealtime() + 10000 ;
			bootAlarmManager.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, bootPendIntent);
		}
		else if(Action2IntegerUtil.ACTION_CONNECTIVITY_CHANGE.equals(intent.getAction())
				||Action2IntegerUtil.ACTION_BOOT_COMPLETED_BROADCAST.equals(intent.getAction())){
			if(InternetConnUtil.isHaveInternet(context)){
				Logger.i(intent.getAction() +" JPushInterface.init");
				JPushInterface.init(context);
			}
			else{
				Logger.i(intent.getAction() +" JPushInterface.init not do");
			}
		}
		else if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Logger.i("接收Registration Id : " + regId);
			//send the Registration Id to your server...
		}else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())){
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Logger.i("接收UnRegistration Id : " + regId);
			//send the UnRegistration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Logger.i("接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			String contentType =  bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Logger.i("通知的额外数据："+ extra);
			NotificationExtraBean noticficationExtraBean = application.getGson().fromJson(extra, new TypeToken<NotificationExtraBean>(){}.getType());

			//content_type 1 个人信息推送（1包括宿舍报修、2公寓报修（室内）、3公共室外，4我捡到了，5推送消息），2全体信息推送：公告

			if(contentType.equals(JPushMessageDict.PERSONAL)){

				if(noticficationExtraBean.getFormType().equals("4")){
					Logger.i("contentType" + contentType  + "---messageForHelpFound");
					messageForHelpFound(context,bundle);
				}
				else if(noticficationExtraBean.getFormType().equals("5")){
					Logger.i("contentType" + contentType  + "---messageForPushMessage");
					messageForPushMessage(context,bundle);
				}
				else{
					//目前1、2、3都是
					Logger.i("contentType" + contentType  + "---messageForNotification");
					messageForRepairOrder(context,bundle);
				}
			}
			else if(contentType.equals(JPushMessageDict.ALL)){
				Logger.i("contentType" + contentType  + "---messageForNotice");
				if(noticficationExtraBean.getFormType().equals("6")){
					Logger.i("contentType" + contentType  + "---messageForPushMessage");
					//通知，点击后使用 url
					messageForLink(context,bundle);
				}
				else if(noticficationExtraBean.getFormType().equals("7")){
					if(application.hasPermission(context.getString(R.string.modular_url_micro_share))){
						Logger.i("has micro share");
						Logger.i("contentType" + contentType  + "---messageForMicroShare");
						//微分享通知
						messageForMicroShare(context,bundle);
					}
				}
				else{
					messageForNotice(context,bundle);
				}
			}
			else{
				//可作快速测试，上线时去掉
				
				Logger.i("contentType" + contentType  + "---messageFor    none");
				ToastUtil.showToast(context, "推送信息类型未知:");
			}
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			Logger.i("接收到推送下来的通知");
			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Logger.i( "接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Logger.i("用户点击打开了通知");


		}
		else {

		}



	}



	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		if(bundle != null){
			StringBuilder sb = new StringBuilder();
			for (String key : bundle.keySet()) {
				if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
					sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
				} else {
					sb.append("\nkey:" + key + ", value:" + bundle.get(key).toString());
				}
			}
			return sb.toString();
		}
		else{
			return null;
		}
	}

	private void messageForRepairOrder(Context context,Bundle bundle){
		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
		String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		boolean hasShoud = hasSound();
		Logger.i("声音：" +hasShoud);
		String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Logger.i("通知的额外数据："+ extra);
		NotificationExtraBean notificationExtraBean = ((RepairApplication)context.getApplicationContext()).getGson().fromJson(extra, new TypeToken<NotificationExtraBean>(){}.getType());
		if(notificationExtraBean == null || notificationExtraBean.getMessageId() ==null ||notificationExtraBean.getMessageId().toString().trim().length() == 0){
			ToastUtil.showToast(context, "报修单信息有误，无法进入");
			showNotifcation(context, title, content, hasShoud, WelcomeActivity.class,null);
		}
		else{
			Bundle data = new Bundle();
			data.putString("repairOrderId", notificationExtraBean.getMessageId());
			data.putString("repairType", notificationExtraBean.getFormType());
			data.putBoolean("isFromIndex", true);
			showNotifcation(context, title, content, hasShoud, RepairOrderDetailActivity.class,data);
		}
	}

	private void showNotifcation(Context context,String title,String content,boolean hasSound, Class<?> cls,Bundle bundle){
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);               
//		Notification notification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
//		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent i = new Intent(context, cls);
		if(bundle != null){
			i.putExtras(bundle);
		}
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
		//PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(context,R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.setLatestEventInfo(context,title, content,contentIntent);

		Notification notification = new Notification.Builder(context)
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

	private void showNotifcationForLink(Context context,String title,String content,boolean hasSound,Bundle bundle){
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);               
//		Notification notification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
//		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent i = new Intent();        
		i.setAction("android.intent.action.VIEW");    
		String url =  bundle.getString("url");
		Uri content_url = Uri.parse(url);   
		i.setData(content_url); 
		if(bundle != null){
			i.putExtras(bundle);
		}
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
		//PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(context,R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.setLatestEventInfo(context,title, content,contentIntent);

		Notification notification = new Notification.Builder(context)
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

	private boolean hasSound(){
		//继续
		String[] weekdayArray = application.getQueitWeekdayArray();//0~8,0为"",1~7为星期天~星期六
		Logger.i("weekdayArray:"+weekdayArray[0]+weekdayArray[1]
				+weekdayArray[2]+weekdayArray[3]
						+weekdayArray[4]+weekdayArray[5]
								+weekdayArray[6]+weekdayArray[7]);
		Calendar calendar = Calendar.getInstance();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);//1~7为星期天~星期六
		Logger.i("dayOfWeek:" +dayOfWeek);
		if(weekdayArray[dayOfWeek].equals("1")){
			//当天免打扰
			return false;
		}
		//该天不为免打扰，继续
		//继续判断时间

		//继续
		int startTime = application.getQueitStartTime();
		int continuedTime = application.getQueitContinuedTime();
		Logger.i("startTime:"+startTime);
		Logger.i("continuedTime:"+continuedTime);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		Logger.i("hour："+hour);
		if(continuedTime == 0){
			Logger.i("持续时间为0，免打扰时间为：无");
			return true;
		}
		if(continuedTime == 24){
			Logger.i("持续时间为24，免打扰时间为：全天");
			return false;
		}
		if(startTime + continuedTime >= 24){
			if((hour >startTime && hour <= 24)
					||(hour<=startTime+continuedTime-24)){
				Logger.i("(hour >startTime && hour <= 24)||(hour<=startTime+continuedTime-24))");
				return false;
			}
			else{
				return true;
			}
		}
		else{
			if(hour>=startTime && hour <=startTime+continuedTime){
				Logger.i("hour>=startTime && hour <=startTime+continuedTime");
				return false;
			}
			else{
				return true;
			}
		}

	}
	/**
	 * 自定义公告
	 * @param context
	 * @param bundle
	 */
	private void messageForNotice(Context context,Bundle bundle){
		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
		String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		boolean hasShoud = hasSound();
		Logger.i("声音：" +hasShoud);
		String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Logger.i("通知的额外数据："+ extra);
		NotificationExtraBean noticficationExtraBean = application.getGson().fromJson(extra, new TypeToken<NotificationExtraBean>(){}.getType());
		String userId = application.getLoginInfoBean().getUserId().toString();
		if(noticficationExtraBean == null || noticficationExtraBean.getMessageId() ==null ||noticficationExtraBean.getMessageId().toString().trim().length() == 0){
			ToastUtil.showToast(context, "公告信息有误，无法进入");
			showNotifcation(context, title, content, hasShoud, WelcomeActivity.class,null);
		}
		else{
			if(userId != null && !userId.equals("0")){
				Bundle data = new Bundle();
				data.putString("noticeId", noticficationExtraBean.getMessageId());
				showNotifcation(context, title, content, hasShoud, NoticeDetailActivity.class,data);
			}
			else{
				ToastUtil.showToast(context, "用户信息有误，无法进入");
			}
		}
	}
	
	/**
	 *	微分享
	 * @param context
	 * @param bundle
	 */
	private void messageForMicroShare(Context context,Bundle bundle){
		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
		String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		boolean hasShoud = hasSound();
		Logger.i("声音：" +hasShoud);
		String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Logger.i("通知的额外数据："+ extra);
		NotificationExtraBean noticficationExtraBean = application.getGson().fromJson(extra, new TypeToken<NotificationExtraBean>(){}.getType());
		String userId = application.getLoginInfoBean().getUserId().toString();
		if(noticficationExtraBean == null || noticficationExtraBean.getMessageId() ==null ||noticficationExtraBean.getMessageId().toString().trim().length() == 0){
			ToastUtil.showToast(context, "微分享推送信息有误，无法进入");
			showNotifcation(context, title, content, hasShoud, WelcomeActivity.class,null);
		}
		else{
			if(userId != null && !userId.equals("0")){
				Bundle data = new Bundle();
				data.putInt("switcherViewId", R.id.switcher_index_micro_share);
				showNotifcation(context, title, content, hasShoud, IndexModularActivity.class,data);
			}
			else{
				ToastUtil.showToast(context, "用户信息有误，无法进入");
			}
		}
	}

	/**
	 * 自定义招领
	 * @param context
	 * @param bundle
	 */
	private void messageForHelpFound(Context context,Bundle bundle){
		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
		String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		boolean hasShoud = hasSound();
		Logger.i("声音：" +hasShoud);
		String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Logger.i("通知的额外数据："+ extra);
		NotificationExtraBean noticficationExtraBean = application.getGson().fromJson(extra, new TypeToken<NotificationExtraBean>(){}.getType());
		if(noticficationExtraBean == null || noticficationExtraBean.getMessageId() ==null ||noticficationExtraBean.getMessageId().toString().trim().length() == 0){
			ToastUtil.showToast(context, "招领信息有误，无法进入");
			showNotifcation(context, title, content, hasShoud, WelcomeActivity.class,null);
		}
		else{
			Bundle data = new Bundle();
			data.putString("contributeId", noticficationExtraBean.getMessageId());
			showNotifcation(context, title, content, hasShoud, HelpFoundDetailActivity.class,data);
		}
	}

	/**
	 * 自定义系统推送
	 * @param context
	 * @param bundle
	 */
	private void messageForPushMessage(Context context,Bundle bundle){
		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
		String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		boolean hasShoud = hasSound();
		Logger.i("声音：" +hasShoud);
		String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Logger.i("通知的额外数据："+ extra);
		NotificationExtraBean noticficationExtraBean = application.getGson().fromJson(extra, new TypeToken<NotificationExtraBean>(){}.getType());
		if(noticficationExtraBean == null || noticficationExtraBean.getMessageId() ==null ||noticficationExtraBean.getMessageId().toString().trim().length() == 0){
			ToastUtil.showToast(context, "系统推送信息有误，无法进入");
			showNotifcation(context, title, content, hasShoud, WelcomeActivity.class,null);
		}
		else{
			Bundle data = new Bundle();
			data.putString("contributeId", noticficationExtraBean.getMessageId());
			data.putString("title","微生活");
			data.putString("modularValue","1" );
			showNotifcation(context, title, content, hasShoud, PushMessageListActivity.class,data);
		}
	}

	/**
	 * 点击打开连接
	 * @param context
	 * @param bundle
	 */
	private void messageForLink(Context context,Bundle bundle){
		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
		String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		boolean hasShoud = hasSound();
		Logger.i("声音：" +hasShoud);
		String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Logger.i("通知的额外数据："+ extra);
		NotificationExtraBean noticficationExtraBean = application.getGson().fromJson(extra, new TypeToken<NotificationExtraBean>(){}.getType());
		if(noticficationExtraBean == null || noticficationExtraBean.getMessageId() ==null ||noticficationExtraBean.getMessageId().toString().trim().length() == 0){
			ToastUtil.showToast(context, "系统推送信息有误，无法进入");
			showNotifcation(context, title, content, hasShoud, WelcomeActivity.class,null);
		}
		else{
			Bundle data = new Bundle();
			data.putString("url",noticficationExtraBean.getUrl());
			showNotifcationForLink(context, title, content, hasShoud,data);
		}
	}
}
