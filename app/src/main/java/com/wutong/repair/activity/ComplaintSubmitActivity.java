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
import com.wutong.common.widget.CustomDialog;
import com.wutong.repair.BaseActivity;
import com.wutong.repair.data.bean.ComplaintBean;
import com.wutong.repair.data.bean.ComplaintCategoryBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.fragment.ComplaintFragment;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repairfjnu.R;

import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class ComplaintSubmitActivity extends BaseActivity {

	private ImageView titlebarbackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;

	private TextView mWriterNameView;
	private EditText mTitleView;
	private EditText mContentView;

	private CustomDialog queryDialog;
	
	private GridView mCategoryListGv;
	private ComplaintSubmitCategoryBaseAdapter mComplaintSubmitCategoryBaseAdapter;
	private List<ComplaintCategoryBean> mComplaintCategoryBeanList;
	private int selectIndex = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_complaint_submit);
		titleBarInit();
		viewInit();
		setupData();
	}
	@Override
	public void onBackPressed() {
		CommonOperateUtil.hideIME(this);
		super.onBackPressed();
	}
	private void titleBarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_complaint_submit);
		titlebarbackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarbackView.setVisibility(ViewGroup.VISIBLE);
		titlebarbackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ComplaintSubmitActivity.this.onBackPressed();

			}
		});

		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String title = mTitleView.getText().toString();
				String content = mContentView.getText().toString();
				if(title.toString().trim().length() == 0){
					ToastUtil.showToast(mContext, "请填写标题");
					return;
				}
				if(content.toString().trim().length() == 0){
					ToastUtil.showToast(mContext, "请填写内容");
					return;
				}
				//
				if(queryDialog == null){
					queryDialog = new CustomDialog.Builder(mContext).setTitle("友情提示")
							.setMessage("确定提交建议？")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									submit();
								}
							})
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							})
							.create();
				}
				queryDialog.show();
			}
		});
	}

	private void viewInit(){
		mWriterNameView = (TextView) findViewById(R.id.complaint_submit_writer_name);
		mTitleView = (EditText) findViewById(R.id.complaint_submit_title);
		mContentView = (EditText) findViewById(R.id.complaint_submit_content);
		
		mCategoryListGv = (GridView) findViewById(R.id.complaint_submit_category_list);
		mComplaintCategoryBeanList = new ArrayList<ComplaintCategoryBean>();
		mComplaintSubmitCategoryBaseAdapter = new ComplaintSubmitCategoryBaseAdapter();
		mCategoryListGv.setAdapter(mComplaintSubmitCategoryBaseAdapter);
		mCategoryListGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				selectIndex = position;
				mComplaintSubmitCategoryBaseAdapter.notifyDataSetChanged();
			}
		});
	}

	private void setupData(){
		mWriterNameView.setText(application.getLoginInfoBean().getRealName());
		//Fix分支
			loadComplaintCategoryListAsyncHttp();
	}

	private void submit(){
		//Fix分支
			if(selectIndex <0){
				ToastUtil.showToast(mContext, "请选择一个类型");
				return;
			}
		ComplaintBean complaintBean = new ComplaintBean();
		String userId = application.getLoginInfoBean().getUserId().toString();
		String title = mTitleView.getText().toString();
		String content = mContentView.getText().toString();
		complaintBean .setPid("0");
		complaintBean.setTitle(title);
		complaintBean.setContent(content);
		complaintBean.setWritterUid(userId);
		submitComplaintAsyncHttp(complaintBean);
	}


	private void submitComplaintAsyncHttp(ComplaintBean complaintBean){
		
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getComplaintPublishOrReplyUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair pidPair = new BasicNameValuePair("pid", "0");
		params.add(pidPair);
		NameValuePair contentPair = new BasicNameValuePair("content", complaintBean.getContent());
		params.add(contentPair);
		NameValuePair titlePair = new BasicNameValuePair("title", complaintBean.getTitle());
		params.add(titlePair);
		NameValuePair userIdPair = new BasicNameValuePair("userId", complaintBean.getWritterUid());
		params.add(userIdPair);
		//Fix分支
			NameValuePair typePair = new BasicNameValuePair("type", mComplaintCategoryBeanList.get(selectIndex).getTypeValue());
			params.add(typePair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					//清空标题和内容
					CommonOperateUtil.hideIME(ComplaintSubmitActivity.this);
					ToastUtil.showToast(mContext, "提交成功！");
					Intent intent =new Intent();
					intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_complaint_fragment_name));
					intent.putExtra(getString(R.string.call_fragment_key_code), ComplaintFragment.CALL_CODE_COMPLAINT_REFRESH);//刷新
					setResult(RESULT_OK,intent);
					ComplaintSubmitActivity.this.finish();
				}
				else{
					ToastUtil.showToast(mContext, "提交失败！");
				}
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				titlebarSubmitView.setEnabled(true);
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				titlebarSubmitView.setEnabled(false);
				showProgressDialog(R.string.tips_for_collecting_for_submit);
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	
	private void loadComplaintCategoryListAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getComplaintCategoryList());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<ComplaintCategoryBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<ComplaintCategoryBean>>>(){}.getType());
				if(responseBean.isSuccess()){
					List<ComplaintCategoryBean> resultList = responseBean.getData();
					if(resultList == null){
						ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
					}
					else if(resultList.isEmpty()){
						//无法提交
						mComplaintCategoryBeanList.clear();
						mComplaintCategoryBeanList.addAll(resultList);
						mComplaintSubmitCategoryBaseAdapter.notifyDataSetChanged();
						return;
					}
					else{
						mComplaintCategoryBeanList.clear();
						mComplaintCategoryBeanList.addAll(resultList);
						mComplaintSubmitCategoryBaseAdapter.notifyDataSetChanged();
						if(!mComplaintCategoryBeanList.isEmpty() ){
							selectIndex = 0;
						}
					}
					
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				titlebarSubmitView.setEnabled(true);
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				titlebarSubmitView.setEnabled(false);
				showProgressDialog();
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	
	static class ComplaintSubmitCategoryViewHolder{
		TextView nameView;
	}
	
	private class ComplaintSubmitCategoryBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mComplaintCategoryBeanList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {
			ComplaintSubmitCategoryViewHolder holder;
			ComplaintCategoryBean complaintCategoryBean = mComplaintCategoryBeanList.get(position);
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
				holder = new ComplaintSubmitCategoryViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.complaint_category_name);
				convertView.setTag(holder);
			}
			else{
				holder = (ComplaintSubmitCategoryViewHolder) convertView.getTag();
			}
			holder.nameView.setText(complaintCategoryBean.getTypeName());
			
			if(position == selectIndex){
				convertView.setEnabled(false);
			}
			else{
				convertView.setEnabled(true);
			}
			return convertView;
		}
		
	}

	private int selectedItemLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_listview_item_complaint_category;

		default:
			return R.layout.listview_item_complaint_category;
		}
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_complaint_submit;

		default:
			return R.layout.activity_complaint_submit;
		}
	}
	
}
