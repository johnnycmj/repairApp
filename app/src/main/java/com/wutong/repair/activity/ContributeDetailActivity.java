package com.wutong.repair.activity;



import net.tsz.afinal.http.AjaxParams;




import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ContributeBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.fragment.NoticeListFragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 投稿
 * @author Jolly
 * 创建时间：2014年3月28日下午3:39:47
 *
 */
public class ContributeDetailActivity extends BaseActivity {
	
	private final static int REQUEST_CODE_RECEIVE = 12;
	
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	
	private ImageView mCoverView;
	private TextView mContentView;
	private TextView mAuthorView;
	private TextView mLikeNumberView;
	private TextView mReceiveNumberView;
	
	private String noticeId;
	private ContributeBean contributeBean;
	
	private Drawable heartLikeDrawable;
	private Drawable heartNormalDrawable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isNeedLogin = false;
		super.onCreate(savedInstanceState);
		setContentView(selectedLayout());
		setStatPageName(mContext, R.string.title_activity_contribute_deial);
		intentInit();
		titleBarInit();
		viewInit();
	}

	private void titleBarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setVisibility(ViewGroup.VISIBLE);
		titlebarTitleView.setText(R.string.title_activity_contribute_deial);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ContributeDetailActivity.this.onBackPressed();

			}
		});
	}
	
	private void viewInit(){
		heartLikeDrawable = getResources().getDrawable(R.drawable.icon_heart_like);
		heartNormalDrawable = getResources().getDrawable(R.drawable.icon_heart_normal);
		
		mCoverView = (ImageView) findViewById(R.id.contribute_detail_cover);
		mContentView = (TextView) findViewById(R.id.contribute_detail_content);
		mAuthorView = (TextView) findViewById(R.id.contribute_detail_writer_name);
		mReceiveNumberView = (TextView) findViewById(R.id.contribute_detail_receive);
		mLikeNumberView = (TextView) findViewById(R.id.contribute_detail_like);
		mLikeNumberView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(contributeBean!= null){
					if(contributeBean.isAgree()){
						ToastUtil.showToast(mContext, "您已经点过赞了");
					}
					else{
						submitLikeContributeHttp();
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取信息不全，无法点赞");
				}
			}
		});
		mReceiveNumberView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ContributeReceiveActivity.class);
				intent.putExtra("contributeId", contributeBean.getId());
				startActivityForResult(intent, REQUEST_CODE_RECEIVE);
			}
		});
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
	
	
	@Override
	public void onBackPressed() {
		Intent intent =new Intent();
		intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_notice_list_name));
		intent.putExtra(getString(R.string.call_fragment_key_code), NoticeListFragment.CALL_CODE_REFRESH);//公告刷新
		setResult(RESULT_OK,intent);
		super.onBackPressed();
	}

	/**
	 * 通知详细
	 */
	private void loadContributeDetailAsyncHttp(){
		
		String url = getString(R.string.http_url_load_contribute_detail,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("contributionId", noticeId);
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
				ResponseBean<ContributeBean> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<ContributeBean>>(){}.getType());
				if(responseBean.isSuccess()){
					ContributeBean resultBean = responseBean.getData();
					if(resultBean != null ){
						contributeBean = resultBean;
						mContentView.setText(resultBean.getContent());
						mAuthorView.setText(resultBean.getWritterName());
						mReceiveNumberView.setText(resultBean.getReceiveNumber());
						mLikeNumberView.setText(resultBean.getLikeNumber());
						if(resultBean.isAgree()){
							mLikeNumberView.setCompoundDrawablesWithIntrinsicBounds(heartLikeDrawable, null, null, null);
						}
						else{
							mLikeNumberView.setCompoundDrawablesWithIntrinsicBounds(heartNormalDrawable, null, null, null);
						}
						if(resultBean.getCoverImage() !=null){
							mCoverView.setVisibility(ViewGroup.VISIBLE);
							application.getImageLoader().displayImage(resultBean.getCoverImage().getUrl(), mCoverView,application.getSimpleDisplayImageOptions());
							
						}
						else{
							mCoverView.setVisibility(ViewGroup.GONE);
						}
					}
					else{
						ToastUtil.showToast(mContext, "返回投稿数据有错");
					}
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}

			}

		});
		
		
	}
	
	
	private void submitLikeContributeHttp(){
		String url = getString(R.string.http_url_load_submit_like_contribute,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("contributionId", noticeId);
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
				ResponseBean<String> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<String>>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "点赞成功");
					mLikeNumberView.setText(responseBean.getData());
					contributeBean.setAgree(true);
					mLikeNumberView.setCompoundDrawablesWithIntrinsicBounds(heartLikeDrawable, null, null, null);
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}

			}

		});
	}
	
	
	
	private int selectedLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_contribute_detail;

		default:
			return R.layout.spring_horse_activity_contribute_detail;
		}
	}
}
