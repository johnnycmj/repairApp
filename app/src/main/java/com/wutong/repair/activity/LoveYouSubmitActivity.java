package com.wutong.repair.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.http.AjaxParams;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFileFormBean;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.http.util.HttpFileAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpFileAsyncTask.OnProgressRefresh;
import com.wutong.androidprojectlibary.validate.util.StringValidateUtil;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.fragment.MicroActivityListFragment;
import com.wutong.repair.util.BitmapUtil;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.CommonSwitcherUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoveYouSubmitActivity extends BaseActivity {
	
	
	private ImageView titlebarbackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;
	
	private EditText mFromNameView;
	private EditText mToNameView;
	private EditText mContentView;
	private EditText mPhoneView;
	private CheckBox mIsOrderRoseView;
	
	private TextView mContentLengthIndicatorView;
	
	private final int MAX_LENGTH_FOR_CONTENT = 520;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_love_you_submit);
		commonInit();
		intentInit();
		titlebarInit();
		viewInit();
		setupData();
	}

	private void commonInit(){
		mTitle = getString(R.string.title_activity_love_you_submit);
	}
	

	private void intentInit(){

	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(mTitle);
		titlebarbackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarbackView.setVisibility(ViewGroup.VISIBLE);
		titlebarbackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoveYouSubmitActivity.this.onBackPressed();

			}
		});

		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				submit();
			}
		});
	}

	private void viewInit(){
		/////
		mFromNameView = (EditText) findViewById(R.id.love_you_from_name);
		mToNameView = (EditText) findViewById(R.id.love_you_to_name);
		mContentView = (EditText) findViewById(R.id.love_you_content);
		mPhoneView = (EditText) findViewById(R.id.love_you_phone);
		mIsOrderRoseView = (CheckBox) findViewById(R.id.love_you_rose_is_order);
		mContentLengthIndicatorView = (TextView) findViewById(R.id.love_you_content_lengh_indicator);
		mContentView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				mContentLengthIndicatorView.setText(getString(R.string.format_length_indicator_for_love_you_submit_content,s.length()));
				if(s.length() > MAX_LENGTH_FOR_CONTENT){
					mContentLengthIndicatorView.setTextColor(getResources().getColor(R.color.love_you_content_indicator_out_of_range));
				}
				else{
					mContentLengthIndicatorView.setTextColor(getResources().getColor(R.color.love_you_content_indicator_normal));
				}
			}
		});
		
	}

	private void setupData(){
		mFromNameView.setText(application.getLoginInfoBean().getRealName());
		mFromNameView.setSelection(mFromNameView.getText().toString().length());
		mPhoneView.setText(application.getLoginInfoBean().getPhone());
	}
	private void submit(){
		if( mFromNameView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "我的名字不能为空哦~");
			return;
		}
		if( mToNameView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "TA的名字不能为空哦~");
			return;
		}
		if( mContentView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "您还没有填写真心话哦~");
			return;
		}
		else if(mContentView.getText().toString().length() > MAX_LENGTH_FOR_CONTENT){
			ToastUtil.showToast(mContext, "真心话不要太多哦~");
			return;
		}
		if(mIsOrderRoseView.isChecked()){
			if(mPhoneView.getText().toString().trim().length() == 0){
				ToastUtil.showToast(mContext, "请填写您的联系方式~");
				return;
			}
			else if(!StringValidateUtil.isPhoneNumber(mPhoneView.getText().toString())){
				ToastUtil.showToast(mContext, "请填写正确的手机号码~");
				return;
			}
		}
		
		submitAsyncHttp();
	}
	
	private void submitAsyncHttp(){
		String url = getString(R.string.http_url_load_submit_love_you,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("author", mFromNameView.getText().toString());
		params.put("receiver", mToNameView.getText().toString());
		params.put("isBooking", mIsOrderRoseView.isChecked()?"1":"0");
		params.put("phone", mIsOrderRoseView.isChecked()?mPhoneView.getText().toString():"");
		params.put("content", mContentView.getText().toString());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				hideProgressDialog();
				com.wutong.androidprojectlibary.http.util.HttpResponseUtil.justToast(errorNo, t, mContext);
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void onStart() {
				super.onStart();
				showProgressDialog();
				titlebarSubmitView.setEnabled(false);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				hideProgressDialog();
				CommonOperateUtil.hideIME(LoveYouSubmitActivity.this);
				ResponseBean<String> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<String>>(){}.getType());
				if(responseBean.isSuccess()){
					submitSuccess();
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}
				titlebarSubmitView.setEnabled(true);
				

			}

		});


	}
	
	private void submitSuccess(){
		ToastUtil.showToast(mContext, "告白成功！");
		Intent intent =new Intent();
		intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_love_you_list_fragment_name));
		intent.putExtra(getString(R.string.call_fragment_key_code), MicroActivityListFragment.MICRO_ACTIVITY_SUBMIT_REQUEST_CODE);//新投稿刷新未受理
		setResult(RESULT_OK,intent);
		LoveYouSubmitActivity.this.finish();
	}

	
}
