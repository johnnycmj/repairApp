package com.wutong.repair.activity;


import java.io.Serializable;
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
import com.wutong.common.widget.ListViewInnerScroll;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.OfficeMaterialBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.fragment.OfficeMaterialApplyFragment;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OfficeMaterialApplyAddActivity extends BaseActivity {

	private final static int REQUEST_CODE_SELECT_OFFICE_MATERIAL = 9;

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;

	private TextView mApplyName;//申领人
	private TextView mApplyDivision;//申领科室
	private ListViewInnerScroll mMaterialListLv;//申领清单
	private TextView mAddOfficeMaterial;//添加申领物品
	private TextView mRemark;//备注

	private OfficeMaterialAppliedBaseAdapter mOfficeMaterialAppliedBaseAdapter;

	private List<OfficeMaterialBean> mSelectedOfficeMaterialList;//选中的办公材料

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_office_material_apply_add);
		dataInit();
		titlebarInit();
		viewInit();
		setupData();
	}

	private void dataInit(){
		mSelectedOfficeMaterialList = new ArrayList<OfficeMaterialBean>();
		mOfficeMaterialAppliedBaseAdapter = new OfficeMaterialAppliedBaseAdapter();
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_office_material_apply_add);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OfficeMaterialApplyAddActivity.this.onBackPressed();

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

	private void viewInit(){
		mApplyName = (TextView) findViewById(R.id.office_material_apply_order_apply_name);
		mApplyDivision = (TextView) findViewById(R.id.office_material_apply_order_division_name);
		mMaterialListLv = (ListViewInnerScroll) findViewById(R.id.office_material_apply_order_selected_material_list);
		mAddOfficeMaterial = (TextView) findViewById(R.id.office_material_apply_order_add_material);//添加申领物品
		mRemark = (TextView) findViewById(R.id.office_material_apply_order_remark);//备注
		mMaterialListLv.setAdapter(mOfficeMaterialAppliedBaseAdapter);
		mAddOfficeMaterial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, OfficeMaterialSelectListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("selectedList", (Serializable) mSelectedOfficeMaterialList);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_CODE_SELECT_OFFICE_MATERIAL);
			}
		});
	}

	private void setupData(){
		//Fix分支
			mApplyDivision.setText(application.getLoginInfoBean().getDivisionName());
		mApplyName.setText(application.getLoginInfoBean().getRealName());
		//
	}

	static class OfficeMaterialAppliedViewHolder{
		TextView nameView;
		ImageView deleteView;
		Button plusView;
		Button minusView;
		TextView applyNumberView;
	}

	/**
	 * 申领的办公用品
	 * @author Jolly
	 * 创建时间：2013年12月20日上午11:28:00
	 *
	 */
	private class OfficeMaterialAppliedBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mSelectedOfficeMaterialList.size();
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
			final OfficeMaterialAppliedViewHolder holder;
			final OfficeMaterialBean officeMaterialBean = mSelectedOfficeMaterialList.get(position);
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
				holder = new OfficeMaterialAppliedViewHolder();
				holder.nameView = (TextView) convertView.findViewById(R.id.office_material_applying_name);
				holder.deleteView = (ImageView) convertView.findViewById(R.id.office_material_applying_delete);
				holder.plusView = (Button) convertView.findViewById(R.id.office_material_applying_number_plus);
				holder.minusView = (Button) convertView.findViewById(R.id.office_material_applying_number_minus);
				holder.applyNumberView = (TextView) convertView.findViewById(R.id.office_material_applying_number);
				convertView.setTag(holder);
			}
			else{
				holder = (OfficeMaterialAppliedViewHolder) convertView.getTag();
			}
			holder.nameView.setText(officeMaterialBean.getName()+"("+officeMaterialBean.getUnit()+")");
			holder.applyNumberView.setText(officeMaterialBean.getApplyingNumber().toString());
			holder.deleteView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mSelectedOfficeMaterialList.remove(officeMaterialBean);
					mOfficeMaterialAppliedBaseAdapter.notifyDataSetChanged();
				}
			});
			holder.minusView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(officeMaterialBean.getApplyingNumber() >0){
						officeMaterialBean.setApplyingNumber(officeMaterialBean.getApplyingNumber()-1);
						holder.applyNumberView.setText(officeMaterialBean.getApplyingNumber().toString());
					}
				}
			});
			holder.plusView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					officeMaterialBean.setApplyingNumber(officeMaterialBean.getApplyingNumber()+1);
					holder.applyNumberView.setText(officeMaterialBean.getApplyingNumber().toString());
				}
			});
			return convertView;
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {
			case REQUEST_CODE_SELECT_OFFICE_MATERIAL:
				//
				//				List<OfficeMaterialBean> addedOfficeMaterialList = (List<OfficeMaterialBean>) data.getSerializableExtra("addMaterialList");
				//				List<OfficeMaterialBean> deletedOfficeMaterialList = (List<OfficeMaterialBean>) data.getSerializableExtra("deleteMaterialList");
				//				for(OfficeMaterialBean officeMaterialBean:addedOfficeMaterialList){
				//					if(!mSelectedOfficeMaterialList.contains(officeMaterialBean)){
				//						mSelectedOfficeMaterialList.add(officeMaterialBean);
				//					}
				//				}
				//				for(OfficeMaterialBean officeMaterialBean:deletedOfficeMaterialList){
				//					if(mSelectedOfficeMaterialList.contains(officeMaterialBean)){
				//						mSelectedOfficeMaterialList.remove(officeMaterialBean);
				//					}
				//				}

				List<OfficeMaterialBean> selectedOfficeMaterialList = (List<OfficeMaterialBean>) data.getSerializableExtra("selectedList");
				mSelectedOfficeMaterialList.clear();
				mSelectedOfficeMaterialList.addAll(selectedOfficeMaterialList);
				Logger.i("AddActivity: mSelectedOfficeMaterialList:" + mSelectedOfficeMaterialList);

				mOfficeMaterialAppliedBaseAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	private void submitApplyOrderHttp(String divisionId,String userId,String remark,String materialIds,String applyingNumbers){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getOfficeMaterialApplyAdd());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair organizationIdPair = new BasicNameValuePair("organizationId", divisionId);
		params.add(organizationIdPair);
		NameValuePair userIdPair = new BasicNameValuePair("userId", userId);
		params.add(userIdPair);
		NameValuePair remarkPair = new BasicNameValuePair("remark", remark);
		params.add(remarkPair);
		NameValuePair materalIdsPair = new BasicNameValuePair("materalIds", materialIds);
		params.add(materalIdsPair);
		NameValuePair countsPair = new BasicNameValuePair("counts", applyingNumbers);
		params.add(countsPair);

		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				Logger.i("result:" + resultResponse);
				ResponseBean<Object> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<Object>>(){}.getType());
				if(responseBean.isSuccess()){
					ToastUtil.showToast(mContext, "提交成功" );

					Intent intent = new Intent();
					intent.putExtra(getString(R.string.call_fragment_key_name), getString(R.string.call_fragment_office_material_apply_name));
					intent.putExtra(getString(R.string.call_fragment_key_code), OfficeMaterialApplyFragment.CALL_CODE_APPLYING_REFRESH);
					setResult(RESULT_OK,intent);
					finish();
				}
				else{
					ToastUtil.showToast(mContext, "提交失败：" + responseBean.getMessage());
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



	private void submit(){
		String divisionId = application.getLoginInfoBean().getDivisionId();
		//Fix分支，代码已删除
		if(divisionId == null || divisionId.trim().length() == 0){
			ToastUtil.showToast(mContext, "无法获取您所在的科室信息，无法提交");
			return;
		}
		Integer userId = application.getLoginInfoBean().getUserId();
		if(userId == null || userId.intValue() == 0){
			ToastUtil.showToast(mContext, "无法获取您的信息，无法提交");
			return;
		}
		if(mSelectedOfficeMaterialList.isEmpty()){
			ToastUtil.showToast(mContext, "您还没有选择申领的材料");
			return;
		}
		StringBuilder materialIds =new StringBuilder();
		StringBuilder applyingNumbers =new StringBuilder();
		for(OfficeMaterialBean officeMaterialBean:mSelectedOfficeMaterialList){
			if(officeMaterialBean.getApplyingNumber().intValue() == 0){
				ToastUtil.showToast(mContext, getString(R.string.format_office_material_apply_material_zero_number,officeMaterialBean.getName()));
				return;
			}
			materialIds.append("," +officeMaterialBean.getId());
			applyingNumbers.append("," +officeMaterialBean.getApplyingNumber().toString());
		}
		String remark = mRemark.getText().toString();
		submitApplyOrderHttp(divisionId, userId.toString(), remark, materialIds.substring(1), applyingNumbers.substring(1));
	}
	
	private int selectedItemLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_listview_item_office_material_applying_list;

		default:
			return R.layout.listview_item_office_material_applying_list;
		}
	}

	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_office_material_apply_add;

		default:
			return R.layout.activity_office_material_apply_add;
		}
	}
}
