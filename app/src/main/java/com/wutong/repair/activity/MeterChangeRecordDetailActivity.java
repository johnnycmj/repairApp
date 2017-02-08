package com.wutong.repair.activity;



import net.tsz.afinal.http.AjaxParams;






import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.RepairmanLikeBean;
import com.wutong.repair.data.bean.RoomMeterRecordBean;
import com.wutong.repair.data.bean.ResponseBean;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 抄表修改详细
 * @author Jolly
 * 创建时间：2014年3月28日下午3:39:47
 *
 */
public class MeterChangeRecordDetailActivity extends BaseActivity {


	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;

	private String noticeId;
	private RoomMeterRecordBean contributeBean;

	private TextView mBulidingOrNameView;
	private TextView mBulidingNumberView;
	private TextView mMeterNameView;
	private TextView mMeterNumberView;

	private TextView mLastValueView;
	private EditText mCurrentValueView;
	private TextView mMultipleView;
	private EditText mRealValueView;
	private TextView mSinglePriceView;
	private EditText mTotalPriceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isNeedLogin = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meter_change_record_detail);
		setStatPageName(mContext, R.string.title_activity_meter_change_record_detail);
		intentInit();
		titleBarInit();
		viewInit();
	}

	private void titleBarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setVisibility(ViewGroup.VISIBLE);
		titlebarTitleView.setText(R.string.title_activity_meter_change_record_detail);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MeterChangeRecordDetailActivity.this.onBackPressed();

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
		mBulidingOrNameView = (TextView) findViewById(R.id.meter_record_building_or_owner);
		mBulidingNumberView = (TextView) findViewById(R.id.meter_record_building_number);
		mMeterNameView = (TextView) findViewById(R.id.meter_record_meter_name);
		mMeterNumberView = (TextView) findViewById(R.id.meter_record_meter_number);

		mLastValueView = (TextView) findViewById(R.id.meter_record_last_value);
		mCurrentValueView = (EditText) findViewById(R.id.meter_record_current_value);
		mMultipleView = (TextView) findViewById(R.id.meter_record_multiple);
		mRealValueView = (EditText) findViewById(R.id.meter_record_real_value);
		mSinglePriceView = (TextView) findViewById(R.id.meter_record_single_price);
		mTotalPriceView = (EditText) findViewById(R.id.meter_record_total_price);
	}

	private void intentInit(){
		noticeId = getIntent().getStringExtra("contributeId");
	}
	private void setupData(){
		loadContributeDetailAsyncHttp();

	}

	@Override
	protected void onResume() {
		super.onResume();
		setupData();
	}



	/**
	 * 通知详细
	 */
	private void loadContributeDetailAsyncHttp(){

		String url = getString(R.string.http_url_load_meter_record_detail,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("recordId", noticeId);
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);

				com.wutong.androidprojectlibary.http.util.HttpResponseUtil.justToast(errorNo, t, mContext);
			}

			@Override
			public void onStart() {
				super.onStart();
				showProgressDialog();
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				hideProgressDialog();
				ResponseBean<RoomMeterRecordBean> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<RoomMeterRecordBean>>(){}.getType());
				if(responseBean.isSuccess()){
					RoomMeterRecordBean resultBean = responseBean.getData();
					if(resultBean != null ){
						contributeBean = resultBean;
						mBulidingOrNameView.setText(resultBean.getRealName());
						mBulidingNumberView.setText(resultBean.getHouseholderNum());
						mMeterNameView.setText(resultBean.getMeterName());
						mMeterNumberView.setText(resultBean.getMeterNum());
						
						mLastValueView.setText(resultBean.getOldValue());
						mCurrentValueView.setText(resultBean.getNewValue());
						mMultipleView.setText(resultBean.getMultiplier());
						mRealValueView.setText(resultBean.getRealityValue());
						mSinglePriceView.setText(resultBean.getPrice());
						mTotalPriceView.setText(resultBean.getMoney());
						mCurrentValueView.setSelection(mCurrentValueView.getText().toString().length());
					}
					else{
						ToastUtil.showToast(mContext, "返回表数据有错");
					}
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}

			}

		});


	}
	
	private void submit(){
		if(mCurrentValueView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "请输入本期数");
			mCurrentValueView.requestFocus();
			return;
		}
		if(mRealValueView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "请输入实际数");
			mRealValueView.requestFocus();
			return;
		}
		if(mTotalPriceView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "请输入金额");
			mTotalPriceView.requestFocus();
			return;
		}
		submitEditRecordAsyncHttp();
	}

	private void submitEditRecordAsyncHttp(){
		String url = getString(R.string.http_url_load_meter_record_edit,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("recordId", noticeId);
		params.put("newValue", mCurrentValueView.getText().toString());
		params.put("realityValue", mRealValueView.getText().toString());
		params.put("money", mTotalPriceView.getText().toString());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				hideProgressDialog();
				com.wutong.androidprojectlibary.http.util.HttpResponseUtil.justToast(errorNo, t, mContext);
			}

			@Override
			public void onStart() {
				super.onStart();
				showProgressDialog();
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				hideProgressDialog();
				ResponseBean<Object> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<Object>>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "修改成功");
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}


			}

		});


	}




}
