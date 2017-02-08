package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
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
import com.wutong.androidprojectlibary.validate.util.StringValidateUtil;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.StringUtil;

public class ChangeContactFragment extends BaseFragment {
	private ImageView titlebarBackView;
	private ImageView titlebarSubmitView;
	private TextView titlebarTitleView;

	private EditText mContactInputView;
	private ImageView mContactCleanView;
	private String oldContact;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_change_contact);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		//
		dataInit();
		titlebarInit();
		viewInit();
		return fragmentView;
	}

	private void dataInit(){
		oldContact = application.getLoginInfoBean().getPhone();
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_fragment_change_contact);
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
				ChangeContactFragment.this.getActivity().onBackPressed();

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
		mContactInputView = (EditText) findViewById(R.id.change_contact_content);
		mContactCleanView = (ImageView) findViewById(R.id.edit_clean_contact);
		mContactInputView.setText(oldContact);
		mContactInputView.setSelection(mContactInputView.getText().toString().length());
		mContactInputView.addTextChangedListener(new TextWatcher() {

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
					mContactCleanView.setVisibility(ViewGroup.GONE);
				}
				else if(mContactInputView.isFocused()){
					mContactCleanView.setVisibility(ViewGroup.VISIBLE);
				}

			}
		});

		mContactInputView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && mContactInputView.getText().toString().trim().length() >0){
					mContactCleanView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					mContactCleanView.setVisibility(ViewGroup.GONE);
				}

			}
		});
		mContactCleanView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContactInputView.setText("");
			}
		});
	}

	private void submit(){
		titlebarSubmitView.setEnabled(false);
		String contact = mContactInputView.getText().toString();
		if(contact.toString().trim().length() == 0 || !StringValidateUtil.isPhoneNumber(contact)){
			mContactInputView.setError(StringUtil.errorHtml(getString(R.string.warn_person_info_contact_invalid)));
			titlebarSubmitView.setEnabled(true);
		}
		else{
			//提交修改号码
			changeContactAsyncHttp(contact,titlebarSubmitView);
		}
	}

	/**
	 * 修改号码
	 * @param contact
	 */
	private void changeContactAsyncHttp(final String contact,final View okView){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getStudentContactChangeUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair userIdPair = new BasicNameValuePair("userId", application.getLoginInfoBean().getUserId().toString());
		params.add(userIdPair);
		NameValuePair phonePair = new BasicNameValuePair("phone", contact);
		params.add(phonePair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "号码修改成功！");
					//本地配置文件中的号码也替换为新的
					if(application.getLoginInfoBean().getPhone()!=null){
						application.getLoginInfoBean().setPhone(contact);
					}

					//
					CommonOperateUtil.hideIME(mActivity);
					Intent intent =new Intent();
					intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_person_info_name));
					intent.putExtra(getString(R.string.call_fragment_key_code), PersonInfoFragment.CALL_CODE_REFRESH);//刷新

					ChangeContactFragment.this.getActivity().setResult(Activity.RESULT_OK, intent);
					ChangeContactFragment.this.getActivity().finish();
				}
				else{
					ToastUtil.showToast(mContext, "号码修改失败！错误信息:"+responseBean.getMessage());
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
				okView.setEnabled(false);
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_fragment_change_contact;

		default:
			return R.layout.fragment_change_contact;
		}
	}
	
}
