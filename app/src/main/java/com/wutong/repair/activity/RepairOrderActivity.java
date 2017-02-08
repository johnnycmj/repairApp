package com.wutong.repair.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Context;
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
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFileFormBean;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.http.util.HttpFileAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpFileAsyncTask.OnProgressRefresh;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.repair.BaseActivity;
import com.wutong.repair.BaseFragmentActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.AssetBean;
import com.wutong.repair.data.bean.BuildingBean;
import com.wutong.repair.data.bean.CheckSameBean;
import com.wutong.repair.data.bean.LandmarkBean;
import com.wutong.repair.data.bean.RepairOrderFormBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.RoomBean;
import com.wutong.repair.data.bean.StreetBean;
import com.wutong.repair.data.bean.TroubleBean;
import com.wutong.repair.dictionary.RepairTypeDict;
import com.wutong.repair.fragment.RepairFragment;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.DataChangeListener;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;
import com.wutong.repair.util.BitmapUtil;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.CommonSwitcherUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;
import com.wutong.repair.util.HttpResponseUtil;

public class RepairOrderActivity extends BaseFragmentActivity {
	//	public static final TroubleBean TROUBLE_TIP_SELECT = new TroubleBean("0","-无项目-");
	//	public static final AssetBean ASSET_TIP_SELECT = new AssetBean("0","-请选择-");
	private final int PHOTO_NOTIFICATION_BEFORE_ID = 0x7979;
	private final int PHOTO_NOTIFICATION_DONE_ID = 0x8080;
	private final int PHOTO_NOTIFICATION_CANCELED = 0x112;

	private final int TAKE_PHOTOS_REQUEST_CODE = 33; 
	private final int IMAGE_FROM_MOBILE_REQUEST_CODE = 22;
	private final int IMAGE_CROPED_REQUEST_CODE = 11;


	private TextView titlebarTitleView;
	private ImageView titlebarBackView;
	private ImageView titlebarSubmitView;

	private TextView mDormInfoView;
	private TextView mTelInfoView;
	private TextView mAssetValueView;
	private SingleSelectDialogFragment mAssetValueDialogFragment;
	private TextView mTroubleValueView;
	private SingleSelectDialogFragment mTroubleValueDialogFragment;
	private LinearLayout mTroubleExistedWarnView;
	private EditText mOtherTroubleValueView;
	private LinearLayout mUploadPhotoLineView;
	private ImageView mTakePhotosView;

	private String appTempDir;
	private String[] takePhotoItems = new String[]{"相机拍摄","手机上传"};
	private TakePhotoWheelAdapter takePhotoWheelAdapter;
	private View takePhotoContentView;
	private WheelView takePhotoWv;
	private CustomDialog takePhotoCustomDialog;

	private List<AssetBean> assetBeanList;
	private List<TroubleBean> troubleBeanList;


	private List<BuildingBean> buildingBeanList;
	private List<StreetBean> streetBeanList;
	private List<RoomBean> roomBeanList;
	private List<LandmarkBean> landmarkBeanList;

	private TextView mBuildingView;
	private SingleSelectDialogFragment mBuildingDialogFragment;
	private TextView mStreetView;
	private SingleSelectDialogFragment mStreetDialogFragment;
	private TextView mRoomView;
	private SingleSelectDialogFragment mRoomDialogFragment;
	private TextView mLandmarkView;
	private SingleSelectDialogFragment mLandmarkDialogFragment;

	private String repairType;

	private String roomName;


	private Integer successedNumber = 0;
	private int totalNumber;

	private TextView repairOrderTypeSelectView;
	private View repairOrderTypeSelectContentView;
	private WheelView repairOrderTypeSelectWv;
	private RepairTypeWheelAdapter repairOrderTypeSelectAdapter;
	private String[] repairOrderTypeItemArray;

	private Vector<String> repairTypeSelectNameArray;
	private Vector<Integer> repairTypeSelectValueArray;

	private CustomDialog repairOderTypeSeletCustomDialog;

	private int currentRepairOrderTypeIndex;

	private boolean hasDormInfoSetup = false;

	private View buildingLayout;
	private View roomLayout;
	private View dormInfoLayout;
	private View streetLayout;
	private View landmarkLayout;


	private String dataAssetId = "";
	private String dataTroubleId = "";
	private String dataStreetId = "";
	private String dataLandmarkId = "";
	private String dataBuildingId = "";
	private String dataRoomId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.i("RepairOrderActivity onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_repair_order);
		titlebarInit();
		viewInit();

	}
	@Override
	public void onBackPressed() {
		CommonOperateUtil.hideIME(this);
		super.onBackPressed();
	}
	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_repair_order);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RepairOrderActivity.this.onBackPressed();
			}
		});
		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(checkDataValid()){
					submitForm();
				}

			}
		});
	}


	private void viewInit(){

		repairType = getIntent().getStringExtra("repairType");
		Logger.i("repairType enter is:" +repairType);
		if(repairType == null){
			repairType = RepairTypeDict.DORM_TYPE;
		}
		//初始化控件
		repairTypeSelectNameArray = new Vector<String>();
		repairTypeSelectValueArray = new Vector<Integer>();
		String[] temp = getResources().getStringArray(R.array.repair_order_types);
		for(int i=0;i < temp.length; i++){
			repairTypeSelectNameArray.add(temp[i]);
			repairTypeSelectValueArray.add(i);
		}

		if(application.getLoginInfoBean().getDormRoomId().equals("0")){
			//隐藏公寓宿舍
			repairTypeSelectNameArray.remove(0);
			repairTypeSelectValueArray.remove(0);
			if(repairType.equals(RepairTypeDict.DORM_TYPE)){
				//移动至公共室内
				repairType = RepairTypeDict.PUBLIC_INNER_TYPE;
			}
		}

		mDormInfoView = (TextView) findViewById(R.id.dorm_name_value);
		mTelInfoView = (TextView) findViewById(R.id.tel_name_value);
		mAssetValueView = (TextView) findViewById(R.id.assets_name_value);
		mTroubleValueView = (TextView) findViewById(R.id.trouble_name_value);
		mOtherTroubleValueView = (EditText) findViewById(R.id.other_trouble_value);
		mUploadPhotoLineView = (LinearLayout) findViewById(R.id.upload_photo_list);
		mTakePhotosView = (ImageView) findViewById(R.id.take_photos);
		mBuildingView = (TextView) findViewById(R.id.building_value);
		mStreetView = (TextView) findViewById(R.id.street_value);
		mRoomView = (TextView) findViewById(R.id.room_value);
		mLandmarkView = (TextView) findViewById(R.id.landmark_value);
		mTroubleExistedWarnView = (LinearLayout) findViewById(R.id.trouble_exist_layout);

		//
		buildingLayout = findViewById(R.id.building_layout);
		roomLayout = findViewById(R.id.room_layout);
		dormInfoLayout = findViewById(R.id.dorm_info_layout);
		streetLayout = findViewById(R.id.street_layout);
		landmarkLayout = findViewById(R.id.landmark_layout);
		//
		//处理 报修类型
		repairOrderTypeSelectContentView = LayoutInflater.from(mContext).inflate(R.layout.common_single_list_select_no_button, null);

		repairOrderTypeItemArray = repairTypeSelectNameArray.toArray(new String[]{});
		repairOrderTypeSelectWv = (WheelView) repairOrderTypeSelectContentView.findViewById(R.id.select_item_wheel);
		repairOrderTypeSelectWv.setBackgroundResource(R.drawable.common_wheel_bg);
		repairOrderTypeSelectWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		repairOrderTypeSelectAdapter = new RepairTypeWheelAdapter();
		repairOrderTypeSelectWv.setViewAdapter(repairOrderTypeSelectAdapter);
		repairOrderTypeSelectView = (TextView) findViewById(R.id.repair_order_type_select);
		repairOderTypeSeletCustomDialog = new CustomDialog.Builder(mContext)
		.setTitle("报修范围")
		.setView(repairOrderTypeSelectContentView)
		.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int index = repairOrderTypeSelectWv.getCurrentItem();
				switchRepairOrderType(index);
				dialog.dismiss();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		})
		.create();
		repairOrderTypeSelectView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!repairOderTypeSeletCustomDialog.isShowing()){
					repairOderTypeSeletCustomDialog.show();
				}

			}
		});

		//
		//通用信息
		String phone = application.getLoginInfoBean().getPhone();
		if(phone != null){
			mTelInfoView.setText(phone);
		}

		//宿舍显示
		mTroubleValueView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTroubleValueDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) troubleBeanList);
				data.putString("title", "报修项目");
				mTroubleValueDialogFragment.setArguments(data);
				mTroubleValueDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mTroubleValueView.setText(itemBean.getTextName());
						dataTroubleId = itemBean.getValue();
						
						String dormId = application.getLoginInfoBean().getDormRoomId();
						//判断 小项是不是“其他”
						if(itemBean.getTextName().startsWith("其他")){//临时方案，以"其他"开头都认为是
							//hide
							mTroubleExistedWarnView.setVisibility(ViewGroup.GONE);
						}
						else{
							if(repairTypeSelectValueArray.get(currentRepairOrderTypeIndex) == 0){
								CheckSameBean checkSameBean = new CheckSameBean();
								checkSameBean.setAssetId(dataAssetId);
								checkSameBean.setDormId(dormId);
								checkSameBean.setTroubleId(dataTroubleId);
								checkRecentlyOrderAsyncHttp(checkSameBean);
							}
							else{
								mTroubleExistedWarnView.setVisibility(ViewGroup.GONE);
							}
						}
						
					}
				});
				mTroubleValueDialogFragment.show(getSupportFragmentManager(), "single-select");
			}
		});
		//
		

		//室内显示

		buildingBeanList = new ArrayList<BuildingBean>();
		roomBeanList = new ArrayList<RoomBean>();
		
		mBuildingView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBuildingDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) buildingBeanList);
				data.putString("title", "报修楼房");
				mBuildingDialogFragment.setArguments(data);
				mBuildingDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mBuildingView.setText(itemBean.getTextName());
						dataBuildingId = itemBean.getValue();
						loadRoomListAsyncHttp(dataBuildingId);
						
					}
				});
				mBuildingDialogFragment.show(getSupportFragmentManager(), "single-select");
			}
		});
		
		mRoomView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mRoomDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) roomBeanList);
				data.putString("title", "报修房间");
				mRoomDialogFragment.setArguments(data);
				mRoomDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mRoomView.setText(itemBean.getTextName());
						dataRoomId = itemBean.getValue();
						
					}
				});
				mRoomDialogFragment.show(getSupportFragmentManager(), "single-select");
			}
		});
		

		loadBuildingListAsyncHttp();

		//室外显示
		streetBeanList = new ArrayList<StreetBean>();
		landmarkBeanList = new ArrayList<LandmarkBean>();
		
		mStreetView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mStreetDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) streetBeanList);
				data.putString("title", "报修街道");
				mStreetDialogFragment.setArguments(data);
				mStreetDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mStreetView.setText(itemBean.getTextName());
						dataStreetId = itemBean.getValue();
						loadLandmarkAsyncHttp(dataStreetId);
					}
				});
				mStreetDialogFragment.show(getSupportFragmentManager(), "single-select");
			}
		});
		mLandmarkView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLandmarkDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) landmarkBeanList);
				data.putString("title", "报修标志建筑");
				mLandmarkDialogFragment.setArguments(data);
				mLandmarkDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mLandmarkView.setText(itemBean.getTextName());
						dataLandmarkId = itemBean.getValue();
						
						
					}
				});
				mLandmarkDialogFragment.show(getSupportFragmentManager(), "single-select");
			}
		});

		loadStreetListAsyncHttp();

		switchRepairOrderType(repairType);

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
		//初始化配置
		Intent intent = getIntent();
		boolean isFromIndex = intent.getBooleanExtra("isFromIndex", false);
		if(isFromIndex){
			SharedPreferences tempPreferences = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
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

		/////报修项
		assetBeanList = new ArrayList<AssetBean>();
		troubleBeanList = new ArrayList<TroubleBean>();
		//
		mAssetValueView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAssetValueDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) assetBeanList);
				data.putString("title", "报修类型");
				mAssetValueDialogFragment.setArguments(data);
				mAssetValueDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						mAssetValueView.setText(itemBean.getTextName());
						dataAssetId = itemBean.getValue();
						mTroubleExistedWarnView.setVisibility(ViewGroup.GONE);
						if(dataAssetId.equals("0")){
							troubleBeanList.clear();
							
							mTroubleValueView.setEnabled(false);
						}
						else{
							mTroubleValueView.setEnabled(true);
							loadTroubleListAsyncHttp(dataAssetId);
						}
					}
				});
				mAssetValueDialogFragment.show(getSupportFragmentManager(), "single-select");
			}
		});

		//


		

		loadAssetListAsyncHttp(String.valueOf((repairTypeSelectValueArray.get(currentRepairOrderTypeIndex)+1)));
	}

	private void takePhotoEvent(){
		if(!takePhotoCustomDialog.isShowing()){
			takePhotoCustomDialog.show();
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
				File file = CommonSwitcherUtil.getAbsoluteImagePath(RepairOrderActivity.this, uri);
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

	public void deleteThumb(View view){
		ViewParent parent = view.getParent().getParent();
		View layout = (View) parent;
		String key = layout.getTag().toString();
		SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.remove(key);
		editor.commit();
		mUploadPhotoLineView = (LinearLayout) findViewById(R.id.upload_photo_list);
		mUploadPhotoLineView.removeView((View)parent);
		ToastUtil.showToast(RepairOrderActivity.this, "成功删除！");
	}



	
	//////////////
	private final class BuildingBaseAdapter extends BaseAdapter{

		private List<BuildingBean> list;


		public BuildingBaseAdapter(List<BuildingBean> list) {
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
			convertView = LayoutInflater.from(RepairOrderActivity.this).inflate(R.layout.spinner_common, null);
			TextView textview = (TextView) convertView.findViewById(R.id.spinner_content);
			BuildingBean buildingBean = list.get(position);
			textview.setText(buildingBean.getName());
			return convertView;
		}

	}
	

	private final class RoomBaseAdapter extends BaseAdapter{

		private List<RoomBean> list;


		public RoomBaseAdapter(List<RoomBean> list) {
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
			convertView = LayoutInflater.from(RepairOrderActivity.this).inflate(R.layout.spinner_common, null);
			TextView textview = (TextView) convertView.findViewById(R.id.spinner_content);
			RoomBean roomBean = list.get(position);
			textview.setText(roomBean.getName());
			return convertView;
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
			totalNumber = map.size();
			if(totalNumber >0){
				//
				Object[] array = map.values().toArray();
				fileMap = new HashMap<String, String>();
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
		httpFileFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderPhotoUploadUrl());
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(repairOrderId,repairOrderId );//服务器用奇葩方法取键名作为参数，不解释
		httpFileFormBean.setParams(paramMap);
		httpFileFormBean.setFileMap(fileMap);
		Logger.i("上传照片数量：" +fileMap.size());
		Logger.i("图片url:" + httpFileFormBean.getUrl());
		httpFileAsyncTask.setProgressRefresh(new OnProgressRefresh() {

			@Override
			public void refreshProgress(int index) {
				successedNumber++;
			}
		});
		httpFileAsyncTask.setOnDealtListener(new HttpFileAsyncTask.OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				Logger.i("resultResponse:" +resultResponse);
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



	/**
	 * 获取报修类型列表
	 * @param repairType
	 */
	private void loadAssetListAsyncHttp(String repairType){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getAssetListLoadUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair itemTypePair = new BasicNameValuePair("itemType", repairType);
		params.add(itemTypePair);
		NameValuePair dormIdPair = new BasicNameValuePair("hotelRoomId", application.getLoginInfoBean().getDormRoomId());
		params.add(dormIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<AssetBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<AssetBean>>>(){}.getType());
				List<AssetBean> resultList = responseBean.getData();

				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList != null){
					if(resultList.isEmpty()){
						assetBeanList.clear();
						assetBeanList.addAll(resultList);
						mAssetValueView.setEnabled(false);
					}
					else{
						assetBeanList.clear();
						assetBeanList.addAll(resultList);
						mAssetValueView.setEnabled(true);
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取报修类型数据异常,错误代码[0]");
				}
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
				hideProgressDialog();
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();
				mAssetValueView.setEnabled(false);
				mTroubleValueView.setEnabled(false);
				mAssetValueView.setText("");
				mTroubleValueView.setText("");
			}
		});
		httpAsyncTask.execute(httpFormBean);

	}



	/**
	 * 报修项
	 * @param assetType
	 */
	private void loadTroubleListAsyncHttp(String assetType){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getTroubleListLoadUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair pidPair = new BasicNameValuePair("pid", assetType);
		params.add(pidPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<TroubleBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<TroubleBean>>>(){}.getType());
				List<TroubleBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList != null){
					if(resultList.isEmpty()){
						troubleBeanList.clear();
						troubleBeanList.addAll(resultList);
						mTroubleValueView.setEnabled(false);
					}
					else{
						troubleBeanList.clear();
						troubleBeanList.addAll(resultList);
						mTroubleValueView.setEnabled(true);
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取报修类型数据异常,错误代码[0]");
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
				mTroubleValueView.setEnabled(false);
				mTroubleValueView.setText("");
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}



	/**
	 * 提交报修单
	 */
	private void submitAsyncHttp(RepairOrderFormBean repairOrderFormBean){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderSubmitUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair userIdPair = new BasicNameValuePair("userId", repairOrderFormBean.getApplyUserId());
		params.add(userIdPair);
		NameValuePair markIdPair = new BasicNameValuePair("markId", repairOrderFormBean.getDormRoomId());
		params.add(markIdPair);
		NameValuePair itemPIdPair = new BasicNameValuePair("itemPId", repairOrderFormBean.getAssetId());
		params.add(itemPIdPair);
		NameValuePair itemIdPair = new BasicNameValuePair("itemId", repairOrderFormBean.getTroubleId());
		params.add(itemIdPair);
		NameValuePair detailPair = new BasicNameValuePair("detail", repairOrderFormBean.getOther());
		params.add(detailPair);
		NameValuePair repairTypePair = new BasicNameValuePair("repairType", repairOrderFormBean.getRepairType());
		params.add(repairTypePair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean<Integer> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<Integer>>(){}.getType());
				Integer repairId = responseBean.getData();
				if(responseBean.isSuccess()&&repairId != null && repairId.intValue() >0){
					SharedPreferences preferences = getSharedPreferences(SettingConfig.TEMP_PHOTO, MODE_PRIVATE);
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

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void beforeDealt() {
				titlebarSubmitView.setEnabled(false);
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}



	/**
	 * 获取建筑物
	 */
	private void loadBuildingListAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getBuildingListLoadUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<BuildingBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<BuildingBean>>>(){}.getType());
				List<BuildingBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList != null){
					if(resultList.isEmpty()){
						buildingBeanList.clear();
						buildingBeanList.addAll(resultList);
						mBuildingView.setEnabled(false);
					}
					else{
						buildingBeanList.clear();
						buildingBeanList.addAll(resultList);
						mBuildingView.setEnabled(true);
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取建筑物数据异常,错误代码[0]");
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
				mBuildingView.setEnabled(false);
				mRoomView.setEnabled(false);
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}



	/**
	 * 获取街道
	 */
	private void loadStreetListAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getStreetListLoadUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean<List<StreetBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<StreetBean>>>(){}.getType());
				List<StreetBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList != null){
					if(resultList.isEmpty()){
						mStreetView.setEnabled(false);
						streetBeanList.clear();
					}
					else{
						streetBeanList.clear();
						streetBeanList.addAll(resultList);
						mStreetView.setEnabled(true);
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取街道数据异常,错误代码[0]");
				}
				hideProgressDialog();
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();
				mStreetView.setEnabled(false);
				mLandmarkView.setEnabled(false);
			}
		});
		httpAsyncTask.execute(httpFormBean);

	}



	/**
	 * 获取宿舍列表
	 */
	private void loadRoomListAsyncHttp(String buildingId){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRoomListLoadUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair hotelFloorIdPair = new BasicNameValuePair("hotelFloorId", buildingId);
		params.add(hotelFloorIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean<List<RoomBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<RoomBean>>>(){}.getType());
				List<RoomBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList != null){
					if(resultList.isEmpty()){
						roomBeanList.clear();
						roomBeanList.addAll(resultList);
						mRoomView.setEnabled(false);

					}
					else{
						roomBeanList.clear();
						roomBeanList.addAll(resultList);
						mRoomView.setEnabled(true);
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取宿舍数据异常,错误代码[0]");
				}

				hideProgressDialog();
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
				hideProgressDialog();
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();
				mRoomView.setEnabled(false);
				mRoomView.setText("");
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}



	/**
	 * 获取标志性建筑
	 * @param streetId
	 */
	private void loadLandmarkAsyncHttp(String streetId){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getLandmarkLoadUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair streetIdPair = new BasicNameValuePair("streetId", streetId);
		params.add(streetIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<LandmarkBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<LandmarkBean>>>(){}.getType());
				List<LandmarkBean> resultList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(resultList != null){
					if(resultList.isEmpty()){
						landmarkBeanList.clear();
						landmarkBeanList.addAll(resultList);
						mLandmarkView.setEnabled(false);
					}
					else{
						landmarkBeanList.clear();
						landmarkBeanList.addAll(resultList);
						mLandmarkView.setEnabled(true);
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取标志性建筑数据异常,错误代码[0]");
				}
			}

			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
				hideProgressDialog();
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();
				mLandmarkView.setEnabled(false);
				mLandmarkView.setText("");
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}



	/**
	 * 宿舍报修，同室同类型报修提醒查询（静默查询，出错不提示）
	 * 
	 */
	private void checkRecentlyOrderAsyncHttp(CheckSameBean checkSameBean){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getCheckSameOrderUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair hotelRoomIdPair = new BasicNameValuePair("hotelRoomId", checkSameBean.getDormId());
		params.add(hotelRoomIdPair);
		NameValuePair itemPIdPair = new BasicNameValuePair("itemPId", checkSameBean.getAssetId());
		params.add(itemPIdPair);
		NameValuePair itemIdPair = new BasicNameValuePair("itemId", checkSameBean.getTroubleId());
		params.add(itemIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean<Integer> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<Integer>>(){}.getType());
				int count = responseBean.getData();
				if(count >0){
					mTroubleExistedWarnView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					mTroubleExistedWarnView.setVisibility(ViewGroup.GONE);
				}
			}

			@Override
			public void failed(Exception exception) {

			}

			@Override
			public void beforeDealt() {

			}
		});
		httpAsyncTask.execute(httpFormBean);
	}



	private boolean checkDataValid(){
		//检查报修大项和报修小项
		//选择了报修小项的“其他”而没有备注
		if(mTroubleValueView.getText().toString().trim().startsWith("其他")
				&& mOtherTroubleValueView.getText().toString().toString().trim().length() ==0){
			ToastUtil.showToast(mContext, "请填写备注，以便维修人员了解详情");
			return false;
		}
		//如果是公共室外，选择了其他+其他，填写备注
		if(String.valueOf((repairTypeSelectValueArray.get(currentRepairOrderTypeIndex)+1)).equals(RepairTypeDict.PUBLIC_OUTTER_TYPE)
				&& mLandmarkView.getText().toString().trim().startsWith("其他")
				&& mOtherTroubleValueView.getText().toString().toString().trim().length() ==0){
			ToastUtil.showToast(mContext, "请填写备注，以便维修人员了解详情");
			return false;
		}
		return true;
	}

	private void submitForm(){
		String userId = application.getLoginInfoBean().getUserId().toString();
		String dormRoomId = application.getLoginInfoBean().getDormRoomId();
		RepairOrderFormBean repairOrderFormBean = new RepairOrderFormBean();
		switch (repairTypeSelectValueArray.get(currentRepairOrderTypeIndex)){
		case 0:
			if(dormRoomId == null || dormRoomId.equals("0")){
				ToastUtil.showToast(mContext, "系统没有您的宿舍信息，无法报修公寓宿舍类型的单子");
				return;
			}
			repairOrderFormBean.setDormRoomId(dormRoomId);
			break;
		case 1:
			if(mBuildingView.getText().toString().trim().length() == 0){
				ToastUtil.showToast(mContext, "请选择一个建筑");
				boolean isfocus = mBuildingView.requestFocus();
				Logger.i("isfocus" + isfocus);
				return;
			}
			if(mRoomView.getText().toString().trim().length() == 0){
				ToastUtil.showToast(mContext, "请选择一个房间");
				boolean isfocus = mRoomView.requestFocus();
				Logger.i("isfocus" + isfocus);
				return;
			}
			repairOrderFormBean.setDormRoomId(dataRoomId);
			break;
		case 2:
			if(mStreetView.getText().toString().trim().length() == 0){
				ToastUtil.showToast(mContext, "请选择一个街道");
				boolean isfocus = mStreetView.requestFocus();
				Logger.i("isfocus" + isfocus);
				return;
			}
			if(mLandmarkView.getText().toString().trim().length() == 0){
				ToastUtil.showToast(mContext, "请选择一个标志性建筑");
				boolean isfocus = mLandmarkView.requestFocus();
				Logger.i("isfocus" + isfocus);
				return;
			}
			repairOrderFormBean.setDormRoomId(dataLandmarkId);
			break;
		}


		//
		if(mAssetValueView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "请选择一个报修类型");
			boolean isfocus = mAssetValueView.requestFocus();
			Logger.i("isfocus" + isfocus);
			return;
		}

		if(mTroubleValueView.getText().toString().trim().length() == 0){
			ToastUtil.showToast(mContext, "请选择一个报修项目");
			boolean isfocus = mTroubleValueView.requestFocus();
			Logger.i("isfocus" + isfocus);
			return;
		}
		//


		repairOrderFormBean.setApplyUserId(userId);


		repairOrderFormBean.setAssetId(dataAssetId);
		repairOrderFormBean.setTroubleId(dataTroubleId);
		repairOrderFormBean.setOther(mOtherTroubleValueView.getText().toString());
		repairOrderFormBean.setRepairType(String.valueOf((repairTypeSelectValueArray.get(currentRepairOrderTypeIndex)+1)));
		Logger.i("repairOrderFormBean:" + repairOrderFormBean);
		submitAsyncHttp(repairOrderFormBean);
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
		for(String key:set){
			View photoView = LayoutInflater.from(RepairOrderActivity.this).inflate(R.layout.imageview_upload_photo,null );
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

	private void submitSuccess(){
		ToastUtil.showToast(RepairOrderActivity.this, "报修单提交成功！");
		Intent intent =new Intent();
		intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_repair_fragment_name));
		intent.putExtra(getString(R.string.call_fragment_key_code), RepairFragment.CALL_CODE_NOT_ACCEPT_REFRESH);//新报修单刷新未受理
		setResult(RESULT_OK,intent);
		RepairOrderActivity.this.finish();
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
//		notification.setLatestEventInfo(context,title, content,contentIntent);

		Intent i = new Intent(context, cls);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context,R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);

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

	/**
	 * 切换报修类型
	 */
	private void switchRepairOrderType(String repairType){
		switchRepairOrderType(repairTypeSelectValueArray.indexOf(Integer.valueOf(repairType)-1));
	}

	/**
	 * 切换报修类型
	 */
	private void switchRepairOrderType(int repairOrderTypeIndex){
		int index = repairTypeSelectValueArray.get(repairOrderTypeIndex);
		switch (index) {
		case 0://宿舍
			//获取宿舍信息
			if(!hasDormInfoSetup){
				roomName = application.getLoginInfoBean().getDormRoomName();
				mDormInfoView.setText(roomName);
				hasDormInfoSetup = true;
			}
			dormInfoLayout.setVisibility(ViewGroup.VISIBLE);

			buildingLayout.setVisibility(ViewGroup.GONE);
			roomLayout.setVisibility(ViewGroup.GONE);
			streetLayout.setVisibility(ViewGroup.GONE);
			landmarkLayout.setVisibility(ViewGroup.GONE);
			break;
		case 1://室内
			buildingLayout.setVisibility(ViewGroup.VISIBLE);
			roomLayout.setVisibility(ViewGroup.VISIBLE);

			dormInfoLayout.setVisibility(ViewGroup.GONE);
			streetLayout.setVisibility(ViewGroup.GONE);
			landmarkLayout.setVisibility(ViewGroup.GONE);
			break;
		case 2://室外
			streetLayout.setVisibility(ViewGroup.VISIBLE);
			landmarkLayout.setVisibility(ViewGroup.VISIBLE);

			dormInfoLayout.setVisibility(ViewGroup.GONE);
			buildingLayout.setVisibility(ViewGroup.GONE);
			roomLayout.setVisibility(ViewGroup.GONE);
			break;
		}
		currentRepairOrderTypeIndex = repairOrderTypeIndex;
		loadAssetListAsyncHttp(String.valueOf((index+1)));
		repairOrderTypeSelectView.setText(repairTypeSelectNameArray.get(repairOrderTypeIndex));
		
	}


	private class RepairTypeWheelAdapter implements WheelViewAdapter{

		@Override
		public int getItemsCount() {
			return repairOrderTypeItemArray.length;
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.wheel_list_item_common, null);
			TextView textView = (TextView) convertView.findViewById(R.id.wheel_item_text); 
			String name = repairOrderTypeItemArray[index];
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


	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_repair_order;

		default:
			return R.layout.activity_repair_order;
		}
	}

}
