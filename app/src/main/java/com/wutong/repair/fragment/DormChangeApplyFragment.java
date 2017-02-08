package com.wutong.repair.fragment;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.DormChangeBuildingBean;
import com.wutong.repair.data.bean.DormChangeRoomBean;
import com.wutong.repair.data.bean.DormInfoChangeApplyBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.DataChangeListener;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SimpleSelect;
import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;
import com.wutong.repair.util.Logger;

/**
 * 宿舍调整申请
 * @author Jolly
 * 创建时间：2014年7月7日
 *
 */
public class DormChangeApplyFragment extends BaseFragment {


	private TextView titlebarTitleView;
	private ImageView titlebarBackView;
	private ImageView titlebarSubmitView;
	private String complaintTitle = "宿舍调整申请";

	private TextView mCurrentDormInfoView;//原住宿舍信息
	private TextView mTypeSelectView;//申请类型
	private AutoCompleteTextView mBuildingView;
	private AutoCompleteTextView mRoomView;
	private TextView mBedSelectView;
	
	private SingleSelectDialogFragment mTypeSingleSelectDialogFragment;
	private SingleSelectDialogFragment mBedSingleSelectDialogFragment;
	
	private List<SingleSelectDialogFragment.SimpleSelect> mTypeList;
	private List<SingleSelectDialogFragment.SimpleSelect> mBedList;
	
	private List<DormChangeBuildingBean> mBuildingList;
	private List<DormChangeRoomBean> mRoomList;
	private BuildingAutoCompleteAdapter mBuildingAutoCompleteAdapter;
	private RoomAutoCompleteAdapter mRoomAutoCompleteAdapter;
	
	private String lastSelectedBuilding = "";
	private String lastSelectedRoom = "";
	private String dataBuildingId = "";
	private String dataRoomId = "";
	private String dataType = "";
	private String dataBed = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(complaintTitle);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_dorm_change_apply, container,false);
		setFragmentView(fragmentView);
		//
		commonInit();
		titleBarInit();
		findViewInit();
		viewInit();
		setupData();
		return fragmentView;
	}
	
	private void commonInit(){
		mBuildingList = new ArrayList<DormChangeBuildingBean>();
		mRoomList = new ArrayList<DormChangeRoomBean>();
		mTypeList = new ArrayList<SingleSelectDialogFragment.SimpleSelect>();
		mTypeList.add(new SimpleSelect("调整为", DormInfoChangeApplyBean.TYPE_CHANGE));
		mTypeList.add(new SimpleSelect("更正为", DormInfoChangeApplyBean.TYPE_OFFSET));
		
		mBedList = new ArrayList<SingleSelectDialogFragment.SimpleSelect>();
		mBedList.add(new SimpleSelect("1号床", "1"));
		mBedList.add(new SimpleSelect("2号床", "2"));
		mBedList.add(new SimpleSelect("3号床", "3"));
		mBedList.add(new SimpleSelect("4号床", "4"));
		mBedList.add(new SimpleSelect("5号床", "5"));
		mBedList.add(new SimpleSelect("6号床", "6"));
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
				DormChangeApplyFragment.this.getActivity().onBackPressed();

			}
		});
		
		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();

			}
		});
	}

	private void findViewInit(){
		mCurrentDormInfoView = (TextView) findViewById(R.id.change_apply_current_dorm_info);
		mTypeSelectView = (TextView) findViewById(R.id.change_apply_type);
		
		mBuildingView = (AutoCompleteTextView) findViewById(R.id.change_apply_building);
		mRoomView = (AutoCompleteTextView) findViewById(R.id.change_apply_room);
		
		mBedSelectView = (TextView) findViewById(R.id.change_apply_bed);
		
	}


	private void viewInit(){
		setFragmentPageName(complaintTitle);
		
		mBuildingAutoCompleteAdapter = new BuildingAutoCompleteAdapter(-1);
		mBuildingView.setAdapter(mBuildingAutoCompleteAdapter);
		mBuildingView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(!s.toString().equals(lastSelectedBuilding)){
					dataBuildingId = "";
				}
			}
		});
		
		mRoomAutoCompleteAdapter = new RoomAutoCompleteAdapter(-1);
		mRoomView.setAdapter(mRoomAutoCompleteAdapter);
		mRoomView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(!s.toString().equals(lastSelectedRoom)){
					dataRoomId = "";
				}
			}
		});
		
		//
		mTypeSelectView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTypeSingleSelectDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) mTypeList);
				mTypeSingleSelectDialogFragment.setArguments(data);
				mTypeSingleSelectDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						dataType = itemBean.getValue();
						mTypeSelectView.setText(itemBean.getTextName());
					}
				});
				mTypeSingleSelectDialogFragment.show(getChildFragmentManager(), "single-select");
			}
		});
		
		mBedSelectView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBedSingleSelectDialogFragment = new SingleSelectDialogFragment();
				Bundle data = new Bundle();
				data.putSerializable("dataList", (Serializable) mBedList);
				mBedSingleSelectDialogFragment.setArguments(data);
				mBedSingleSelectDialogFragment.setDataChangeListener(new DataChangeListener() {

					@Override
					public void onDataChange(SingleSelectable itemBean) {
						dataBed = itemBean.getValue();
						mBedSelectView.setText(itemBean.getTextName());
					}
				});
				mBedSingleSelectDialogFragment.show(getChildFragmentManager(), "single-select");
			}
		});
	}
	

	private void setupData(){
		mCurrentDormInfoView.setText(application.getLoginInfoBean().getDormRoomName());
		loadBuildingHttp();
	}

	private void loadBuildingHttp(){
		String url = getString(R.string.http_url_load_dorm_change_apply_building_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("start", "0");
		params.put("limit", "9999");
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
				ResponseBean<List<DormChangeBuildingBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<DormChangeBuildingBean>>>(){}.getType());
				List<DormChangeBuildingBean> resultList = responseBean.getData();
				if(resultList != null && !resultList.isEmpty()){
					mBuildingView.setEnabled(true);
					mBuildingList.clear();
					mBuildingList.addAll(resultList);
					
				}
				else{
					mBuildingView.setEnabled(false);
				}
			}

		});
	}
	
	private void loadRoomHttp(){
		String url = getString(R.string.http_url_load_dorm_change_apply_room_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("hotelFloorId", dataBuildingId);
		params.put("start", "0");
		params.put("limit", "9999");
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
				mRoomView.getText().clear();
				dataRoomId = "";
				lastSelectedRoom = "";
			}

			@Override
			public void onSuccess(Object t) {
				super.onSuccess(t);
				hideProgressDialog();
				ResponseBean<List<DormChangeRoomBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<DormChangeRoomBean>>>(){}.getType());
				List<DormChangeRoomBean> resultList = responseBean.getData();
				if(resultList != null && !resultList.isEmpty()){
					mRoomView.setEnabled(true);
					mRoomList.clear();
					mRoomList.addAll(resultList);
					
				}
				else{
					mRoomView.setEnabled(false);
				}
			}

		});
	}
	
	private void submit(){
		if(dataType.length() == 0){
			ToastUtil.showToast(mContext, "请选择申请理由");
			return;
		}
		if(dataBuildingId.length() == 0){
			ToastUtil.showToast(mContext, "没有选择正确的宿舍楼");
			return;
		}
		if(dataRoomId.length() == 0){
			ToastUtil.showToast(mContext, "没有选择正确的宿舍号");
			return;
		}
		if(dataBed.length() == 0){
			ToastUtil.showToast(mContext, "请选择床位");
			return;
		}
		submitHttp();
	}
	
	private void submitHttp(){
		String url = getString(R.string.http_url_load_dorm_change_apply_submit,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("userId", application.getLoginInfoBean().getUserId().toString());
		params.put("hotelRoomId", dataRoomId);
		params.put("hotelBedId", dataBed);
		params.put("type", dataType);
		params.put("remark", "安卓 申请换宿舍");
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
				ResponseBean<Object> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<Object>>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "调整宿舍申请成功！");
					getActivity().setResult(Activity.RESULT_OK);
					getActivity().finish();
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}
			}

		});
	}
	
	static class BuildingViewHolder{
		TextView nameView;
	}
	
	private class BuildingAutoCompleteAdapter extends BaseAdapter implements Filterable{
		private BuildingFilter mFilter;
		private List<DormChangeBuildingBean> mObjects;//过滤后的item
		private final Object mLock = new Object();
		private int maxMatch=10;//最多显示多少个选项,负数表示全部
		public BuildingAutoCompleteAdapter(int maxMatch){
			this.maxMatch=maxMatch;
		}
		
		private class BuildingFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence prefix) {
				FilterResults results = new FilterResults();  
				
	            if (prefix == null || prefix.length() == 0) {  
	                synchronized (mLock) {
	                	Logger.i("mBuildingList.size="+mBuildingList.size());
	                    List<DormChangeBuildingBean> list = new ArrayList<DormChangeBuildingBean>(mBuildingList);
	                    results.values = list;  
	                    results.count = list.size(); 
	                    return results;
	                }  
	            } else {
	                String prefixString = prefix.toString().toLowerCase();  
	  
	                final int count = mBuildingList.size();  
	  
	                final List<DormChangeBuildingBean> newValues = new ArrayList<DormChangeBuildingBean>(count);  
	  
	                for (int i = 0; i < count; i++) {
	                    final DormChangeBuildingBean value = mBuildingList.get(i);  
	                    final String valueText = value.getName();  
	  
	                    if(valueText.contains(prefixString)){//匹配所有
	                    	 newValues.add(value);
	                    }
	                    if(maxMatch>0){//有数量限制  
	                        if(newValues.size()>maxMatch-1){//不要太多  
	                            break;  
	                        }  
	                    }  
	                }  
	  
	                results.values = newValues;  
	                results.count = newValues.size();  
	            }  
	  
	            return results;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				mObjects = (List<DormChangeBuildingBean>) results.values;  
	            if (results.count > 0) {  
	                notifyDataSetChanged();  
	            } else {  
	                notifyDataSetInvalidated();  
	            }
			}
			
		}
		
		@Override
		public int getCount() {
			return mObjects.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			BuildingViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_dorm_change_building, null);
				holder = new BuildingViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.item_building_name);
				convertView.setTag(holder);
			}
			else{
				holder = (BuildingViewHolder) convertView.getTag();
			}
			final DormChangeBuildingBean itemBean = mObjects.get(position);
			holder.nameView.setText(itemBean.getName());
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dataBuildingId = itemBean.getId();
					lastSelectedBuilding = itemBean.getName();
					mBuildingView.setText(lastSelectedBuilding);
					mBuildingView.dismissDropDown();
					loadRoomHttp();
				}
			});
			return convertView;
		}

		@Override
		public Filter getFilter() {
			if (mFilter == null) {  
	            mFilter = new BuildingFilter();  
	        }  
	        return mFilter;
		}
		
	}
	
	static class RoomViewHolder{
		TextView nameView;
	}
	
	private class RoomAutoCompleteAdapter extends BaseAdapter implements Filterable{
		private RoomFilter mFilter;
		private List<DormChangeRoomBean> mObjects;//过滤后的item
		private final Object mLock = new Object();
		private int maxMatch=10;//最多显示多少个选项,负数表示全部
		public RoomAutoCompleteAdapter(int maxMatch){
			this.maxMatch=maxMatch;
		}
		
		private class RoomFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence prefix) {
				FilterResults results = new FilterResults();  
				
	            if (prefix == null || prefix.length() == 0) {  
	                synchronized (mLock) {
	                    List<DormChangeRoomBean> list = new ArrayList<DormChangeRoomBean>(mRoomList);
	                    results.values = list;  
	                    results.count = list.size(); 
	                    return results;
	                }  
	            } else {
	                String prefixString = prefix.toString().toLowerCase();  
	  
	                final int count = mRoomList.size();  
	  
	                final List<DormChangeRoomBean> newValues = new ArrayList<DormChangeRoomBean>(count);  
	  
	                for (int i = 0; i < count; i++) {
	                    final DormChangeRoomBean value = mRoomList.get(i);  
	                    final String valueText = value.getName();  
	  
	                    if(valueText.contains(prefixString)){//匹配所有
	                    	 newValues.add(value);
	                    }
	                    if(maxMatch>0){//有数量限制  
	                        if(newValues.size()>maxMatch-1){//不要太多  
	                            break;  
	                        }  
	                    }  
	                }  
	  
	                results.values = newValues;  
	                results.count = newValues.size();  
	            }  
	  
	            return results;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				mObjects = (List<DormChangeRoomBean>) results.values;  
	            if (results.count > 0) {  
	                notifyDataSetChanged();  
	            } else {  
	                notifyDataSetInvalidated();  
	            }
			}
			
		}
		
		@Override
		public int getCount() {
			return mObjects.size();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			BuildingViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_dorm_change_building, null);
				holder = new BuildingViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.item_building_name);
				convertView.setTag(holder);
			}
			else{
				holder = (BuildingViewHolder) convertView.getTag();
			}
			final DormChangeRoomBean itemBean = mObjects.get(position);
			holder.nameView.setText(itemBean.getName());
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dataRoomId = itemBean.getId();
					lastSelectedRoom = itemBean.getName();
					mRoomView.setText(lastSelectedRoom);
					mRoomView.dismissDropDown();
				}
			});
			return convertView;
		}

		@Override
		public Filter getFilter() {
			if (mFilter == null) {  
	            mFilter = new RoomFilter();
	        }  
	        return mFilter;
		}
		
	}
}
