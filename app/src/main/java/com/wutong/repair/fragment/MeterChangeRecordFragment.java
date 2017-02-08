package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.util.AjaxCallBackPlus;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.activity.MeterChangeRecordDetailActivity;
import com.wutong.repair.data.bean.RoomMeterRecordBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.RoomMeterRecordBean;

/**
 * 520活动列表
 * @author Jolly
 * 创建时间：2014年5月4日下午4:14:45
 *
 */
public class MeterChangeRecordFragment extends BaseFragment {

	public final static int MICRO_ACTIVITY_SUBMIT_REQUEST_CODE = 9;

	private TextView titlebarTitleView;
	private ImageView titlebarBackView;

	private ListView mContributesView;
	private BaseAdapter loveYouAdapter;
	private List<RoomMeterRecordBean> loveYouList;


	private EditText mSearchInputView;
	private ImageView mSearchCleanView;
	private ImageView mSearchSubmitView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_meter_change_record);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_meter_change_record_list, container,false);
		setFragmentView(fragmentView);
		//
		commonInit();
		titleBarInit();
		viewInit();
		return fragmentView;
	}

	private void commonInit(){
		mTitle = getString(R.string.title_fragment_meter_change_record);

		loveYouList= new ArrayList<RoomMeterRecordBean>();
	}

	private void titleBarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(mTitle);
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
				MeterChangeRecordFragment.this.getActivity().onBackPressed();

			}
		});
	}




	private void viewInit(){
		mSearchInputView = (EditText) findViewById(R.id.meter_change_record_search_input);
		mSearchCleanView = (ImageView) findViewById(R.id.meter_change_record_search_clean);
		mSearchSubmitView = (ImageView) findViewById(R.id.meter_change_record_search_submit);
		mSearchInputView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().trim().length() == 0){
					mSearchCleanView.setVisibility(ViewGroup.GONE);
				}
				else if(mSearchInputView.isFocused()){
					mSearchCleanView.setVisibility(ViewGroup.VISIBLE);
				}
				
			}
		});
		
		mSearchInputView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && mSearchInputView.getText().toString().trim().length() >0){
					mSearchCleanView.setVisibility(ViewGroup.VISIBLE);
				}
				else{
					mSearchCleanView.setVisibility(ViewGroup.GONE);
				}
				
			}
		});
		
		mSearchCleanView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSearchInputView.setText("");
			}
		});
		mSearchSubmitView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadMoreContribute();
			}
		});
		
		mContributesView = (ListView)  findViewById(R.id.complaint_list);
		View emptyView = findViewById(R.id.complaint_index_empty);
		mContributesView.setEmptyView(emptyView);


		mContributesView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position < loveYouList.size()){
					RoomMeterRecordBean messageBean = loveYouList.get(position);
					Intent intent = new Intent(mContext, MeterChangeRecordDetailActivity.class);
					intent.putExtra("contributeId",messageBean.getId() );
					startActivity(intent);
				}
			}
		});

		loveYouAdapter = new LoveYouBaseAdapter();
		mContributesView.setAdapter(loveYouAdapter);
		loadMoreContribute();
		//

	}





	private void loadMoreContribute(){
		loadCurrentActivityListAsyncHttp();
	}

	static class CompliantViewHolder{
		TextView bulidingOrNameView;
		TextView bulidingNumberView;
		TextView meterNameView;
		TextView meterNumberView;
		TextView timeView;
	}



	private class LoveYouBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return loveYouList.size();
		}

		@Override
		public Object getItem(int position) {
			return loveYouList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			CompliantViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_meter_record_list, null);
				holder = new CompliantViewHolder();

				holder.bulidingOrNameView = (TextView) convertView.findViewById(R.id.meter_record_building_or_owner);
				holder.bulidingNumberView = (TextView) convertView.findViewById(R.id.meter_record_building_number);
				holder.meterNameView = (TextView) convertView.findViewById(R.id.meter_record_meter_name);
				holder.meterNumberView = (TextView) convertView.findViewById(R.id.meter_record_meter_number);
				holder.timeView = (TextView) convertView.findViewById(R.id.meter_record_time);
				convertView.setTag(holder);
			}

			RoomMeterRecordBean complaintBean = loveYouList.get(position);
			holder = (CompliantViewHolder) convertView.getTag();
			holder.bulidingOrNameView.setText(complaintBean.getRealName());
			holder.bulidingNumberView.setText(complaintBean.getHouseholderNum());
			holder.meterNameView.setText(complaintBean.getMeterName());
			holder.meterNumberView.setText(complaintBean.getMeterNum());
			holder.timeView.setText(complaintBean.getCheckDate());
			
			return convertView;
		}

	}

	/**
	 * 获取表白列表
	 * @param complaintPagerBean
	 * @param isRefresh
	 */
	private void loadCurrentActivityListAsyncHttp(){
		String url = getString(R.string.http_url_load_meter_record_list,application.getDomainUrl());
		AjaxParams params = new AjaxParams();
		params.put("meterNum", mSearchInputView.getText().toString().trim());
		params.put("start", "0");
		params.put("limit", "9999");
		application.getFinalHttp().post(url, params, new AjaxCallBackPlus<Object>(){

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				hideProgressDialog();
				super.onFailure(t, errorNo, strMsg);
				if(!MeterChangeRecordFragment.this.isAdded() ||! MeterChangeRecordFragment.this.isVisible()){
					return;
				}
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
				ResponseBean<List<RoomMeterRecordBean>> responseBean = application.getGson().fromJson(t.toString(), new TypeToken<ResponseBean<List<RoomMeterRecordBean>>>(){}.getType());
				List<RoomMeterRecordBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				else{
					loveYouList.clear();
					loveYouList.addAll(responseList);
					loveYouAdapter.notifyDataSetChanged();
				}

			}

		});

	}







}
