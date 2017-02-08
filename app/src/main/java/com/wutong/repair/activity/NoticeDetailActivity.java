package com.wutong.repair.activity;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.NoticeBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.ResponseOldBean;
import com.wutong.repair.dictionary.NoticeTypeDict;
import com.wutong.repair.fragment.NoticeListFragment;
import com.wutong.repair.util.InternetConnUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.HttpResponseUtil;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


public class NoticeDetailActivity extends BaseActivity {
	
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	
	private TextView mTitleView;
	private TextView mContentView;
	private TextView mAuthorView;
	private TextView mTypeNameView;
	private TextView mNoticeDateView;
	
	private String noticeId;
	private boolean isRead;
	private NoticeBean noticeBean;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isNeedLogin = false;
		super.onCreate(savedInstanceState);
		setContentView(selectedLayout());
		setStatPageName(mContext, R.string.title_activity_notice_deial);
		intentInit();
		titleBarInit();
		viewInit();
	}

	private void titleBarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setVisibility(ViewGroup.VISIBLE);
		titlebarTitleView.setText(R.string.title_activity_notice_deial);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NoticeDetailActivity.this.onBackPressed();

			}
		});
	}
	
	private void viewInit(){
		mTitleView = (TextView) findViewById(R.id.notice_title);
		mContentView = (TextView) findViewById(R.id.notice_content);
		mAuthorView = (TextView) findViewById(R.id.notice_author);
		mNoticeDateView = (TextView) findViewById(R.id.notice_date);
		mTypeNameView = (TextView) findViewById(R.id.notice_type_name);
	}

	private void intentInit(){
		noticeId = getIntent().getStringExtra("noticeId");
		isRead = getIntent().getBooleanExtra("isRead",false);
		Logger.i("isRead:"+isRead);
	}
	private void setupData(){
		loadNoticeDetailAsyncHttp();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupData();
	}
	
	
	@Override
	public void onBackPressed() {
		Intent intent =new Intent();
		intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_notice_list_name));
		intent.putExtra(getString(R.string.call_fragment_key_code), NoticeListFragment.CALL_CODE_REFRESH);//公告刷新
		setResult(RESULT_OK,intent);
		super.onBackPressed();
	}

	/**
	 * 通知详细
	 */
	private void loadNoticeDetailAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getNoticeDetailUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair noticeIdPair = new BasicNameValuePair("noticeId", noticeId);
		params.add(noticeIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {
			
			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<NoticeBean> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<NoticeBean>>(){}.getType());
				if(responseBean.isSuccess()){
					NoticeBean resultBean = responseBean.getData();
					if(resultBean != null ){
						mTitleView.setText(resultBean.getTitle());
						//Fix分支
							mContentView.setText(resultBean.getContent());
						mAuthorView.setText(resultBean.getAuthor());
						mNoticeDateView.setText(resultBean.getDate());
						mTypeNameView.setText(resultBean.getTypeName());
//						//判断类型。上色
//						if(resultBean.getTypeId().equals(NoticeTypeDict.URGENCY_NOTICE)){
//							mTitleView.setTextColor(getResources().getColor(R.color.notice_type_urgency));
//						}
//						else if(resultBean.getTypeId().equals(NoticeTypeDict.NORMAL_NOTICE)){
//							mTitleView.setTextColor(getResources().getColor(R.color.notice_type_normal));
//						}
//						else{
//							mTitleView.setTextColor(getResources().getColor(R.color.notice_type_other));
//						}
						//设置已读
						if(!isRead){
							sendNoticeIsReadAsyncHttp();
						}
					}
					else{
						ToastUtil.showToast(mContext, "返回公告数据有错");
					}
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}
			}
			
			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
			}
			
			@Override
			public void beforeDealt() {
				showProgressDialog();
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	
	private void sendNoticeIsReadAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getNoticeIsReadSendUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair noticeIdPair = new BasicNameValuePair("noticeId", noticeId);
		params.add(noticeIdPair);
		final String userId = application.getLoginInfoBean().getUserId().toString();
		NameValuePair userIdPair = new BasicNameValuePair("userId", userId);
		params.add(userIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {
			
			@Override
			public void success(String resultResponse) {
				// TODO Auto-generated method stub
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					Logger.i("已读发送成功,noticeId:" +noticeId +"userId:"+userId);
				}
				else{
					Logger.w("已读发送失败,noticeId:" +noticeId +"userId:"+userId);
				}
			}
			
			@Override
			public void failed(Exception exception) {
				
			}
			
			@Override
			public void beforeDealt() {
				
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	
	
	private int selectedLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_notice_detail;

		default:
			return R.layout.activity_notice_detail;
		}
	}
}
