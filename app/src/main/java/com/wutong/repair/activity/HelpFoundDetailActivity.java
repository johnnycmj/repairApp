package com.wutong.repair.activity;




import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;












import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.HelpFoundBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.fragment.NoticeListFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 招领详情
 * @author Jolly
 * 创建时间：2014年3月28日下午3:39:47
 *
 */
public class HelpFoundDetailActivity extends BaseActivity {


	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private ImageView mCoverView;
	private TextView mRemarkView;
	private TextView mAuthorView;
	private TextView mPublishTimeView;
	private TextView mFoundTimeView;
	private TextView mPlaceView;
	private View mCallAreaView;
	private ImageView mCallView;
	private TextView mItemTypeView;
	private View mLosterLayout;
	private TextView mLosterInfoView;
	private String noticeId;
	private HelpFoundBean helpFoundBean;

	private Button mFinishView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isNeedLogin = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_found_detail);
		setStatPageName(mContext, R.string.title_help_found_detail);
		intentInit();
		titleBarInit();
		viewInit();
	}

	private void titleBarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setVisibility(ViewGroup.VISIBLE);
		titlebarTitleView.setText(mTitle);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HelpFoundDetailActivity.this.onBackPressed();

			}
		});
	}

	private void viewInit(){

		mCoverView = (ImageView) findViewById(R.id.found_detail_cover);
		mRemarkView = (TextView) findViewById(R.id.found_detail_remark);
		mAuthorView = (TextView) findViewById(R.id.found_detail_writter_name);
		mCallAreaView = findViewById(R.id.found_detail_dial_area);
		mCallView = (ImageView) findViewById(R.id.found_detail_dial_call_img);
		mPublishTimeView = (TextView) findViewById(R.id.found_detail_publish_time);
		mPlaceView  = (TextView) findViewById(R.id.found_detail_place);
		mItemTypeView  = (TextView) findViewById(R.id.found_detail_item_type);
		mFoundTimeView  = (TextView) findViewById(R.id.found_detail_found_time);
		mLosterLayout = findViewById(R.id.found_detail_lost_info_area);
		mLosterInfoView  = (TextView) findViewById(R.id.found_detail_contact);
		mFinishView = (Button) findViewById(R.id.found_detail_finish);

		mFinishView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CustomDialog queryDialog = new CustomDialog.Builder(mContext).setTitle("友情提示")
						.setMessage("小微提醒：确定关闭之后别人将看不到该条信息哦(*>﹏<*)~~")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								submitFinishAsyncHttp();
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.create();

				queryDialog.show();
			}
		});
		
	}

	private void intentInit(){
		noticeId = getIntent().getStringExtra("contributeId");
		boolean isMyPublish = getIntent().getBooleanExtra("isMyPublish", false);
		if(isMyPublish){
			mTitle = getString(R.string.title_help_found_detail_my_publish);
		}
		else{
			mTitle = getString(R.string.title_help_found_detail);
		}
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

		String url = getString(R.string.http_url_load_lost_or_found_detail,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("lostFoundId", noticeId);
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
				ResponseBean<HelpFoundBean> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<HelpFoundBean>>(){}.getType());
				if(responseBean.isSuccess()){
					final HelpFoundBean resultBean = responseBean.getData();
					if(resultBean != null ){
						helpFoundBean = resultBean;
						mRemarkView.setText(resultBean.getContent());
						mAuthorView.setText(resultBean.getWritterName());
						mPublishTimeView.setText(resultBean.getCreateTime());
						mFoundTimeView.setText(resultBean.getFoundTime());
						if(resultBean.getAddress()!=null &&resultBean.getAddress().trim().length() > 0){
							mPlaceView.setText(resultBean.getPlaceName() +"("+ resultBean.getAddress()+")");
						}
						else{
							mPlaceView.setText(resultBean.getPlaceName());
						}
						mItemTypeView.setText(resultBean.getItemTypeName());
						if(resultBean.getContact() != null && resultBean.getContact().trim().length() > 0){
							mLosterLayout.setVisibility(ViewGroup.VISIBLE);
							mLosterInfoView.setText(resultBean.getContact());
							mLosterInfoView.getPaint().setFakeBoldText(true);
						}
						else{
							mLosterLayout.setVisibility(ViewGroup.GONE);
						}
						mCoverView.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.setClass(mContext, PhotoImageShowActivity.class);
								Bundle bundle = new Bundle();
								ArrayList<String> imageList = new ArrayList<String>();
								imageList.add(resultBean.getCoverImage().getUrl());
								bundle.putStringArrayList("imageList",imageList);
								bundle.putInt("imageType", PhotoImageShowActivity.HTTP_URL_TYPE);
								intent.putExtras(bundle);
								startActivity(intent);
							}
						});
						mCallView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								String phone = resultBean.getPhone();
								if(phone.toString().trim().length() == 0 || !resultBean.isPublicContact()){
									CustomDialog queryDialog = null;
									queryDialog = new CustomDialog.Builder(mContext).setTitle("友情提示")
											.setMessage("好心人没有公开联系方式，是否联系小微帮忙解决？")
											.setPositiveButton("是", new DialogInterface.OnClickListener() {

												@Override
												public void onClick(DialogInterface dialog,
														int which) {
													dialog.dismiss();
													Intent intent = new Intent(Intent.ACTION_DIAL ,Uri.parse ("tel:" +getString(R.string.tel_customer_service_phone))); 
													startActivity(intent);
												}
											})
											.setNegativeButton("否", new DialogInterface.OnClickListener() {

												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
												}
											})
											.create();
									queryDialog.show();

								}
								else{
									Intent intent = new Intent(Intent.ACTION_DIAL ,Uri.parse ("tel:" +phone)); 
									startActivity(intent);

								}
							}
						});
						if(resultBean.getCoverImage() !=null){
							mCoverView.setVisibility(ViewGroup.VISIBLE);
							application.getImageLoader().displayImage(resultBean.getCoverImage().getUrl(), mCoverView,application.getSimpleDisplayImageOptions());

						}
						else{
							mCoverView.setVisibility(ViewGroup.GONE);
						}

						if(resultBean.getStatus().equals("1") && application.getLoginInfoBean().getUserId().toString().equals(resultBean.getWritterId())){
							mFinishView.setVisibility(ViewGroup.VISIBLE);
						}
						else{
							mFinishView.setVisibility(ViewGroup.GONE);
						}
					}
					else{
						ToastUtil.showToast(mContext, "返回数据有错");
					}
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}

			}

		});


	}

	/**
	 * 完成/结束掉
	 */
	private void submitFinishAsyncHttp(){
		String url = getString(R.string.http_url_load_lost_or_found_finish,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("lostFoundId", noticeId);
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				hideProgressDialog();
				com.wutong.androidprojectlibary.http.util.HttpResponseUtil.justToast(errorNo, t, mContext);
				mFinishView.setEnabled(true);
			}

			@Override
			public void onStart() {
				super.onStart();
				showProgressDialog();
				mFinishView.setEnabled(false);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				hideProgressDialog();
				ResponseBean<Object> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<Object>>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "已成功关闭");
					finish();
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}
				mFinishView.setEnabled(true);


			}

		});


	}


}
