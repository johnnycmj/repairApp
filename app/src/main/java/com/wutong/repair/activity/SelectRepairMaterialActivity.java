package com.wutong.repair.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
import com.wutong.repair.util.HttpResponseUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SelectRepairMaterialActivity extends BaseActivity {

	public final static int ADD_TYPE = 1;
	public final static int RECOVERY_TYPE = 2;
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;

	private ListView mMaterialAllView;
	private List<MaterialBean> mMaterialList;//未使用（显示）
	private List<MaterialBean> mSelectedMaterial;//已使用
	private BaseAdapter adapter;

	private boolean outIsLargeCharacter;
	private RepairOrderBean outRepairOrderBean;
	private int outType;

	private Button mSubmitSelectBtn;
	private String errorTypeKey;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_select_repair_material);
		dataInit();
		intentInit();
		titlebarInit();
		viewInit();
		setupData();
	}
	private void dataInit(){
		mMaterialList = new ArrayList<MaterialBean>();
		mSelectedMaterial = new ArrayList<MaterialBean>();
		errorTypeKey = UUID.randomUUID().toString();
	}

	@SuppressWarnings("unchecked")
	private void intentInit(){
		final Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		outRepairOrderBean = (RepairOrderBean)bundle.get("repairOrderBean");
		List<MaterialBean> tempList = (List<MaterialBean>) bundle.get("MaterialSelected");
		outIsLargeCharacter = intent.getBooleanExtra("isLargeCharacter",false);
		outType = intent.getIntExtra("type", 0);
		if(tempList != null){
			mSelectedMaterial.clear();
			mSelectedMaterial.addAll(tempList);
		}
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_select_repair_material);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SelectRepairMaterialActivity.this.onBackPressed();

			}
		});

	}

	private void viewInit(){
		//初始化
		mMaterialAllView = (ListView) findViewById(R.id.materail_all);

		mSubmitSelectBtn = (Button) findViewById(R.id.material_select_submit);

		View emptyView = findViewById(R.id.material_select_empty);
		mMaterialAllView.setEmptyView(emptyView);

		adapter = new MaterailSelectBaseAdapter(mMaterialList);
		mMaterialAllView.setAdapter(adapter);

		mSubmitSelectBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("MaterialSelectedResult", (Serializable)mSelectedMaterial);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				SelectRepairMaterialActivity.this.setResult(Activity.RESULT_OK, intent);
				SelectRepairMaterialActivity.this.finish();
			}
		});
		
		//动态设置大小

		if(outIsLargeCharacter){
			mSubmitSelectBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
		}
		else{
			mSubmitSelectBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		}
	}

	private void setupData(){
		switch (outType) {
		case ADD_TYPE:
			loadMaterialListAsyncHttp();
			break;
		case RECOVERY_TYPE:
			loadMaterialUsedAsyncHttp();
			break;
		default:
			ToastUtil.showToast(mContext, "装载数据时发现意外类型");
			break;
		}
	}

	static class MaterialViewHolder{
		TextView nameView;
		TextView numberView;
		TextView unitView;
		TextView brandView;
		TextView specView;
		CheckBox checkStatusView;

		TextView usedLabelNumberView;
		TextView remainLabelNumberView;

	}

	private final class MaterailSelectBaseAdapter extends BaseAdapter{

		private List<MaterialBean> list;

		public MaterailSelectBaseAdapter(List<MaterialBean> list) {
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			final MaterialBean materialBean = list.get(position);
			MaterialViewHolder holder;
			if(convertView == null){
				holder = new MaterialViewHolder();
				if(outIsLargeCharacter){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_select_repair_material_large, null);
				}
				else{
					convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
				}
				holder.nameView = (TextView) convertView.findViewById(R.id.material_name);
				holder.checkStatusView= (CheckBox) convertView.findViewById(R.id.repair_materail_is_checked);
				holder.numberView = (TextView) convertView.findViewById(R.id.last_used_number);
				holder.unitView = (TextView) convertView.findViewById(R.id.repair_material_unit);
				holder.usedLabelNumberView = (TextView) convertView.findViewById(R.id.repair_material_last_used_label);
				holder.remainLabelNumberView = (TextView) convertView.findViewById(R.id.repair_material_remain_label);
				holder.brandView = (TextView) convertView.findViewById(R.id.repair_materail_brand);
				holder.specView = (TextView) convertView.findViewById(R.id.repair_materail_spec);
				convertView.setTag(holder);
			}
			else{
				holder = (MaterialViewHolder) convertView.getTag();
			}
			holder.nameView.setText(materialBean.getMaterialName());
			holder.brandView.setText(materialBean.getBrand());
			holder.specView.setText(materialBean.getSpecification());
			holder.unitView.setText(getString(R.string.format_repair_material_unit,materialBean.getUnit()));
			switch (outType) {
			case ADD_TYPE:
				holder.numberView.setText(getString(R.string.format_repair_material_number,materialBean.getTotalNumber().toString()));
				holder.usedLabelNumberView.setVisibility(ViewGroup.GONE);
				holder.remainLabelNumberView.setVisibility(ViewGroup.VISIBLE);
				break;
			case RECOVERY_TYPE:
				//Fix分支
					holder.numberView.setText(getString(R.string.format_repair_material_number,materialBean.getTotalNumber().toString()));
				holder.usedLabelNumberView.setVisibility(ViewGroup.VISIBLE);
				holder.remainLabelNumberView.setVisibility(ViewGroup.GONE);
				break;
			default:
				ToastUtil.showToast(mContext, "装载列表数据时意外类型", errorTypeKey);
				break;
			}
			//
			holder.checkStatusView.setOnCheckedChangeListener(null);//1.去掉事件
			if(mSelectedMaterial.contains(materialBean)){
				holder.checkStatusView.setChecked(true);
			}
			else{
				holder.checkStatusView.setChecked(false);
			}
			holder.checkStatusView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						if(!mSelectedMaterial.contains(materialBean)){
							mSelectedMaterial.add(materialBean);
						}
					}
					else{
						mSelectedMaterial.remove(materialBean);
					}
				}
			});
			return convertView;
		}

	}

	/**
	 * 获取维修工的材料列表
	 */
	private void loadMaterialListAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairmanMaterialListUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair repairManIdPair = new BasicNameValuePair("repairManId", application.getLoginInfoBean().getUserId().toString());
		params.add(repairManIdPair);
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
				mMaterialList.clear();
				mMaterialList.addAll(responseList);
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



	/**
	 * 获取报修单下的材料使用情况
	 */
	private void loadMaterialUsedAsyncHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderMaterialUsedUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair fromIdPair = new BasicNameValuePair("fromId", outRepairOrderBean.getId());
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
				mMaterialList.clear();
				mMaterialList.addAll(responseList);
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
			return R.layout.spring_horse_listview_select_repair_material;

		default:
			return R.layout.listview_select_repair_material;
		}
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_select_repair_material;

		default:
			return R.layout.activity_select_repair_material;
		}
	}

}
