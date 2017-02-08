package com.wutong.repair;


import java.util.HashMap;
import java.util.Map;
import com.wutong.repair.activity.LoginActivity;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.StatUtil;
import com.wutong.repairfjnu.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseActivity extends Activity {
	protected String mTitle;
	protected boolean isNeedLogin = true;
	protected String mStatPageName = "[父页]";

	
	protected RepairApplication application;
	protected Context mContext;
	protected Dialog mDialog;//数据处理进度暂停弹框
	private TextView progressMessageView;//进度框的文字控件
	private Map<String, Boolean> singleMap;
	private BroadcastReceiver receiver = new BroadcastReceiver() {   
		@Override   
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.wutong.crashkill")){
				finish();
			}
			else if(intent.getAction().equals("com.wutong.kill")){
				finish();
			}
			else if(intent.getAction().equals("com.wutong.logout")){
				if(!this.getClass().equals(LoginActivity.class)){
					finish();
				}
			}
		}   
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (RepairApplication) getApplication();
		mContext = this;
		singleMap = new HashMap<String, Boolean>();
		Logger.i("isNeedLogin"+ isNeedLogin +this.getClass().getName());
		if(isNeedLogin && (application.getLoginInfoBean() == null || !application.getLoginInfoBean().isLoginSuccess())){
			Intent intent = new Intent();
			intent.setClass(mContext, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			this.sendBroadcast(new Intent("com.wutong.logout"));
		}
		//ProgressDialog 效果
		View progressMessageContentView = LayoutInflater.from(mContext).inflate(R.layout.progress_dialog_view, null);
		progressMessageView = (TextView) progressMessageContentView.findViewById(R.id.progress_message);
		mDialog = new Dialog(mContext, R.style.progress_dialog);
		mDialog.setCancelable(false);
		mDialog.setContentView(progressMessageContentView);

//		CrashHandler crashHandler = CrashHandler.getInstance();  
//		crashHandler.init(this);
		IntentFilter filter = new IntentFilter();   
		filter.addAction("com.wutong.crashkill");
		filter.addAction("com.wutong.logout");
		filter.addAction("com.wutong.kill");
		this.registerReceiver(this.receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		StatUtil.onPause(this);
		StatUtil.onPageEnd(mContext, mStatPageName + this.getClass().getName().substring(26));
		Logger.i("activity:" +this.getClass().getName() +"onPageEnd:" +mStatPageName);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		StatUtil.onResume(this);
		StatUtil.onPageStart(this,  mStatPageName + this.getClass().getName().substring(26));
		Logger.i("activity:" +this.getClass().getName() +"onPageStart:" +mStatPageName);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		changeBackgroud();
		changeSkin();
	}

	public void setStatPageName(String statPageName) {
		this.mStatPageName = statPageName;
	}
	public void setStatPageName(Context context,int statPageNameId) {
		this.mStatPageName = context.getString(statPageNameId);
	}
	@Override
	public void finish() {
		Logger.i("this:" + getClass().getName() + "finish");
		super.finish();
	} 
	
	@Override
	protected void onDestroy() {
		Logger.i("this:" + getClass().getName() + "onDestroy");
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	
	protected void showProgressDialog(){
		showProgressDialog(getString(R.string.tips_for_loading));
		
	}
	
	protected void showProgressDialog(int resId){
		showProgressDialog(getString(resId));
		
	}
	protected void showProgressDialog(String message){
		if(!mDialog.isShowing()){
			progressMessageView.setText(message);
			mDialog.show();
		}
		
	}
	protected void showProgressDialog(String message,String key){
		synchronized (singleMap) {
			Boolean hasShow = singleMap.get(key);
			if(hasShow == null || !hasShow.booleanValue()){
				showProgressDialog(message);
				singleMap.put(key, true);
			}
		}
		
	}
	protected void hideProgressDialog(){
		if(mDialog.isShowing()){
			mDialog.dismiss();
		}
	}
	
	protected void changeBackgroud(){
		switch (application.getSkinType()) {
		case 1:
			View headTitlebarView = findViewById(R.id.head_titlebar_layout);
			if(headTitlebarView != null){
				headTitlebarView.setBackgroundResource(R.drawable.labor_title_bg);
			}
			break;

		default:
			break;
		}
	}
	
	protected void changeSkin(){
		
	}
}
