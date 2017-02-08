package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.Collection;

import net.tsz.afinal.http.AjaxParams;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.activity.ContributeSubmitActivity;
import com.wutong.repair.activity.EditInfoActivity;
import com.wutong.repair.activity.PhotoImageShowActivity;
import com.wutong.repair.data.bean.MicroShareBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;

public class MicroShareFragment extends BaseFragment {
	
	private TextView titlebarTitleView;
	private ImageView titlebarBackView;
	
	private ImageView titlebarRightLineView;
	private TextView titlebarcontributeView;
	private ImageView mPicturePerDayIv;
	private TextView mDayOfDateTv;
	private TextView mYearMonthOfDateTv;
	private TextView mContentTv;
	
	private TextView mLikeTv;
	
	private ImageView mContributeImgView;
	private TextView mContributeTipView;
	
	private MicroShareBean microShareBean;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		dataInit();
		argumentInit();
		titlebarInit();
		viewInit();
		setupData();
		return fragmentView;
	}
	
	private void dataInit(){
		
	}
	
	private void argumentInit(){
		
	}
	
	private void titlebarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(R.string.title_fragment_micro_share);
		titlebarcontributeView = (TextView) findViewById(R.id.titlebar_tips);
		titlebarcontributeView.setText("投稿箱");
		titlebarRightLineView = (ImageView) findViewById(R.id.titlebar_right_line);
		titlebarcontributeView.setVisibility(ViewGroup.VISIBLE);
		titlebarRightLineView.setVisibility(ViewGroup.VISIBLE);
		
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
				MicroShareFragment.this.getActivity().onBackPressed();

			}
		});
		
		titlebarcontributeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, EditInfoActivity.class);
				intent.putExtra("fragmentLabel", getString(R.string.fragment_url_contribute_list));
				MicroShareFragment.this.getActivity().startActivity(intent);
			}
		});
	}
	private void viewInit(){
		setFragmentPageName(mContext, R.string.title_fragment_micro_share);
		mPicturePerDayIv = (ImageView) findViewById(R.id.micro_share_picture_per_day);
		mDayOfDateTv = (TextView) findViewById(R.id.micro_share_day_of_date);
		mYearMonthOfDateTv = (TextView) findViewById(R.id.micro_share_year_month_of_date);
		mContentTv = (TextView) findViewById(R.id.micro_share_content);
		mLikeTv = (TextView) findViewById(R.id.micro_share_like);
		
		mContributeImgView = (ImageView) findViewById(R.id.micro_share_contribute_img);
		mContributeTipView = (TextView) findViewById(R.id.micro_share_contribute_tip);
		mLikeTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				submitLikeShareHttp();
			}
		});
		
		OnClickListener publishClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, ContributeSubmitActivity.class);
				intent.putExtra("isFromIndex", true);
				startActivity(intent);
			}
		};
		mContributeImgView.setOnClickListener(publishClickListener);
		mContributeTipView.setOnClickListener(publishClickListener);
	}
	
	private void setupData(){
		//Fix分支
			mContributeImgView.setVisibility(ViewGroup.VISIBLE);
			mContributeTipView.setVisibility(ViewGroup.VISIBLE);
			titlebarcontributeView.setVisibility(ViewGroup.GONE);
			titlebarRightLineView.setVisibility(ViewGroup.GONE);
		loadMicroShareHttp();
	}
	
	private void loadMicroShareHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getMicroShareLoad());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<MicroShareBean> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<MicroShareBean>>(){}.getType());
				final MicroShareBean result = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				else if(result == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					microShareBean = result;
					mContentTv.setText(result.getContent());
					mDayOfDateTv.setText(result.getDayOfDate());
					mYearMonthOfDateTv.setText(result.getYearMonthOfDate());
					mLikeTv.setText(result.getLikeNumber());
					String url = result.getPictureUrl();
					if(url != null && url.trim().length() > 0){
						application.getImageLoader().displayImage(result.getPictureUrl(), mPicturePerDayIv,application.getSimpleDisplayImageOptions());
					}
					else{
						mPicturePerDayIv.setImageResource(R.drawable.image_default);
					}
					
					mPicturePerDayIv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setClass(mContext, PhotoImageShowActivity.class);
							Bundle bundle = new Bundle();
							ArrayList<String> imageList = new ArrayList<String>();
							imageList.add(result.getPictureUrl());
							bundle.putStringArrayList("imageList",imageList);
							bundle.putInt("imageType", PhotoImageShowActivity.HTTP_URL_TYPE);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					});
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
	
	private void submitLikeShareHttp(){
		String url = getString(R.string.http_url_load_submit_like_share,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("onePictureId", microShareBean.getPictrueId());
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
					mLikeTv.setText(responseBean.getData());
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}

			}

		});
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_fragment_micro_share;

		default:
			return R.layout.fragment_micro_share;
		}
	}
}
