package com.wutong.repair.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
import com.wutong.repair.data.bean.MaterialBean;
import com.wutong.repair.data.bean.RepairOrderBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.UsedMaterialFormBean;
import com.wutong.repair.dictionary.RepairOrderDict;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repairfjnu.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@Deprecated 
public class ApplyMaterialActivity extends BaseActivity {

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;

	private ListView mPreApplyMaterialsView;
	private Button mAppendMaterailView;
	private List<MaterialBean> mMaterialSelected;
	private List<MaterialBean> mMaterialUnSelected;
	private UsedMaterailBaseAdapter adapter;
	private boolean isReadOnly = false;

	private RepairOrderBean repairOrderBean;
	
	private boolean outIsLargeCharacter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_material);
		setStatPageName(mContext, R.string.title_activity_apply_material);
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
		titlebarTitleView.setText(R.string.title_activity_apply_material);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ApplyMaterialActivity.this.onBackPressed();

			}
		});
		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				titlebarSubmitView.setEnabled(false);
				//收集信息
				showProgressDialog("正在收集材料信息");
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
					ToastUtil.showToast(ApplyMaterialActivity.this, "没有材料或者本次没有新增材料数量再提交。");
					hideProgressDialog();
					titlebarSubmitView.setEnabled(true);
				}
			}
		});
	}

	private void viewInit(){
		//初始化
		mPreApplyMaterialsView = (ListView) findViewById(R.id.pre_apply_materails);
		mAppendMaterailView = (Button) findViewById(R.id.append_materail);
		View emptyView = findViewById(R.id.apply_material_empty);
		mPreApplyMaterialsView.setEmptyView(emptyView);
		//

		//如果不允许，隐藏下方按钮
		if(repairOrderBean.getStatus() == null){
			ToastUtil.showToast(mContext, "单子状态出现异常，部分显示和功能可能不正确");
		}
		else if(repairOrderBean.getStatus().equals(String.valueOf(RepairOrderDict.START_TO_DEAL_STATUS))
				||repairOrderBean.getStatus().equals(String.valueOf(RepairOrderDict.WANNA_REFIX_STATUS))){
			isReadOnly = false;
			mAppendMaterailView.setVisibility(ViewGroup.VISIBLE);
			mAppendMaterailView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(ApplyMaterialActivity.this,MaterailSelectActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("MaterialUnSelected", (Serializable)mMaterialUnSelected);
					bundle.putSerializable("MaterialSelected", (Serializable)mMaterialSelected);
					intent.putExtra("isLargeCharacter",outIsLargeCharacter);
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);

				}
			});
			//
			titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			isReadOnly = true;
		}

		mMaterialUnSelected = new ArrayList<MaterialBean>();

		mMaterialSelected = new ArrayList<MaterialBean>();
		adapter = new UsedMaterailBaseAdapter(mMaterialSelected );
		mPreApplyMaterialsView.setAdapter(adapter);

		//动态设置大小
		
		if(outIsLargeCharacter){
			mAppendMaterailView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
		}
		else{
			mAppendMaterailView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		}
		
		loadMaterialListAsyncHttp();
	}

	

	static class UsedMaterailViewHolder{
		TextView materialNameView;
		Button minusView;
		Button plusView;
		TextView usedNumberView;
		TextView incrementUsedNumberView;
		ImageView deleteView;
		ImageView deleteRightView;
		int currentPlusedNumber = 0;

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
					convertView = LayoutInflater.from(ApplyMaterialActivity.this).inflate(R.layout.listview_material_used_large, null);
				}
				else{
					convertView = LayoutInflater.from(ApplyMaterialActivity.this).inflate(R.layout.listview_material_used, null);
				}
				UsedMaterailViewHolder holder = new UsedMaterailViewHolder();
				holder.materialNameView = (TextView) convertView.findViewById(R.id.material_name);
				holder.minusView = (Button) convertView.findViewById(R.id.seekbar_minus);
				holder.plusView = (Button) convertView.findViewById(R.id.seekbar_plus);
				holder.usedNumberView = (TextView) convertView.findViewById(R.id.last_used_number);
				holder.incrementUsedNumberView = (TextView) convertView.findViewById(R.id.increment_used_number);
				holder.deleteView = (ImageView) convertView.findViewById(R.id.pre_list_delete);
				holder.deleteRightView = (ImageView) convertView.findViewById(R.id.pre_list_delete_right);
				convertView.setTag(holder);
			}
			Object obj = getItem(position);
			final UsedMaterailViewHolder holder = (UsedMaterailViewHolder) convertView.getTag();
			if(obj != null){
				final MaterialBean materialBean = (MaterialBean)obj;

				final int lastUsedNumber = materialBean.getSelectedNumber();
				final int availableNumber = materialBean.getTotalNumber();//当前可用总数应当是现有剩余+上次使用

				holder.usedNumberView.setText(String.valueOf(lastUsedNumber));
				holder.materialNameView.setText(materialBean.getMaterialName());
				holder.incrementUsedNumberView.setText(materialBean.getImcrementNumber().toString());
				holder.deleteView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						materialBean.setChecked(false);
						mMaterialUnSelected.add(materialBean);
						mMaterialSelected.remove(materialBean);
						adapter.notifyDataSetChanged();

					}
				});
				holder.minusView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//如果大于0，减1
						if(holder.currentPlusedNumber > 0){
							holder.currentPlusedNumber-=1;
							materialBean.setSelectedNumber(lastUsedNumber);
							holder.incrementUsedNumberView.setText(String.valueOf(holder.currentPlusedNumber));
							materialBean.setImcrementNumber(holder.currentPlusedNumber);
						}
					}
				});
				holder.plusView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//如果小于最大可用数，加1
						if(holder.currentPlusedNumber <availableNumber){
							holder.currentPlusedNumber+=1;
							materialBean.setSelectedNumber(lastUsedNumber);
							holder.incrementUsedNumberView.setText(String.valueOf(holder.currentPlusedNumber));
							materialBean.setImcrementNumber(holder.currentPlusedNumber);
						}
					}
				});
				//对readonly隐藏信息部分
				if(isReadOnly){
					holder.deleteView.setVisibility(ViewGroup.GONE);
					holder.deleteRightView.setVisibility(ViewGroup.GONE);
					holder.minusView.setVisibility(ViewGroup.GONE);
					holder.plusView.setVisibility(ViewGroup.GONE);
					holder.incrementUsedNumberView.setVisibility(ViewGroup.GONE);
				}

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
			List<MaterialBean> materialUnSelected = (List<MaterialBean>) data.getSerializableExtra("MaterialUnSelectedResult");
			List<MaterialBean> materialSelected = (List<MaterialBean>) data.getSerializableExtra("MaterialSelectedResult");
			mMaterialSelected.clear();
			mMaterialSelected.addAll(materialSelected);
			mMaterialUnSelected.clear();
			mMaterialUnSelected.addAll(materialUnSelected);




			adapter.notifyDataSetChanged();
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
				mMaterialUnSelected.clear();
				mMaterialSelected.clear();
				mMaterialUnSelected.addAll(responseList);
				adapter.notifyDataSetChanged();
				loadMaterialUsedAsyncHttp();
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);

			}

			@Override
			public void beforeDealt() {
				showProgressDialog("获取拥有的材料信息……");
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
				trimUsedMaterial(mMaterialSelected,mMaterialUnSelected);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);

			}

			@Override
			public void beforeDealt() {
				showProgressDialog("获取材料使用信息……");
			}
		});
		httpAsyncTask.execute(httpFormBean);
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
				countSplitArray +=","+maps.get(key);
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
					ToastUtil.showToast(ApplyMaterialActivity.this, responseBean.getMessage());
				}
				titlebarSubmitView.setEnabled(true);
			}

			@Override
			public void failed(Exception exception) {
				ToastUtil.showToast(ApplyMaterialActivity.this, "提交异常");
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
			ToastUtil.showToast(ApplyMaterialActivity.this, "请确保材料数量不超过拥有数量");
		}

	}



	private void submitSuccess(){
		ToastUtil.showToast(ApplyMaterialActivity.this, "材料使用情况已提交！");
		ApplyMaterialActivity.this.finish();
	}

	private boolean checkForm(){
		if(!mMaterialSelected.isEmpty()){
			for(MaterialBean materialBean:mMaterialSelected){
				if(materialBean.getSelectedNumber().intValue() > materialBean.getTotalNumber().intValue()){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 把候选列表（所有维修工下的材料列表）中移除报修单下的使用过的材料，并更新已使用的材料的剩余数量
	 * @param mMaterialSelected
	 * @param mMaterialUnSelected
	 */
	private void trimUsedMaterial(List<MaterialBean> mMaterialSelected,List<MaterialBean> mMaterialUnSelected){
		Iterator<MaterialBean> unSelecetIterator = mMaterialUnSelected.iterator();
		MaterialBean unSelectBean;
		MaterialBean selectedBean;
		while(unSelecetIterator.hasNext()){
			unSelectBean = unSelecetIterator.next();
			if(mMaterialSelected.contains(unSelectBean)){//xxxBean.contains(xxBean)这句需要该bean重写方法hasCode和equals并保证其id是唯一标识。
				//替换信息到selected，然后移除unSelected 列表中该对象
				selectedBean = mMaterialSelected.get(mMaterialSelected.indexOf(unSelectBean));
				selectedBean.setTotalNumber(unSelectBean.getTotalNumber());
				//mMaterialUnSelected.remove(unSelectBean);
				unSelecetIterator.remove();

			}
		}
	}
}
