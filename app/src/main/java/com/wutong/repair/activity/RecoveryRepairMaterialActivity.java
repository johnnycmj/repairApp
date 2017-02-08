package com.wutong.repair.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.wutong.repair.data.bean.UsedMaterialFormBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RecoveryRepairMaterialActivity extends BaseActivity {
	private final static int REQUEST_CODE_SELECT_MATERIAL = 98;
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;
	
	private ListView mPreApplyMaterialsView;
	private Button mSelectMaterailView;
	private List<MaterialBean> mMaterialSelected;
	private UsedMaterailBaseAdapter adapter;

	private RepairOrderBean repairOrderBean;
	
	private boolean outIsLargeCharacter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_recovery_repair_material);
		intentInit();
		titlebarInit();
		viewInit();
	}

	private void intentInit(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		repairOrderBean = (RepairOrderBean)bundle.get("repairOrderBean");
		outIsLargeCharacter = intent.getBooleanExtra("isLargeCharacter",false);
	}
	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_recovery_repair_material);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RecoveryRepairMaterialActivity.this.onBackPressed();

			}
		});
		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				titlebarSubmitView.setEnabled(false);
				submit();
			}
		});
	}
	
	private void viewInit(){
		//初始化
		mPreApplyMaterialsView = (ListView) findViewById(R.id.pre_apply_materails);
		mSelectMaterailView = (Button) findViewById(R.id.select_materail);
		View emptyView = findViewById(R.id.apply_material_empty);
		mPreApplyMaterialsView.setEmptyView(emptyView);
		//

		//如果不允许，隐藏下方按钮
		if(repairOrderBean.getStatus() == null){
			ToastUtil.showToast(mContext, "单子状态出现异常，部分显示和功能可能不正确");
		}

		mMaterialSelected = new ArrayList<MaterialBean>();
		adapter = new UsedMaterailBaseAdapter(mMaterialSelected );
		mPreApplyMaterialsView.setAdapter(adapter);

		mSelectMaterailView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SelectRepairMaterialActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("type", SelectRepairMaterialActivity.RECOVERY_TYPE);
				bundle.putSerializable("repairOrderBean", repairOrderBean);
				bundle.putSerializable("MaterialSelected", (Serializable)mMaterialSelected);
				intent.putExtra("isLargeCharacter",outIsLargeCharacter);
				intent.putExtras(bundle);
				
				startActivityForResult(intent, REQUEST_CODE_SELECT_MATERIAL);
			}
		});
		//动态设置大小
		
		if(outIsLargeCharacter){
			mSelectMaterailView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
		}
		else{
			mSelectMaterailView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		}
		
	}

	

	static class UsedMaterailViewHolder{
		TextView materialNameView;
		TextView usedNumberView;
		TextView brandView;
		TextView specView;
		TextView unitView;
		Button minusView;
		Button plusView;
		TextView incrementUsedNumberView;
		ImageView deleteView;
		ImageView deleteRightView;

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
					convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_add_repair_material_large, null);
				}
				else{
					convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
				}
				UsedMaterailViewHolder holder = new UsedMaterailViewHolder();
				holder.materialNameView = (TextView) convertView.findViewById(R.id.material_name);
				holder.usedNumberView = (TextView) convertView.findViewById(R.id.last_used_number);
				holder.specView = (TextView) convertView.findViewById(R.id.repair_materail_spec);
				holder.brandView = (TextView) convertView.findViewById(R.id.repair_materail_brand);
				holder.unitView = (TextView) convertView.findViewById(R.id.repair_material_unit);
				holder.minusView = (Button) convertView.findViewById(R.id.seekbar_minus);
				holder.plusView = (Button) convertView.findViewById(R.id.seekbar_plus);
				holder.incrementUsedNumberView = (TextView) convertView.findViewById(R.id.increment_used_number);
				holder.deleteView = (ImageView) convertView.findViewById(R.id.pre_list_delete);
				holder.deleteRightView = (ImageView) convertView.findViewById(R.id.pre_list_delete_right);
				convertView.setTag(holder);
			}
			Object obj = getItem(position);
			final UsedMaterailViewHolder holder = (UsedMaterailViewHolder) convertView.getTag();
			if(obj != null){
				final MaterialBean materialBean = (MaterialBean)obj;
				final int lastUsedNumber;
				//Fix分支
					lastUsedNumber = materialBean.getTotalNumber();

				holder.usedNumberView.setText(getString(R.string.format_repair_material_number,materialBean.getSelectedNumber().toString()));
				holder.materialNameView.setText(materialBean.getMaterialName());
				holder.specView.setText(materialBean.getSpecification());
				holder.brandView.setText(materialBean.getBrand());
				holder.unitView.setText(getString(R.string.format_repair_material_unit,materialBean.getUnit()));
				holder.incrementUsedNumberView.setText(materialBean.getImcrementNumber().toString());
				holder.deleteView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						materialBean.setChecked(false);
						mMaterialSelected.remove(materialBean);
						adapter.notifyDataSetChanged();
					}
				});
				holder.minusView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//如果大于0，减1
						if(materialBean.getCurrentRecoveryNumber().intValue() > 0){
							materialBean.setCurrentRecoveryNumber(materialBean.getCurrentRecoveryNumber()-1);
							materialBean.setSelectedNumber(lastUsedNumber);
							holder.incrementUsedNumberView.setText(materialBean.getCurrentRecoveryNumber().toString());
							materialBean.setImcrementNumber(materialBean.getCurrentRecoveryNumber().intValue());
						}
					}
				});
				holder.plusView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//如果小于最大可用数，加1
						if(materialBean.getCurrentRecoveryNumber().intValue() <lastUsedNumber){
							materialBean.setCurrentRecoveryNumber(materialBean.getCurrentRecoveryNumber()+1);
							materialBean.setSelectedNumber(lastUsedNumber);
							holder.incrementUsedNumberView.setText(materialBean.getCurrentRecoveryNumber().toString());
							materialBean.setImcrementNumber(materialBean.getCurrentRecoveryNumber().intValue());
						}
					}
				});

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
		Logger.i("onActivityResult,resultCode:"+resultCode);
		if(resultCode == Activity.RESULT_OK){
			List<MaterialBean> materialSelected = (List<MaterialBean>) data.getSerializableExtra("MaterialSelectedResult");
			mMaterialSelected.clear();
			mMaterialSelected.addAll(materialSelected);




			adapter.notifyDataSetChanged();
		}
	}



	
	private void submit(){
		//收集信息
		showProgressDialog(R.string.tips_for_collecting_for_submit);
		UsedMaterialFormBean usedMaterialFormBean = new UsedMaterialFormBean();
		String repairOrderId = repairOrderBean.getId();
		usedMaterialFormBean.setRepairOrderId(repairOrderId );
		List<Map<String, Integer>> materialUsed = new ArrayList<Map<String,Integer>>();
		Map<String, Integer> map ;
		for(MaterialBean materialBean:mMaterialSelected){
			if(materialBean.getImcrementNumber().intValue() != 0){
				map = new HashMap<String, Integer>();
				map.put(materialBean.getId(),materialBean.getImcrementNumber());
				materialUsed.add(map);
			}
		}
		usedMaterialFormBean.setMaterialUsed(materialUsed);
		if(materialUsed.size() >0){
			submitMaterialUsedAsyncHttp(usedMaterialFormBean);
			hideProgressDialog();
		}
		else{
			ToastUtil.showToast(mContext, "没有材料或者本次没有新增材料数量再提交。");
			hideProgressDialog();
			titlebarSubmitView.setEnabled(true);
		}
	}



	/**
	 * 使用材料提交
	 * @param usedMaterialFormBean
	 */
	private void submitMaterialUsedAsyncHttp(UsedMaterialFormBean usedMaterialFormBean){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getRepairOrderMaterialAddUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair repairManIdPair = new BasicNameValuePair("repairManId", application.getLoginInfoBean().getUserId().toString());
		params.add(repairManIdPair);
		String countSplitArray = "";
		String assetsIdSplitArray = "";
		List<Map<String, Integer>>  materialUsedMap = usedMaterialFormBean.getMaterialUsed();

		for(Map<String, Integer> maps:materialUsedMap){
			Set<String> keySet = maps.keySet();
			for(String key:keySet){
				assetsIdSplitArray +=","+key;
				countSplitArray +=",-"+maps.get(key);
			}
		}
		assetsIdSplitArray = assetsIdSplitArray.toString().trim().length()>0?assetsIdSplitArray.substring(1):"";
		countSplitArray = countSplitArray.toString().trim().length()>0?countSplitArray.substring(1):"";
		NameValuePair countPair = new BasicNameValuePair("count", countSplitArray);
		params.add(countPair);
		NameValuePair assetsIdPair = new BasicNameValuePair("assetsId", assetsIdSplitArray);
		params.add(assetsIdPair);
		NameValuePair formIdPair = new BasicNameValuePair("formId", repairOrderBean.getId());
		params.add(formIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void success(String resultResponse) {
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					submitSuccess();
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
				}
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void failed(Exception exception) {
				ToastUtil.showToast(mContext, "提交异常");
				HttpResponseUtil.justToast(exception, mContext);
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void beforeDealt() {

			}
		});
		if(checkForm()){
			httpAsyncTask.execute(httpFormBean);
		}
		else{
			ToastUtil.showToast(mContext, "请确保材料数量不超过拥有数量");
		}

	}



	private void submitSuccess(){
		ToastUtil.showToast(mContext, "材料使用情况已提交！");
		setResult(RESULT_OK);
		RecoveryRepairMaterialActivity.this.finish();
	}

	private boolean checkForm(){
		if(!mMaterialSelected.isEmpty()){
			for(MaterialBean materialBean:mMaterialSelected){
				if(materialBean.getImcrementNumber().intValue() > materialBean.getSelectedNumber().intValue()){
					return false;
				}
			}
		}
		else{
			return false;
		}
		return true;
	}

	private int selectedItemLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_listview_add_repair_material;

		default:
			return R.layout.listview_add_repair_material;
		}
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_recovery_repair_material;

		default:
			return R.layout.activity_recovery_repair_material;
		}
	}
}
