package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.AssessBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class QueryCheckDoneActivity extends BaseActivity {
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private List<AssessBean> assessBeanList;
	private ListView assessListView;
	private AssessBaseAdapter assessBaseAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_query_check_done);
		titlebarInit();
		viewInit();
		setupData();
	}
	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_query_check_done);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				QueryCheckDoneActivity.this.onBackPressed();

			}
		});



	}

	private void setupData(){
		loadAssessListAsyncHttp();
	}


	private void viewInit(){
		assessListView = (ListView) findViewById(R.id.query_assess_list);
		assessBeanList = new ArrayList<AssessBean>();
		assessBaseAdapter = new AssessBaseAdapter();
		assessListView.setAdapter(assessBaseAdapter);
		assessListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long id) {
				if(position < assessBeanList.size()){
					submit(position);
				}

			}
		});
	}


	private void submit(int position){
		AssessBean assessBean = assessBeanList.get(position);
		Intent intent = new Intent();
		intent.putExtra("data", assessBean.getAssessTypeCode());
		setResult(RESULT_OK, intent);
		finish();

	}

	private class AssessBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return assessBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AssessBean assessBean = assessBeanList.get(position);
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_common_single_select, null);
			}
			TextView nameView = (TextView) convertView.findViewById(R.id.common_single_select_name);
			nameView.setText(assessBean.getAssessTypeName());
			return convertView;
		}

	}

	/**
	 * 获取评价列表
	 */
	private void loadAssessListAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getAssessListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean<List<AssessBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<AssessBean>>>(){}.getType());
				List<AssessBean> responseList = responseBean.getData();
				if(responseBean.isSuccess()){
					if(responseList == null){
						ToastUtil.showToast(mContext, R.string.error_data_null);
					}
					else if(responseList.isEmpty()){
						ToastUtil.showToast(mContext, "没有评价内容，您无法评价和确认。请联系我们解决该问题");
					}
					else{
						assessBeanList.clear();
						assessBeanList.addAll(responseList);
						assessBaseAdapter.notifyDataSetChanged();
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取评价失败");
				}
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {

			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_query_check_done;

		default:
			return R.layout.activity_query_check_done;
		}
	}
}
