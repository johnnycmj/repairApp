package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.RoomAssetMaterialBean;
import com.wutong.repair.util.HttpResponseUtil;

import android.os.Bundle;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class RoomAssetActivity extends BaseActivity {

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private ListView mAssetExpandListView;
	private List<RoomAssetMaterialBean> roomAssetMaterialBeanList;
	private RoomAssetBaseAdapter roomAssetBaseAdapter;

	//初始化配置
	private	String roomId;
	private	String roomName;
	private boolean outIsLargeCharacter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_room_asset);
		intentInit();
		titlebarInit();
		viewInit();
	}

	private void intentInit(){
		//初始化配置
		Intent intent = getIntent();
		roomId = intent.getStringExtra("roomId");
		roomName = intent.getStringExtra("roomName");
		outIsLargeCharacter = intent.getBooleanExtra("isLargeCharacter",false);
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_room_asset);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RoomAssetActivity.this.onBackPressed();

			}
		});
	}
	private void viewInit(){
		//
		mAssetExpandListView = (ListView) findViewById(R.id.room_assets_list);

		View emptyView = findViewById(R.id.room_asset_empty);
		mAssetExpandListView.setEmptyView(emptyView);
		TextView roomNameView = (TextView) findViewById(R.id.fix_info_room_info);
		TextView roomNameLabelView = (TextView) findViewById(R.id.fix_info_room_info_label);
		roomNameView.setText(roomName);
		emptyView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadRoomAssetAsyncHttp(roomId);
				
			}
		});
		
		if(outIsLargeCharacter){
			roomNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			roomNameLabelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
		}
		else{
			roomNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			roomNameLabelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		}
		
		
		roomAssetMaterialBeanList = new ArrayList<RoomAssetMaterialBean>();
		roomAssetBaseAdapter = new RoomAssetBaseAdapter();
		mAssetExpandListView.setAdapter(roomAssetBaseAdapter);
		loadRoomAssetAsyncHttp(roomId);
	}

	private class RoomAssetBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return roomAssetMaterialBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return roomAssetMaterialBeanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RoomAssetMaterialBean roomAssetMaterialBean = (RoomAssetMaterialBean) getItem(position);
			String name = roomAssetMaterialBean.getAssetsName();
			String spec = roomAssetMaterialBean.getSpec();
			String brand = roomAssetMaterialBean.getBrand();
			String numberUint = roomAssetMaterialBean.getGrantNumber()+roomAssetMaterialBean.getUnit();
			String remark = roomAssetMaterialBean.getRemark();
			if(outIsLargeCharacter){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_room_asset_large, null);
			}
			else{
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
			}
			TextView nameView = (TextView) convertView.findViewById(R.id.room_asset_name);
			TextView specView = (TextView) convertView.findViewById(R.id.room_asset_spec);
			TextView brandView = (TextView) convertView.findViewById(R.id.room_asset_brand);
			TextView numberUnitView = (TextView) convertView.findViewById(R.id.room_asset_number_unit);
			TextView remarkView = (TextView) convertView.findViewById(R.id.room_asset_with_remained);
			nameView.setText(name);
			specView.setText(spec);
			brandView.setText(brand);
			numberUnitView.setText(numberUint);
			remarkView.setText(remark);
			return convertView;
		}
		
	}


	

	private void loadRoomAssetAsyncHttp(String roomId){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRoomAssetListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair hotelRoomIdPair = new BasicNameValuePair("hotelRoomId", roomId);
		params.add(hotelRoomIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<RoomAssetMaterialBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<RoomAssetMaterialBean>>>(){}.getType());
				List<RoomAssetMaterialBean> robResponse = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(robResponse == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				roomAssetMaterialBeanList.clear();
				roomAssetMaterialBeanList.addAll(robResponse);
				roomAssetBaseAdapter.notifyDataSetChanged();
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
	private int selectedItemLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_list_item_room_asset;

		default:
			return R.layout.list_item_room_asset;
		}
	}
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_room_asset;

		default:
			return R.layout.activity_room_asset;
		}
	}
}
