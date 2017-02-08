package com.wutong.repair;

import java.util.HashMap;
import java.util.Map;
import com.wutong.repair.activity.LoginActivity;
import com.wutong.repair.fragment.MoreFragment;
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
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseFragmentActivity extends FragmentActivity {
	protected boolean isNeedLogin = true;
	protected String mStatPageName = "[父页(片段)]";
	protected RepairApplication application;
	protected Context mContext;
	protected Dialog mDialog;//数据处理进度暂停弹框
	private TextView progressMessageView;//进度框的文字控件
	protected Map<String, CallFragmentListener> callFragmentListenerMap;
	private Map<String, Boolean> singleMap;
	private BroadcastReceiver receiver = new BroadcastReceiver() {   
		@Override   
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.wutong.crashkill")){
				finish();
			}
			else if(intent.getAction().equals("com.wutong.logout")){
				if(!this.getClass().equals(LoginActivity.class)){
					finish();
				}
			}
		}   
	};
	
	/**
	 * 放入调用fragment方法的接口实例
	 * @param callName
	 * @param callFragmentListener
	 */
	public void putCallFragment(String callName,CallFragmentListener callFragmentListener){
		this.callFragmentListenerMap.put(callName, callFragmentListener);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (RepairApplication) getApplication();
		mContext = this;
		singleMap = new HashMap<String, Boolean>();
		if(isNeedLogin && (application.getLoginInfoBean() == null || !application.getLoginInfoBean().isLoginSuccess())){
			Intent intent = new Intent();
			intent.setClass(mContext, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			this.sendBroadcast(new Intent("com.wutong.logout"));
		}
		//
		callFragmentListenerMap = new HashMap<String, CallFragmentListener>();
		
		
		//ProgressDialog 效果
		View progressMessageContentView = LayoutInflater.from(mContext).inflate(R.layout.progress_dialog_view, null);
		progressMessageView = (TextView) progressMessageContentView.findViewById(R.id.progress_message);
		mDialog = new Dialog(mContext, R.style.progress_dialog);
		mDialog.setCancelable(false);
		mDialog.setContentView(progressMessageContentView);

		IntentFilter filter = new IntentFilter();   
		filter.addAction("com.wutong.crashkill");
		filter.addAction("com.wutong.logout");
		this.registerReceiver(this.receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		StatUtil.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		StatUtil.onResume(this);
	}
	@Override
	public void finish() {
		super.finish();
	} 
	
	public void setStatPageName(String statPageName) {
		this.mStatPageName = statPageName;
	}
	
	public void setStatPageName(Context context,int statPageNameId) {
		this.mStatPageName = context.getString(statPageNameId);
	}
	protected void showProgressDialog(String message){
		if(!mDialog.isShowing()){
			progressMessageView.setText(message);
			mDialog.show();
		}
		
	}
	protected void showProgressDialog(){
		showProgressDialog(getString(R.string.tips_for_loading));
		
	}
	
	protected void showProgressDialog(int resId){
		showProgressDialog(getString(resId));
		
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.i("requestCode:" +requestCode +",resultCode:"+resultCode);
		if (resultCode == Activity.RESULT_OK ) {//正确返回
			if(data != null){
				String callFunctionName = data.getStringExtra(getString(R.string.call_fragment_key_name));
				int callCode = data.getIntExtra(getString(R.string.call_fragment_key_code),0);
				CallFragmentListener callFragmentListener = callFragmentListenerMap.get(callFunctionName);
				if(callFragmentListener != null){
					callFragmentListener.onCall(callCode);
				}
				else{
					Logger.w("意外的call fragment:" +callFunctionName);
				}
			}
			else{
				Logger.i("onActivityResult  ok,but data is null");
			}
		}
		
		else{
			Logger.i("其他返回code");
		}
	}
	
	/**
	 * 调用Fragment方法的接口
	 * @author Jolly
	 * 创建时间：2013-10-18上午10:22:29
	 *
	 */
	public interface CallFragmentListener{
		public void onCall(int callCode);
	}

	@Override
	protected void onDestroy() {
		Logger.i("FragmentActivity->onDestroy");
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	@Override
	protected void onStart() {
		Logger.i("FragmentActivity->onStart");
		
		super.onStart();
		changeBackgroud();
		changeSkin();
	}
	@Override
	protected void onStop() {
		Logger.i("FragmentActivity->onStop");
		super.onStop();
	}
	@Override
	protected void onRestart() {
		Logger.i("FragmentActivity->onRestart");
		super.onRestart();
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
