package com.wutong.repair.activity;

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
import com.wutong.androidprojectlibary.log.util.Logger;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshView;
import com.wutong.common.widget.PullToRefreshView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.wutong.repair.BaseFragment;
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repair.data.bean.NoticeBean;
import com.wutong.repair.data.bean.NoticePagerBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.TimeUtil;
import com.wutong.repairfjnu.R;

public class NoticeListFragment extends BaseFragment {
	public final static int CALL_CODE_REFRESH = 1;
	private TextView titlebarTitleView;
	private ImageView titlebarBackView;
	
	private String noticeTitle = "公告";

	private PullToRefreshView mNoticePullToRefreshView;
	private ListView mNoticeListView;
	private BaseAdapter noticeAdapter;
	private List<NoticeBean> mNoticeBeanList;
	private int noticeCurrentStart = 0;
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_notice_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.vp_av_index_tab_notice, container,false);
		setFragmentView(fragmentView);
		//
		titlebarInit();
		viewInit();
		callInit();
		return fragmentView;
	}

	private void titlebarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(noticeTitle);
		
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
				NoticeListFragment.this.getActivity().onBackPressed();

			}
		});
	}
	
	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_notice_list_name), new CallFragmentListener() {
			
			@Override
			public void onCall(int callCode) {
				switch (callCode) {
				case CALL_CODE_REFRESH:
					loadMoreNotice(true);
					break;
				
				default:
					break;
				}
				
			}
		});
	}

	private void viewInit(){
		mNoticePullToRefreshView = (PullToRefreshView) findViewById(R.id.notice_list_pull_refresh_view);
		mNoticeListView = (ListView) findViewById(R.id.notice_list);
		footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_common_no_more, null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		mNoticeListView.addFooterView(footerView);
		View emptyView = findViewById(R.id.notice_list_empty);
		mNoticeListView.setEmptyView(emptyView);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMoreNotice(true);

			}
		});

		mNoticeBeanList = new ArrayList<NoticeBean>();
		noticeAdapter = new NoticeListBaseAdapter();
		mNoticeListView.setAdapter(noticeAdapter);
		mNoticeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view, int position,
					long noticeId) {
				Intent intent = new Intent(mContext,NoticeDetailActivity.class);
				intent.putExtra("noticeId", String.valueOf(noticeId));
				NoticeBean noticeBean = (NoticeBean) view.getTag();
				intent.putExtra("isRead", noticeBean.getIsRead().equals("1")?true:false);
				NoticeListFragment.this.getActivity().startActivityForResult(intent, 0);
			}
		});
		mNoticePullToRefreshView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshView view) {
				loadMoreNotice(true);

			}
		});
		mNoticePullToRefreshView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshView view) {
				loadMoreNotice(false);

			}
		});
		loadMoreNotice(true);//setup data

	}

	private class NoticeListBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mNoticeBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return mNoticeBeanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return Long.valueOf(mNoticeBeanList.get(position).getId());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_notice_list, null);
			TextView titleView = (TextView) convertView.findViewById(R.id.notice_title);
			TextView dateView = (TextView) convertView.findViewById(R.id.notice_date);
			TextView contentView = (TextView) convertView.findViewById(R.id.notice_content);
			NoticeBean noticeBean = mNoticeBeanList.get(position);
			titleView.setText(noticeBean.getTitle());
			dateView.setText(noticeBean.getDate());
			contentView.setText(getString(R.string.format_notice_content,noticeBean.getContent()));
			//			//判断类型。上色
			//			if(noticeBean.getTypeId().equals(NoticeTypeDict.URGENCY_NOTICE)){
			//				titleView.setTextColor(getResources().getColor(R.color.notice_type_urgency));
			//			}
			//			else if(noticeBean.getTypeId().equals(NoticeTypeDict.NORMAL_NOTICE)){
			//				titleView.setTextColor(getResources().getColor(R.color.notice_type_normal));
			//			}
			//			else{
			//				titleView.setTextColor(getResources().getColor(R.color.notice_type_other));
			//			}
			//判断已读状态
			if(noticeBean.getIsRead().equals("0")){
				titleView.getPaint().setFakeBoldText(true);
			}
			convertView.setTag(noticeBean);
			return convertView;
		}

	}

	/**
	 * 通知列表
	 * @param noticePagerBean
	 * @param isRefresh
	 */
	private void loadNoticeListAsyncHttp(NoticePagerBean noticePagerBean,final boolean isRefresh){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getNoticeListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair userIdPair = new BasicNameValuePair("userId", application.getLoginInfoBean().getUserId().toString());
		params.add(userIdPair);
		NameValuePair startPair = new BasicNameValuePair("start", noticePagerBean.getStart());
		params.add(startPair);
		NameValuePair limitPair = new BasicNameValuePair("limit", noticePagerBean.getLimit());
		params.add(limitPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				Logger.i("notice list: resultResponse");
				if(!NoticeListFragment.this.isAdded() ||! NoticeListFragment.this.isVisible()){
					return;
				}
				ResponseBean<List<NoticeBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<NoticeBean>>>(){}.getType());
				List<NoticeBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					if(isRefresh){
						mNoticeBeanList.clear();
						mNoticeBeanList.addAll(resultList);
						mNoticePullToRefreshView.onHeaderRefreshComplete("最后更新：" +TimeUtil.currentTime());
					}
					else{
						mNoticeBeanList.addAll(resultList);
						mNoticePullToRefreshView.onFooterRefreshComplete();
					}
					
					if(resultList.size() < application.getPagingSize()){
						//
						if(!isFooterAdded){
							mNoticeListView.addFooterView(footerView);
							isFooterAdded = true;
						}
					}
					else{
						if(isFooterAdded){
							mNoticeListView.removeFooterView(footerView);
							isFooterAdded = false;
						}
					}
					noticeAdapter.notifyDataSetChanged();
				}
				
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				if(!NoticeListFragment.this.isAdded() ||! NoticeListFragment.this.isVisible()){
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


	private void loadMoreNotice(boolean isRefresh){
		if(isRefresh){
			noticeCurrentStart = 0;
		}
		else{
			noticeCurrentStart += application.getPagingSize();
		}
		String userId = application.getLoginInfoBean().getUserId().toString();
		NoticePagerBean noticePagerBean = new NoticePagerBean(userId,noticeCurrentStart, application.getPagingSize());
		loadNoticeListAsyncHttp(noticePagerBean,isRefresh);
	}
}
