package com.wutong.repair.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFileFormBean;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpFileAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.common.widget.PullToRefreshInvertView;
import com.wutong.common.widget.PullToRefreshInvertView.OnFooterRefreshListener;
import com.wutong.common.widget.PullToRefreshInvertView.OnHeaderRefreshListener;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.FeedbackBean;
import com.wutong.repair.data.bean.FeedbackPagerBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.StringUtil;
import com.wutong.repair.util.TimeUtil;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class FeedbackActivity extends BaseActivity{
	
	private final static int VIEW_CODE_FEEDBACK_USEFUL_ALERT = 98;
	private final static int VIEW_CODE_FEEDBACK_USEFUL_LIST_ITEM = 99;

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private final static int UP_DULATION = 60;//最长录音长度
	private int maxVoiceWidth;
	private int minVoiceWidth;
	private int voiceDifferenceWidth;

	private PullToRefreshInvertView feedbackPullToRefreshInvertView;

	private ListView mFeedbackListView;
	private BaseAdapter adapter;
	private List<FeedbackBean> feedbackBeanList;
	private View alertView;
	private CustomDialog useFullDialog;
	private CustomDialog voiceTalkingDialog;
	private SharedPreferences preferences;
	private List<String> usefulExpressionList;
	private EditText mFeedbackInputView;
	private ImageView mfeedBackUsefulExpresstionView;
//	private ImageView mfeedBackSwitchVoiceView;
//	private ImageView mfeedBackSwitchKeyboardView;
	private Button mFeedbackSendView;
	private Button mFeedbackPressToVoiceView;
	private String repairOrderId ;
	private AnimationDrawable anim;
	private AnimationDrawable currentAnim;

	private MediaRecorder mediaRecorder;
	private MediaPlayer mediaPlayer;
	private String savePath;
	private File recordFile;

	private int currentStart = 0;
	private int visibleFirstIndex = 0;   //可视项第一个索引  
	private int currentVisibleItemCount;       // 当前窗口可见项总数 
	private FeedbackOnScrollListener feedbackOnScrollListener;

	private int userId;//用户id

	private int totalFeedbackNumber;//本页面反馈的数量，包括语音和文字

	private boolean outIsLargeCharacter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_feedback);
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
		checkPlayAndStop();
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_feedback);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FeedbackActivity.this.onBackPressed();

			}
		});
	}



	@Override
	public void onBackPressed() {
		CommonOperateUtil.hideIME(this);
		Intent intent =new Intent();
		intent.putExtra("data", totalFeedbackNumber);
		setResult(RESULT_OK,intent);
		super.onBackPressed();
	}

	private void intentInit(){
		Intent intent = getIntent();
		repairOrderId  = intent.getStringExtra("repairOrderId");
		outIsLargeCharacter = intent.getBooleanExtra("isLargeCharacter",false);
	}

	@SuppressWarnings("unchecked")
	private void viewInit(){
		maxVoiceWidth = getResources().getDimensionPixelSize(R.dimen.feedback_void_max_lenght);
		minVoiceWidth = getResources().getDimensionPixelSize(R.dimen.feedback_void_min_lenght);
		voiceDifferenceWidth = maxVoiceWidth - minVoiceWidth;
		userId = application.getLoginInfoBean().getUserId().intValue();

		preferences = getSharedPreferences(SettingConfig.FAST_MESSAGE_CONFIG, MODE_PRIVATE);
		feedbackPullToRefreshInvertView = (PullToRefreshInvertView) findViewById(R.id.feedback_pull_refresh_invert);
		mFeedbackListView = (ListView) findViewById(R.id.feedback_list);
		mFeedbackInputView = (EditText) findViewById(R.id.feedback_input);
		mFeedbackSendView = (Button) findViewById(R.id.feedback_send);
//		mfeedBackSwitchVoiceView = (ImageView) findViewById(R.id.feedback_switch_voice);
//		mfeedBackSwitchKeyboardView = (ImageView) findViewById(R.id.feedback_switch_keyboard);
		mFeedbackPressToVoiceView = (Button) findViewById(R.id.feedback_press_voice);
		View emptyView = findViewById(R.id.feedback_empty);
		mFeedbackListView.setEmptyView(emptyView);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadMoreFeedback(true);
			}
		});
		feedbackPullToRefreshInvertView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

			@Override
			public void onHeaderRefresh(PullToRefreshInvertView view) {
				loadMoreFeedback(false);
			}
		});
		feedbackPullToRefreshInvertView.setOnFooterRefreshListener(new OnFooterRefreshListener() {

			@Override
			public void onFooterRefresh(PullToRefreshInvertView view) {
				loadMoreFeedback(true);

			}
		});

		feedbackOnScrollListener = new FeedbackOnScrollListener();
		mFeedbackListView.setOnScrollListener(feedbackOnScrollListener);

		ImageView imageView = new ImageView(this);
		anim = (AnimationDrawable) getResources().getDrawable(R.drawable.voice_talking_anim);
		imageView.setImageDrawable(anim);
		voiceTalkingDialog = new CustomDialog.Builder(this)
		.setView(imageView)
		.create();
		
//		mfeedBackSwitchVoiceView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				v.setVisibility(ViewGroup.GONE);//隐藏自己
//				mfeedBackSwitchKeyboardView.setVisibility(ViewGroup.VISIBLE);//显示键盘
//				mFeedbackInputView.setVisibility(ViewGroup.GONE);//隐藏输入框
//				mFeedbackSendView.setVisibility(ViewGroup.GONE);//隐藏发送按钮
//				if(application.hasPermission(getString(R.string.roles_permissions_fast_expresstion))){
//					mfeedBackUsefulExpresstionView.setVisibility(ViewGroup.GONE);
//				}
//				mFeedbackPressToVoiceView.setVisibility(ViewGroup.VISIBLE);//显示按住说话按钮
//			}
//		});

		mFeedbackInputView.addTextChangedListener(new TextWatcher() {

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
					mFeedbackSendView.setEnabled(true);
					mFeedbackSendView.setVisibility(ViewGroup.VISIBLE);
					mfeedBackUsefulExpresstionView.setVisibility(ViewGroup.GONE);
				}
				else{
					mFeedbackSendView.setEnabled(false);
					mFeedbackSendView.setVisibility(ViewGroup.GONE);
					if(application.hasPermission(getString(R.string.roles_permissions_fast_expresstion))){
						mfeedBackUsefulExpresstionView.setVisibility(ViewGroup.VISIBLE);
					}
				}

			}
		});

//		mfeedBackSwitchKeyboardView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				v.setVisibility(ViewGroup.GONE);//隐藏自己
//				mfeedBackSwitchVoiceView.setVisibility(ViewGroup.VISIBLE);//显示键盘
//				mFeedbackInputView.setVisibility(ViewGroup.VISIBLE);//隐藏输入框
//				if(mFeedbackInputView.getText().toString().trim().length() >0){
//					mFeedbackSendView.setVisibility(ViewGroup.VISIBLE);//隐藏发送按钮
//				}
//				if(application.hasPermission(getString(R.string.roles_permissions_fast_expresstion))
//						&& mFeedbackInputView.getText().toString().trim().length() == 0){
//					mfeedBackUsefulExpresstionView.setVisibility(ViewGroup.VISIBLE);
//				}
//				mFeedbackPressToVoiceView.setVisibility(ViewGroup.GONE);//显示按住说话按钮
//			}
//		});
		
		mFeedbackPressToVoiceView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					voiceTalkingDialog.show();
					anim.start();
					voiceTalkInit();
					//开始录音
					try {
						synchronized (mediaRecorder) {
							mediaRecorder.prepare();
							mediaRecorder.start();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case MotionEvent.ACTION_UP:
					anim.stop();
					voiceTalkingDialog.dismiss();
					try {
						synchronized (mediaRecorder) {
							mediaRecorder.stop();
							mediaRecorder.release();
						}
						//上传
						MediaPlayer mediaPlayerTemp = new MediaPlayer();
						mediaPlayerTemp.setDataSource(recordFile.getAbsolutePath());
						mediaPlayerTemp.prepare();
						int duration = mediaPlayerTemp.getDuration();
						mediaPlayerTemp.release();
						if(duration <500){
							Logger.i("录音长度：" + duration);
							ToastUtil.showToast(mContext, "录音时间不能太短");
						}
						else{
							duration = (int) Math.ceil(duration *1.0/1000);//转化成秒
							submitFeedbackRecordAsyncHttp(recordFile.getAbsolutePath(),duration);
						}
					} catch (Exception e) {
						e.printStackTrace();
						if(e.getMessage().contains("stop failed")){
							ToastUtil.showToast(mContext, "录音时间不能太短");
						}
					}
					break;
				default:
					Logger.w("意外的events:" +event.getAction());
					break;
				}
				return true;
			}
		});
		mFeedbackInputView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch(actionId){  
				case EditorInfo.IME_NULL:  
					sendFeedback();
					Logger.i("null for default_content: " + v.getText() );  
					break;  
				case EditorInfo.IME_ACTION_SEND:
					sendFeedback();
					Logger.i("action send for email_content: "  + v.getText());  
					break;  
				case EditorInfo.IME_ACTION_DONE:
					sendFeedback();
					Logger.i("action done for number_content: "  + v.getText());  
					break;  
				}  
				return true;
			}
		});
		mFeedbackSendView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendFeedback();
			}
		});

		feedbackBeanList = new ArrayList<FeedbackBean>();

		adapter = new FeedbackBaseAdapter(feedbackBeanList);
		mFeedbackListView.setAdapter(adapter);
		mfeedBackUsefulExpresstionView = (ImageView) findViewById(R.id.feedback_useful_expressions);
		//判断学生还是维修工
		if(application.hasPermission(getString(R.string.roles_permissions_fast_expresstion))){
			//
			mfeedBackUsefulExpresstionView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showUsefulExpressionForView();
				}
			});
			alertView = LayoutInflater.from(mContext).inflate(selectedItemView(VIEW_CODE_FEEDBACK_USEFUL_ALERT), null);

			ListView usefulExpresstionView = (ListView) alertView.findViewById(R.id.feedback_useful_expression_listview);

			innerFastMessagesInit();
			usefulExpressionList = new ArrayList<String>();
			Map<String, String> usefulExpresstions = (Map<String, String>) preferences.getAll();
			Set<String> keys = new TreeMap<String, String>(usefulExpresstions).keySet();
			for(String key:keys){
				usefulExpressionList.add(usefulExpresstions.get(key));
			}
			usefulExpresstionView.setAdapter(new UsefulExpressionBaseAdapter());
			usefulExpresstionView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> paramAdapterView,
						View paramView, int paramInt, long paramLong) {
					if(paramInt < usefulExpressionList.size()){
						mFeedbackInputView.setText(usefulExpressionList.get(paramInt));
						useFullDialog.dismiss();
					}
				}
			});
		}
		else{
			mfeedBackUsefulExpresstionView.setVisibility(ViewGroup.GONE);
		}

		if(outIsLargeCharacter){
			mFeedbackInputView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		}
		else{
			mFeedbackInputView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		}

		loadMoreFeedback(true);
	}




	private final class FeedbackBaseAdapter extends BaseAdapter{

		private List<FeedbackBean> list;

		public FeedbackBaseAdapter(List<FeedbackBean> list) {
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
			final FeedbackBean feedbackBean = (FeedbackBean) getItem(position);
			if(userId == feedbackBean.getFeedbackUserId().intValue()){
				if(outIsLargeCharacter){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_feedback_right_large, null);
				}
				else{
					convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_feedback_right, null);
				}
				TextView feedbackNameView = (TextView) convertView.findViewById(R.id.feedback_name);
				feedbackNameView.setText("我");
			}
			else{
				if(outIsLargeCharacter){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_feedback_left_large, null);
				}
				else{
					convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_feedback_left, null);
				}
				TextView feedbackNameView = (TextView) convertView.findViewById(R.id.feedback_name);
				feedbackNameView.setText(feedbackBean.getFeedbackName());
			}

			View feedbackVoiceLayout = convertView.findViewById(R.id.feedback_voice_layout);
			View feedbackContentLayout = convertView.findViewById(R.id.feedback_content_layout);
			TextView feedbackContentView = (TextView) convertView.findViewById(R.id.feedback_content);
			TextView feedbackTimeView = (TextView) convertView.findViewById(R.id.feedback_time);
			ImageView feedbackContentVoiceView = (ImageView) convertView.findViewById(R.id.feedback_content_voice);

			if(feedbackBean.getContentType()== null){
				ToastUtil.showToast(mContext, "position:" +position +"contentType 异常");
				return convertView;
			}
			else if(feedbackBean.getContentType().equals("1")){
				feedbackVoiceLayout.setVisibility(ViewGroup.GONE);
				feedbackContentLayout.setVisibility(ViewGroup.VISIBLE);
				feedbackContentView.setVisibility(ViewGroup.VISIBLE);
				feedbackContentView.setText(feedbackBean.getContent());
				feedbackTimeView.setText(feedbackBean.getTime());
			}
			else if(feedbackBean.getContentType().equals("2")){
				feedbackVoiceLayout.setVisibility(ViewGroup.VISIBLE);
				feedbackContentLayout.setVisibility(ViewGroup.GONE);
				feedbackContentView.setVisibility(ViewGroup.GONE);
				final ProgressBar prepareView = (ProgressBar) convertView.findViewById(R.id.prepare_loading);
				feedbackTimeView.setText(feedbackBean.getTime());
				final AnimationDrawable leftAnim = (AnimationDrawable) feedbackContentVoiceView.getDrawable();
				RelativeLayout voiceBodyView = (RelativeLayout) convertView.findViewById(R.id.feedback_voice_body);
				TextView voiceDuration = (TextView) convertView.findViewById(R.id.feedback_voice_duration);
				try{
					int duration=0;
					if(feedbackBean.getDuration() != null){
						duration = feedbackBean.getDuration();
					}
					Logger.i("duration:" +duration);
					LayoutParams lp = voiceBodyView.getLayoutParams();
					if(duration >= UP_DULATION){
						//最长
						lp.width = maxVoiceWidth;
					}
					else{
						lp.width = (int) (minVoiceWidth + duration * 1.0/UP_DULATION * voiceDifferenceWidth);
					}

					voiceBodyView.setLayoutParams(lp);
					voiceDuration.setText(duration+"″");
				}catch (Exception e) {
					e.printStackTrace();
				}

				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(mediaPlayer != null && mediaPlayer.isPlaying()){
							mediaPlayer.stop();
						}
						if(currentAnim != null &&currentAnim.isRunning()){
							currentAnim.stop();
							currentAnim.selectDrawable(0);
						}
						mediaPlayer = new MediaPlayer(); 
						try {
							leftAnim.start();
							prepareView.setVisibility(ViewGroup.VISIBLE);
							mediaPlayer.setDataSource(mContext,Uri.parse(feedbackBean.getContent()));
							mediaPlayer.prepare();
							mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									currentAnim.stop();
									currentAnim.selectDrawable(0);
								}
							});
							mediaPlayer.start();
							leftAnim.start();
							currentAnim = leftAnim;
							prepareView.setVisibility(ViewGroup.GONE);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				});
			}
			return convertView;
		}

	}

	/**
	 * 获取反馈列表
	 */
	private void loadFeedbackListAsyncHttp(FeedbackPagerBean feedbackPagerBean,final boolean isRefresh){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getFeedbackListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair repairFormIdPair = new BasicNameValuePair("repairFormId", feedbackPagerBean.getId());
		params.add(repairFormIdPair);
		NameValuePair startPair = new BasicNameValuePair("start", feedbackPagerBean.getStart());
		params.add(startPair);
		NameValuePair limitPair = new BasicNameValuePair("limit", feedbackPagerBean.getLimit());
		params.add(limitPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<FeedbackBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<FeedbackBean>>>(){}.getType());
				List<FeedbackBean> robResponse =  responseBean.getData();
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
						feedbackBeanList.clear();
						feedbackBeanList.addAll(robResponse);
						adapter.notifyDataSetChanged();
						mFeedbackListView.setSelection(feedbackBeanList.size()-1);
					}
					else{
						feedbackBeanList.addAll(0,robResponse);
						adapter.notifyDataSetChanged();
					}
				}
				if(isRefresh){
					feedbackPullToRefreshInvertView.onFooterRefreshComplete();
				}
				else{
					feedbackPullToRefreshInvertView.onHeaderRefreshComplete();
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



	/**
	 * 发送文本反馈内容
	 * @param repairOrderId
	 * @param content
	 */
	private void submitFeedbackTextAsyncHttp(String repairOrderId,final String content){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getFeedbackTextAddUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair repairFormIdPair = new BasicNameValuePair("repairFormId", repairOrderId);
		params.add(repairFormIdPair);
		NameValuePair userIdPair = new BasicNameValuePair("userId", application.getLoginInfoBean().getUserId().toString());
		params.add(userIdPair);
		NameValuePair contentPair = new BasicNameValuePair("content", content);
		params.add(contentPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "反馈成功");
					mFeedbackInputView.setText("");
					CommonOperateUtil.hideIME(FeedbackActivity.this);
					//添加到本地
					FeedbackBean feedbackBean = new FeedbackBean();
					feedbackBean.setContentType("1");
					feedbackBean.setContent(content);
					feedbackBean.setFeedbackUserId(application.getLoginInfoBean().getUserId().intValue());
					feedbackBean.setTime(TimeUtil.currentTime());
					feedbackBean.setFeedbackName(application.getLoginInfoBean().getRealName());
					feedbackBeanList.add(feedbackBean);
					totalFeedbackNumber++;
					adapter.notifyDataSetChanged();
					mFeedbackListView.setSelection(feedbackBeanList.size()-1);
				}
				else{
					ToastUtil.showToast(mContext, "反馈失败");
				}
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
				mFeedbackSendView.setEnabled(true);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog(R.string.tips_for_collecting_for_submit);
				mFeedbackSendView.setEnabled(false);
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}






	public void sendFeedback(){
		final String context =  mFeedbackInputView.getText().toString();
		if(context == null || context.toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "必须要有内容");

		}
		else{

			submitFeedbackTextAsyncHttp(repairOrderId,context);
		}
	}


	private void showUsefulExpressionForView(){
		if(useFullDialog == null){
			useFullDialog = new CustomDialog.Builder(mContext).setTitle("快速回复")
					.setView(alertView)
					.create();
		}
		useFullDialog.show();
	}

	private class UsefulExpressionBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return usefulExpressionList.size();
		}

		@Override
		public Object getItem(int paramInt) {
			return usefulExpressionList.get(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			return paramInt;
		}

		@Override
		public View getView(int paramInt, View convertView,
				ViewGroup paramViewGroup) {
			convertView = LayoutInflater.from(mContext).inflate(selectedItemView(VIEW_CODE_FEEDBACK_USEFUL_LIST_ITEM), null);
			TextView textView = (TextView) convertView.findViewById(R.id.useful_expression_content);
			textView.setText(getItem(paramInt).toString());
			return convertView;
		}

	}

	private void innerFastMessagesInit(){
		//检查反馈
		SharedPreferences defaultPreferences = application.getDefaultPreferences();
		boolean usefulExpresstionFlag = defaultPreferences.getBoolean(SettingConfig.FirstRunning.FEEDBACK_USEFUL_EXPRESSION_FLAG, false);
		if(!usefulExpresstionFlag){
			//初始化反馈的快速回复默认项
			//提取已经自定义的值
			List<String> fastMessageList = new ArrayList<String>();
			SharedPreferences fastMessagePreferences = getSharedPreferences(SettingConfig.FAST_MESSAGE_CONFIG, MODE_PRIVATE);
			Map<String, String> usefulExpresstions = (Map<String, String>) fastMessagePreferences.getAll();

			Set<String> keys = new TreeMap<String, String>(usefulExpresstions).keySet();
			for(String key:keys){
				fastMessageList.add(usefulExpresstions.get(key));
			}
			String[] innerExpressions = getResources().getStringArray(R.array.inner_useful_expresstions);
			for(String expresstion:innerExpressions){
				fastMessageList.add(expresstion);
			}
			//
			String msg;
			Editor editor = fastMessagePreferences.edit();
			for(int i = 0; i <fastMessageList.size(); i++){
				msg = fastMessageList.get(i);
				editor.putString(String.format("%1$04d", i), msg);
			}
			editor.commit();
			//修改flag为true
			defaultPreferences.edit().putBoolean(SettingConfig.FirstRunning.FEEDBACK_USEFUL_EXPRESSION_FLAG, true).commit();
		}
	}

	private void voiceTalkInit(){
		savePath = Environment.getExternalStorageDirectory().getPath() + "/record.amr";
		recordFile = new File(savePath);
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
		
		//
		mediaRecorder.setAudioEncodingBitRate(16);
		mediaRecorder.setAudioSamplingRate(8000);
		mediaRecorder.setAudioChannels(1);
	}

	/**
	 * 录音
	 */
	private void submitFeedbackRecordAsyncHttp(final String path,final int duration){
		HttpFileAsyncTask httpFileAsyncTask = new HttpFileAsyncTask(mContext);
		HttpFileFormBean httpFileFormBean = new HttpFileFormBean();
		httpFileFormBean.setUrl(application.getCommonHttpUrlActionManager().getFeedbackRecordAddUrl());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("repairFormId",repairOrderId );
		paramMap.put("userId",application.getLoginInfoBean().getUserId().toString());
		paramMap.put("remark","android remark");
		paramMap.put("second",String.valueOf(duration));
		httpFileFormBean.setParams(paramMap);
		Map<String, String> fileMap = new HashMap<String, String>();
		fileMap.put(String.valueOf(new Date().getTime()), path);
		httpFileFormBean.setFileMap(fileMap);
		httpFileAsyncTask.setOnDealtListener(new HttpFileAsyncTask.OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					//添加到本地
					FeedbackBean feedbackBean = new FeedbackBean();
					feedbackBean.setContentType("2");
					feedbackBean.setContent(path);
					feedbackBean.setDuration(duration);
					feedbackBean.setFeedbackUserId(application.getLoginInfoBean().getUserId().intValue());
					feedbackBean.setTime(TimeUtil.currentTime());
					feedbackBean.setFeedbackName(application.getLoginInfoBean().getRealName());
					feedbackBeanList.add(feedbackBean);
					totalFeedbackNumber++;
					adapter.notifyDataSetChanged();
					mFeedbackListView.setSelection(feedbackBeanList.size()-1);
				}
				else{
					ToastUtil.showToast(mContext, "反馈语音失败");
				}
				hideProgressDialog();
				loadMoreFeedback(true);
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog(R.string.tips_for_collecting_for_submit);
			}
		});
		httpFileAsyncTask.execute(httpFileFormBean);

	}



	private void loadMoreFeedback(boolean isRefresh){
		if(isRefresh){
			currentStart = 0;
		}
		else{
			currentStart += application.getPagingSize();
		}
		FeedbackPagerBean complaintPagerBean = new FeedbackPagerBean(repairOrderId,currentStart, application.getPagingSize());
		loadFeedbackListAsyncHttp(complaintPagerBean, isRefresh);
	}


	private class FeedbackOnScrollListener implements OnScrollListener{

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			Logger.i("scrollState:" + scrollState);
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleFirstIndex == 0) {  
				loadMoreFeedback(false);  
			} 
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			Logger.i("firstVisibleItem:" + firstVisibleItem);
			visibleFirstIndex = firstVisibleItem;

		}

	}

	private void checkPlayAndStop(){
		//语音停止
		if(mediaPlayer != null && mediaPlayer.isPlaying()){
			mediaPlayer.stop();
		}
	}

	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_feedback;

		default:
			return R.layout.activity_feedback;
		}
	}
	
	private int selectedItemView(int viewCode){
		switch (application.getSkinType()) {
		case 1:
			switch (viewCode) {
			case VIEW_CODE_FEEDBACK_USEFUL_ALERT:
				return R.layout.spring_horse_feedback_useful_expression_alert;
			case VIEW_CODE_FEEDBACK_USEFUL_LIST_ITEM:
				return R.layout.spring_horse_listview_useful_expression;
			default:
				break;
			}

		default:
			switch (viewCode) {
			case VIEW_CODE_FEEDBACK_USEFUL_ALERT:
				return R.layout.feedback_useful_expression_alert;
			case VIEW_CODE_FEEDBACK_USEFUL_LIST_ITEM:
				return R.layout.listview_useful_expression;
			default:
				break;
			}
		}
		return 0;
	}

	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			Drawable emptyTopDraw = getResources().getDrawable(R.drawable.spring_horse_empty_icon_repair_order);
			View emptyView = findViewById(R.id.feedback_empty);
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
