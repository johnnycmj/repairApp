package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;




import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.common.widget.CustomDialog.Builder;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.ApplicationInfoUtil;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.InternetConnUtil;
import com.wutong.repair.util.MobileInfoUtil;


public class FeedbackUsFragment extends BaseFragment {
	private ImageView titlebarBackView;
	private ImageView titlebarSubmitView;
	private TextView titlebarTitleView;

	private EditText mContentView;
	private EditText mEmailView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_feedback_us);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		//
		titlebarInit();
		viewInit();
		return fragmentView;
	}
	
	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_feekback_us);
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
				FeedbackUsFragment.this.getActivity().onBackPressed();

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
		mContentView = (EditText) findViewById(R.id.feedback_us_content);
		mEmailView = (EditText) findViewById(R.id.feedback_us_contact_info);
	}


	private void submit(){
		//check
		if(mContentView.getText().toString().trim().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "内容为空可不能提交哦。");
			return ;
		}
		//提交
		String message = "";
		if(mEmailView.getText().toString().trim().toString().trim().length() == 0){
			message +="填写正确的联系方式能够方便我们与您取得联系，将服务做的更好\n";
		}
		message +="确定提交您的宝贵意见或建议么？";
		//
		Builder builder = new Builder(mContext);
		builder.setTitle("友情提醒");
		builder.setMessage(message)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				submitFeedbackUsAsyncHttp();

			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		}).create().show();
	}

	private void submitFeedbackUsAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getFeedbackUsSendUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair contentPair = new BasicNameValuePair("content", mContentView.getText().toString());
		params.add(contentPair);
		NameValuePair versionPair = new BasicNameValuePair("version", application.getAppFlag() + "/"+ ApplicationInfoUtil.getVersionName(mContext));
		params.add(versionPair);
		NameValuePair phoneModelPair = new BasicNameValuePair("phoneModel", MobileInfoUtil.getMobileMore());
		params.add(phoneModelPair);
		NameValuePair brandPair = new BasicNameValuePair("brand", MobileInfoUtil.getMobileBrand());
		params.add(brandPair);
		NameValuePair operatingSystemPair = new BasicNameValuePair("operatingSystem", MobileInfoUtil.getSystemVersionName());
		params.add(operatingSystemPair);
		String netState;
		if(InternetConnUtil.isWifiConnect(mContext)){
			netState = "WIFI";
		}
		else if(InternetConnUtil.isMobileConnect(mContext)){
			netState = "MOBILE";
		}
		else{
			netState = "NETWORK DISCONNECTED";
		}
		NameValuePair netStatePair = new BasicNameValuePair("netState", netState);
		params.add(netStatePair);
		String contactInfo = "[System Catch:"+application.getLoginInfoBean().liteInfo()+"]" ;
		contactInfo += mEmailView.getText().toString();
		NameValuePair suggestionPhonePair = new BasicNameValuePair("email", contactInfo);
		params.add(suggestionPhonePair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					//清空标题和内容
					CommonOperateUtil.hideIME(FeedbackUsFragment.this.getActivity());
					ToastUtil.showToast(mContext, "提交成功！");
					FeedbackUsFragment.this.getActivity().finish();
				}
				else{
					ToastUtil.showToast(mContext, "提交失败！");
				}
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
				hideProgressDialog();
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void beforeDealt() {
				titlebarSubmitView.setEnabled(false);
				showProgressDialog(R.string.tips_for_collecting_for_submit);
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_feekback_us;

		default:
			return R.layout.activity_feekback_us;
		}
	}
	
	
}
