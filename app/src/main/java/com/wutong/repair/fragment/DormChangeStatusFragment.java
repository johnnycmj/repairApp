package com.wutong.repair.fragment;


import java.util.List;

import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.activity.ExtraModularActivity;
import com.wutong.repair.data.bean.DormInfoChangeApplyBean;
import com.wutong.repair.data.bean.ResponseBean;

/**
 * 宿舍调整状态
 * @author Jolly
 * 创建时间：2014年7月7日
 *
 */
public class DormChangeStatusFragment extends BaseFragment {


	private TextView titlebarTitleView;
	private ImageView titlebarBackView;
	private String complaintTitle = "宿舍调整";

	private TextView mCurrentDormInfoView;//当前宿舍信息

	private LinearLayout mApplyOrderEmptyLayout;//空数据
	private LinearLayout mApplyOrderStatusLayout;//有宿舍调整申请
	private TextView mApplyOrderStatusFlagView;//申请单的状态标签：已通过，待审核，未通过
	private TextView mApplyOrderTimeView;//申请时间
	private TextView mApplyOrderPreDormInfoView;//申请原宿舍信息
	private TextView mApplyOrderAfterDormInfoView;//申请后的宿舍信息
	private ImageView mApplyOrderStatusIconView;//申请的状态
	private TextView mApplyOrderTypeForChangeApplyView;//申请类型-调整为
	private TextView mApplyOrderTypeForOffsetView;//申请类型-更正为

	private Button mForwardToApplyView;//我要调整宿舍

	private Drawable mSuccessDraw;//已通过
	private Drawable mRefuseDraw;//未通过

	private DormInfoChangeApplyBean mDormInfoChangeApplyBean;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(complaintTitle);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_dorm_change_status, container,false);
		setFragmentView(fragmentView);
		//
		commonInit();
		titleBarInit();
		findViewInit();
		viewInit();
		return fragmentView;
	}
	
	

	@Override
	public void onResume() {
		super.onResume();
		setupData();
	}

	private void commonInit(){
		mSuccessDraw = getResources().getDrawable(R.drawable.icon_sstz_tg);
		mRefuseDraw = getResources().getDrawable(R.drawable.icon_sstz_tgw);
	}

	private void titleBarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(complaintTitle);
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
				DormChangeStatusFragment.this.getActivity().onBackPressed();

			}
		});
	}

	private void findViewInit(){
		mCurrentDormInfoView = (TextView) findViewById(R.id.dorm_info);
		mApplyOrderEmptyLayout = (LinearLayout) findViewById(R.id.change_dorm_empty_status_layout);
		mApplyOrderStatusLayout = (LinearLayout) findViewById(R.id.change_dorm_status_layout);
		mApplyOrderStatusFlagView = (TextView) findViewById(R.id.dorm_apply_status_flag);
		mApplyOrderTimeView = (TextView) findViewById(R.id.dorm_apply_time);
		mApplyOrderPreDormInfoView = (TextView) findViewById(R.id.dorm_info_pre);
		mApplyOrderAfterDormInfoView = (TextView) findViewById(R.id.dorm_info_after);
		mApplyOrderStatusIconView = (ImageView) findViewById(R.id.dorm_info_status_icon);
		mApplyOrderTypeForChangeApplyView = (TextView) findViewById(R.id.dorm_op_change_apply);
		mApplyOrderTypeForOffsetView = (TextView) findViewById(R.id.dorm_op_offset);
		mForwardToApplyView = (Button) findViewById(R.id.change_dorm_apply);
	}


	private void viewInit(){
		setFragmentPageName(complaintTitle);


		mForwardToApplyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ExtraModularActivity.class);
				intent.putExtra("fragmentLabel", getString(R.string.fragment_url_dorm_change_apply));
				startActivity(intent);
			}
		});
	}

	

	private void setupData(){
		loadCurrentDormHttp();
		loadDataHttp();
	}

	/**
	 * 获取当前宿舍信息
	 */
	private void loadCurrentDormHttp(){
		mCurrentDormInfoView.setText(application.getLoginInfoBean().getDormRoomName());
	}

	private void loadDataHttp(){

		String url = getString(R.string.http_url_load_dorm_change_status_last_order,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
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
				ResponseBean<List<DormInfoChangeApplyBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<DormInfoChangeApplyBean>>>(){}.getType());
				List<DormInfoChangeApplyBean> resultList = responseBean.getData();
				if(!responseBean.isSuccess()){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList != null && !resultList.isEmpty()){
					mDormInfoChangeApplyBean = resultList.get(0);
					//显示申请单信息
					mApplyOrderEmptyLayout.setVisibility(ViewGroup.GONE);
					mApplyOrderStatusLayout.setVisibility(ViewGroup.VISIBLE);
					mForwardToApplyView.setVisibility(ViewGroup.GONE);

					mApplyOrderTimeView.setText(mDormInfoChangeApplyBean.getTime());
					mApplyOrderPreDormInfoView.setText(mDormInfoChangeApplyBean.getApplyFromDorm()+"-"+mDormInfoChangeApplyBean.getApplyFromBed()+"号床");
					mApplyOrderAfterDormInfoView.setText(mDormInfoChangeApplyBean.getApplyToDorm()+"-"+mDormInfoChangeApplyBean.getApplyToBed()+"号床");
					if(mDormInfoChangeApplyBean.getType().equals(DormInfoChangeApplyBean.TYPE_CHANGE)){
						//调整为
						mApplyOrderTypeForChangeApplyView.setVisibility(ViewGroup.VISIBLE);
						mApplyOrderTypeForOffsetView.setVisibility(ViewGroup.GONE);
					}
					else if(mDormInfoChangeApplyBean.getType().equals(DormInfoChangeApplyBean.TYPE_OFFSET)){
						//更正为
						mApplyOrderTypeForChangeApplyView.setVisibility(ViewGroup.GONE);
						mApplyOrderTypeForOffsetView.setVisibility(ViewGroup.VISIBLE);
					}
					else{
						ToastUtil.showToast(mContext, "意外的申请类型");
					}
					if(mDormInfoChangeApplyBean.getStatus().equals(DormInfoChangeApplyBean.STATUS_WAITING_ACCEPT)){
						//待审核
						mApplyOrderStatusFlagView.setBackgroundResource(R.drawable.sstz_lable_blue);
						mApplyOrderStatusFlagView.setText("待审核");
						mForwardToApplyView.setVisibility(ViewGroup.GONE);
						mApplyOrderStatusIconView.setImageDrawable(null);
					}
					else if(mDormInfoChangeApplyBean.getStatus().equals(DormInfoChangeApplyBean.STATUS_HAS_ACCEPTED)){
						//已通过
						mApplyOrderStatusFlagView.setBackgroundResource(R.drawable.sstz_lable_red);
						mApplyOrderStatusFlagView.setText("已通过");
						mForwardToApplyView.setVisibility(ViewGroup.VISIBLE);
						mApplyOrderStatusIconView.setImageResource(R.drawable.icon_sstz_tg);
					}
					else if(mDormInfoChangeApplyBean.getStatus().equals(DormInfoChangeApplyBean.STATUS_REFUSED)){
						//未通过
						mApplyOrderStatusFlagView.setBackgroundResource(R.drawable.sstz_lable_gray);
						mApplyOrderStatusFlagView.setText("未通过");
						mForwardToApplyView.setVisibility(ViewGroup.VISIBLE);
						mApplyOrderStatusIconView.setImageResource(R.drawable.icon_sstz_tgw);
					}
					else{
						ToastUtil.showToast(mContext, "意外的申请状态");
						mForwardToApplyView.setVisibility(ViewGroup.GONE);
						mApplyOrderStatusFlagView.setBackgroundColor(getResources().getColor(R.color.transparent));
						mApplyOrderStatusFlagView.setText("");
					}

				}
				else{
					//显示空数据layout，显示申请按钮
					mApplyOrderEmptyLayout.setVisibility(ViewGroup.VISIBLE);
					mApplyOrderStatusLayout.setVisibility(ViewGroup.GONE);
					mForwardToApplyView.setVisibility(ViewGroup.VISIBLE);
				}
			}

		});

	}
}
