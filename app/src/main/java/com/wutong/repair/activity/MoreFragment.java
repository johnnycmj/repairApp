package com.wutong.repair.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.gson.reflect.TypeToken;
import com.wutong.androidprojectlibary.http.bean.HttpFormBean;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask;
import com.wutong.androidprojectlibary.http.util.HttpAsyncTask.OnDealtListener;
import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseFragment;
import com.wutong.repair.data.bean.ModularNetworkBean;
import com.wutong.repair.data.bean.ResponseBean;
import com.wutong.repair.data.bean.UpdateBean;
import com.wutong.repair.util.ApplicationInfoUtil;
import com.wutong.repair.util.FileUtil;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;
import com.wutong.repairfjnu.R;

public class MoreFragment extends BaseFragment {
	private TextView titlebarTitleView;
	private String moreTitle = "更多";

	private LinearLayout extraModularContainerView;
	private List<ModularNetworkBean> extraModularList;

	private View feedbackUsView;
	private View cleanCacheView;
	private View checkUpdateView;
	private View aboutView;
	private View logoutView;

	private TextView cacheSizeView;
	private TextView currentVersionView;

	private ProgressDialog cleanProgressDialog;//显示清理进度
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_more);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.vp_av_index_tab_more, container,false);
		setFragmentView(fragmentView);
		//
		titlebarInit();
		viewInit();
		return fragmentView;
	}

	private void titlebarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(moreTitle);

	}

	private void viewInit(){
		extraModularContainerView = (LinearLayout) findViewById(R.id.more_extra_modular_container);
		feedbackUsView = findViewById(R.id.page_more_feedback_us);
		cleanCacheView = findViewById(R.id.page_more_clean_cache);
		checkUpdateView = findViewById(R.id.page_more_check_update);
		aboutView = findViewById(R.id.page_more_about);
		logoutView = findViewById(R.id.logout_btn);
		cacheSizeView = (TextView) findViewById(R.id.page_more_cache_size);
		currentVersionView = (TextView) findViewById(R.id.page_more_version);
		
		feedbackUsView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ExtraModularActivity.class);
				intent.putExtra("fragmentLabel", getString(R.string.modular_url_feedbackus));
				startActivity(intent);

			}
		});
		
		cleanCacheView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cleanCache();

			}
		});

		checkUpdateView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgressDialog("正在检查更新");
				loadCheckUpdate();

			}
		});

		aboutView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ExtraModularActivity.class);
				intent.putExtra("fragmentLabel", getString(R.string.modular_url_about));
				startActivity(intent);

			}
		});

		logoutView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logout();

			}
		});

		

		//
		String verson =getString(R.string.format_page_more_version,ApplicationInfoUtil.getVersionName(mContext));
		currentVersionView.setText(verson);
		queryCacheSize();
		
		//模块
		extraModularList = application.getModularNetworkBeanExtraList();
		
		View modularView;
		TextView textView;
		for(final ModularNetworkBean modularNetworkBean : extraModularList){
			modularView = LayoutInflater.from(mContext).inflate(R.layout.index_more_extra_modular, null);
			textView = (TextView) modularView.findViewById(R.id.more_extra_modular_name);
			modularView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View paramView) {
					Intent intent = new Intent(mContext, ExtraModularActivity.class);
					intent.putExtra("fragmentLabel", modularNetworkBean.getUrl());
					startActivity(intent);
					
				}
			});
			extraModularContainerView.addView(modularView);
			if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_person_info))){
				//个人信息
				textView.setText(R.string.av_index_tab_label_modular_page_more_person_info);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_material_info))){
				//报修材料
				textView.setText(R.string.av_index_tab_label_modular_page_more_repairman_material_info);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_fast_message_setting))){
				//快速反馈设置
				textView.setText(R.string.av_index_tab_label_modular_page_more_repairman_fast_message_setting);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_workbench))){
				//工作台
				textView.setText(R.string.av_index_tab_label_modular_page_more_manager_workbench);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_not_disturb_setting))){
				//免打扰设置
				textView.setText(R.string.av_index_tab_label_modular_page_more_not_disturb_setting);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_notice))){
				//公告
				textView.setText(R.string.av_index_tab_label_modular_page_more_notice);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_complain))){
				//投诉
				textView.setText(R.string.av_index_tab_label_modular_page_more_compliant);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_repair_repair_all))
					|| modularNetworkBean.getUrl().equals(getString(R.string.modular_url_applicant_repair_all))){
				//报修
				textView.setText(R.string.av_index_tab_label_modular_page_more_repair);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_feedbackus))){
				//意见反馈
				textView.setText(R.string.av_index_tab_label_modular_page_more_feedback_us);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_office_material_apply_statistic))){
				//申领管理
				textView.setText(R.string.av_index_tab_label_modular_page_more_office_material_statistic);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_office_material_apply))){
				//行政申领
				textView.setText(R.string.av_index_tab_label_modular_page_more_office_material_apply);
			}
			else{
				textView.setText("*" +modularNetworkBean.getModularName());
			}
		}
		
		
	

	}

	private void queryCacheSize(){
		String cacheExternalStorageDir = Environment.getExternalStorageDirectory().getPath() + SettingConfig.PHOTO_CACHE_DIR;
		Logger.i("cacheExternalStorageDir:"+cacheExternalStorageDir);
		long cacheSize;
		try {
			cacheSize = FileUtil.getInstance().getDirSize(new File(cacheExternalStorageDir));
			Logger.i("cacheSize:"+cacheSize);
			//追加空格修正尾部字不完整
			cacheSizeView.setText(getString(R.string.format_page_more_common_right_fix,FileUtil.getInstance().FormetFileSize(cacheSize)));
		} catch (Exception e) {
			cacheSizeView.setText("获取失败");
			e.printStackTrace();
		}
	}


	/**
	 * 注销
	 */
	private void logout(){
		Builder builder = new Builder(mContext);
		builder.setTitle("确认注销？");
		builder.setPositiveButton("注销", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				application.logoutInit();
				Intent intent = new Intent();
				intent.setClass(mContext, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				MoreFragment.this.getActivity().sendBroadcast(new Intent("com.wutong.logout"));
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();
	}

	/**
	 * 获取更新
	 */
	private void loadCheckUpdate(){
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask(mContext,application.getHttpClientManager());
		HttpFormBean httpFormBean = new HttpFormBean();
		httpFormBean.setUrl(application.getCommonHttpUrlActionManager().getCheckVersionUrl());
		Collection<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair schoolTypePair = new BasicNameValuePair("schoolType", application.getAppFlag());
		params.add(schoolTypePair);
		NameValuePair platformPair = new BasicNameValuePair("platform", "1");
		params.add(platformPair);
		httpFormBean.setParams(params);
		httpAsyncTask.setOnDealtListener(new OnDealtListener() {

			@Override
			public void success(String resultResponse) {
				ResponseBean<UpdateBean> responseBean = application.getGson().fromJson(resultResponse, new TypeToken<ResponseBean<UpdateBean>>(){}.getType());
				UpdateBean updateBean = responseBean.getData();
				if(updateBean == null){
					ToastUtil.showToast(mContext, R.string.error_data_null);
					return;
				}
				if(responseBean.isSuccess()){
					if(ApplicationInfoUtil.getVersionCode(mContext) < Integer.valueOf(updateBean.getVersionCode())){
						//有更新
						Intent intent = new Intent(mContext,UpdateDownloadDialogActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Bundle bundle = new Bundle();
						bundle.putSerializable("updateBean", (Serializable)updateBean);
						intent.putExtras(bundle);
						startActivity(intent);
					}
					else{
						//无更新
						ToastUtil.showToast(mContext, "已经是最新版本！");
					}
				}
				else{
					ToastUtil.showToast(mContext, "检查更新失败，获取数据异常");
				}
				hideProgressDialog();
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

	private void  cleanCache(){
		cleanProgressDialog = new ProgressDialog(mContext);
		String cacheInnerStorageDir = Environment.getExternalStorageDirectory().getPath() + SettingConfig.PHOTO_CACHE_DIR;
		String cacheExternalStorageDir = Environment.getRootDirectory().getPath() + SettingConfig.PHOTO_CACHE_DIR;
		File cacheInnerStorageFile = new File(cacheInnerStorageDir);
		File cacheExternalStorageFile = new File(cacheExternalStorageDir);
		File[] innerStorageFiles = new File[]{};
		File[] externalStorageFiles = new File[]{};
		if(cacheInnerStorageFile.exists() && cacheInnerStorageFile.isDirectory()){
			innerStorageFiles = cacheInnerStorageFile.listFiles();
			cleanProgressDialog.setTitle("清理缓存");
			cleanProgressDialog.setMessage("正在清理缓存");
			cleanProgressDialog.setMax(innerStorageFiles.length);
			cleanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			cleanProgressDialog.show();
			CleanCacheAsyncTask cleanCacheAsyncTask = new CleanCacheAsyncTask();
			cleanCacheAsyncTask.execute(innerStorageFiles);
		}
		if(cacheExternalStorageFile.exists() && cacheExternalStorageFile.isDirectory()){
			externalStorageFiles = cacheExternalStorageFile.listFiles();
			cleanProgressDialog.setTitle("清理缓存");
			cleanProgressDialog.setMessage("正在清理缓存");
			cleanProgressDialog.setMax(externalStorageFiles.length);
			cleanProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			cleanProgressDialog.show();
			CleanCacheAsyncTask cleanCacheAsyncTask = new CleanCacheAsyncTask();
			cleanCacheAsyncTask.execute(externalStorageFiles);
		}
	}

	private final class CleanCacheAsyncTask extends AsyncTask<File, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(File... files) {

			boolean isDelete;
			int index = 0;
			for(File file: files){
				if(!file.isDirectory()){
					isDelete = file.delete();
					Logger.i(file.getPath() +"/isDelete:" + isDelete);
					if(!isDelete){
						return isDelete;
					}
					else{
						publishProgress(index);
					}
				}
				index ++;
			}
			return true;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			cleanProgressDialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(cleanProgressDialog != null && cleanProgressDialog.isShowing()){
				cleanProgressDialog.dismiss();
			}
			if(result){
				ToastUtil.showToast(mContext, "清理成功！");
				//刷新
				queryCacheSize();
			}
			else{
				ToastUtil.showToast(mContext, "清理失败！");
			}
		}

	}

}
