package com.wutong.repair.activity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ContributePagerBean;
import com.wutong.repair.data.bean.HelpFoundBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.Logger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class HelpLostSubmitSuccessActivity extends BaseActivity {

	private TextView titlebarTitleView;

	private String outFindDate;
	private String outItemType;
	private String outPlaceId;

	private int tryTimes = 0;
	private final int MAX_TRY_TIMES = 3;

	private long loadPreTime;
	private long loadFinishTime;
	private final long MAX_WAIT = 3000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_lost_submit_success);
		intentInit();
		titlebarInit();
		setupData();
	}

	@Override
	public void onBackPressed() {
		//不做任何操作
	}

	private void setupData(){
		loadPreTime = new Date().getTime();
		ContributePagerBean complaintPagerBean = new ContributePagerBean(0, application.getPagingSize());
		loadContributeListAsyncHttp(complaintPagerBean, true);
	}

	private void intentInit(){
		outPlaceId = getIntent().getStringExtra("placeId");
		outItemType = getIntent().getStringExtra("itemType");
		outFindDate = getIntent().getStringExtra("findDate");
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_lost_submit);

	}

	private void loadContributeListAsyncHttp(ContributePagerBean complaintPagerBean,final boolean isRefresh){
		String url = getString(R.string.http_url_load_lost_like_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("apartmentId", outPlaceId);//地点
		params.put("findDate", outFindDate);
		params.put("itemType", outItemType);//物品类别
		params.put("start", complaintPagerBean.getStart());
		params.put("limit", complaintPagerBean.getLimit());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
				com.wutong.androidprojectlibary.http.util.HttpResponseUtil.justToast(errorNo, t, mContext);
				loadFinishTime = new Date().getTime();
				long dulation = loadFinishTime - loadPreTime;
				StartForwordAsyncTask asyncTask = new StartForwordAsyncTask();
				asyncTask.execute(dulation,Long.valueOf(0));
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
				ResponseBean<List<HelpFoundBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<HelpFoundBean>>>(){}.getType());
				List<HelpFoundBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					loadFinishTime = new Date().getTime();
					long dulation = loadFinishTime - loadPreTime;
					if(dulation < MAX_WAIT){
						Logger.i("网速太给力，等待至第三秒跳转");
						StartForwordAsyncTask asyncTask = new StartForwordAsyncTask();
						asyncTask.execute(dulation,Long.valueOf(responseList.size()),responseList);
					}
					else{
						StartForwordAsyncTask asyncTask = new StartForwordAsyncTask();
						asyncTask.execute(MAX_WAIT,Long.valueOf(responseList.size()),responseList);
					}
				}

			}

		});

	}
	
	private class StartForwordAsyncTask extends AsyncTask<Object, Void, Integer>{
		long size;
		List<HelpFoundBean> list;

		@Override
		protected Integer doInBackground(Object... params) {
			long duration = (Long) params[0];
			size = (Long) params[1];
			list = (List<HelpFoundBean>) params[2];
			try {
				Thread.sleep(MAX_WAIT-duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(size == 0){
				Intent intent = new Intent(mContext, HelpLostNoLikeActivity.class);
				startActivity(intent);
				finish();
			}
			else{
				//有疑似
				Intent intent = new Intent(mContext, LikeableFoundActivity.class);
				intent.putExtra("placeId", outPlaceId);
				intent.putExtra("itemType", outItemType);
				intent.putExtra("findDate", outFindDate);
				Bundle data = new Bundle();
				data.putSerializable("foundList",(Serializable) list );
				intent.putExtras(data);
				startActivity(intent);
				finish();
			}
		}
	}

}
