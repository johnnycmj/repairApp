package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;



import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseFragment;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.MD5;
import com.wutong.repair.util.StringUtil;
import com.wutong.repairfjnu.R;

public class ChangePasswordFragment extends BaseFragment {

	private ImageView titlebarBackView;
	private ImageView titlebarSubmitView;
	private TextView titlebarTitleView;

	private EditText oldInputView;
	private EditText newInputView;
	private EditText confirmInputView;

	private ImageView oldCleanView;
	private ImageView newCleanView;
	private ImageView confirmCleanView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_change_password);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_password_change, container,false);
		setFragmentView(fragmentView);
		//
		titlebarInit();
		viewInit();
		return fragmentView;
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_fragment_change_password);
		titlebarTitleView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		boolean isTitlebarBackShow = this.getActivity().getIntent().getBooleanExtra("titlebar_back_is_show", false);
		if(isTitlebarBackShow){
			titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			titlebarBackView.setVisibility(ViewGroup.GONE);
		}
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChangePasswordFragment.this.getActivity().onBackPressed();

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
		oldInputView = (EditText) findViewById(R.id.password_old);
		newInputView = (EditText) findViewById(R.id.password_new);
		confirmInputView = (EditText) findViewById(R.id.password_confirm);

		oldCleanView = (ImageView) findViewById(R.id.edit_clean_old);
		newCleanView = (ImageView) findViewById(R.id.edit_clean_new);
		confirmCleanView = (ImageView) findViewById(R.id.edit_clean_confirm);

		oldInputView.addTextChangedListener(new TextWatcher() {

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
					oldCleanView.setVisibility(ViewGroup.GONE);
				}
				else if(oldInputView.isFocused()){
					oldCleanView.setVisibility(ViewGroup.VISIBLE);
				}

			}
		});

		newInputView.addTextChangedListener(new TextWatcher() {

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
					newCleanView.setVisibility(ViewGroup.GONE);
				}
				else if(newInputView.isFocused()){
					newCleanView.setVisibility(ViewGroup.VISIBLE);
				}

			}
		});
		confirmInputView.addTextChangedListener(new TextWatcher() {

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
					confirmCleanView.setVisibility(ViewGroup.GONE);
				}
				else if(confirmInputView.isFocused()){
					confirmCleanView.setVisibility(ViewGroup.VISIBLE);
				}

			}
		});

		oldInputView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && oldInputView.getText().toString().trim().length() >0){
					oldCleanView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					oldCleanView.setVisibility(ViewGroup.GONE);
				}

			}
		});

		newInputView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && newInputView.getText().toString().trim().length() >0){
					newCleanView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					newCleanView.setVisibility(ViewGroup.GONE);
				}

			}
		});

		confirmInputView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && confirmInputView.getText().toString().trim().length() >0){
					confirmCleanView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					confirmCleanView.setVisibility(ViewGroup.GONE);
				}

			}
		});


		oldCleanView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				oldInputView.setText("");
			}
		});
		newCleanView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newInputView.setText("");
			}
		});
		confirmCleanView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmInputView.setText("");
			}
		});
	}

	private void submit(){
		titlebarSubmitView.setEnabled(false);
		//取出三个密码信息
		String oldPwd = oldInputView.getText().toString();
		String newPwd = newInputView.getText().toString();
		String confirmPwd = confirmInputView.getText().toString();
		if(oldPwd.toString().trim().length() == 0){
			oldInputView.setError(StringUtil.errorHtml(getString(R.string.warn_person_info_password_request)));
			oldInputView.requestFocus();
			titlebarSubmitView.setEnabled(true);
		}
		else if(newPwd.toString().trim().length() <6 || newPwd.toString().trim().length() > 18
				){
			newInputView.setError(StringUtil.errorHtml(getString(R.string.warn_person_info_password_invalid)));
			newInputView.requestFocus();
			titlebarSubmitView.setEnabled(true);
		}
		else if(confirmPwd.toString().trim().length() <6 || confirmPwd.toString().trim().length() > 18){
			confirmInputView.setError(StringUtil.errorHtml(getString(R.string.warn_person_info_password_invalid)));
			confirmInputView.requestFocus();
			titlebarSubmitView.setEnabled(true);
		}
		else if(!newPwd.equals(confirmPwd)){
			confirmInputView.setError(StringUtil.errorHtml(getString(R.string.warn_person_info_password_unsame)));
			confirmInputView.requestFocus();
			titlebarSubmitView.setEnabled(true);
		}
		else{
			//提交修改密码
			changePasswordAsyncHttp(oldPwd,newPwd,titlebarSubmitView);
		}
	}

	/**
	 * 修改密码
	 */
	private void changePasswordAsyncHttp(String oldPwd,String newPwd,final View okView){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getChangePasswordUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair userIdPair = new BasicNameValuePair("userId", application.getLoginInfoBean().getUserId().toString());
		params.add(userIdPair);
		NameValuePair pwdPair = new BasicNameValuePair("pwd", MD5.getMD5Str(MD5.getMD5Str(oldPwd)));
		params.add(pwdPair);
		NameValuePair newPwdPair = new BasicNameValuePair("newPwd", MD5.getMD5Str(MD5.getMD5Str(newPwd)));
		params.add(newPwdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean<String> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<String>>(){}.getType());
				if(responseBean.isSuccess() && responseBean.getData()!=null && responseBean.getData().indexOf("错误") == -1){
					ToastUtil.showToast(mContext, "密码修改成功！");
					CommonOperateUtil.hideIME(mActivity);
					ChangePasswordFragment.this.getActivity().finish();
				}
				else{
					ToastUtil.showToast(mContext, "密码修改失败！错误信息:"+responseBean.getData());
				}
				okView.setEnabled(true);
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
				okView.setEnabled(true);
			}

			@Override
			public void beforeDealt() {
				// TODO Auto-generated method stub
				okView.setEnabled(false);
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

}
