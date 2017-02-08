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
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ComplaintBean;
import com.wutong.repair.data.bean.ComplaintPagerBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.TimeUtil;

import android.os.Bundle;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class ComplaintDetailActivity extends BaseActivity {
	
	
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	
	private PullToRefreshView mComplaintPullToRefreshView;
	private ListView mComplaintsView;
	private BaseAdapter adapter;
	private List<ComplaintBean> complaintList;
	
	private EditText mContentView;
	private Button mSubmitView;
	
	private Context mContext;

	private int currentStart = 0;

	private View headPublishView;
	private TextView headTitleView;
	private TextView headContentView;
	private TextView headWriterView;
	private TextView headCreateTimeView;
	private TextView headCategoryView;

	private TextView headDormitoryNameView;
	private TextView headRepairmanNameView;
	private TextView headPhoneView;
	private String complaintId;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_complaint_detail);
		complaintId = getIntent().getStringExtra("complaintId");
		titleBarInit();
		viewInit();
		complaintList= new ArrayList<ComplaintBean>();
		adapter = new CompliantBaseAdapter(complaintList);
		mComplaintsView.setAdapter(adapter);
		setupData();
	}




	@Override
	public void onBackPressed() {
		CommonOperateUtil.hideIME(this);
		super.onBackPressed();
	}




	private void titleBarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(getString(R.string.title_activity_complaint_detail));
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ComplaintDetailActivity.this.onBackPressed();
				
			}
		});
	}

	private void viewInit(){
		mComplaintPullToRefreshView = (PullToRefreshView) findViewById(R.id.complaint_detail_pull_refresh_view);
		mComplaintsView = (ListView) findViewById(R.id.complaint_receive_list);

		View emptyView = findViewById(R.id.complaint_detail_empty);
		mComplaintsView.setEmptyView(emptyView);
		emptyView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadMoreComplaint(true);
				
			}
		});
		
		mComplaintPullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
			
			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMoreComplaint(true);
				
			}
		});
		mComplaintPullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {
			
			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMoreComplaint(false);
				
			}
		});
		
		headPublishView = LayoutInflater.from(mContext).inflate(R.layout.listview_complaint_detail_top, null);

		headTitleView = (TextView) headPublishView.findViewById(R.id.complaint_detail_top_title);
		headContentView = (TextView) headPublishView.findViewById(R.id.complaint_content);
		headWriterView = (TextView) headPublishView.findViewById(R.id.complaint_writer);
		headCreateTimeView = (TextView) headPublishView.findViewById(R.id.complaint_create_time);
		headCategoryView = (TextView) headPublishView.findViewById(R.id.complaint_category_name);
		headDormitoryNameView = (TextView) headPublishView.findViewById(R.id.complaint_dormitory_name);
		headRepairmanNameView = (TextView) headPublishView.findViewById(R.id.complaint_repairman_name);
		headPhoneView = (TextView) headPublishView.findViewById(R.id.complaint_writer_phone);
		mComplaintsView.addHeaderView(headPublishView);

		
		mContentView = (EditText) findViewById(R.id.complaint_receive_content);
		mSubmitView = (Button) findViewById(R.id.complaint_submit);
		mSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();
			}
		});
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
				if(s.toString().trim().length() > 0){
					mSubmitView.setEnabled(true);
					mSubmitView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					mSubmitView.setEnabled(false);
					mSubmitView.setVisibility(ViewGroup.GONE);
				}
			}
		});
		

		//
	}

	private void setupData(){
		loadMoreComplaint(true);
	}


	private void loadMoreComplaint(boolean isRefresh){
		if(isRefresh){
			currentStart = 0;
		}
		else{
			currentStart += application.getPagingSize();
		}
		ComplaintPagerBean complaintPagerBean = new ComplaintPagerBean(complaintId,currentStart, application.getPagingSize());
		loadComplaintListAsyncHttp(complaintPagerBean,isRefresh);
	}

	static class CompliantViewHolder{
		TextView titleView;
		TextView contentView;
		TextView writerView;
		TextView createTimeView;
	}

	private class CompliantBaseAdapter extends BaseAdapter{
		private List<ComplaintBean> list;
		public CompliantBaseAdapter(List<ComplaintBean> list) {
			super();
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			CompliantViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
				holder = new CompliantViewHolder();
				holder.contentView = (TextView) convertView.findViewById(R.id.complaint_content);
				holder.writerView = (TextView) convertView.findViewById(R.id.complaint_writer);
				holder.createTimeView = (TextView) convertView.findViewById(R.id.complaint_create_time);
				convertView.setTag(holder);
			}
			ComplaintBean complaintBean = list.get(position);
			holder = (CompliantViewHolder) convertView.getTag();
			String content = complaintBean.getContent();
			String writer = complaintBean.getWritter();
			String createTime = complaintBean.getCreateTime();
			holder.contentView.setText(content);
			holder.writerView.setText(writer);
			holder.createTimeView.setText(createTime);

			return convertView;
		}

	}

	/**
	 * 获取投诉列表
	 * @param complaintPagerBean
	 * @param isRefresh
	 */
	private void loadComplaintListAsyncHttp(ComplaintPagerBean complaintPagerBean,final boolean isRefresh){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getComplaintSubListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair startPair = new BasicNameValuePair("start", complaintPagerBean.getStart());
		params.add(startPair);
		NameValuePair limitPair = new BasicNameValuePair("limit", complaintPagerBean.getLimit());
		params.add(limitPair);
		NameValuePair pidPair = new BasicNameValuePair("pid", complaintId);
		params.add(pidPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<ComplaintBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<ComplaintBean>>>(){}.getType());
				List<ComplaintBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				if(responseList != null && !responseList.isEmpty()){
					if(isRefresh){
						complaintList.clear();
						complaintList.addAll(responseList);
						adapter.notifyDataSetChanged();
						mComplaintPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						complaintList.addAll(responseList);
						adapter.notifyDataSetChanged();
						mComplaintPullToRefreshView.onFooterRefreshComplete();
					}
				}
				else{
					mComplaintPullToRefreshView.onHeaderRefreshComplete();
					mComplaintPullToRefreshView.onFooterRefreshComplete();
				}
				if(isRefresh){
					//
					ComplaintBean complaintBean = complaintList.remove(0);
					if(complaintBean != null){
						String content = complaintBean.getContent();
						String writer = complaintBean.getWritter();
						String createTime = complaintBean.getCreateTime();
						String categoryName = complaintBean.getCategoryName();
						headTitleView.setText(complaintBean.getTitle());
						headContentView.setText(content);
						headWriterView.setText(writer);
						headCreateTimeView.setText(createTime);
						if(categoryName == null){
							headCategoryView.setVisibility(ViewGroup.GONE);
						}
						else{
							headCategoryView.setVisibility(ViewGroup.VISIBLE);
							headCategoryView.setText(categoryName);
						}
						//建议人所在宿舍、宿舍的维修人信息
						if(application.hasPermission(getString(R.string.roles_permissions_has_see_dormitory_info_of_complaint_detail))){
							if(complaintBean.getDormitoryName() != null){
								headDormitoryNameView.setVisibility(ViewGroup.VISIBLE);
								headDormitoryNameView.setText(complaintBean.getDormitoryName());
							}
							else{
								headDormitoryNameView.setVisibility(ViewGroup.GONE);
							}
							if(complaintBean.getRepairmanList() != null && !complaintBean.getRepairmanList().isEmpty()){
								headRepairmanNameView.setVisibility(ViewGroup.VISIBLE);
								headRepairmanNameView.setText(complaintBean.getRepairmanList().get(0).getName());
							}
							else{
								headRepairmanNameView.setVisibility(ViewGroup.GONE);
							}
							if(complaintBean.getPhone() != null && complaintBean.getPhone().length() >0){
								headPhoneView.setText("("+complaintBean.getPhone()+")");
							}
							else{
								headPhoneView.setText("");
							}
						}
						else{
							headDormitoryNameView.setVisibility(ViewGroup.GONE);
							headRepairmanNameView.setVisibility(ViewGroup.GONE);
						}
					}
					else{
						ToastUtil.showToast(mContext, "建议不存在，无法查看");
					}
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



	
	private void submit(){
		ComplaintBean complaintBean = new ComplaintBean();
		String userId = application.getLoginInfoBean().getUserId().toString();
		
		String content = mContentView.getText().toString();

		complaintBean .setPid(complaintId);
		complaintBean.setContent(content);
		complaintBean.setTitle("");
		complaintBean.setWritterUid(userId);
		submitComplaintAsyncHttp(complaintBean);
	}
	
	private void submitComplaintAsyncHttp(final ComplaintBean complaintBean){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getComplaintPublishOrReplyUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair pidPair = new BasicNameValuePair("pid", complaintBean.getPid());
		params.add(pidPair);
		NameValuePair contentPair = new BasicNameValuePair("content", complaintBean.getContent());
		params.add(contentPair);
		NameValuePair titlePair = new BasicNameValuePair("title", complaintBean.getTitle());
		params.add(titlePair);
		NameValuePair userIdPair = new BasicNameValuePair("userId", complaintBean.getWritterUid());
		params.add(userIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {
			
			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					//清空标题和内容
					CommonOperateUtil.hideIME(ComplaintDetailActivity.this);
					ToastUtil.showToast(mContext, "提交成功！");
					mContentView.setText("");
					//
					complaintBean.setCreateTime(TimeUtil.currentTime());
					complaintBean.setWritter(application.getLoginInfoBean().getRealName());
					complaintList.add(0,complaintBean);
					adapter.notifyDataSetChanged();
					mComplaintsView.setSelection(complaintList.size()-1);
				}
				else{
					ToastUtil.showToast(mContext, "提交失败！");
					mSubmitView.setEnabled(true);
				}
			}
			
			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
				mSubmitView.setEnabled(true);
			}
			
			@Override
			public void beforeDealt() {
				mSubmitView.setEnabled(false);
				showProgressDialog(R.string.tips_for_collecting_for_submit);
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	
	private int selectedItemLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_listview_complaint_detail;

		default:
			return R.layout.listview_complaint_detail;
		}
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_complaint_detail;

		default:
			return R.layout.activity_complaint_detail;
		}
	}
}
