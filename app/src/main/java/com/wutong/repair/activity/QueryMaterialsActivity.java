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
import com.wutong.common.widget.CustomDialog;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.MaterialGrantBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.RoomAssetMaterialBean;
import com.wutong.repair.dictionary.QueryMaterialDict;
import com.wutong.repair.fragment.MaterialInfoFragment;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.StringUtil;
import com.wutong.repair.util.TimeUtil;

import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class QueryMaterialsActivity extends BaseActivity {
	
	private final static int VIEW_CODE_HEAD_VIEW = 123;
	private final static int VIEW_CODE_LIST_VIEW = 124;

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;
	private MaterialGrantBean materialGrantBean;

	private ListView mQueryMaterialView;
	private List<RoomAssetMaterialBean> roomAssetMaterialBeanList;
	private QueryMaterialBaseAdapter adapter;
	
	private String grantId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_query_materials);
		intentInit();
		titlebarInit();
		viewInit();
	}
	
	private void intentInit(){
		Intent intent = getIntent();

		grantId = intent.getStringExtra("grantId");
		materialGrantBean = (MaterialGrantBean) intent.getSerializableExtra("materialGrantBean");
	}
	
	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_query_materials);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				QueryMaterialsActivity.this.onBackPressed();

			}
		});
		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CustomDialog dialog = new CustomDialog.Builder(mContext)
				.setTitle("材料发放确认")
				.setMessage("请确保发放材料的信息无误再确定。")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
						submitQueryMaterialGrantAsyncHttp(materialGrantBean.getId());
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				})
				.create();
				dialog.show();
			}
		});
	}
	
	private void viewInit(){
		//
		mQueryMaterialView = (ListView) findViewById(R.id.query_material_list);

		//


		TextView materialgrantTimeView = (TextView) findViewById(R.id.query_material_time);
		TextView materialgrantStatusView = (TextView) findViewById(R.id.query_material_status);
		//数据初始化
		roomAssetMaterialBeanList = new ArrayList<RoomAssetMaterialBean>();
		materialgrantTimeView.setText(materialGrantBean.getGrantTime());
		materialgrantStatusView.setText(QueryMaterialDict.getStatusName(materialGrantBean.getStatus()));
		if(materialGrantBean.getStatus() == null){
			ToastUtil.showToast(mContext, "状态异常，显示颜色可能不正确");
		}
		else if(materialGrantBean.getStatus().equals("2")){
			materialgrantStatusView.setTextColor(getResources().getColor(R.color.common_status_red));
		}
		//
		if(materialGrantBean.getStatus() == null){
			ToastUtil.showToast(mContext, "状态异常，部分操作无法显示");
		}
		else if(materialGrantBean.getStatus().equals(QueryMaterialDict.UNQUERY)){
			titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		}
		adapter = new QueryMaterialBaseAdapter(roomAssetMaterialBeanList);
		mQueryMaterialView.setAdapter(adapter);

		loadQueryingMaterialListAsyncHttp(grantId);
	}
	


	private class QueryMaterialBaseAdapter extends BaseAdapter{
		private List<RoomAssetMaterialBean> list;

		public QueryMaterialBaseAdapter(List<RoomAssetMaterialBean> list) {
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
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(VIEW_CODE_LIST_VIEW), null);
			TextView nameView = (TextView) convertView.findViewById(R.id.query_material_name);
			TextView brandView = (TextView) convertView.findViewById(R.id.query_material_brand);
			TextView specView = (TextView) convertView.findViewById(R.id.query_material_spec);
			TextView materialView = (TextView) convertView.findViewById(R.id.query_material_number);
			RoomAssetMaterialBean roomAssetMaterialBean = list.get(position);
			nameView.setText(roomAssetMaterialBean.getAssetsName());
			brandView.setText("品牌："+roomAssetMaterialBean.getBrand());
			specView.setText("型号："+roomAssetMaterialBean.getSpec());
			materialView.setText(getString(R.string.format_material_select_number_unit,
					roomAssetMaterialBean.getGrantNumber(),roomAssetMaterialBean.getUnit()));
			return convertView;
		}

	}

	
	private void loadQueryingMaterialListAsyncHttp(String grantId){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getMaterialGrantDetailUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair applyIdPair = new BasicNameValuePair("applyId", grantId);
		params.add(applyIdPair);
		NameValuePair repairManIdPair = new BasicNameValuePair("repairManId", application.getLoginInfoBean().getUserId().toString());
		params.add(repairManIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {
			
			@Override
			public void success(String resultResponse) {
				ResponseBean<List<RoomAssetMaterialBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<RoomAssetMaterialBean>>>(){}.getType());
				List<RoomAssetMaterialBean> response = responseBean.getData();
				if(responseBean.isSuccess() == false){
					ToastUtil.showToast(mContext, responseBean.getMessage());
					return;
				}
				if(response == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				if(response != null){
					roomAssetMaterialBeanList.clear();
					roomAssetMaterialBeanList.addAll(response);
					adapter.notifyDataSetChanged();
				}
			}
			
			@Override
			public void failed(Exception exception) {
				HttpResponseUtil.justToast(exception, mContext);
			}
			
			@Override
			public void beforeDealt() {
				
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	
	

	private void submitQueryMaterialGrantAsyncHttp(String grantId){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getMaterialGrantQueryUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair applyIdPair = new BasicNameValuePair("applyId", grantId);
		params.add(applyIdPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean>(){}.getType());
				if(responseBean.isSuccess()){
					//关闭当前页面
					ToastUtil.showToast(mContext, "确认成功！");
					
					Intent intent =new Intent();
					intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_material_info_fragment_name));
					intent.putExtra(getString(R.string.call_fragment_key_code), MaterialInfoFragment.CALL_CODE_GRANT_REFRESH);//新报修单刷新未受理
					setResult(RESULT_OK,intent);
					QueryMaterialsActivity.this.finish();
				}
				else{
					ToastUtil.showToast(mContext, responseBean.getMessage());
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

	private int selectedItemLayout(int viewCode){
		switch (application.getSkinType()) {
		case 1:
			switch (viewCode) {
			case VIEW_CODE_HEAD_VIEW:
				return R.layout.spring_horse_listview_header_query_material;
			case VIEW_CODE_LIST_VIEW:
				return R.layout.spring_horse_listview_query_material;
			default:
				break;
			}

		default:
			switch (viewCode) {
			case VIEW_CODE_HEAD_VIEW:
				return R.layout.listview_header_query_material;
			case VIEW_CODE_LIST_VIEW:
				return R.layout.listview_query_material;
			default:
				break;
			}
		}
		return 0;
	}
	
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_query_materials;

		default:
			return R.layout.activity_query_materials;
		}
	}
}
