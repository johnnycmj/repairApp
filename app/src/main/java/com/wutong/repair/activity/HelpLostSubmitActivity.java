package com.wutong.repair.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.tsz.afinal.http.AjaxParams;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFileFormBean;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.http.util.HttpFileAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpFileAsyncTask.OnProgressRefresh;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.repair.fragment.HelpFoundListFragment;
import com.wutong.repair.fragment.util.DateTimeDialogFragment;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment;
import com.wutong.repair.fragment.util.DateTimeDialogFragment.DateListener;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.DataChangeListener;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;
import com.wutong.repair.BaseActivity;
import com.wutong.repair.BaseFragmentActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.BuildingBean;
import com.wutong.repair.data.bean.FoundItemTypeBean;
import com.wutong.repair.data.bean.LostOrFoundPlaceBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.BitmapUtil;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.CommonSwitcherUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpLostSubmitActivity extends BaseFragmentActivity {
	private final int PHOTO_NOTIFICATION_BEFORE_ID = 0x7979;
	private final int PHOTO_NOTIFICATION_DONE_ID = 0x8080;
	private final int PHOTO_NOTIFICATION_CANCELED = 0x112;

	private final int TAKE_PHOTOS_REQUEST_CODE = 33; 
	private final int IMAGE_FROM_MOBILE_REQUEST_CODE = 22;
	private final int IMAGE_CROPED_REQUEST_CODE = 11;
	
	
	private ImageView titlebarbackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;
	
	
	private TextView mNameView;
	private TextView mPhoneView;
	
	private EditText mRemarkView;

	private ImageView mTakePhotosView;
	private CustomDialog queryDialog;
	private String appTempDir;
	private String[] takePhotoItems = new String[]{"相机拍摄","手机上传"};
	private TakePhotoWheelAdapter takePhotoWheelAdapter;
	private View takePhotoContentView;
	private WheelView takePhotoWv;
	private CustomDialog takePhotoCustomDialog;

	private ImageView mPreUploadPhotoView;
	private ImageView mPreUploadPhotoDeleteView;
	
	private EditText mAddressView;
	
	private TextView mFoundTimeSelectView;
	private DateTimeDialogFragment mFoundTimeDialogFragment;
	private SimpleDateFormat localSdf;
	private SimpleDateFormat submitSdf;
	
	private TextView mPlaceSelectView;
	private SingleSelectDialogFragment mPlaceDialogFragment;
	private List<LostOrFoundPlaceBean> mPlaceList;
	
	private TextView mItemTypeSelectView;
	private SingleSelectDialogFragment mItemTypeDialogFragment;
	private List<FoundItemTypeBean> mItemTypeList;
	
	private String dataItemType = "0";
	private String dataBuildingId = "0";
	private String dataFoundTime = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_lost_submit);
		commonInit();
		intentInit();
		titlebarInit();
		viewInit();
		setupData();
	}
	
	private void commonInit(){
		//
		
		mItemTypeList = new ArrayList<FoundItemTypeBean>();
		mPlaceList = new ArrayList<LostOrFoundPlaceBean>();
		
		localSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
		submitSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
		
		dataFoundTime = submitSdf.format(Calendar.getInstance().getTime());
	}


	private void intentInit(){

	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_lost_submit);
		titlebarbackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarbackView.setVisibility(ViewGroup.VISIBLE);
		titlebarbackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HelpLostSubmitActivity.this.onBackPressed();

			}
		});

		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//
				submit();
			}
		});
	}

	private void viewInit(){
		/////
		mNameView = (TextView) findViewById(R.id.lost_submit_name);
		mPhoneView = (TextView) findViewById(R.id.lost_submit_phone);
		mRemarkView = (EditText) findViewById(R.id.lost_submit_remark);
		
		mAddressView = (EditText) findViewById(R.id.lost_submit_address);
		
		mTakePhotosView = (ImageView) findViewById(R.id.take_photos);
		mPreUploadPhotoView = (ImageView) findViewById(R.id.upload_photo_thumb);
		mPreUploadPhotoDeleteView = (ImageView) findViewById(R.id.thumb_delete);
		mItemTypeSelectView = (TextView) findViewById(R.id.lost_submit_type);
		mPlaceSelectView = (TextView) findViewById(R.id.lost_submit_street);
		mFoundTimeSelectView = (TextView) findViewById(R.id.lost_submit_time);
		
		mFoundTimeSelectView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mFoundTimeDialogFragment = new DateTimeDialogFragment();
				mFoundTimeDialogFragment.setDateListener(new DateListener() {

					@Override
					public void onDateSelect(int year,int month,int day,int hour,int minute) {
						Calendar calendar = Calendar.getInstance();
						calendar.set(year, month, day, hour, minute, 0);

						Date date = calendar.getTime();
						mFoundTimeSelectView.setText(localSdf.format(date));
						dataFoundTime = submitSdf.format(date);
					}
				});
				Bundle dataTime = new Bundle();
				dataTime.putString("datetime", DateTimeDialogFragment.sdf.format(Calendar.getInstance().getTime()));
				dataTime.putBoolean("isBefore", true);
				mFoundTimeDialogFragment.setArguments(dataTime);
				mFoundTimeDialogFragment.show(getSupportFragmentManager(), "test");
			}
		});
		mItemTypeSelectView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mItemTypeDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) mItemTypeList);
				mItemTypeDialogFragment.setArguments(data);
				mItemTypeDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mItemTypeSelectView.setText(itemBean.getTextName());
						dataItemType = itemBean.getValue();
					}
				});
				mItemTypeDialogFragment.show(getSupportFragmentManager(), "single-select");
			}
		});
		
		mPlaceSelectView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPlaceDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) mPlaceList);
				mPlaceDialogFragment.setArguments(data);
				mPlaceDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mPlaceSelectView.setText(itemBean.getTextName());
						dataBuildingId = itemBean.getValue();
					}
				});
				mPlaceDialogFragment.show(getSupportFragmentManager(), "single-select");
			}
		});
		mPreUploadPhotoDeleteView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteThumb(v);
			}
		});
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
		//初始化配置
		Intent intent = getIntent();
		boolean isFromIndex = intent.getBooleanExtra("isFromIndex", false);
		if(isFromIndex){
			SharedPreferences tempPreferences = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
			tempPreferences.edit().clear().commit();
		}
		/////
		takePhotoContentView = LayoutInflater.from(mContext).inflate(R.layout.common_single_list_select_no_button, null);

		takePhotoWv = (WheelView) takePhotoContentView.findViewById(R.id.select_item_wheel);
		takePhotoWv.setBackgroundResource(R.drawable.common_wheel_bg);
		takePhotoWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		takePhotoWheelAdapter = new TakePhotoWheelAdapter();
		takePhotoWv.setViewAdapter(takePhotoWheelAdapter);
		//事件监听
		mTakePhotosView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				takePhotoEvent();
			}
		});

		takePhotoCustomDialog = new CustomDialog.Builder(mContext).setTitle("设置")
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

	private void setupData(){
		loadPlaceListHttp();
		loadItemTypeListHttp();
		mNameView.setText(application.getLoginInfoBean().getRealName());
		mPhoneView.setText(application.getLoginInfoBean().getPhone());
	}
	private void submit(){
		if(mFoundTimeSelectView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "请选择弄丢时间");
			return;
		}
		if(mPlaceSelectView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "请选择弄丢地点");
			return;
		}
		if(mItemTypeSelectView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "请选择物品类型");
			return;
		}
		if(mRemarkView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "描述内容不能为空哦");
			mRemarkView.requestFocus();
			return;
		}
		if(queryDialog == null){
			queryDialog = new CustomDialog.Builder(mContext).setTitle("友情提示")
					.setMessage("确定提交？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
							submitAsyncHttp();
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
		queryDialog.show();
	}
	
	private void submitAsyncHttp(){
		String url = getString(R.string.http_url_load_lost_or_found_submit,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("apartmentId", dataBuildingId);//地点
		params.put("address", mAddressView.getText().toString());
		params.put("type", "1");//失物或招领
		params.put("itemType", dataItemType);//物品类别
		params.put("findDate", dataFoundTime);
		params.put("isOpenCollect","1");//失主公开联系方式
		params.put("phone", application.getLoginInfoBean().getPhone());
		params.put("detail", application.getLoginInfoBean().getPhone());
		params.put("title", "我弄丢了");
		params.put("content",  mRemarkView.getText().toString());
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				hideProgressDialog();
				com.wutong.androidprojectlibary.http.util.HttpResponseUtil.justToast(errorNo, t, mContext);
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void onStart() {
				super.onStart();
				showProgressDialog();
				titlebarSubmitView.setEnabled(false);
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				hideProgressDialog();
				CommonOperateUtil.hideIME(HelpLostSubmitActivity.this);
				ResponseBean<String> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<String>>(){}.getType());
				String repairId = responseBean.getData();
				if(responseBean.isSuccess()&&repairId != null && !repairId.equals("0")){
					SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
					Map<String, ?> map = preferences.getAll();
					if(map.size() >0 ){
						collectPhotoInfoAndSend(String.valueOf(repairId),map);
					}
					submitSuccess();
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}
				titlebarSubmitView.setEnabled(true);
				

			}

		});


	}
	
	private void submitSuccess(){
		Intent intent =new Intent();
		intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_lost_or_found_list_fragment_name));
		intent.putExtra(getString(R.string.call_fragment_key_code), HelpFoundListFragment.HELP_FOUND_SUBMIT_REQUEST_CODE);
		
		intent.setClass(mContext, HelpLostSubmitSuccessActivity.class);
		intent.putExtra("placeId", dataBuildingId);
		intent.putExtra("itemType", dataItemType);
		intent.putExtra("findDate", dataFoundTime);
		startActivity(intent);
		setResult(RESULT_OK,intent);
		HelpLostSubmitActivity.this.finish();
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

	private void takePhotoEvent(){
		if(!takePhotoCustomDialog.isShowing()){
			takePhotoCustomDialog.show();
		}
	}
	/**
	 * 收集图片信息然后发送
	 */
	private void collectPhotoInfoAndSend(String repairOrderId,Map<String, ?> map){
		CollectPhotoAsyncTask collectPhotoAsyncTask = new CollectPhotoAsyncTask();
		collectPhotoAsyncTask.execute(repairOrderId,map);
	}

	private class CollectPhotoAsyncTask extends AsyncTask<Object, Void, Integer>{
		private String repairOrderId ;
		private Map<String, String> fileMap;
		@Override
		protected Integer doInBackground(Object... paramArrayOfParams) {
			repairOrderId = paramArrayOfParams[0].toString();
			Map<String, ?> map = (Map<String, ?>) paramArrayOfParams[1];
			if(map.size() >0){
				//
				Object[] array = map.values().toArray();
				fileMap = new HashMap<String, String>();
				for(int i = 0;i<map.size();i++){
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
			}
			return 1;
		}

		@Override
		protected void onPreExecute() {
			showNotifcation(mContext,PHOTO_NOTIFICATION_BEFORE_ID, "正在收集图片信息", "请耐心等候", false,RepairOrderActivity.class);
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(repairOrderId != null && fileMap != null){
				uploadPhotoAsyncHttpFile(repairOrderId,fileMap);
			}
		}

	}
	
	/**
	 * 传送图片
	 */
	@SuppressLint("NewApi")
	private void uploadPhotoAsyncHttpFile(String repairOrderId,Map<String, String> fileMap){
		HttpFileAsyncTask httpFileAsyncTask = new HttpFileAsyncTask(mContext);
		HttpFileFormBean httpFileFormBean = new HttpFileFormBean();
		String url = getString(R.string.http_url_load_lost_or_found_submit_upload_image,application.getDomainUrl());
		httpFileFormBean.setUrl(url);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(repairOrderId,repairOrderId );//服务器用奇葩方法取键名作为参数，不解释
		httpFileFormBean.setParams(paramMap);
		httpFileFormBean.setFileMap(fileMap);
		Logger.i("上传照片数量：" +fileMap.size());
		Logger.i("图片url:" + httpFileFormBean.getUrl());
		httpFileAsyncTask.setProgressRefresh(new OnProgressRefresh() {

			@Override
			public void refreshProgress(int index) {
			}
		});
		httpFileAsyncTask.setOnDealtListener(new HttpFileAsyncTask.OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				Logger.i("resultResponse:" +resultResponse);
				showNotifcation(mContext,PHOTO_NOTIFICATION_DONE_ID, "图片发送完成", "", false,RepairOrderActivity.class);
				cleanPhotoCache();
			}

			@Override
			public void failed(Exception exception) {
				ToastUtil.showToast(mContext, "发送图片异常");
				cleanPhotoCache();
			}

			@Override
			public void beforeDealt() {
				showNotifcation(mContext,PHOTO_NOTIFICATION_BEFORE_ID, "信息已提交,正在上传图片", "请耐心等候", false,HelpLostSubmitActivity.class);

			}
		});
		if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.HONEYCOMB){
			httpFileAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,httpFileFormBean);
		}
		else{
			httpFileAsyncTask.execute(httpFileFormBean);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
				File file = CommonSwitcherUtil.getAbsoluteImagePath(HelpLostSubmitActivity.this, uri);
				String fileFullName = file.getAbsolutePath();
				Logger.i("FROM IMAGE [fileName:" +fileName + "][fileFullName:"+fileFullName +"]");
				SharedPreferences preferencesLast = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
				Editor editorLast = preferencesLast.edit();
				editorLast.putString(SettingConfig.RepairOrderForm.LAST_KEY_NAME, fileFullName);
				editorLast.commit();
				showImages();
			}
			else if(requestCode == IMAGE_CROPED_REQUEST_CODE){

			}
			else{
				//意外情况
				ToastUtil.showToast(mContext, "意外的返回");
			}

		}
		else if(resultCode == Activity.RESULT_CANCELED ){
			SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
			Editor editor = preferences.edit();
			String last = preferences.getString(SettingConfig.RepairOrderForm.LAST_KEY_NAME, null);
			//
			//
			if(last != null){
				editor.remove(last);
				editor.remove(SettingConfig.RepairOrderForm.LAST_KEY_NAME);
			}
			editor.commit();
		}
	}

	public void deleteThumb(View view){
		mPreUploadPhotoView.setImageDrawable(null);
		((View)mPreUploadPhotoView.getParent()).setVisibility(ViewGroup.GONE);
		SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
		ToastUtil.showToast(mContext, "成功删除！");
	}

	private void showImages(){
		SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
		String path = preferences.getString(SettingConfig.RepairOrderForm.LAST_KEY_NAME, null);
		if(path!=null){
			try {
				File preSaveFile = new File(path);
				Bitmap bitmap = BitmapUtil.convertBitmap(preSaveFile);
				mPreUploadPhotoView.setImageBitmap(bitmap);
				((View)mPreUploadPhotoView.getParent()).setVisibility(ViewGroup.VISIBLE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			((View)mPreUploadPhotoView.getParent()).setVisibility(ViewGroup.GONE);
		}
	}

	private void takePhoto(){
		String fileName = String.valueOf(new Date().getTime());
		String fileFullName = appTempDir+fileName+".jpg";
		Logger.i("FROM TAKE_PHOTOS [fileName:" +fileName + "][fileFullName:"+fileFullName +"]");
		File preSaveFile = new File(fileFullName);
		SharedPreferences preferencesLast = getSharedPreferences(SettingConfig.TEMP_PHOTO_LAST_TIME, MODE_PRIVATE);
		Editor editorLast = preferencesLast.edit();
		editorLast.putString(SettingConfig.RepairOrderForm.LAST_KEY_NAME, fileFullName);
		editorLast.commit();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(preSaveFile));
		startActivityForResult(intent, TAKE_PHOTOS_REQUEST_CODE);
	}

	private void cleanPhotoCache(){
		File appTempDirFile = new File(appTempDir);
		if(appTempDirFile.exists() && appTempDirFile.isDirectory()){
			File[] appTempDirFileChildren = appTempDirFile.listFiles();
			CleanCacheAsyncTask cleanCacheAsyncTask = new CleanCacheAsyncTask();
			cleanCacheAsyncTask.execute(appTempDirFileChildren);
		}
	}

	private void showNotifcation(Context context,final int notificationId,String title,String content,boolean hasSound, Class<?> cls){
		final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);               
//		Notification notification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());

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
	
	private void loadItemTypeListHttp(){
		String url = getString(R.string.http_url_load_lost_or_found_item_type_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
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
				ResponseBean<List<FoundItemTypeBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<FoundItemTypeBean>>>(){}.getType());
				List<FoundItemTypeBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					mItemTypeList.clear();
					mItemTypeList.addAll(responseList);
				}

			}

		});

	}
	
	private void loadPlaceListHttp(){
		String url = getString(R.string.http_url_load_lost_or_found_place_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
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
				ResponseBean<List<LostOrFoundPlaceBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<LostOrFoundPlaceBean>>>(){}.getType());
				List<LostOrFoundPlaceBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					mPlaceList.clear();
					mPlaceList.addAll(responseList);
				}

			}

		});

	}
}
