package com.wutong.repair.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repair.data.bean.ComplaintBean;
import com.wutong.repair.data.bean.ComplaintPagerBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.TimeUtil;
import com.wutong.repairfjnu.R;

public class ComplaintFragment extends BaseFragment {
	
	public final static int CALL_CODE_COMPLAINT_REFRESH = 1;
	
	private TextView titlebarTitleView;
	private ImageView titlebarAddView;
	private ImageView titlebarBackView;
	private String complaintTitle = "建议";

	private PullToRefreshView mComplaintPullToRefreshView;
	private ListView mComplaintsView;
	private BaseAdapter complaintAdapter;
	private List<ComplaintBean> complaintList;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;
	private int complaintCurrentStart = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_complaint_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.vp_av_index_tab_complaint, container,false);
		setFragmentView(fragmentView);
		//
		titleBarInit();
		viewInit();
		callInit();
		return fragmentView;
	}

	private void titleBarInit(){
		titlebarAddView = (ImageView) findViewById(R.id.titlebar_add);
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(complaintTitle);
		titlebarAddView.setVisibility(ViewGroup.VISIBLE);
		titlebarAddView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, ComplaintSubmitActivity.class);
				startActivityForResult(intent, 0);

			}
		});
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
				ComplaintFragment.this.getActivity().onBackPressed();

			}
		});
	}
	
	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_complaint_fragment_name), new CallFragmentListener() {
			
			@Override
			public void onCall(int callCode) {
				switch (callCode) {
				case CALL_CODE_COMPLAINT_REFRESH:
					loadMoreComplaint(true);
					break;
				
				default:
					break;
				}
				
			}
		});
	}


	private void viewInit(){
		mComplaintPullToRefreshView = (PullToRefreshView) findViewById(R.id.complaint_index_pull_refresh_view);
		mComplaintsView = (ListView)  findViewById(R.id.complaint_list);
		footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_common_no_more, null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		mComplaintsView.addFooterView(footerView);
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
		View emptyView = findViewById(R.id.complaint_index_empty);
		mComplaintsView.setEmptyView(emptyView);

		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMoreComplaint(true);

			}
		});

		//
		//
		mComplaintsView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View convertView, int position,
					long id) {
				Intent intent = new Intent();
				intent.setClass(mContext, ComplaintDetailActivity.class);
				intent.putExtra("complaintId", String.valueOf(id));
				Bundle bundle = new Bundle();
				bundle.putSerializable("complaintBean", (Serializable)complaintList.get(position));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		complaintList= new ArrayList<ComplaintBean>();
		complaintAdapter = new CompliantBaseAdapter(complaintList);
		mComplaintsView.setAdapter(complaintAdapter);
		complaintCurrentStart = 0;
		loadMoreComplaint(true);
		//

	}



	private void loadMoreComplaint(boolean isRefresh){
		if(isRefresh){
			complaintCurrentStart = 0;
		}
		else{
			complaintCurrentStart += application.getPagingSize();
		}
		ComplaintPagerBean complaintPagerBean = new ComplaintPagerBean(complaintCurrentStart, application.getPagingSize());
		loadComplaintListAsyncHttp(complaintPagerBean,isRefresh);
	}

	static class CompliantViewHolder{
		TextView titleView;
		TextView writerView;
		TextView receiveView;
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
			return Long.valueOf(list.get(position).getId());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CompliantViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_complaint_index, null);
				holder = new CompliantViewHolder();

				holder.titleView = (TextView) convertView.findViewById(R.id.complaint_title);
				holder.writerView = (TextView) convertView.findViewById(R.id.complaint_writer);
				holder.receiveView = (TextView) convertView.findViewById(R.id.complaint_receive);
				holder.createTimeView = (TextView) convertView.findViewById(R.id.complaint_create_time);
				convertView.setTag(holder);
			}

			ComplaintBean complaintBean = list.get(position);
			holder = (CompliantViewHolder) convertView.getTag();

			String title = complaintBean.getTitle();
			String writer = complaintBean.getWritter();
			String receiveNumber = complaintBean.getReceiveNumber();
			String createTime = complaintBean.getCreateTime();
			holder.titleView.setText(getString(R.string.format_complaint_title, title));
			holder.writerView.setText(getString(R.string.format_complaint_writer, writer));
			holder.receiveView.setText(receiveNumber);
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
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getComplaintListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair startPair = new BasicNameValuePair("start", complaintPagerBean.getStart());
		params.add(startPair);
		NameValuePair limitPair = new BasicNameValuePair("limit", complaintPagerBean.getLimit());
		params.add(limitPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				if(!ComplaintFragment.this.isAdded() ||! ComplaintFragment.this.isVisible()){
					return;
				}
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
				else{
					if(isRefresh){
						complaintList.clear();
						complaintList.addAll(responseList);
						mComplaintPullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						complaintList.addAll(responseList);
						mComplaintPullToRefreshView.onFooterRefreshComplete();
					}
					
					if(responseList.size() < application.getPagingSize()){
						//
						if(!isFooterAdded){
							mComplaintsView.addFooterView(footerView);
							isFooterAdded = true;
						}
					}
					else{
						if(isFooterAdded){
							mComplaintsView.removeFooterView(footerView);
							isFooterAdded = false;
						}
					}
					complaintAdapter.notifyDataSetChanged();
				}
				
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				if(!ComplaintFragment.this.isAdded() ||! ComplaintFragment.this.isVisible()){
					return;
				}
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog("获取信息……");
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
}
