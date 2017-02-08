package com.wutong.repair.activity;


import java.io.Serializable;
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
import com.wutong.repair.data.bean.LoginInfoBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.UpdateBean;
import com.wutong.repair.util.ApplicationInfoUtil;
import com.wutong.repair.util.CipherUtil;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.MD5;
import com.wutong.repair.util.MobileInfoUtil;
import com.wutong.repair.util.SettingConfig;
import com.wutong.repair.util.StringUtil;





import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class LoginActivity extends BaseActivity {
	private final static int REQUEST_CODE_INTRODUCTION_GUIDE = 881;


	private String mUsername;
	private String mPassword;

	private EditText mUsernameView;
	private EditText mPasswordView;
	private ImageView userNameCleanView;
	private ImageView passwordCleanView; 
	private CheckBox mRemeberPasswordView;
	private CheckBox mAutoSignInView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private Button mLoginSubmitView;

	private TextView mUpdateMustTipsTv;

	private Dialog mAttentionDialog;//弹窗
	private View mAttentionContentView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isNeedLogin = false;
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		setStatPageName(mContext,R.string.title_activity_login);
		viewInit();
		checkUpdateMust();


		//		attentionInit();
	}

	@Override
	protected void onStart() {

		super.onStart();
		checkAutoSignIn();
	}


	private void viewInit(){
		mLoginSubmitView = (Button) findViewById(R.id.sign_in_button);
		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);
		mRemeberPasswordView = (CheckBox) findViewById(R.id.remeber_password);
		mAutoSignInView = (CheckBox) findViewById(R.id.auto_sign_in);
		userNameCleanView = (ImageView) findViewById(R.id.username_clean);
		passwordCleanView = (ImageView) findViewById(R.id.password_clean);

		mUpdateMustTipsTv = (TextView) findViewById(R.id.update_must_tips);

		mUpdateMustTipsTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkUpdateMust();
			}
		});

		mUsernameView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().trim().length() == 0){
					userNameCleanView.setVisibility(ViewGroup.GONE);
				}
				else if(mUsernameView.isFocused()){
					userNameCleanView.setVisibility(ViewGroup.VISIBLE);
				}

			}
		});
		mPasswordView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().trim().length() == 0){
					passwordCleanView.setVisibility(ViewGroup.GONE);
				}
				else if(mPasswordView.isFocused()){
					passwordCleanView.setVisibility(ViewGroup.VISIBLE);
				}

			}
		});

		mPasswordView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && mPasswordView.getText().toString().trim().length() >0){
					passwordCleanView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					passwordCleanView.setVisibility(ViewGroup.GONE);
				}

			}
		});

		mUsernameView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && mUsernameView.getText().toString().trim().length() >0){
					userNameCleanView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					userNameCleanView.setVisibility(ViewGroup.GONE);
				}

			}
		});

		userNameCleanView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mUsernameView.setText("");

			}
		});
		passwordCleanView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPasswordView.setText("");

			}
		});
		
		mRemeberPasswordView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			}
		});
		mAutoSignInView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					mRemeberPasswordView.setChecked(isChecked);
				}
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		mLoginSubmitView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mLoginSubmitView.setEnabled(false);
				attemptLogin();
			}
		});

		

	}



	/**
	 * 尝试登陆
	 * <pre>
	 * 如果用户名、密码非法，阻止登陆行为，否则进行登录
	 * </pre>
	 */
	public void attemptLogin() {



		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;

		if(TextUtils.isEmpty(mUsername)){
			ToastUtil.showToast(mContext, "用户名不能为空");
			mUsernameView.requestFocus();
			cancel = true;
		}

		else if (TextUtils.isEmpty(mPassword)) {
			ToastUtil.showToast(mContext, "密码不能为空");
			mPasswordView.requestFocus();
			cancel = true;
		} 
		else if (mPassword.toString().trim().length() < 4) {
			ToastUtil.showToast(mContext,R.string.error_invalid_password);
			//			mPasswordView.setError(StringUtil.errorHtml(getString(R.string.error_invalid_password)));
			mPasswordView.requestFocus();
			cancel = true;
		}

		if (cancel) {
			mLoginSubmitView.setEnabled(true);
		} else {
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			userLoginHttp(mUsername, mPassword);
		}
	}

	private void showProgress(final boolean show) {
		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	/**
	 * 登录
	 */
	private void userLoginHttp(String userName,String password){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getLoginUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair appFlagPair = new BasicNameValuePair("appFlag", application.getAppFlag());
		params.add(appFlagPair);

		NameValuePair versionCodePair = new BasicNameValuePair("code", String.valueOf(ApplicationInfoUtil.getVersionCode(mContext)));
		params.add(versionCodePair);

		NameValuePair usernamePair = new BasicNameValuePair("userName", userName);
		params.add(usernamePair);
		NameValuePair passwordPair = new BasicNameValuePair("password", MD5.getMD5Str(MD5.getMD5Str(password)));
		params.add(passwordPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean<List<LoginInfoBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<LoginInfoBean>>>(){}.getType());
				List<LoginInfoBean> loginInfoBeans = responseBean.getData();
				if(responseBean.isSuccess()){


					if(loginInfoBeans != null && !loginInfoBeans.isEmpty()){
						LoginInfoBean loginInfoBean = loginInfoBeans.get(0);
						if(loginInfoBean.isLoginSuccess()){
							//记录个人信息
							SharedPreferences preferences = getSharedPreferences(SettingConfig.LOGIN_CONFIG, MODE_PRIVATE);
							SharedPreferences preferencesInfo = application.getDefaultPreferences();
							String uid = loginInfoBean.getUserId().toString();
							Editor editorInfo = preferencesInfo.edit();
							String encodeUid = CipherUtil.Base64Cipher.encript(uid);
							Logger.i("encodeUid:" +encodeUid);
							editorInfo.putString(SettingConfig.PersonInfo.UID,encodeUid );
							editorInfo.commit();
							//
							application.setLoginInfoBean(mContext,loginInfoBean);
							//保存帐号密码
							Editor edit = preferences.edit();
							String username = mUsernameView.getText().toString();
							String password = mPasswordView.getText().toString();
							username = CipherUtil.Base64Cipher.encript(username);
							password = CipherUtil.Base64Cipher.encript(password);
							edit.putString(SettingConfig.LoginForm.USERNAME_TEXTVIEW, username);
							edit.putString(SettingConfig.LoginForm.PASSWORD_TEXTVIEW, password);
							edit.putBoolean(SettingConfig.LoginForm.AUTO_SIGN_IN,mAutoSignInView.isChecked());
							edit.putBoolean(SettingConfig.LoginForm.REMEBER_PASSWORD,mRemeberPasswordView.isChecked());
							edit.commit();
//							application.refreshJpushInfo();

							Intent intent = new Intent();
							intent.setClass(LoginActivity.this, IndexModularActivity.class);
							startActivity(intent);
							finish();

						}
						else{
							showProgress(false);
							ToastUtil.showToast(mContext, "登录失败"+loginInfoBean.getResultMsg());
						}
					}
					else{
						ToastUtil.showToast(mContext, "登录失败，数据异常[0]");
					}
					showProgress(false);
					mLoginSubmitView.setEnabled(true);
				}
				else{
					showProgress(false);
					ToastUtil.showToast(mContext, responseBean.getMessage());
					mLoginSubmitView.setEnabled(true);
				}
			}

			@Override
			public void failed(Exception exception) {
				showProgress(false);
				mLoginSubmitView.setEnabled(true);
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				showProgress(true);

			}
		});
		httpAsyncTask.execute(httpFormBean);
	}


	private void checkAutoSignIn(){
		SharedPreferences preferences = getSharedPreferences(SettingConfig.LOGIN_CONFIG, MODE_PRIVATE);
		boolean autoSignIn = preferences.getBoolean(SettingConfig.LoginForm.AUTO_SIGN_IN, false);
		boolean remeberPassword = preferences.getBoolean(SettingConfig.LoginForm.REMEBER_PASSWORD, false);
		if(autoSignIn){
			String username = preferences.getString(SettingConfig.LoginForm.USERNAME_TEXTVIEW, null);
			String password = preferences.getString(SettingConfig.LoginForm.PASSWORD_TEXTVIEW, null);
			if(username == null || password == null || username.toString().trim().length() == 0 || password.toString().trim().length() == 0){
				showToastError("读取配置出现问题，请重新输入帐户信息");
				mUsernameView.setText("");
				mPasswordView.setText("");
				mRemeberPasswordView.setChecked(false);
				mAutoSignInView.setChecked(false);
			}

			else{
				username = CipherUtil.Base64Cipher.decript(username);
				password = CipherUtil.Base64Cipher.decript(password);
				mUsernameView.setText(username);
				mPasswordView.setText(password);
				mRemeberPasswordView.setChecked(true);
				mAutoSignInView.setChecked(true);
				attemptLogin();
			}

		}
		else if(remeberPassword){
			String username = preferences.getString(SettingConfig.LoginForm.USERNAME_TEXTVIEW, null);
			String password = preferences.getString(SettingConfig.LoginForm.PASSWORD_TEXTVIEW, null);
			if(username == null  || password == null){
				mUsernameView.setText("");
				mPasswordView.setText("");
				mRemeberPasswordView.setChecked(false);
			}
			else{
				username = CipherUtil.Base64Cipher.decript(username);
				password = CipherUtil.Base64Cipher.decript(password);
				Logger.i("username:" + username +"|password:"+password);
				mUsernameView.setText(username);
				mPasswordView.setText(password);
				mRemeberPasswordView.setChecked(true);
			}
		}
	}


	/**
	 * 显示登陆错误提示
	 * @param text
	 */
	private void showToastError(String text){
		ToastUtil.showToast(mContext, text);
	}





	private void attentionInit(){
		mAttentionContentView = LayoutInflater.from(mContext).inflate(R.layout.attention_dialog_view, null);
		View attentionCloseView = mAttentionContentView.findViewById(R.id.attention_close); 
		attentionCloseView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mAttentionDialog.isShowing()){
					mAttentionDialog.dismiss();
				}

			}
		});

		mAttentionDialog = new Dialog(mContext, R.style.attention_dialog);
		mAttentionDialog.setCancelable(false);
		mAttentionDialog.setContentView(mAttentionContentView);
		Window attentionWindow = mAttentionDialog.getWindow();
		android.view.WindowManager.LayoutParams attentionContentLp = attentionWindow.getAttributes();
		attentionContentLp.width = MobileInfoUtil.getDisplayWidth(this) - 40;
		attentionContentLp.height = (int) (MobileInfoUtil.getDisplayHeight(this) * 2.0/3);

		attentionWindow.setAttributes(attentionContentLp);
		if(!mAttentionDialog.isShowing()){
			mAttentionDialog.show();
		}

	}

	/**
	 * 强制更新检查
	 */
	private void checkUpdateMust(){
		mLoginSubmitView.setEnabled(false);
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getCheckVersionUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair schoolTypePair = new BasicNameValuePair("schoolType", application.getAppFlag());
		params.add(schoolTypePair);
		NameValuePair platformPair = new BasicNameValuePair("platform", "1");
		params.add(platformPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<UpdateBean> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<UpdateBean>>(){}.getType());
				UpdateBean updateBean = responseBean.getData();
				if(updateBean == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				if(responseBean.isSuccess()){
					if(ApplicationInfoUtil.getVersionCode(mContext) < Integer.valueOf(updateBean.getVersionCode())){
						//有更新
						Intent intent = new Intent(mContext,UpdateDownloadDialogActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Bundle bundle = new Bundle();
						bundle.putSerializable("updateBean", (Serializable)updateBean);
						intent.putExtras(bundle);
						startActivity(intent);
					}
					else{
						//无更新
						//						ToastUtil.showToast(mContext, "已经是最新版本！");
						//引导页启动
						//introduction();
					}
					//
					if(updateBean.isMustUpdate(ApplicationInfoUtil.getVersionName(mContext))){

						mLoginSubmitView.setEnabled(false);
						mUpdateMustTipsTv.setVisibility(ViewGroup.VISIBLE);
					}
					else{
						mLoginSubmitView.setEnabled(true);
						mUpdateMustTipsTv.setVisibility(ViewGroup.GONE);
					}
				}
				else{
					ToastUtil.showToast(mContext, R.string.tips_for_update_must_load_data_error);
				}
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
				mUpdateMustTipsTv.setVisibility(ViewGroup.VISIBLE);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog(R.string.tips_for_update_must_loading);
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	private void introduction(){
		boolean isTipsShown = application.getDefaultPreferences().getBoolean(getString(R.string.guide_key_for_applicant_repair_and_person_info_change_introduction)+ApplicationInfoUtil.getVersionCode(mContext), false);
		if(!isTipsShown){
			Intent intent = new Intent(mContext,IntroductionGuideActivity.class);
			startActivityForResult(intent, REQUEST_CODE_INTRODUCTION_GUIDE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {
			case REQUEST_CODE_INTRODUCTION_GUIDE:
				SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
				defaultPreferences.edit().putBoolean(mContext.getString(R.string.guide_key_for_applicant_repair_and_person_info_change_introduction)+ApplicationInfoUtil.getVersionCode(mContext), true).commit();

				break;

			default:
				break;
			}
			break;
		case RESULT_CANCELED:
			switch (requestCode) {
			case REQUEST_CODE_INTRODUCTION_GUIDE:
				this.finish();
				break;

			default:
				break;
			}
			break;
		default:
			break;
		}
	}
}
