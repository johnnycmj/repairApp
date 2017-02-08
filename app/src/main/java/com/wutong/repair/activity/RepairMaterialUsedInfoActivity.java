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
import com.wutong.repair.data.bean.MaterialBean;
import com.wutong.repair.data.bean.RepairOrderBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.dictionary.RepairOrderDict;
import com.wutong.repair.util.HttpResponseUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RepairMaterialUsedInfoActivity extends BaseActivity {

	private final static int REQUEST_CODE_ADD_MATERIAL = 98;
	private final static int REQUEST_CODE_RECOVERY_MATERIAL = 99;
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private ListView mPreApplyMaterialsView;
	private Button mAppendMaterailView;
	private Button mRecoveryMaterailView;
	private List<MaterialBean> mMaterialSelected;
	private UsedMaterailBaseAdapter adapter;

	private RepairOrderBean repairOrderBean;

	private boolean outIsLargeCharacter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_repair_material_used_info);
		intentInit();
		titlebarInit();
		viewInit();
		setupData();
	}

	private void intentInit(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		repairOrderBean = (RepairOrderBean)bundle.get("repairOrderBean");
		outIsLargeCharacter = intent.getBooleanExtra("isLargeCharacter",false);
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_repair_material_used_info);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RepairMaterialUsedInfoActivity.this.onBackPressed();

			}
		});
	}

	private void viewInit(){
		//初始化
		mPreApplyMaterialsView = (ListView) findViewById(R.id.pre_apply_materails);
		mAppendMaterailView = (Button) findViewById(R.id.append_materail);
		mRecoveryMaterailView = (Button) findViewById(R.id.recovery_materail);
		View emptyView = findViewById(R.id.apply_material_empty);
		mPreApplyMaterialsView.setEmptyView(emptyView);

		mAppendMaterailView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, AddRepairMaterialActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("repairOrderBean", repairOrderBean);
				intent.putExtra("isLargeCharacter",outIsLargeCharacter);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_CODE_ADD_MATERIAL);
			}
		});
		mRecoveryMaterailView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, RecoveryRepairMaterialActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("repairOrderBean", repairOrderBean);
				intent.putExtra("isLargeCharacter",outIsLargeCharacter);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_CODE_RECOVERY_MATERIAL);
			}
		});
		//

		//如果不允许，隐藏下方按钮
		if(repairOrderBean.getStatus() == null){
			ToastUtil.showToast(mContext, "单子状态出现异常，部分显示和功能可能不正确");
		}
		else if(repairOrderBean.getStatus().equals(String.valueOf(RepairOrderDict.START_TO_DEAL_STATUS))
				||repairOrderBean.getStatus().equals(String.valueOf(RepairOrderDict.WANNA_REFIX_STATUS))){
			//可以修改
			mAppendMaterailView.setVisibility(ViewGroup.VISIBLE);
			mRecoveryMaterailView.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			//不可修改
			mAppendMaterailView.setVisibility(ViewGroup.GONE);
			mRecoveryMaterailView.setVisibility(ViewGroup.GONE);
		}


		mMaterialSelected = new ArrayList<MaterialBean>();
		adapter = new UsedMaterailBaseAdapter(mMaterialSelected );
		mPreApplyMaterialsView.setAdapter(adapter);

		//动态设置大小

		if(outIsLargeCharacter){
			mAppendMaterailView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			mRecoveryMaterailView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
		}
		else{
			mAppendMaterailView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			mRecoveryMaterailView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		}

	}


	private void setupData(){
		loadMaterialUsedAsyncHttp();
	}

	static class UsedMaterailViewHolder{
		TextView materialNameView;
		TextView usedNumberView;
		TextView brandView;
		TextView specView;
		TextView unitView;

	}



	private final class UsedMaterailBaseAdapter extends BaseAdapter{

		private List<MaterialBean> list;

		public UsedMaterailBaseAdapter(List<MaterialBean> list) {
			super();
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position ) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position ) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				if(outIsLargeCharacter){
					convertView = LayoutInflater.from(RepairMaterialUsedInfoActivity.this).inflate(R.layout.listview_material_used_info_large, null);
				}
				else{
					convertView = LayoutInflater.from(RepairMaterialUsedInfoActivity.this).inflate(selectedItemLayout(), null);
				}
				UsedMaterailViewHolder holder = new UsedMaterailViewHolder();
				holder.materialNameView = (TextView) convertView.findViewById(R.id.material_name);
				holder.usedNumberView = (TextView) convertView.findViewById(R.id.last_used_number);
				holder.specView = (TextView) convertView.findViewById(R.id.repair_materail_spec);
				holder.brandView = (TextView) convertView.findViewById(R.id.repair_materail_brand);
				holder.unitView = (TextView) convertView.findViewById(R.id.repair_material_unit);
				convertView.setTag(holder);
			}
			Object obj = getItem(position);
			final UsedMaterailViewHolder holder = (UsedMaterailViewHolder) convertView.getTag();
			if(obj != null){
				final MaterialBean materialBean = (MaterialBean)obj;

				//Fix分支
					holder.usedNumberView.setText(getString(R.string.format_repair_material_number,materialBean.getTotalNumber().toString()));
				holder.materialNameView.setText(materialBean.getMaterialName());
				holder.specView.setText(materialBean.getSpecification());
				holder.brandView.setText(materialBean.getBrand());
				holder.unitView.setText(getString(R.string.format_repair_material_unit,materialBean.getUnit()));
				return convertView;
			}
			else{
				return convertView;
			}
		}

	}



	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
		case REQUEST_CODE_ADD_MATERIAL:
			loadMaterialUsedAsyncHttp();
			break;
		case REQUEST_CODE_RECOVERY_MATERIAL:
			loadMaterialUsedAsyncHttp();
			break;
		default:
			break;
		}

	}





	/**
	 * 获取报修单下的材料使用情况
	 */
	private void loadMaterialUsedAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderMaterialUsedUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair fromIdPair = new BasicNameValuePair("fromId", repairOrderBean.getId());
		params.add(fromIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<MaterialBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<MaterialBean>>>(){}.getType());
				List<MaterialBean> responseList = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(responseList == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				mMaterialSelected.clear();
				mMaterialSelected.addAll(responseList);
				adapter.notifyDataSetChanged();
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
			return R.layout.spring_horse_listview_material_used_info;

		default:
			return R.layout.listview_material_used_info;
		}
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_repair_material_used_info;

		default:
			return R.layout.activity_repair_material_used_info;
		}
	}

}
