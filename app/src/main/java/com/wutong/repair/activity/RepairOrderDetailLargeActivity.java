package com.wutong.repair.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFileFormBean;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.bean.UrlBitmapFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpFileAsyncTask;
import com.wutong.androidprojectlibary.http.util.UrlLoadBitmapAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.http.util.HttpFileAsyncTask.OnProgressRefresh;
import com.wutong.androidprojectlibary.http.util.UrlLoadBitmapAsyncTask.OnUrlLoadBitmapDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.common.widget.CustomDialog.Builder;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.AssessBean;
import com.wutong.repair.data.bean.PhotoBean;
import com.wutong.repair.data.bean.RepairOrderBean;
import com.wutong.repair.data.bean.RepairOrderStatusFormBean;
import com.wutong.repair.data.bean.RepairmanBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.dictionary.RepairOrderDict;
import com.wutong.repair.dictionary.RepairOrderOperateTypeDict;
import com.wutong.repair.dictionary.RepairTypeDict;
import com.wutong.repair.fragment.RepairFragment;
import com.wutong.repair.util.BitmapUtil;
import com.wutong.repair.util.CommonSwitcherUtil;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;


public class RepairOrderDetailLargeActivity extends BaseActivity {
	private final static String RUNTIME_PERMISSION_KEY = "REPAIR_ORDER_DETAIL"; 


	private final int PHOTO_NOTIFICATION_BEFORE_ID = 0x7979;
	private final int PHOTO_NOTIFICATION_DONE_ID = 0x8080;
	private final int PHOTO_NOTIFICATION_CANCELED = 0x112;

	private final int TAKE_PHOTOS_REQUEST_CODE = 33; 
	private final int IMAGE_FROM_MOBILE_REQUEST_CODE = 22;
	private final int IMAGE_CROPED_REQUEST_CODE = 11;
	private final int FEEDBACK_REQUEST_CODE = 44;
	private final int QUERY_REDO_REQUEST_CODE = 55;
	private final int QUERY_CHECK_DONE_REQUEST_CODE = 66;

	private final int HOW_REPAIR_REQUEST_CODE = 77;

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;


	private TextView mRepairCodeView;
	private TextView mPlaceView;
	private TextView mContactNameView;
	private TextView mContactTelView;
	private ImageView mContactTelCallView;
	private TextView mAssetAndTroubleView;
	private TextView mOtherTroubleNameView;
	private TextView mStartTimeView;
	private TextView mAcceptTimeView;
	private TextView mEndTimeView;
	private TextView mFeedbackCountView;
	private TextView mRoomAssetInfoView;
	private TextView mMaterialInfoView;

	private ImageView mStatusIconView;

	private String[] takePhotoItems = new String[]{"相机拍摄","手机上传"};
	private TakePhotoWheelAdapter takePhotoWheelAdapter;
	private View takePhotoContentView;
	private WheelView takePhotoWv;
	private CustomDialog takePhotoCustomDialog;
	
	private LinearLayout mUploadPhotoLineView;
	private Button mTakePhotosView;
	private RepairOrderBean resultBean;
	private boolean isFromIndex;
	private String repairOrderId;
	private String repairType;
	private boolean outIsLargeCharacter;

	private String appTempDir;
	private Integer roomId;
	private View alertStartToDealView;
	private DatePicker mDateView;
	private TimePicker mTimeView;
	private CheckBox mIsMakeAnAppointmentView;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日(E) HH:mm");
	private CustomDialog startToDealDialog;
	private List<PhotoBean> photoBeanList;
	private List<String> photoUrlList;

	private View feedbackLayout;
	private View roomAssetLayout;
	private View materialInfoLayout;

	private int currentStatus;

	private int callCode;

	private final int DEFAULT_HOUR = 9;//默认预约时间
	private final int DEFAULT_TIME = 0;//默认预约时间

	private Integer successedNumber = 0;
	private int totalNumber;


	//
	private Button mOperationAcceptBtn;//
	private Button mOperationFinishBtn;
	private Button mOperationRefixFinishBtn;

	private final static int LARGE_OPERATION_ACCEPT = 1;//受理
	private final static int LARGE_OPERATION_FINISH = 2;//完成
	private final static int LARGE_OPERATION_REFIX_FINISH = 3;//返修完成
	//



	private CustomDialog assignmentCustomDialog;

	private View assignmentAlertContentView;
	private WheelView repairmanSelectView;
	private RepairmanWheelAdapter repairmanWheelAdapter;
	private List<RepairmanBean> repairmanBeanList;


	private ViewPager imageViewPager;
	private ImagePagerAdapter imagePagerAdapter;

	private RepairOrderStatusFormBean completeRepairOrderStatusFormBean;

	static class RuntimePermission{
		static final String REPAIRMAN_ROLE = "repairman_role";//维修工
		static final String APPLIER_ROLE = "applier_role";//报修人
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isNeedLogin = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repair_order_detail_large);
		setStatPageName(mContext, R.string.title_activity_repair_order_detail);
		intentInit();
		titlebarInit();
		viewInit();
		setupData();

	}


	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setVisibility(ViewGroup.VISIBLE);
		titlebarTitleView.setText(R.string.title_activity_repair_order_detail);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RepairOrderDetailLargeActivity.this.onBackPressed();

			}
		});

	}

	private void intentInit(){
		//数据初始化
		Intent intent = getIntent();
		isFromIndex = intent.getBooleanExtra("isFromIndex", false);
		repairOrderId = intent.getStringExtra("repairOrderId");
		repairType = intent.getStringExtra("repairType");
		outIsLargeCharacter = intent.getBooleanExtra("isLargeCharacter",false);

	}

	private void viewInit(){
		assignmentAlertContentView = LayoutInflater.from(mContext).inflate(R.layout.common_single_list_select, null);
		Button okBtnView = (Button) assignmentAlertContentView.findViewById(R.id.ok_btn);
		Button cancelBtnView = (Button) assignmentAlertContentView.findViewById(R.id.cancel_btn);
		okBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int repairmanIndex = repairmanSelectView.getCurrentItem();
				String repairmanId = repairmanBeanList.get(repairmanIndex).getId();
				submitAssignment(repairType, repairOrderId, repairmanId);
				assignmentCustomDialog.dismiss();
			}
		});
		cancelBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				assignmentCustomDialog.dismiss();

			}
		});
		repairmanSelectView = (WheelView) assignmentAlertContentView.findViewById(R.id.select_item_wheel);
		repairmanSelectView.setBackgroundResource(R.drawable.common_wheel_bg);
		repairmanSelectView.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		repairmanBeanList = new ArrayList<RepairmanBean>();

		repairmanWheelAdapter = new RepairmanWheelAdapter();
		repairmanSelectView.setViewAdapter(repairmanWheelAdapter);

		assignmentCustomDialog = new Builder(mContext)
		.setTitle("选择派工的维修工")
		.setView(assignmentAlertContentView)
		.create();


		//初始化

		subViewInit();

		//
		mOperationAcceptBtn = (Button) findViewById(R.id.repair_detail_accept_order);
		mOperationFinishBtn = (Button) findViewById(R.id.repair_detail_finish_order);
		mOperationRefixFinishBtn = (Button) findViewById(R.id.repair_detail_refix_finish_order);
		mOperationAcceptBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkAndAccept();
			}
		});
		mOperationFinishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				complete();
			}
		});
		mOperationRefixFinishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refixComplete();
			}
		});
		//
		alertStartToDealView = LayoutInflater.from(mContext).inflate(R.layout.pre_deal_time_alert, null);
		mDateView = (DatePicker) alertStartToDealView.findViewById(R.id.datePicker);
		mTimeView = (TimePicker) alertStartToDealView.findViewById(R.id.timePicker);
		mTimeView.setCurrentHour(mTimeView.getCurrentHour()+1);
		mTimeView.setCurrentMinute(DEFAULT_TIME);
		mIsMakeAnAppointmentView = (CheckBox) alertStartToDealView.findViewById(R.id.is_make_an_appointment);


		if(isFromIndex){
			SharedPreferences tempPreferences = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
			tempPreferences.edit().clear().commit();
		}
		//
		/////
		String sdStatus = Environment.getExternalStorageState();
		if (sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			Logger.v(sdStatus+"SD card is not avaiable/writeable right now.");
		}
		else{
			ToastUtil.showToast(mContext, "未检测到SD卡，拍照不可用");
			mTakePhotosView.setEnabled(false);
		}
		appTempDir =SettingConfig.getPhotoCacheDir(this);
		File file = new File(appTempDir);
		if(!file.exists()){
			file.mkdirs();// 创建文件夹
		}
		takePhotoContentView = LayoutInflater.from(mContext).inflate(R.layout.common_single_list_select_no_button, null);

		takePhotoWv = (WheelView) takePhotoContentView.findViewById(R.id.select_item_wheel);
		takePhotoWv.setBackgroundResource(R.drawable.common_wheel_bg);
		takePhotoWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		takePhotoWheelAdapter = new TakePhotoWheelAdapter();
		takePhotoWv.setViewAdapter(takePhotoWheelAdapter);
		mTakePhotosView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!takePhotoCustomDialog.isShowing()){
					takePhotoCustomDialog.show();
				}
			}
		});
		
		takePhotoCustomDialog = new Builder(mContext).setTitle("设置")
				.setView(takePhotoContentView)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int index = takePhotoWv.getCurrentItem();
						dialog.dismiss(); 
						switch (index) {
						case 0:
							takePhoto();
							break;
						case 1:
							Intent intent = new Intent(Intent.ACTION_PICK, null);  
							intent.setDataAndType( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/jpg");  
							startActivityForResult(intent, IMAGE_FROM_MOBILE_REQUEST_CODE); 
							break;
						}
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



	@Override
	protected void onResume() {
		super.onResume();
	}

	private void setupData(){
		loadRepairOrderDetailAsyncHttp(repairOrderId);
		loadThumbAsyncHttp(repairOrderId);
		loadRepairmanListAsyncHttp();
	}




	public void deleteThumb(View view){
		ViewParent parent = view.getParent().getParent();
		View layout = (View) parent;
		String key = layout.getTag().toString();
		SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
		int total = preferences.getAll().size();
		Editor editor = preferences.edit();
		editor.remove(key);
		editor.commit();
		mUploadPhotoLineView = (LinearLayout) findViewById(R.id.upload_photo_list);
		mUploadPhotoLineView.removeView((View)parent);
		if(mUploadPhotoLineView.getChildCount() == 0){
			mUploadPhotoLineView.setVisibility(ViewGroup.GONE);
		}
		ToastUtil.showToast(mContext, "成功删除！");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Logger.i("requestCode:" +requestCode +",resultCode:"+resultCode);
		if (resultCode == Activity.RESULT_OK ) {
			if(requestCode == TAKE_PHOTOS_REQUEST_CODE){
				showImages();
			}
			else if(requestCode == IMAGE_FROM_MOBILE_REQUEST_CODE){
				//获取文件路径 保存
				Uri uri = data.getData();
				String fileName = String.valueOf(new Date().getTime());
				File file = CommonSwitcherUtil.getAbsoluteImagePath(RepairOrderDetailLargeActivity.this, uri);
				String fileFullName = file.getAbsolutePath();
				Logger.i("FROM IMAGE [fileName:" +fileName + "][fileFullName:"+fileFullName +"]");
				SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
				SharedPreferences preferencesLast = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
				Editor editor = preferences.edit();
				Editor editorLast = preferencesLast.edit();
				editor.putString(fileName, fileFullName);
				editorLast.putString(SettingConfig.RepairOrderForm.LAST_KEY_NAME, fileName);
				editor.commit();
				editorLast.commit();
				showImages();
			}
			else if(requestCode == IMAGE_CROPED_REQUEST_CODE){

			}
			else if(requestCode == FEEDBACK_REQUEST_CODE){
				int number = data.getIntExtra("", 0);
				resultBean.setFeedbackCount(resultBean.getFeedbackCount()+number);
				mFeedbackCountView.setText(resultBean.getFeedbackCount().toString());

			}
			else if(requestCode == QUERY_REDO_REQUEST_CODE){
				String reason = data.getStringExtra("reason");
				needRefixAsyncHttp(repairOrderId,reason);
			}
			else if(requestCode == QUERY_CHECK_DONE_REQUEST_CODE){
				RepairOrderStatusFormBean repairOrderStatusFormBean =  new RepairOrderStatusFormBean();
				if(resultBean.getStatus().equals(String.valueOf(RepairOrderDict.REFIX_DEALED_STATUS))){
					repairOrderStatusFormBean.setOperate(RepairOrderOperateTypeDict.REFIX_FINISH_AFFIRM);
					repairOrderStatusFormBean.setOperateName("确认(返修)");
				}
				else{
					repairOrderStatusFormBean.setOperate(RepairOrderOperateTypeDict.AFFIRM);
					repairOrderStatusFormBean.setOperateName("确认");
				}
				repairOrderStatusFormBean.setRepairOrderId(repairOrderId);
				changeStatusAsyncHttp(repairOrderStatusFormBean,new ChangeStatusListener() {
					
					@Override
					public void onSuccess() {
						//满意评价
						String code = data.getStringExtra("data");
						AssessBean assessBean = new AssessBean();
						assessBean.setRepairFormId(repairOrderId);
						assessBean.setAssessTypeCode(code);
						submitAssessAsyncHttp(assessBean);
					}
					
					@Override
					public void onFailed() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onBefore() {
						// TODO Auto-generated method stub
						
					}
				});
			}

			else if(requestCode == HOW_REPAIR_REQUEST_CODE){
				String howRepairContent = data.getStringExtra("data");
				completeRepairOrderStatusFormBean.setHowRepairContent(howRepairContent);
				changeStatusAsyncHttp(completeRepairOrderStatusFormBean,null);
			}
			else{
				//意外情况
				ToastUtil.showToast(mContext, "意外的返回");
			}

		}
		else if(resultCode == Activity.RESULT_CANCELED ){
			SharedPreferences preferencesPhoto = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
			SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
			Editor editorPhoto = preferencesPhoto.edit();
			Editor editor = preferences.edit();
			String last = preferences.getString(SettingConfig.RepairOrderForm.LAST_KEY_NAME, null);
			//
			editorPhoto.remove(last).commit();
			//
			if(last != null){
				editor.remove(last);
				editor.remove(SettingConfig.RepairOrderForm.LAST_KEY_NAME);
			}
			editor.commit();
		}
	}
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	/**
	 * 获取报修单详细
	 */
	private void loadRepairOrderDetailAsyncHttp(String repairOrderId){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		if(repairType.equals(RepairTypeDict.DORM_TYPE)){
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderDetailDormUrl());
		}
		else if(repairType.equals(RepairTypeDict.PUBLIC_INNER_TYPE)){
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderDetailPublicInnerUrl());
		}
		else if(repairType.equals(RepairTypeDict.PUBLIC_OUTTER_TYPE)){
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderDetailPublicOutterUrl());
		}
		else{
			ToastUtil.showToast(mContext, "意外的报修单类型");
			return;
		}
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair repairFormIdPair = new BasicNameValuePair("repairFormId", repairOrderId);
		params.add(repairFormIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<RepairOrderBean> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<RepairOrderBean>>(){}.getType());
				resultBean = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultBean == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				//运行时权限初始化
				String userIdStr = application.getLoginInfoBean().getUserId().toString();
				String repairBeanUserId = resultBean.getUserId();
				String repairmanId = resultBean.getRepairmanId();
				Integer dormId = resultBean.getRoomId();
				application.removeRuntimePermission(RUNTIME_PERMISSION_KEY);
				if(userIdStr.equals(repairBeanUserId) 
						||(application.getLoginInfoBean().getDormRoomId() != null &&dormId != null &&application.getLoginInfoBean().getDormRoomId() != null && application.getLoginInfoBean().getDormRoomId().equals(dormId.toString()))){//报修人id相符，是学生、员工等报修人权限,报修人的宿舍id相符是同宿舍报修，视为报修人
					application.putRuntimePermission(RUNTIME_PERMISSION_KEY, RuntimePermission.APPLIER_ROLE);
				}
				else if(userIdStr.equals(repairmanId)){//维修人id相符，是维修工的权限
					application.putRuntimePermission(RUNTIME_PERMISSION_KEY, RuntimePermission.REPAIRMAN_ROLE);
				}
				//
				int orderStatus = Integer.valueOf(resultBean.getStatus()).intValue();



				if(application.hasRunTimePermission(RUNTIME_PERMISSION_KEY,RuntimePermission.REPAIRMAN_ROLE)){
					if(orderStatus == 2){
						//拍照按钮
						mTakePhotosView.setVisibility(ViewGroup.VISIBLE);
					}
					//Fix分支
						if(resultBean.getRoomId()!= null
								&&resultBean.getRoomId().intValue() >0
								&& resultBean.getFormType().equals("1")){
							//资产查看
							roomAssetLayout.setVisibility(ViewGroup.VISIBLE);
						}
					//材料
					materialInfoLayout.setVisibility(ViewGroup.VISIBLE);

					//
					//					((ImageView)findViewById(R.id.repair_detail_contact_icon)).setImageResource(R.drawable.icon_student);
				}
				else if(application.hasRunTimePermission(RUNTIME_PERMISSION_KEY,RuntimePermission.APPLIER_ROLE)){
					mTakePhotosView.setVisibility(ViewGroup.GONE);
					mContactTelView.setVisibility(ViewGroup.GONE);
					((ImageView)findViewById(R.id.repair_detail_contact_icon)).setImageResource(R.drawable.icon_repairman);
					findViewById(R.id.repair_detail_contact_call_layout).setVisibility(ViewGroup.GONE);
				}
				if(orderStatus ==1){//【未受理】组
					((View)mAcceptTimeView.getParent()).setVisibility(ViewGroup.GONE);
					((View)mEndTimeView.getParent()).setVisibility(ViewGroup.GONE);
				}
				else if(orderStatus ==2 || orderStatus ==-1){//【维修中】组
					((View)mAcceptTimeView.getParent()).setVisibility(ViewGroup.VISIBLE);
					((View)mEndTimeView.getParent()).setVisibility(ViewGroup.GONE);
				}
				else{
					((View)mAcceptTimeView.getParent()).setVisibility(ViewGroup.VISIBLE);
					((View)mEndTimeView.getParent()).setVisibility(ViewGroup.VISIBLE);
				}

				Logger.i("信息获取状态为："+orderStatus);
				if(application.hasPermission(getString(R.string.roles_permissions_manager_assignment_order))
						&& orderStatus != 4 && orderStatus != 7 && orderStatus != 3 && orderStatus != 6){
					//派工

				}
				if(application.hasPermission(getString(R.string.roles_permissions_manager_remind_repairman))
						&&(repairmanId !=null && repairmanId.toString().trim().length() > 0 && !repairmanId.equals("0"))
						&& orderStatus != 4 && orderStatus != 7 && orderStatus != 3 && orderStatus != 6) {
					//催单(附加 有维修工id + 状态不是已确认和返修确认、返修完成、已维修)

				}
				if(application.hasPermission(getString(R.string.roles_permissions_applicant_query_and_check))
						&&(orderStatus == 3 || orderStatus == 6 )
						&&application.hasRunTimePermission(RUNTIME_PERMISSION_KEY,RuntimePermission.APPLIER_ROLE)
						&&resultBean.getFormType().equals(RepairTypeDict.DORM_TYPE)){
					//确认验收

				}
				if(application.hasPermission(getString(R.string.roles_permissions_applicant_apply_refix))
						&&(orderStatus == 3 || orderStatus == 6 )
						&&application.hasRunTimePermission(RUNTIME_PERMISSION_KEY,RuntimePermission.APPLIER_ROLE)
						&&resultBean.getFormType().equals(RepairTypeDict.DORM_TYPE)){
					//申请返修

				}

				if(application.hasPermission(getString(R.string.roles_permissions_repairman_accept_order))
						&&orderStatus == 1){
					//受理报修单
					showOperation(LARGE_OPERATION_ACCEPT);
				}

				if(application.hasPermission(getString(R.string.roles_permissions_repairman_forward_order))
						&&orderStatus == 1){
					//大额度报修单

				}

				if(application.hasPermission(getString(R.string.roles_permissions_repairman_finish_order))
						&&orderStatus == 2){
					//完成报修单
					showOperation(LARGE_OPERATION_FINISH);
				}

				if(application.hasPermission(getString(R.string.roles_permissions_repairman_refix_finish_order))
						&&orderStatus == -1){
					//完成报修单（返修）
					showOperation(LARGE_OPERATION_REFIX_FINISH);
				}




				//
				if(resultBean.getStatus().equals("-1")){
					//返修
					mStatusIconView.setImageResource(R.drawable.status_icon_repairing_redo);
				}
				else if(resultBean.getStatus().equals("6")){
					//返修完成
					mStatusIconView.setImageResource(R.drawable.status_icon_repairing_redo_finish);
				}
				else if(resultBean.getStatus().equals("8")){
					//已转发
					mStatusIconView.setImageResource(R.drawable.status_icon_repairing_has_forwarded);
				}
				else if(resultBean.getStatus().equals("3")){
					//已维修
					mStatusIconView.setImageResource(R.drawable.status_icon_repairing_has_repaired);
				}
				else{
					mStatusIconView.setImageDrawable(null);
				}

				subViewSetpuData(resultBean);

			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
				hideProgressDialog();
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	/**
	 * 获取图片信息
	 * @param repairOrderId
	 */
	@SuppressLint("NewApi")
	private void loadThumbAsyncHttp(String repairOrderId){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderImageListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair formIdPair = new BasicNameValuePair("formId", repairOrderId);
		params.add(formIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean<List<PhotoBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<PhotoBean>>>(){}.getType());
				List<PhotoBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList != null && !resultList.isEmpty()){
					photoBeanList.clear();
					photoBeanList.addAll(resultList);
					photoUrlList = new ArrayList<String>();
					for(PhotoBean photoBean:resultList){
						photoUrlList.add(photoBean.getUrl());
					}
				}
				else{
				}
				imagePagerAdapter.notifyDataSetChanged();
			}

			@Override
			public void failed(Exception exception) {

			}

			@Override
			public void beforeDealt() {

			}
		});
		if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.HONEYCOMB){
			httpAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,httpFormBean);
		}
		else{
			httpAsyncTask.execute(httpFormBean);
		}
	}

	/**
	 * 状态改变
	 * @param repairOrderStatusFormBean
	 */
	private void changeStatusAsyncHttp(RepairOrderStatusFormBean repairOrderStatusFormBean,final ChangeStatusListener changeStatusListener){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		switch (repairOrderStatusFormBean.getOperate()) {
		case RepairOrderOperateTypeDict.REPAIRING:
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderAcceptUrl());
			callCode = RepairFragment.CALL_CODE_NOT_ACCEPT_AND_REPAIRING_REFRESH;
			break;
		case RepairOrderOperateTypeDict.FINISH:
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderFinishUrl());
			callCode = RepairFragment.CALL_CODE_REPAIRING_AND_FINISH_REFRESH_FORWARD_FINISH;
			break;
		case RepairOrderOperateTypeDict.REFIX_FINISH:
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderRefixFinishUrl());
			callCode = RepairFragment.CALL_CODE_REPAIRING_AND_FINISH_REFRESH_FORWARD_FINISH;
			break;
		case RepairOrderOperateTypeDict.MUCH_MONEY_FORWARD:
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderMuchMoneyForwardUrl());
			callCode = RepairFragment.CALL_CODE_NOT_ACCEPT_REFRESH;
			break;
		case RepairOrderOperateTypeDict.AFFIRM:
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderAffirmUrl());
			callCode = RepairFragment.CALL_CODE_FINISH_REFRESH;
			break;
		case RepairOrderOperateTypeDict.REFIX_FINISH_AFFIRM:
			httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderRefixAffirmUrl());
			callCode = RepairFragment.CALL_CODE_FINISH_REFRESH;
			break;
		default:
			return;
		}
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair formIdPair = new BasicNameValuePair("formId", repairOrderStatusFormBean.getRepairOrderId());
		params.add(formIdPair);
		NameValuePair formTypePair = new BasicNameValuePair("formType", repairType);
		params.add(formTypePair);

		//完成时，部分需求要输入【如何完成】
		NameValuePair howRepairPair = new BasicNameValuePair("reason", repairOrderStatusFormBean.getHowRepairContent());
		params.add(howRepairPair);

		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
					Map<String, ?> map = preferences.getAll();
					if(map.size() >0 ){
						collectPhotoInfoAndSend(String.valueOf(repairOrderId),map);
					}
					submitSuccess(callCode);
					if(changeStatusListener != null){
						changeStatusListener.onSuccess();
					}
				}
				else{
					ToastUtil.showToast(mContext, "提交失败！错误信息："+responseBean.getMessage());
				}

			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
				if(changeStatusListener != null){
					changeStatusListener.onFailed();
				}
			}

			@Override
			public void beforeDealt() {
				if(changeStatusListener != null){
					changeStatusListener.onBefore();
				}
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	/**
	 * 收集图片信息然后发送
	 */
	private void collectPhotoInfoAndSend(String repairOrderId,Map<String, ?> map){
		totalNumber = map.size();
		if(totalNumber >0){
			//
			Object[] array = map.values().toArray();
			Map<String, String> fileMap = new HashMap<String, String>();
			for(int i = 0;i<totalNumber;i++){
				String path = array[i].toString();
				try {
					String time = String.valueOf(new Date().getTime());
					String compressPath = appTempDir+"repair"+time+".temp";
					FileOutputStream output = new FileOutputStream(compressPath);
					BitmapUtil.photoCompress(path, output);
					fileMap.put(time, compressPath);
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
			uploadPhotoAsyncHttpFile(repairOrderId,fileMap);
		}
	}

	/**
	 * 传送图片
	 */
	@SuppressLint("NewApi")
	private void uploadPhotoAsyncHttpFile(String repairOrderId,Map<String, String> fileMap){
		HttpFileAsyncTask httpFileAsyncTask = new HttpFileAsyncTask(mContext);
		HttpFileFormBean httpFileFormBean = new HttpFileFormBean();
		httpFileFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderPhotoUploadUrl());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(repairOrderId,repairOrderId );//服务器用奇葩方法取键名作为参数，不解释
		httpFileFormBean.setParams(paramMap);
		httpFileFormBean.setFileMap(fileMap);
		httpFileAsyncTask.setProgressRefresh(new OnProgressRefresh() {

			@Override
			public void refreshProgress(int index) {
				successedNumber++;
			}
		});
		httpFileAsyncTask.setOnDealtListener(new HttpFileAsyncTask.OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				if(successedNumber ==totalNumber){
					showNotifcation(mContext,PHOTO_NOTIFICATION_DONE_ID, "图片发送完成", "", false,RepairOrderActivity.class);
				}
				else{
					showNotifcation(mContext,PHOTO_NOTIFICATION_DONE_ID, "图片发送完成,有" +(totalNumber-successedNumber)+"张传送失败", "", false,RepairOrderActivity.class);
				}
				cleanPhotoCache();
			}

			@Override
			public void failed(Exception exception) {
				ToastUtil.showToast(mContext, "发送图片异常");
				cleanPhotoCache();
			}

			@Override
			public void beforeDealt() {
				showNotifcation(mContext,PHOTO_NOTIFICATION_BEFORE_ID, "报修单已提交,正在上传图片", "请耐心等候", false,RepairOrderActivity.class);

			}
		});
		if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.HONEYCOMB){
			httpFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,httpFileFormBean);
		}
		else{
			httpFileAsyncTask.execute(httpFileFormBean);
		}
	}





	private void takePhoto(){
		String fileName = String.valueOf(new Date().getTime());
		String fileFullName = appTempDir+fileName+".jpg";
		Logger.i("FROM TAKE_PHOTOS [fileName:" +fileName + "][fileFullName:"+fileFullName +"]");
		File preSaveFile = new File(fileFullName);
		SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
		SharedPreferences preferencesLast = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
		Editor editor = preferences.edit();
		Editor editorLast = preferencesLast.edit();
		editor.putString(fileName, fileFullName);
		editorLast.putString(SettingConfig.RepairOrderForm.LAST_KEY_NAME, fileName);
		editor.commit();
		editorLast.commit();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(preSaveFile));
		startActivityForResult(intent, TAKE_PHOTOS_REQUEST_CODE);
	}



	private void showImages(){
		SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
		Map<String, ?> map = preferences.getAll();
		mUploadPhotoLineView.removeAllViews();
		Set<String> set = map.keySet();
		if(set.size() >0){
			mUploadPhotoLineView.setVisibility(ViewGroup.VISIBLE);
			for(String key:set){
				View photoView = LayoutInflater.from(mContext).inflate(R.layout.imageview_upload_photo,null );
				photoView.setTag(key);
				ImageView imageView = (ImageView) photoView.findViewById(R.id.upload_photo_thumb);
				try {
					String path = map.get(key).toString();
					File preSaveFile = new File(path);
					Bitmap bitmap = BitmapUtil.convertBitmap(preSaveFile);
					imageView.setImageBitmap(bitmap);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mUploadPhotoLineView.addView(photoView);
			}
		}
		else{
			mUploadPhotoLineView.setVisibility(ViewGroup.GONE);
		}
	}
	private void submitSuccess(int callCode){
		ToastUtil.showToast(mContext, "提交成功！");
		Intent intent =new Intent();
		intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_repair_fragment_name));
		intent.putExtra(getString(R.string.call_fragment_key_code), callCode);//报修单刷新维修中或者已完成
		setResult(RESULT_OK,intent);
		RepairOrderDetailLargeActivity.this.finish();
	}

	/**
	 * 发送文本反馈内容
	 * @param repairOrderId
	 * @param content
	 */
	private void submitFeedbackTextAsyncHttp(String repairOrderId,String content){
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
				// TODO Auto-generated method stub
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "已提交一条反馈信息");
				}
				else{
					ToastUtil.showToast(mContext, "一条反馈信息提交失败");
				}
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}



	private void checkAndAccept(){
		if(startToDealDialog == null){
			Builder builder = new Builder(mContext).setTitle("受理报修单")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							alertStartToDealView.requestFocus();//防止用户坑爹输入框直接输入时间进行修改，然后点确定，缺少触发时间变化。
							dialog.dismiss();
							RepairOrderStatusFormBean repairOrderStatusFormBean =  new RepairOrderStatusFormBean();
							repairOrderStatusFormBean.setOperate(RepairOrderOperateTypeDict.REPAIRING);
							repairOrderStatusFormBean.setOperateName("受理报修单");
							repairOrderStatusFormBean.setRepairOrderId(repairOrderId);
							changeStatusAsyncHttp(repairOrderStatusFormBean,new ChangeStatusListener() {
								
								@Override
								public void onSuccess() {
									//是否预约，如果是，将时间取出，整合成反馈
									if(mIsMakeAnAppointmentView.isChecked()){
										Calendar calendar = Calendar.getInstance();
										calendar.set(mDateView.getYear(), mDateView.getMonth(), mDateView.getDayOfMonth(), mTimeView.getCurrentHour(), mTimeView.getCurrentMinute());
										String feedbackContext = "维修预约时间："+sdf.format(calendar.getTime());
										Logger.i("feedbackContext:" + feedbackContext);
										submitFeedbackTextAsyncHttp(repairOrderId,feedbackContext);
									}
								}
								
								@Override
								public void onFailed() {
									// TODO Auto-generated method stub
									
								}
								
								@Override
								public void onBefore() {
									// TODO Auto-generated method stub
									
								}
							});
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			if(resultBean.getFormType().equals(RepairTypeDict.DORM_TYPE)){
				builder.setView(alertStartToDealView);
			}
			else{
				builder.setMessage("确定受理该单？");
			}
			startToDealDialog = builder.create();
		}
		startToDealDialog.show();

	}

	private void muchMoneyForward(){
		CustomDialog dialog = new Builder(mContext).setTitle("友情提示")
				.setMessage("确定需要转发该大额报修单么？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
						RepairOrderStatusFormBean repairOrderStatusFormBean =  new RepairOrderStatusFormBean();
						repairOrderStatusFormBean.setOperate(RepairOrderOperateTypeDict.MUCH_MONEY_FORWARD);
						repairOrderStatusFormBean.setOperateName("转发报修单");
						repairOrderStatusFormBean.setRepairOrderId(repairOrderId);
						changeStatusAsyncHttp(repairOrderStatusFormBean,null);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create();
		dialog.show();
	}



	private void complete(){
		completeRepairOrderStatusFormBean =  new RepairOrderStatusFormBean();
		completeRepairOrderStatusFormBean.setOperate(RepairOrderOperateTypeDict.FINISH);
		completeRepairOrderStatusFormBean.setOperateName("完成报修单");
		completeRepairOrderStatusFormBean.setRepairOrderId(repairOrderId);
		if(application.hasPermission(getString(R.string.roles_permissions_has_how_repair_receive))){
			Intent intent = new Intent(mContext, HowRepairActivity.class);
			startActivityForResult(intent, HOW_REPAIR_REQUEST_CODE);
		}
		else{
			CustomDialog dialog = new Builder(mContext).setTitle("友情提示")
					.setMessage("确定需要完成该单么？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
							//无需howrepaircontent
							changeStatusAsyncHttp(completeRepairOrderStatusFormBean,null);
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.create();
			dialog.show();
		}
	}

	private void refixComplete(){
		completeRepairOrderStatusFormBean =  new RepairOrderStatusFormBean();
		completeRepairOrderStatusFormBean.setOperate(RepairOrderOperateTypeDict.FINISH);
		completeRepairOrderStatusFormBean.setOperateName("完成报修单");
		completeRepairOrderStatusFormBean.setRepairOrderId(repairOrderId);
		if(application.hasPermission(getString(R.string.roles_permissions_has_how_repair_receive))){
			Intent intent = new Intent(mContext, HowRepairActivity.class);
			startActivityForResult(intent, HOW_REPAIR_REQUEST_CODE);
		}
		else{
			CustomDialog dialog = new Builder(mContext).setTitle("友情提示")
					.setMessage("确认完成该单么？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
							changeStatusAsyncHttp(completeRepairOrderStatusFormBean,null);
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
					.create();
			dialog.show();
		}
	}
	private void cleanPhotoCache(){
		File appTempDirFile = new File(appTempDir);
		if(appTempDirFile.exists() && appTempDirFile.isDirectory()){
			File[] appTempDirFileChildren = appTempDirFile.listFiles();
			CleanCacheAsyncTask cleanCacheAsyncTask = new CleanCacheAsyncTask();
			cleanCacheAsyncTask.execute(appTempDirFileChildren);
		}
	}
	private final class CleanCacheAsyncTask extends AsyncTask<File, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(File... files) {

			boolean isDelete;
			int index = 0;
			for(File file: files){
				if(!file.isDirectory()){
					isDelete = file.delete();
					Logger.i(file.getPath() +"/isDelete:" + isDelete);
					if(!isDelete){
						return isDelete;
					}
					else{
					}
				}
				index ++;
			}
			return true;

		}


		@Override
		protected void onPostExecute(Boolean result) {
		}
	}

	private class ImageThumbBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return photoBeanList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		@SuppressLint("NewApi")
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_thumb_large,null );
			PhotoBean photoBean = photoBeanList.get(position);
			final ImageView imageView = (ImageView) convertView.findViewById(R.id.thumb_image);
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, PhotoImageShowActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("imageList",new ArrayList<String>(photoUrlList));
					bundle.putInt("imageType", PhotoImageShowActivity.HTTP_URL_TYPE);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			UrlBitmapFormBean urlBitmapFormBean = new UrlBitmapFormBean();
			urlBitmapFormBean.setUrl(photoBean.getUrl());
			urlBitmapFormBean.setScale(true);

			int maxSize = getResources().getDimensionPixelSize(R.dimen.thumb_image_max_size);
			Logger.i("maxSize:"+maxSize);
			urlBitmapFormBean.setMaxSize(maxSize );
			UrlLoadBitmapAsyncTask urlLoadBitmapAsyncTask = new UrlLoadBitmapAsyncTask(mContext);
			urlLoadBitmapAsyncTask.setOnUrlLoadBitmapDealtListener(new OnUrlLoadBitmapDealtListener() {

				@Override
				public void success(Bitmap resultResponse) {
					int width = resultResponse.getWidth();
					int height = resultResponse.getHeight();
					imageView.setImageBitmap(resultResponse);
				}

				@Override
				public void failed(Exception exception) {
					imageView.setImageResource(R.drawable.image_failed);

				}

				@Override
				public void beforeDealt() {

				}
			});
			if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.HONEYCOMB){
				urlLoadBitmapAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlBitmapFormBean);
			}
			else{
				urlLoadBitmapAsyncTask.execute(urlBitmapFormBean);
			}

			return convertView;
		}

	}

	private void showNotifcation(Context context,final int notificationId,String title,String content,boolean hasSound, Class<?> cls){
		final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);               
//		Notification notification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
//		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent i = new Intent(context, cls);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
		//PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(context,R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.setLatestEventInfo(context,title, content,contentIntent);

		Notification notification = new Notification.Builder(mContext)
				.setContentTitle(title)
				.setContentText(content)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(contentIntent)
				.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// 添加声音提示 
		if(hasSound){
			notification.defaults=Notification.DEFAULT_ALL;
			// audioStreamType的值必须AudioManager中的值，代表着响铃的模式  
			notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER; 
		}
		else{
			notification.defaults=Notification.DEFAULT_LIGHTS;
		}

		notificationManager.notify(notificationId, notification);
		switch (notificationId) {
		case PHOTO_NOTIFICATION_BEFORE_ID:

			break;
		case PHOTO_NOTIFICATION_DONE_ID:
			notificationManager.cancel(PHOTO_NOTIFICATION_BEFORE_ID);
			final Handler handler=new Handler(){
				@Override 
				public void handleMessage(Message msg) {  
					notificationManager.cancel(notificationId);
					super.handleMessage(msg);  
				}
			}; 
			Runnable runnable=new Runnable() { 
				@Override
				public void run() { 
					handler.sendEmptyMessage(PHOTO_NOTIFICATION_CANCELED);
				} 
			};
			handler.postDelayed(runnable, 5000);
			break;
		default:
			break;
		}
	}




	@Override
	protected void onDestroy() {
		super.onDestroy();
		application.removeRuntimePermission(RUNTIME_PERMISSION_KEY);
	}




	private class RepairmanWheelAdapter implements WheelViewAdapter{

		@Override
		public int getItemsCount() {
			return repairmanBeanList.size();
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.wheel_list_item_common, null);
			TextView textView = (TextView) convertView.findViewById(R.id.wheel_item_text); 
			RepairmanBean repairmanBean = repairmanBeanList.get(index);
			textView.setText(repairmanBean.getName());
			return convertView;
		}

		@Override
		public View getEmptyItem(View convertView, ViewGroup parent) {
			return null;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {

		}

	}

	private void loadRepairmanListAsyncHttp(){
		String url = application.getCommonHttpUrlActionManager().getRepairmanListLoadUrl();
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.post(url, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				HttpResponseUtil.justToast(new Exception(t), mContext);
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(Object t) {
				ResponseBean<List<RepairmanBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<RepairmanBean>>>(){}.getType());
				List<RepairmanBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
				}
				else{
					repairmanBeanList.clear();
					repairmanBeanList.addAll(resultList);
					repairmanWheelAdapter = new RepairmanWheelAdapter();
					repairmanSelectView.setViewAdapter(repairmanWheelAdapter);
				}
			}

		});
	}
	private void submitAssignment(String formType,String repairOrderId,String repairmanId){
		String url = application.getCommonHttpUrlActionManager().getAssignmentSubmitUrl();
		AjaxParams params = new AjaxParams();
		params.put("formType", formType);
		params.put("repairFormId", repairOrderId);
		params.put("repairManId", repairmanId);
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.post(url, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onSuccess(Object t) {
				ResponseBean responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "派工成功");
					finish();
				}
				else{
					ToastUtil.showToast(mContext, "派工失败");
				}
			}

		});
	}

	/**
	 * 催办
	 */
	private void remindEmployment(){
		showQueryDialog("确定催促么？", new QueryDialogPositive() {

			@Override
			public void positive() {
				submitRemindAsyncHttp();

			}
		});
	}

	private void showQueryDialog(String message,final QueryDialogPositive dialogPositive){
		Builder builder = new Builder(mContext);
		builder.setMessage(message);
		builder.setTitle("催单提醒");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialogPositive.positive();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();

	}

	public interface QueryDialogPositive{
		public void positive();
	}

	/**
	 * 催单发送
	 */
	private void submitRemindAsyncHttp(){
		String url = application.getCommonHttpUrlActionManager().getRemindEmploymentUrl();
		AjaxParams params = new AjaxParams();
		params.put("repairManId", resultBean.getRepairmanId());
		params.put("repairFormId", repairOrderId);
		params.put("formType", repairType);
		new FinalHttp().post(url, params, new AjaxCallBack<Object>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				HttpResponseUtil.justToast(new Exception(t), mContext);
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(Object t) {
				ResponseBean responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "催单成功");
					finish();
				}
				else{
					ToastUtil.showToast(mContext, "催单失败");
				}
			}

		});
	}

	private void checkAndQuery(){

		//
		Intent intent = new Intent(mContext, QueryCheckDoneActivity.class);
		startActivityForResult(intent, QUERY_CHECK_DONE_REQUEST_CODE);

	}
	private void needRefix(){

		Intent intent = new Intent(mContext,QueryRedoActivity.class);
		startActivityForResult(intent, QUERY_REDO_REQUEST_CODE);

	}

	/**
	 * 
	 * @param assessBean
	 */
	private void submitAssessAsyncHttp(AssessBean assessBean){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getAssessAddUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair formIdPair = new BasicNameValuePair("formId", repairOrderId);
		params.add(formIdPair);
		NameValuePair formTypePair = new BasicNameValuePair("formType", repairType);
		params.add(formTypePair);
		NameValuePair assessTypePair = new BasicNameValuePair("assessType", assessBean.getAssessTypeCode());
		params.add(assessTypePair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "评价提交成功！");
				}
				else{
					ToastUtil.showToast(mContext, "评价提交失败！");
				}
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {

			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	/**
	 * 修改状态为返修-特殊处理
	 */
	private void needRefixAsyncHttp(final String repairOrderId,final String content){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderRefixUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair formIdPair = new BasicNameValuePair("formId", repairOrderId);
		params.add(formIdPair);
		NameValuePair formTypePair = new BasicNameValuePair("formType", repairType);
		params.add(formTypePair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					submitFeedbackTextAsyncHttp(repairOrderId, content);
					submitSuccess(RepairFragment.CALL_CODE_REPAIRING_AND_FINISH_REFRESH_FORWARD_REPAIRING);
				}
				else{
					ToastUtil.showToast(mContext, "返修提交失败！错误信息："+responseBean.getMessage());
				}
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);

			}

			@Override
			public void beforeDealt() {

			}
		});
		httpAsyncTask.execute(httpFormBean);
	}

	private class ImagePagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return photoBeanList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int position,
				Object object) {
			((ViewPager)container).removeView((View)object);
		}

		@Override
		@SuppressLint("NewApi")
		public Object instantiateItem(ViewGroup container, int position) {
			View viewPagerView;
			if(container.getChildCount() > position){
				viewPagerView = container.getChildAt(position);
				if(viewPagerView ==null){
					viewPagerView = LayoutInflater.from(mContext).inflate(R.layout.viewpage_image,null );
				}
			}
			else{
				viewPagerView = LayoutInflater.from(mContext).inflate(R.layout.viewpage_image,null );
			}
			//
			PhotoBean photoBean = photoBeanList.get(position);
			final ImageView imageView = (ImageView) viewPagerView.findViewById(R.id.thumb_image);
			viewPagerView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, PhotoImageShowActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("imageList",new ArrayList<String>(photoUrlList));
					bundle.putInt("imageType", PhotoImageShowActivity.HTTP_URL_TYPE);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			UrlBitmapFormBean urlBitmapFormBean = new UrlBitmapFormBean();
			urlBitmapFormBean.setUrl(photoBean.getUrl());
			urlBitmapFormBean.setScale(true);

			int maxSize = getResources().getDimensionPixelSize(R.dimen.thumb_image_max_size);
			Logger.i("maxSize:"+maxSize);
			urlBitmapFormBean.setMaxSize(maxSize );
			UrlLoadBitmapAsyncTask urlLoadBitmapAsyncTask = new UrlLoadBitmapAsyncTask(mContext);
			urlLoadBitmapAsyncTask.setOnUrlLoadBitmapDealtListener(new OnUrlLoadBitmapDealtListener() {

				@Override
				public void success(Bitmap resultResponse) {
					imageView.setImageBitmap(resultResponse);
				}

				@Override
				public void failed(Exception exception) {
					imageView.setImageResource(R.drawable.image_failed);

				}

				@Override
				public void beforeDealt() {

				}
			});
			if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.HONEYCOMB){
				urlLoadBitmapAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,urlBitmapFormBean);
			}
			else{
				urlLoadBitmapAsyncTask.execute(urlBitmapFormBean);
			}

			((ViewPager)container).addView(viewPagerView);
			return viewPagerView;
		}

	}

	private void subViewInit(){
		roomAssetLayout = findViewById(R.id.room_asset_layout);
		materialInfoLayout = findViewById(R.id.material_info_layout);
		feedbackLayout = findViewById(R.id.repair_detail_feedback_layout);
		mRepairCodeView = (TextView) findViewById(R.id.repair_detail_repair_code);
		mPlaceView = (TextView) findViewById(R.id.repair_detail_place);
		mContactNameView = (TextView) findViewById(R.id.repair_detail_contact_name);
		mContactTelView = (TextView) findViewById(R.id.repair_detail_contact_tel);
		mContactTelCallView = (ImageView) findViewById(R.id.repair_detail_contact_call);
		mAssetAndTroubleView = (TextView) findViewById(R.id.repair_detail_asset_and_trouble);
		mOtherTroubleNameView = (TextView) findViewById(R.id.fix_info_other_trouble_name);
		mFeedbackCountView = (TextView) findViewById(R.id.repair_detail_feedback_count);
		mStartTimeView = (TextView) findViewById(R.id.repair_detail_start_time);
		mAcceptTimeView = (TextView) findViewById(R.id.repair_detail_accept_time);
		mEndTimeView = (TextView) findViewById(R.id.repair_detail_end_time);
		mUploadPhotoLineView = (LinearLayout) findViewById(R.id.upload_photo_list);
		mTakePhotosView = (Button) findViewById(R.id.repair_detail_take_photo);
		mRoomAssetInfoView = (TextView) findViewById(R.id.repair_detail_room_asset_info);
		mMaterialInfoView = (TextView) findViewById(R.id.repair_detail_material_info);
		mStatusIconView = (ImageView) findViewById(R.id.status_icon);
		imageViewPager = (ViewPager) findViewById(R.id.viewpager_repair_order_image);
		imageViewPager.setOffscreenPageLimit(5);
		//材料layout点击进入材料详细页面
		materialInfoLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("repairOrderBean", resultBean);
				intent.putExtra("isLargeCharacter",outIsLargeCharacter);
				intent.putExtras(bundle);
				intent.setClass(mContext, RepairMaterialUsedInfoActivity.class);
				startActivity(intent);

			}
		});

		//反馈layout点击进入反馈页面
		feedbackLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("repairOrderId",repairOrderId);
				intent.setClass(mContext, FeedbackActivity.class);
				intent.putExtra("isLargeCharacter",outIsLargeCharacter);
				startActivityForResult(intent, FEEDBACK_REQUEST_CODE);

			}
		});


		//资产layout点击进入资产页面
		roomAssetLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("roomId",roomId.toString());
				intent.putExtra("roomName",resultBean.getRoomName());
				intent.putExtra("isLargeCharacter", outIsLargeCharacter);
				intent.setClass(mContext, RoomAssetActivity.class);
				startActivity(intent);

			}
		});

		mContactTelCallView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				String phone = mContactTelView.getText().toString();
				if(phone.toString().trim().length() == 0){
					ToastUtil.showToast(mContext, "联系电话为空");
				}
				else{
					Intent intent = new Intent(Intent.ACTION_DIAL ,Uri.parse ("tel:" +phone)); 
					startActivity(intent);

				}

			}
		});

		photoBeanList = new ArrayList<PhotoBean>();
		imagePagerAdapter = new ImagePagerAdapter();
		imageViewPager.setAdapter(imagePagerAdapter);
	}

	private void subViewSetpuData(RepairOrderBean repairOrderBean){
		roomId = repairOrderBean.getRoomId();
		mRepairCodeView.setText(repairOrderBean.getRepairCode());
		mPlaceView.setText(repairOrderBean.getRoomName());
		//
		if(application.hasRunTimePermission(RUNTIME_PERMISSION_KEY,RuntimePermission.REPAIRMAN_ROLE)){

			mContactNameView.setText(repairOrderBean.getUserName());
			mContactTelView.setText(repairOrderBean.getUserContactTel());
		}
		else if(application.hasRunTimePermission(RUNTIME_PERMISSION_KEY,RuntimePermission.APPLIER_ROLE)){
			mContactNameView.setText(repairOrderBean.getRepairmanName());
			mContactTelView.setText(repairOrderBean.getRepairmanContactTel());
		}
		else{
			mContactNameView.setText(repairOrderBean.getUserName());
			mContactTelView.setText(repairOrderBean.getUserContactTel());
		}
		//
		mStartTimeView.setText(repairOrderBean.getStartTime());
		mAcceptTimeView.setText(repairOrderBean.getAcceptTime());
		mEndTimeView.setText(repairOrderBean.getEndTime());
		mAssetAndTroubleView.setText(getString(R.string.format_repair_order_asset_and_trouble, repairOrderBean.getAssetName(),repairOrderBean.getTroubleName()));
		String other = repairOrderBean.getOther();
		currentStatus = Integer.valueOf(repairOrderBean.getStatus());
		mOtherTroubleNameView.setText(other);
		mOtherTroubleNameView.setText(repairOrderBean.getOther());
		mFeedbackCountView.setText(repairOrderBean.getFeedbackCount().toString() +"条");
		mRoomAssetInfoView.setText(repairOrderBean.getAssetCount().toString()+"件");
		mMaterialInfoView.setText(repairOrderBean.getMaterialCount().toString()+"件");
	}


	private void showOperation(int operationIndex){
		mOperationAcceptBtn.setVisibility(ViewGroup.GONE);//
		mOperationFinishBtn.setVisibility(ViewGroup.GONE);
		mOperationRefixFinishBtn.setVisibility(ViewGroup.GONE);
		switch (operationIndex) {
		case LARGE_OPERATION_ACCEPT:
			mOperationAcceptBtn.setVisibility(ViewGroup.VISIBLE);
			break;
		case LARGE_OPERATION_FINISH:
			mOperationFinishBtn.setVisibility(ViewGroup.VISIBLE);
			break;
		case LARGE_OPERATION_REFIX_FINISH:
			mOperationRefixFinishBtn.setVisibility(ViewGroup.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	public interface ChangeStatusListener{
		public void onFailed();
		public void onSuccess();
		public void onBefore();
	}
	
	private class TakePhotoWheelAdapter implements WheelViewAdapter{

		@Override
		public int getItemsCount() {
			return takePhotoItems.length;
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.wheel_list_item_common, null);
			TextView textView = (TextView) convertView.findViewById(R.id.wheel_item_text); 
			String name = takePhotoItems[index];
			textView.setText(name);
			return convertView;
		}

		@Override
		public View getEmptyItem(View convertView, ViewGroup parent) {
			return null;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {

		}

	}
	
	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			View subPageLayout = findViewById(R.id.repair_order_detail_background_layout);
			if(subPageLayout != null){
				subPageLayout.setBackgroundResource(R.drawable.spring_horse_repair_order_detail);
			}
			break;

		default:
			break;
		}
	}
}
