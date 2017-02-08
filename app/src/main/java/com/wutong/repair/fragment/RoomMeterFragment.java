package com.wutong.repair.fragment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.common.widget.ListViewInnerScroll;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ChildMeterBean;
import com.wutong.repair.data.bean.MeterBuildingBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.RoomMeterBean;
import com.wutong.repair.util.HttpResponseUtil;

/**
 * 住户抄表
 * @author Jolly
 * 创建时间：2014年2月21日下午6:54:51
 *
 */
public class RoomMeterFragment extends BaseFragment{

	private TextView titlebarTitleView;
	private ImageView titlebarBackView;

	private TextView selectBuildingView;
	private View selectBuildingContentView;
	private WheelView selectBuildingWv;
	private CustomDialog selectBuildingCustomDialog;
	private MeterBuildingWheelAdapter meterBuildingWheelAdapter;
	private Button submitView;

	private ListView roomMeterListView;
	private List<RoomMeterBean> roomMeterBeanList;
	private RoomMeterBaseAdapter roomMeterBaseAdapter;

	private ListViewInnerScroll[] innerListViewList;
	private ChildMeterBaseAdapter[] innerChildMeterBaseAdapterList;

	private List<MeterBuildingBean> meterBuildingBeanList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_room_meter, container,false);
		setFragmentView(fragmentView);
		//
		dataInit();
		titleBarInit();
		viewInit();
		setupData();
		return fragmentView;
	}

	private void dataInit(){
		meterBuildingBeanList = new ArrayList<MeterBuildingBean>();
		roomMeterBeanList = new ArrayList<RoomMeterBean>();
	}

	private void titleBarInit(){
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
				RoomMeterFragment.this.getActivity().onBackPressed();

			}
		});

		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(R.string.title_fragment_room_meter);


	}

	private void viewInit(){
		selectBuildingView = (TextView) findViewById(R.id.select_building);
		selectBuildingContentView = LayoutInflater.from(mContext).inflate(R.layout.common_single_list_select_no_button, null);

		selectBuildingWv = (WheelView) selectBuildingContentView.findViewById(R.id.select_item_wheel);
		selectBuildingWv.setBackgroundResource(R.drawable.common_wheel_bg);
		selectBuildingWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		selectBuildingCustomDialog = new CustomDialog.Builder(mContext)
		.setTitle("住户楼")
		.setView(selectBuildingContentView)
		.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int index = selectBuildingWv.getCurrentItem();
				MeterBuildingBean meterBuildingBean = meterBuildingBeanList.get(index);
				String buildingId = meterBuildingBean.getId();
				selectBuildingView.setText(meterBuildingBean.getName());
				loadRoomMeterListHttp(buildingId);
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
		//
		submitView = (Button) findViewById(R.id.submit);
		roomMeterListView = (ListView) findViewById(R.id.room_meter_list);
		View emptyView = findViewById(R.id.empty_view);
		roomMeterListView.setEmptyView(emptyView);
		roomMeterBeanList = new ArrayList<RoomMeterBean>();
		roomMeterBaseAdapter = new RoomMeterBaseAdapter();
		roomMeterListView.setAdapter(roomMeterBaseAdapter);

		selectBuildingView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!selectBuildingCustomDialog.isShowing()){
					selectBuildingCustomDialog.show();
				}
			}
		});

		submitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();
			}
		});
	}

	private void setupData(){
		loadBuildingListHttp();
	}

	private void submit(){
		StringBuilder childMeterIdBuilder = new StringBuilder();
		StringBuilder currentValueBuilder = new StringBuilder();
		boolean smallFlag = false;
		for(RoomMeterBean roomMeterBean :roomMeterBeanList){
			List<ChildMeterBean> childMeterBeans = roomMeterBean.getChildMeterList();
			for(ChildMeterBean cBean:childMeterBeans){
				if(!smallFlag && cBean.getDataCurrentValue() != null && cBean.getDataCurrentValue().length() >0){
					int currentValue = Integer.valueOf(cBean.getDataCurrentValue());
					int recentlyValue = Integer.valueOf(cBean.getRecentlyValue());
//					if(currentValue < recentlyValue){
//						smallFlag = true;
//						//
//						new CustomDialog.Builder(mContext)
//						.setTitle("错误")
//						.setMessage(roomMeterBean.getRoomOwnerCode() +cBean.getName()+"本期数小于上期数")
//						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								dialog.dismiss();
//							}
//						})
//						.create().show();
//						break;
//					}
					childMeterIdBuilder.append(",").append(cBean.getId());
					currentValueBuilder.append(",").append(cBean.getDataCurrentValue());
				}
			}
		}
		if(smallFlag){
			return;
		}
		else if(childMeterIdBuilder.length() == 0){
			ToastUtil.showToast(mContext, "请抄表后提交");
			return;
		}
		else{
			submitAsyncHttp(childMeterIdBuilder.substring(1), currentValueBuilder.substring(1));
		}
	}

	private void submitSuccess(){
		roomMeterBeanList.clear();
		roomMeterBaseAdapter.notifyDataSetChanged();
		submitView.setVisibility(ViewGroup.INVISIBLE);
		selectBuildingView.setText("");
	}

	/**
	 * 获取楼列表信息
	 */
	private void loadBuildingListHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getMeterBuildingList());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<MeterBuildingBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<MeterBuildingBean>>>(){}.getType());
				List<MeterBuildingBean> robResponse =  responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(robResponse == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else if(robResponse.size() == 0){
					meterBuildingBeanList.clear();

				}
				else{
					meterBuildingBeanList.clear();
					meterBuildingBeanList.addAll(robResponse);
					meterBuildingWheelAdapter = new MeterBuildingWheelAdapter();
					selectBuildingWv.setViewAdapter(meterBuildingWheelAdapter);
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

	private void loadRoomMeterListHttp(String buildingId){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRoomMeterList());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair buildingIdPair = new BasicNameValuePair("floorId", buildingId);
		params.add(buildingIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<RoomMeterBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<RoomMeterBean>>>(){}.getType());
				List<RoomMeterBean> robResponse =  responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					meterBuildingBeanList.clear();
					roomMeterBaseAdapter.notifyDataSetChanged();
					submitView.setVisibility(ViewGroup.INVISIBLE);
					return;
				}
				if(robResponse == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					meterBuildingBeanList.clear();
					roomMeterBaseAdapter.notifyDataSetChanged();
					submitView.setVisibility(ViewGroup.INVISIBLE);
					return;
				}
				else if(robResponse.size() == 0){
					meterBuildingBeanList.clear();
					roomMeterBaseAdapter.notifyDataSetChanged();
					submitView.setVisibility(ViewGroup.INVISIBLE);
				}
				else{
					innerListViewList = new ListViewInnerScroll[robResponse.size()];
					innerChildMeterBaseAdapterList = new ChildMeterBaseAdapter[robResponse.size()];
					roomMeterBeanList.clear();
					roomMeterBeanList.addAll(robResponse);
					roomMeterBaseAdapter.notifyDataSetChanged();
					submitView.setVisibility(ViewGroup.VISIBLE);

				}


			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
				meterBuildingBeanList.clear();
				roomMeterBaseAdapter.notifyDataSetChanged();
				submitView.setVisibility(ViewGroup.GONE);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog();

			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	/**
	 * 提交
	 */
	private void submitAsyncHttp(String childMeterIdArray,String currentValueArray){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getSubmitRoomMeter());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair childMeterIdArrayPair = new BasicNameValuePair("meterIds", childMeterIdArray);
		params.add(childMeterIdArrayPair);
		NameValuePair currentValueArrayPair = new BasicNameValuePair("counts", currentValueArray);
		params.add(currentValueArrayPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<Object> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<Object>>(){}.getType());
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				else{
					ToastUtil.showToast(mContext, "提交成功");
					submitSuccess();
				}


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
		httpAsyncTask.execute(httpFormBean);
	}

	private class MeterBuildingWheelAdapter implements WheelViewAdapter{

		@Override
		public int getItemsCount() {
			return meterBuildingBeanList.size();
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.wheel_list_item_common, null);
			TextView textView = (TextView) convertView.findViewById(R.id.wheel_item_text); 
			String name = meterBuildingBeanList.get(index).getName();
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


	static class RoomMeterViewHolder{
		TextView roomOwnerCode;
		TextView roomOwnerName;
	} 

	private class RoomMeterBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return roomMeterBeanList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {
			RoomMeterViewHolder holder;
			RoomMeterBean roomMeterBean = roomMeterBeanList.get(position);
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_room_meter_group, null);
			holder = new RoomMeterViewHolder();
			holder.roomOwnerCode = (TextView) convertView.findViewById(R.id.room_owner_code);
			holder.roomOwnerName = (TextView) convertView.findViewById(R.id.room_owner_name);
			holder.roomOwnerCode.setText(roomMeterBean.getRoomOwnerCode());
			holder.roomOwnerName.setText(roomMeterBean.getRoomOwnerName());
			innerListViewList[position] =  (ListViewInnerScroll) convertView.findViewById(R.id.room_owner_meter_list);
			innerChildMeterBaseAdapterList[position] =  new ChildMeterBaseAdapter(roomMeterBean);
			innerListViewList[position].setAdapter(innerChildMeterBaseAdapterList[position]);

			innerChildMeterBaseAdapterList[position].notifyDataSetChanged();

			return convertView;
		}

	}
	static class ChildMeterViewHolder{
		TextView nameView;
		TextView recentlyValueView;
		TextView recentlyWriteDateView;
		EditText currentValueView;
	}

	private class ChildMeterBaseAdapter extends BaseAdapter{

		private RoomMeterBean roomMeterBean;


		public ChildMeterBaseAdapter(RoomMeterBean roomMeterBean) {
			super();
			this.roomMeterBean = roomMeterBean;
		}

		@Override
		public int getCount() {
			return roomMeterBean.getChildMeterList().size();
		}

		@Override
		public Object getItem(int position) {
			return roomMeterBean.getChildMeterList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ChildMeterViewHolder holder;
			final ChildMeterBean childMeterBean = roomMeterBean.getChildMeterList().get(position);
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_room_meter_child, null);
			holder = new ChildMeterViewHolder();
			holder.nameView = (TextView) convertView.findViewById(R.id.child_meter_name);
			holder.currentValueView = (EditText) convertView.findViewById(R.id.child_meter_current_value);
			holder.recentlyValueView = (TextView) convertView.findViewById(R.id.child_meter_recently_value);
			holder.recentlyWriteDateView = (TextView) convertView.findViewById(R.id.child_meter_date);
			holder.nameView.setText(childMeterBean.getName());
			holder.recentlyValueView.setText(childMeterBean.getRecentlyValue());
			holder.recentlyWriteDateView.setText(childMeterBean.getRecentlyWriteDate());

			String currentValue = childMeterBean.getDataCurrentValue();
			holder.currentValueView.setText(currentValue !=null?currentValue:"");

			holder.currentValueView.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					childMeterBean.setDataCurrentValue(s.toString());

				}
			});
			return convertView;
		}

	}
}
