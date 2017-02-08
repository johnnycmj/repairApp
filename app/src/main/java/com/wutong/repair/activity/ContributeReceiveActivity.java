package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;


import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.PullToRefreshInvertView;
import com.wutong.common.widget.PullToRefreshInvertView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshInvertView.OnHeaderRefreshListener;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ContributeBean;
import com.wutong.repair.data.bean.FeedbackPagerBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.TimeUtil;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class ContributeReceiveActivity extends BaseActivity{


	private ImageView titlebarBackView;
	private TextView titlebarTitleView;


	private PullToRefreshInvertView contributePullToRefreshInvertView;

	private ListView mContributeListView;
	private BaseAdapter adapter;
	private List<ContributeBean> contributeBeanList;
	private EditText mContributeInputView;
	private Button mContributeSendView;
	private String outContributeId ;


	private int currentStart = 0;
	private int visibleFirstIndex = 0;   //可视项第一个索引  
	private int currentVisibleItemCount;       // 当前窗口可见项总数 
	private ContributeOnScrollListener contributeOnScrollListener;


	private int totalContributeNumber;//本页面反馈的数量，包括语音和文字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_contribute_receive);
		intentInit();
		titlebarInit();
		viewInit();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_contribute_receive);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ContributeReceiveActivity.this.onBackPressed();

			}
		});
	}



	@Override
	public void onBackPressed() {
		CommonOperateUtil.hideIME(this);
		Intent intent =new Intent();
		intent.putExtra("data", totalContributeNumber);
		setResult(RESULT_OK,intent);
		super.onBackPressed();
	}

	private void intentInit(){
		Intent intent = getIntent();
		outContributeId  = intent.getStringExtra("contributeId");
	}

	private void viewInit(){

		contributePullToRefreshInvertView = (PullToRefreshInvertView) findViewById(R.id.receive_pull_refresh_invert);
		mContributeListView = (ListView) findViewById(R.id.receive_list);
		mContributeInputView = (EditText) findViewById(R.id.receive_input);
		mContributeSendView = (Button) findViewById(R.id.receive_send);
		View emptyView = findViewById(R.id.receive_empty);
		mContributeListView.setEmptyView(emptyView);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMoreContribute(true);
			}
		});
		contributePullToRefreshInvertView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshInvertView view) {
				loadMoreContribute(false);
			}
		});
		contributePullToRefreshInvertView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshInvertView view) {
				loadMoreContribute(true);

			}
		});

		contributeOnScrollListener = new ContributeOnScrollListener();
		mContributeListView.setOnScrollListener(contributeOnScrollListener);


		mContributeInputView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence paramCharSequence, int paramInt1,
					int paramInt2, int paramInt3) {

			}

			@Override
			public void beforeTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {

			}

			@Override
			public void afterTextChanged(Editable paramEditable) {
				if(paramEditable.toString().trim().length() > 0){
					mContributeSendView.setEnabled(true);
					mContributeSendView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					mContributeSendView.setEnabled(false);
					mContributeSendView.setVisibility(ViewGroup.GONE);
				}

			}
		});

		mContributeSendView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendFeedback();
			}
		});

		contributeBeanList = new ArrayList<ContributeBean>();

		adapter = new FeedbackBaseAdapter(contributeBeanList);
		mContributeListView.setAdapter(adapter);


		loadMoreContribute(true);
	}


	static class ContributeViewHolder{
		TextView writterNameView;
		TextView createTimeView;
		TextView contentView;
	}

	private final class FeedbackBaseAdapter extends BaseAdapter{

		private List<ContributeBean> list;

		public FeedbackBaseAdapter(List<ContributeBean> list) {
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
			final ContributeBean contributeBean = (ContributeBean) getItem(position);
			ContributeViewHolder holder = new ContributeViewHolder();
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_contribute_receive_list, null);
				holder = new ContributeViewHolder();
				holder.contentView = (TextView) convertView.findViewById(R.id.receive_item_content);
				holder.createTimeView = (TextView) convertView.findViewById(R.id.receive_item_create_time);
				holder.writterNameView = (TextView) convertView.findViewById(R.id.receive_item_writter_name);
				convertView.setTag(holder);
			}
			else{
				holder = (ContributeViewHolder) convertView.getTag();
			}
			
			holder.contentView.setText(contributeBean.getContent());
			holder.createTimeView.setText(contributeBean.getCreateTime());
			holder.writterNameView.setText(contributeBean.getWritterName());
			
			return convertView;
		}

	}

	/**
	 * 获取回复列表
	 */
	private void loadReceiveListAsyncHttp(FeedbackPagerBean feedbackPagerBean,final boolean isRefresh){
		String url = getString(R.string.http_url_load_contribute_receive_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("contributionPId", outContributeId);
		params.put("start", feedbackPagerBean.getStart());
		params.put("limit", feedbackPagerBean.getLimit());
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
				ResponseBean<List<ContributeBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<ContributeBean>>>(){}.getType());
				List<ContributeBean> robResponse =  responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(robResponse == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				if(robResponse != null &&!robResponse.isEmpty()){
					if(isRefresh){
						contributeBeanList.clear();
						contributeBeanList.addAll(robResponse);
						adapter.notifyDataSetChanged();
						mContributeListView.setSelection(contributeBeanList.size()-1);
					}
					else{
						contributeBeanList.addAll(0,robResponse);
						adapter.notifyDataSetChanged();
					}
				}
				if(isRefresh){
					contributePullToRefreshInvertView.onFooterRefreshComplete();
				}
				else{
					contributePullToRefreshInvertView.onHeaderRefreshComplete();
				}
			}

		});

		//

	}



	/**
	 * 发送文本反馈内容
	 * @param repairOrderId
	 * @param content
	 */
	private void submitReceiveAsyncHttp(final String content){
		String url = getString(R.string.http_url_load_submit_contribute_and_receive,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("contributionPId", outContributeId);
		params.put("title", "Android 回复投稿评论");
		params.put("content", content);
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				hideProgressDialog();
				com.wutong.androidprojectlibary.http.util.HttpResponseUtil.justToast(errorNo, t, mContext);
				mContributeSendView.setEnabled(true);
			}

			@Override
			public void onStart() {
				super.onStart();
				showProgressDialog();
				mContributeSendView.setEnabled(false);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				hideProgressDialog();
				ResponseBean<String> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<String>>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "回复成功");
					mContributeInputView.setText("");
					CommonOperateUtil.hideIME(ContributeReceiveActivity.this);
					//添加到本地
					ContributeBean contributeBean = new ContributeBean();
					contributeBean.setContent(content);
					contributeBean.setWritterId(application.getLoginInfoBean().getUserId().toString());
					contributeBean.setCreateTime(TimeUtil.currentTime());
					contributeBean.setWritterName(application.getLoginInfoBean().getRealName());
					contributeBeanList.add(1,contributeBean);
					totalContributeNumber++;
					adapter.notifyDataSetChanged();
					mContributeListView.setSelection(0);
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}

			}

		});


	}






	public void sendFeedback(){
		final String context =  mContributeInputView.getText().toString();
		if(context == null || context.toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "必须要有内容");

		}
		else{

			submitReceiveAsyncHttp(context);
		}
	}






	private void loadMoreContribute(boolean isRefresh){
		if(isRefresh){
			currentStart = 0;
		}
		else{
			currentStart += application.getPagingSize();
		}
		FeedbackPagerBean complaintPagerBean = new FeedbackPagerBean(outContributeId,currentStart, application.getPagingSize());
		loadReceiveListAsyncHttp(complaintPagerBean, isRefresh);
	}


	private class ContributeOnScrollListener implements OnScrollListener{

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			Logger.i("scrollState:" + scrollState);
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleFirstIndex == 0) {  
				loadMoreContribute(false);  
			} 
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			Logger.i("firstVisibleItem:" + firstVisibleItem);
			visibleFirstIndex = firstVisibleItem;

		}

	}



	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.activity_contribute_receive;

		default:
			return R.layout.activity_contribute_receive;
		}
	}



	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			Drawable emptyTopDraw = getResources().getDrawable(R.drawable.spring_horse_empty_icon_repair_order);
			View emptyView = findViewById(R.id.receive_empty);
			TextView tipView = (TextView) emptyView.findViewById(R.id.empty_tip);
			TextView clickTipView = (TextView) emptyView.findViewById(R.id.empty_click_tip);
			tipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			clickTipView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_list_empty_text_color));
			tipView.setCompoundDrawablesWithIntrinsicBounds(null, emptyTopDraw, null, null);
			break;

		default:
			break;
		}
	}


}
