package com.wutong.repair.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

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
import com.wutong.repair.data.bean.OfficeMaterialApplyBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 申领单-详情
 * @author Jolly
 * 创建时间：2014年2月24日下午3:23:55
 *
 */
public class OfficeMaterialApplyDetailActivity extends BaseActivity {



	private final static int REQUEST_CODE_MATERIAL_LIST = 99;

	private final static int QUERY_OP = 1;
	private final static int CANCEL_OP = 2;
	private final static String CANCEL_OP_NAME = "撤销";
	private final static String QUERY_OP_NAME = "确认";


	public final static int PERMISSION_MANAGER = 1;//管理人员查看
	public final static int PERMISSION_DIVISION = 2;//科室下的人查看

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarOperationView;

	private TextView mApplyCodeTv;//申领单编号
	private TextView mDivisionNameTv;//科室名称
	private TextView mStatusNameTv;//状态
	private TextView mApplicantTv;//申领人
	private TextView mCancelManTv;//撤销人
	private TextView mQueryManTv;//确认人
	private TextView mStockOutManTv;//出库人
	private TextView mApplyTimeTv;//申领时间
	private TextView mCancelTimeTv;//撤销时间
	private TextView mQueryTimeTv;//确认时间
	private TextView mStockOutTimeTv;//出库时间

	private View mApplicantLayout;//申领人
	private View mCancelManLayout;//撤销人
	private View mQueryManLayout;//确认人
	private View mStockOutManLayout;//出库人
	private View mApplyTimeLayout;//申领时间
	private View mCancelTimeLayout;//撤销时间
	private View mQueryTimeLayout;//确认时间
	private View mStockOutTimeLayout;//出库时间

	private TextView mMaterialListTv;//申领清单
	private TextView mRemarkTv;//备注

	private String outApplyId;
	private int outPermissionType;
	private String outDivisionName;

	private CustomDialog mOperationCustomDialog;
	private WheelView mOperationWheelView;
	private Vector<String> mOperationNameArray;
	private Vector<Integer> mOperationValueArray;

	private int resultCallCode;

	private String title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_office_material_apply_detail);
		dataInit();
		intentInit();
		titlebarInit();
		viewInit();
		setupData();
	}

	private void dataInit(){
		mOperationNameArray = new Vector<String>();
		mOperationValueArray = new Vector<Integer>();
	}


	private void intentInit(){
		outApplyId = getIntent().getStringExtra("applyId");
		outPermissionType = getIntent().getIntExtra("permissionType", 0);
		outDivisionName = getIntent().getStringExtra("divisionName");
		if(outDivisionName == null){
			title = getString(R.string.title_activity_office_material_apply_detail);
		}
		else{
			title = outDivisionName +"的申领详情";
		}
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
				OfficeMaterialApplyDetailActivity.this.onBackPressed();

			}
		});
		titlebarOperationView = (ImageView) findViewById(R.id.titlebar_more);


	}



	@Override
	public void onBackPressed() {
		if(resultCallCode != 0){
			Intent intent =new Intent();
			intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_office_material_apply_name));
			intent.putExtra(getString(R.string.call_fragment_key_code), resultCallCode);
			setResult(RESULT_OK,intent);
		}
		super.onBackPressed();
	}

	private void viewInit(){
		mApplyCodeTv = (TextView) findViewById(R.id.office_material_apply_detail_apply_code);
		mDivisionNameTv = (TextView) findViewById(R.id.office_material_apply_detail_division_name);
		mStatusNameTv = (TextView) findViewById(R.id.office_material_apply_detail_status_name);
		mApplicantTv = (TextView) findViewById(R.id.office_material_apply_detail_applicant);
		mCancelManTv = (TextView) findViewById(R.id.office_material_apply_detail_cancel);
		mQueryManTv = (TextView) findViewById(R.id.office_material_apply_detail_query);
		mStockOutManTv = (TextView) findViewById(R.id.office_material_apply_detail_stock_out);
		mApplyTimeTv = (TextView) findViewById(R.id.office_material_apply_detail_applicant_time);
		mCancelTimeTv = (TextView) findViewById(R.id.office_material_apply_detail_cancel_time);
		mQueryTimeTv = (TextView) findViewById(R.id.office_material_apply_detail_query_time);
		mStockOutTimeTv = (TextView) findViewById(R.id.office_material_apply_detail_stock_out_time);

		mApplicantLayout =  findViewById(R.id.office_material_apply_detail_applicant_layout);
		mCancelManLayout =  findViewById(R.id.office_material_apply_detail_cancel_layout);
		mQueryManLayout =  findViewById(R.id.office_material_apply_detail_query_layout);
		mStockOutManLayout =  findViewById(R.id.office_material_apply_detail_stock_out_layout);
		mApplyTimeLayout =  findViewById(R.id.office_material_apply_detail_applicant_time_layout);
		mCancelTimeLayout =  findViewById(R.id.office_material_apply_detail_cancel_time_layout);
		mQueryTimeLayout =  findViewById(R.id.office_material_apply_detail_query_time_layout);
		mStockOutTimeLayout =  findViewById(R.id.office_material_apply_detail_stock_out_time_layout);

		mMaterialListTv = (TextView) findViewById(R.id.office_material_apply_detail_material_list);
		mRemarkTv = (TextView) findViewById(R.id.office_material_apply_detail_remark);


	}

	private void setupData(){
		loadOfficeMaterialDetail();
	}

	private void loadOfficeMaterialDetail(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getOfficeMaterialApplyLoadDetail());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair applyIdPair = new BasicNameValuePair("applyId", outApplyId);
		params.add(applyIdPair);

		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<OfficeMaterialApplyBean> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<OfficeMaterialApplyBean>>(){}.getType());
				if(responseBean.isSuccess()){
					OfficeMaterialApplyBean result = responseBean.getData();
					if(result == null){
						ToastUtil.showToast(mContext, "服务端返回数据异常：" + responseBean.getMessage());
						return;
					}
					else{
						//装载数据
						setupOfficeMaterial(result);
					}
				}
				else{
					ToastUtil.showToast(mContext, "获取失败：" + responseBean.getMessage());
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

	/**
	 * 加载数据到界面上
	 * @param applyBean
	 */
	private void setupOfficeMaterial(final OfficeMaterialApplyBean applyBean){

		mOperationNameArray.clear();
		mOperationValueArray.clear();

		mApplyCodeTv.setText(applyBean.getApplyNum());
		//Fix分支
			mDivisionNameTv.setText(applyBean.getDivisionName());	
		mStatusNameTv.setText(applyBean.getStatusName());

		mRemarkTv.setText(applyBean.getRemark());

		mApplicantTv.setText(applyBean.getApplicantName());
		mApplyTimeTv.setText(applyBean.getApplyTime());

		String status = applyBean.getStatus();
		if(status == null){
			ToastUtil.showToast(mContext, "申领状态异常");
		}
		else if(status.equals("1")){//申领中
			mStatusNameTv.setBackgroundResource(R.drawable.common_green_bg);
			if(outPermissionType == PERMISSION_DIVISION){
				titlebarOperationView.setVisibility(ViewGroup.VISIBLE);
				mOperationNameArray.add(CANCEL_OP_NAME);
				mOperationValueArray.add(CANCEL_OP);
			}
			//无
			mStockOutManLayout.setVisibility(ViewGroup.GONE);
			mStockOutTimeLayout.setVisibility(ViewGroup.GONE);

			mQueryManLayout.setVisibility(ViewGroup.GONE);
			mQueryTimeLayout.setVisibility(ViewGroup.GONE);

			mCancelManLayout.setVisibility(ViewGroup.GONE);
			mCancelTimeLayout.setVisibility(ViewGroup.GONE);
		}
		else if(status.equals("2")){//出库
			mStatusNameTv.setBackgroundResource(R.drawable.common_orange_bg);
			if(outPermissionType == PERMISSION_DIVISION){
				titlebarOperationView.setVisibility(ViewGroup.VISIBLE);
				mOperationNameArray.add(QUERY_OP_NAME);
				mOperationValueArray.add(QUERY_OP);
			}
			mStockOutManTv.setText(applyBean.getStockOutName());
			mStockOutTimeTv.setText(applyBean.getStockOutTime());

			mStockOutManLayout.setVisibility(ViewGroup.VISIBLE);
			mStockOutTimeLayout.setVisibility(ViewGroup.VISIBLE);

			mQueryManLayout.setVisibility(ViewGroup.GONE);
			mQueryTimeLayout.setVisibility(ViewGroup.GONE);

			mCancelManLayout.setVisibility(ViewGroup.GONE);
			mCancelTimeLayout.setVisibility(ViewGroup.GONE);
		}
		else if(status.equals("3")){//确认
			mStatusNameTv.setBackgroundResource(R.drawable.common_blue_bg);
			titlebarOperationView.setVisibility(ViewGroup.GONE);
			mStockOutManTv.setText(applyBean.getStockOutName());
			mStockOutTimeTv.setText(applyBean.getStockOutTime());
			mQueryManTv.setText(applyBean.getConfirmorName());
			mQueryTimeTv.setText(applyBean.getConfirmTime());

			mStockOutManLayout.setVisibility(ViewGroup.VISIBLE);
			mStockOutTimeLayout.setVisibility(ViewGroup.VISIBLE);

			mQueryManLayout.setVisibility(ViewGroup.VISIBLE);
			mQueryTimeLayout.setVisibility(ViewGroup.VISIBLE);

			mCancelManLayout.setVisibility(ViewGroup.GONE);
			mCancelTimeLayout.setVisibility(ViewGroup.GONE);
		}
		else if(status.equals("4")){//撤销
			mStatusNameTv.setBackgroundResource(R.drawable.common_gray_bg);
			titlebarOperationView.setVisibility(ViewGroup.GONE);
			mCancelManTv.setText(applyBean.getCancelName());
			mCancelTimeTv.setText(applyBean.getCancelTime());

			mStockOutManLayout.setVisibility(ViewGroup.GONE);
			mStockOutTimeLayout.setVisibility(ViewGroup.GONE);

			mQueryManLayout.setVisibility(ViewGroup.GONE);
			mQueryTimeLayout.setVisibility(ViewGroup.GONE);

			mCancelManLayout.setVisibility(ViewGroup.VISIBLE);
			mCancelTimeLayout.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			ToastUtil.showToast(mContext, "申领信息出现意外的状态");
		}

		if(applyBean.getOfficeMaterialBeanList() == null){
			ToastUtil.showToast(mContext, "申领清单异常");
			mMaterialListTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ToastUtil.showToast(mContext, "申领清单异常，无法查看");

				}
			});
		}
		else{
			mMaterialListTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putSerializable("materialList", (Serializable) applyBean.getOfficeMaterialBeanList());
					bundle.putString("status", applyBean.getStatus());
					bundle.putString("applyId", applyBean.getOffciceMaterialApplyId());
					bundle.putInt("type", OfficeMaterialApplyMaterialListActivity.MATERIAL_LIST);
					bundle.putString("divisionName", outDivisionName);
					Intent intent = new Intent(mContext,OfficeMaterialApplyMaterialListActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
		}

		if(mOperationNameArray.size() >0){
			View operationContentView = LayoutInflater.from(mContext).inflate(R.layout.common_single_list_select, null);
			mOperationWheelView = (WheelView) operationContentView.findViewById(R.id.select_item_wheel);
			mOperationWheelView.setBackgroundResource(R.drawable.common_wheel_bg);
			mOperationWheelView.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
			mOperationWheelView.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mOperationNameArray.toArray(new String[]{})));
			Button okBtnView = (Button) operationContentView.findViewById(R.id.ok_btn);
			Button cancelBtnView = (Button) operationContentView.findViewById(R.id.cancel_btn);
			okBtnView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = mOperationWheelView.getCurrentItem();
					int operationIndex = mOperationValueArray.get(index).intValue();
					operation(applyBean,operationIndex);
					mOperationCustomDialog.dismiss();
				}
			});
			cancelBtnView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mOperationCustomDialog.dismiss();

				}
			});

			mOperationCustomDialog = new CustomDialog.Builder(mContext)
			.setTitle("操作")
			.setView(operationContentView)
			.create();
			titlebarOperationView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(!mOperationCustomDialog.isShowing()){
						mOperationCustomDialog.show();
					}
				}
			});
		}

	}

	private void operation(OfficeMaterialApplyBean applyBean,int position){
		Bundle bundle = new Bundle();
		Intent intent;
		switch (position) {
		case QUERY_OP:
			bundle.putSerializable("materialList", (Serializable) applyBean.getOfficeMaterialBeanList());
			bundle.putString("status", applyBean.getStatus());
			bundle.putString("applyId", applyBean.getOffciceMaterialApplyId());
			bundle.putInt("type", OfficeMaterialApplyMaterialListActivity.QUERY_ORDER);
			intent = new Intent(mContext,OfficeMaterialApplyMaterialListActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE_MATERIAL_LIST);
			break;
		case CANCEL_OP:
			bundle.putSerializable("materialList", (Serializable) applyBean.getOfficeMaterialBeanList());
			bundle.putString("status", applyBean.getStatus());
			bundle.putString("applyId", applyBean.getOffciceMaterialApplyId());
			bundle.putInt("type", OfficeMaterialApplyMaterialListActivity.CANCEL_ORDER);
			intent = new Intent(mContext,OfficeMaterialApplyMaterialListActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_CODE_MATERIAL_LIST);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {
			case REQUEST_CODE_MATERIAL_LIST:
				resultCallCode = data.getIntExtra("callCode", 0);
				loadOfficeMaterialDetail();
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_office_material_apply_detail;

		default:
			return R.layout.activity_office_material_apply_detail;
		}
	}
}
