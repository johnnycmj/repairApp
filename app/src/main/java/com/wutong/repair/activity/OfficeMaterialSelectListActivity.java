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
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.OfficeMaterialBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class OfficeMaterialSelectListActivity extends BaseActivity {
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;

	private ListView mOfficeMaterialLv;
	private OfficeMaterialSelectBaseAdapter mOfficeMaterialSelectBaseAdapter;
	private List<OfficeMaterialBean> mOfficeMaterialBeanList;
	private List<OfficeMaterialBean> mSelectedOfficeMaterialList;//选中的办公材料
	private View footerView;
	private View mFooterNoMoreView;
	private boolean isFooterAdded = true;

	private List<OfficeMaterialBean> addedOfficeMaterialList;
	private List<OfficeMaterialBean> deletedOfficeMaterialList;

	private int currentStart = 0;
	
	private int lastItemIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_office_material_select_list);
		dataInit();
		intentInit();
		titlebarInit();
		viewInit();
		setupData();

	}
	private void dataInit(){
		addedOfficeMaterialList = new ArrayList<OfficeMaterialBean>();
		deletedOfficeMaterialList = new ArrayList<OfficeMaterialBean>();
		mOfficeMaterialBeanList = new ArrayList<OfficeMaterialBean>();
		mSelectedOfficeMaterialList = new ArrayList<OfficeMaterialBean>(); 
		mOfficeMaterialSelectBaseAdapter = new OfficeMaterialSelectBaseAdapter();
	}
	private void intentInit(){
		List<OfficeMaterialBean> tempMaterialList = (List<OfficeMaterialBean>) getIntent().getSerializableExtra("selectedList");
		mSelectedOfficeMaterialList.addAll(tempMaterialList);
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_office_material_select_list);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OfficeMaterialSelectListActivity.this.onBackPressed();

			}
		});
		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				//				bundle.putSerializable("addMaterialList", (Serializable) addedOfficeMaterialList);
				//				bundle.putSerializable("deleteMaterialList", (Serializable) deletedOfficeMaterialList);
				bundle.putSerializable("selectedList", (Serializable) mSelectedOfficeMaterialList);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void viewInit(){
		mOfficeMaterialLv = (ListView) findViewById(R.id.office_material_apply_material_list);
		footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_common_no_more, null);
		mFooterNoMoreView = footerView.findViewById(R.id.common_footer_no_more);
		mFooterNoMoreView.setVisibility(ViewGroup.VISIBLE);
		mOfficeMaterialLv.addFooterView(footerView);
		mOfficeMaterialLv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lastItemIndex == mOfficeMaterialSelectBaseAdapter.getCount() - 1) {
					loadMoreMaterialList();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItemIndex = firstVisibleItem + visibleItemCount - 1;//footer

			}
		});
		mOfficeMaterialLv.setAdapter(mOfficeMaterialSelectBaseAdapter);
	}

	private void setupData(){
		currentStart = 0;
		loadMoreMaterialList();
	}


	static class OfficeMaterialViewHolder{
		CheckBox checkView;
		TextView materialNameView;
		TextView materialUnitView;
		TextView materialBrandView;
		TextView materialSpecView;
	}

	private class OfficeMaterialSelectBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mOfficeMaterialBeanList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final OfficeMaterialBean officeMaterialBean = mOfficeMaterialBeanList.get(position);
			OfficeMaterialViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
				holder = new OfficeMaterialViewHolder();
				holder.checkView = (CheckBox) convertView.findViewById(R.id.office_material_material_is_checked);
				holder.materialNameView = (TextView) convertView.findViewById(R.id.office_material_material_name);
				holder.materialBrandView = (TextView) convertView.findViewById(R.id.office_material_brand);
				holder.materialUnitView = (TextView) convertView.findViewById(R.id.office_material_material_unit);
				holder.materialSpecView = (TextView) convertView.findViewById(R.id.office_material_spec);
				convertView.setTag(holder);
			}
			else{
				holder = (OfficeMaterialViewHolder) convertView.getTag();
			}
			holder.materialNameView.setText(officeMaterialBean.getName());
			holder.materialUnitView.setText("("+officeMaterialBean.getUnit()+")");
			holder.materialBrandView.setText(officeMaterialBean.getBrand());
			holder.materialSpecView.setText(officeMaterialBean.getCodeIndex());
			holder.checkView.setOnCheckedChangeListener(null);
			if(mSelectedOfficeMaterialList.contains(officeMaterialBean)){
				holder.checkView.setChecked(true);
			}
			else{
				holder.checkView.setChecked(false);
			}
			holder.checkView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						if(!mSelectedOfficeMaterialList.contains(officeMaterialBean)){
							mSelectedOfficeMaterialList.add(officeMaterialBean);
							Logger.i("isChecked and add:" + officeMaterialBean);
							Logger.i("mSelectedOfficeMaterialList:" + mSelectedOfficeMaterialList);
						}
					}
					else{
						if(mSelectedOfficeMaterialList.contains(officeMaterialBean)){
							mSelectedOfficeMaterialList.remove(officeMaterialBean);
							Logger.i("unChecked and remove:" + officeMaterialBean);
							Logger.i("mSelectedOfficeMaterialList:" + mSelectedOfficeMaterialList);
						}
					}
				}
			});
			return convertView;
		}

	}

	private void loadMoreMaterialList(){

		loadMaterialListHttp(String.valueOf(currentStart), String.valueOf(application.getPagingSize()));
	}

	/**
	 * 
	 */
	private void loadMaterialListHttp(String start,String limit){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getOfficeMaterialApplyLoadMaterialList());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair startPair = new BasicNameValuePair("start", start);
		params.add(startPair);
		NameValuePair limitPair = new BasicNameValuePair("limit", limit);
		params.add(limitPair);

		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				hideProgressDialog();
				ResponseBean<List<OfficeMaterialBean>> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<List<OfficeMaterialBean>>>(){}.getType());
				if(responseBean.isSuccess()){
					List<OfficeMaterialBean> resultList = responseBean.getData();
					if(resultList == null){
						ToastUtil.showToast(mContext, R.string.error_data_null);
						return;
					}
					else{
						mOfficeMaterialBeanList.addAll(resultList);
						currentStart += application.getPagingSize();
						
					
						
						if(resultList.size() < application.getPagingSize()){
							//
							if(!isFooterAdded){
								mOfficeMaterialLv.addFooterView(footerView);
								isFooterAdded = true;
							}
						}
						else{
							if(isFooterAdded){
								mOfficeMaterialLv.removeFooterView(footerView);
								isFooterAdded = false;
							}
						}
						mOfficeMaterialSelectBaseAdapter.notifyDataSetChanged();
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
				showProgressDialog("获取记录中");
			}
		});
		httpAsyncTask.execute(httpFormBean);
	}
	private int selectedItemLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_listview_item_office_material_select_list;

		default:
			return R.layout.listview_item_office_material_select_list;
		}
	}
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_office_material_select_list;

		default:
			return R.layout.activity_office_material_select_list;
		}
	}
}
