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
import com.wutong.common.widget.CustomDialog.Builder;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.OfficeMaterialBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.fragment.OfficeMaterialApplyFragment;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 申领清单、确认申领清单、撤销申领清单
 * @author Jolly
 * 创建时间：2013年12月20日下午4:47:03
 *
 */
public class OfficeMaterialApplyMaterialListActivity extends BaseActivity {

	public final static int MATERIAL_LIST = 1;
	public final static int CANCEL_ORDER = 2;
	public final static int QUERY_ORDER = 3;

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;

	private List<OfficeMaterialBean> mOfficeMaterialBeanList;
	private ListView mOfficeMaterialMaterialListLv;
	private BaseAdapter mOfficeMaterialMaterialLisAdapter;

	private TextView mOperationTipTv;

	private String outApplyId;
	private String outStatus;
	private int outType;
	private String outDivisionName;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_office_material_apply_material_list);
		intentInit();
		titlebarInit();
		viewInit();
		setupData();
	}

	private void intentInit(){
		List<OfficeMaterialBean> tempList = (List<OfficeMaterialBean>) getIntent().getSerializableExtra("materialList");
		outStatus = getIntent().getStringExtra("status");
		outApplyId = getIntent().getStringExtra("applyId");
		outType = getIntent().getIntExtra("type", 0);
		outDivisionName = getIntent().getStringExtra("divisionName");
		if(outDivisionName == null){
			title = getString(R.string.title_activity_office_material_apply_material_list);
		}
		else{
			title = outDivisionName +"的申领详情";
		}
		mOfficeMaterialBeanList = new ArrayList<OfficeMaterialBean>();
		mOfficeMaterialBeanList.addAll(tempList);
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setVisibility(ViewGroup.VISIBLE);
		titlebarTitleView.setText(title);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OfficeMaterialApplyMaterialListActivity.this.onBackPressed();

			}
		});

		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submit(outType);
			}
		});
	}

	private void viewInit(){
		mOfficeMaterialMaterialListLv = (ListView) findViewById(R.id.office_material_apply_material_list);
		mOfficeMaterialMaterialLisAdapter = new OfficeMAterialMaterialBaseAdapter();
		mOfficeMaterialMaterialListLv.setAdapter(mOfficeMaterialMaterialLisAdapter);

		mOperationTipTv = (TextView) findViewById(R.id.office_material_apply_material_list_tip);
		switch (outType) {
		case MATERIAL_LIST:
			mOperationTipTv.setVisibility(ViewGroup.GONE);
			titlebarTitleView.setText(R.string.title_activity_office_material_apply_material_list);
			break;
		case CANCEL_ORDER:
			titlebarTitleView.setText(R.string.title_activity_office_material_cancel_order);
			mOperationTipTv.setText(getString(R.string.format_office_material_apply_operation_cancel_tip,mOfficeMaterialBeanList.size()));
			mOperationTipTv.setVisibility(ViewGroup.VISIBLE);
			titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
			break;
		case QUERY_ORDER:
			titlebarTitleView.setText(R.string.title_activity_office_material_query_order);
			mOperationTipTv.setText(getString(R.string.format_office_material_apply_operation_query_tip,mOfficeMaterialBeanList.size()));
			mOperationTipTv.setVisibility(ViewGroup.VISIBLE);
			titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
			break;
		default:
			break;
		}
	}

	private void setupData(){

	}

	static class MaterialViewHolder{
		TextView nameView;
		TextView applyNumberView;
		TextView brandView;
		TextView specificationView;
		TextView stockOutNumberView;
		TextView stockOutNumberLabelView;
	}


	private class OfficeMAterialMaterialBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mOfficeMaterialBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MaterialViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
				holder = new MaterialViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.office_material_material_name);
				holder.brandView = (TextView) convertView.findViewById(R.id.office_material_brand);
				holder.applyNumberView = (TextView) convertView.findViewById(R.id.office_material_apply_number);
				holder.specificationView = (TextView) convertView.findViewById(R.id.office_material_spec);
				holder.stockOutNumberView = (TextView) convertView.findViewById(R.id.office_material_stock_out_number);
				holder.stockOutNumberLabelView = (TextView) convertView.findViewById(R.id.office_material_stock_out_number_label);
				convertView.setTag(holder);
			}
			OfficeMaterialBean officeMaterialBean = mOfficeMaterialBeanList.get(position);
			holder = (MaterialViewHolder) convertView.getTag();
			holder.nameView.setText(officeMaterialBean.getName());
			holder.brandView.setText(officeMaterialBean.getBrand());
			holder.applyNumberView.setText(officeMaterialBean.getApplyNumber()+officeMaterialBean.getUnit());
			holder.stockOutNumberView.setText(officeMaterialBean.getStockOutNumber().toString()+officeMaterialBean.getUnit());
			if(outStatus !=null && (outStatus.equals("2") || outStatus.equals("3"))){
				holder.stockOutNumberView.setVisibility(ViewGroup.VISIBLE);
				holder.stockOutNumberLabelView.setVisibility(ViewGroup.VISIBLE);
			}
			else{
				holder.stockOutNumberView.setVisibility(ViewGroup.INVISIBLE);
				holder.stockOutNumberLabelView.setVisibility(ViewGroup.INVISIBLE);
			}

			
			
			//Fix分支
				((TextView) convertView.findViewById(R.id.material_page_specification_label)).setText(R.string.workbench_material_page_stat_spec_to_code_index_label);
				holder.specificationView.setText(officeMaterialBean.getCodeIndex());
			return convertView;
		}
	}

	private void submit(int type){
		Builder builder;
		switch (type) {
		case CANCEL_ORDER:
			builder = new Builder(mContext)
			.setTitle("撤销操作").setMessage("是否对该申领单进行撤销操作")
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			})
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancelOrderHttp();
				}
			});
			builder.create().show();
			break;
		case QUERY_ORDER:
			builder = new Builder(mContext)
			.setTitle("确认操作").setMessage("是否对该申领单进行确认操作")
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			})
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					queryOrderHttp();
				}
			});
			builder.create().show();
			break;
		default:
			break;
		}
	}

	
	private void cancelOrderHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getOfficeMaterialApplyCancel());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair applyIdPair = new BasicNameValuePair("applyId", outApplyId);
		params.add(applyIdPair);
		NameValuePair userIdPair = new BasicNameValuePair("userId", application.getLoginInfoBean().getUserId().toString());
		params.add(userIdPair);

		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				Logger.i("result:" + resultResponse);
				ResponseBean<Object> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<Object>>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "撤销成功" );
					Intent intent = new Intent();
					intent.putExtra("callCode", OfficeMaterialApplyFragment.CALL_CODE_APPLYING_AND_CANCEL_REFRESH);
					setResult(RESULT_OK,intent);
					finish();
				}
				else{
					ToastUtil.showToast(mContext, "撤销失败：" + responseBean.getMessage());
				}
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog("撤销中");
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	
	private void queryOrderHttp(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getOfficeMaterialApplyQuery());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair applyIdPair = new BasicNameValuePair("applyId", outApplyId);
		params.add(applyIdPair);
		NameValuePair userIdPair = new BasicNameValuePair("userId", application.getLoginInfoBean().getUserId().toString());
		params.add(userIdPair);
		

		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				Logger.i("result:" + resultResponse);
				ResponseBean<Object> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<Object>>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "确认成功" );
					Intent intent = new Intent();
					intent.putExtra("callCode", OfficeMaterialApplyFragment.CALL_CODE_QUERY_AND_STOCK_OUT_REFRESH);
					setResult(RESULT_OK,intent);
					finish();
				}
				else{
					ToastUtil.showToast(mContext, "确认失败：" + responseBean.getMessage());
				}
			}

			@Override
			public void failed(Exception exception) {
				hideProgressDialog();
				HttpResponseUtil.justToast(exception, mContext);
			}

			@Override
			public void beforeDealt() {
				showProgressDialog("提交确认中");
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	
	private int selectedItemLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_list_item_office_material_list;

		default:
			return R.layout.list_item_office_material_list;
		}
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_office_material_apply_material_list;

		default:
			return R.layout.activity_office_material_apply_material_list;
		}
	}
}
