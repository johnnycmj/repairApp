package com.wutong.repair.activity;


import java.io.File;

import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repairfjnu.R;
import com.wutong.repair.RepairApplication;
import com.wutong.repair.data.bean.UpdateBean;
import com.wutong.repair.util.ApplicationInfoUtil;
import com.wutong.repair.util.DownSingleUtil;
import com.wutong.repair.util.InternetConnUtil;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;
import com.wutong.repair.util.HttpResponseUtil;
import com.wutong.repair.util.TimeUtil;
import com.wutong.repair.util.DownSingleUtil.OnUpdateProgressListener;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @author Jolly
 * <pre>需要传入 SerializableExtra  {@link UpdateBean}</pre>
 */
public class UpdateDownloadDialogActivity extends Activity {
	private TextView mVersionTitleView;
	private TextView mVersionNameView;
	private TextView mVersionLogView;
	private Button mDownloadView;
	private Button mCancelView;
	private Button mCloseView;
	private Context mContext;
	private ProgressBar downloadProgressBar;
	private UpdateBean updateBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_update_download_dialog);
		mVersionTitleView = (TextView) findViewById(R.id.update_download_title);
		mVersionTitleView.setText(getString(R.string.app_name)+getString(R.string.title_activity_update_download_dialog));
		Intent intent = getIntent();
		updateBean = (UpdateBean) intent.getSerializableExtra("updateBean");
		mVersionNameView = (TextView) findViewById(R.id.version_name);
		mVersionLogView = (TextView) findViewById(R.id.version_log);
		mDownloadView = (Button) findViewById(R.id.update_download);
		mCancelView = (Button) findViewById(R.id.update_cancel);
		mCloseView = (Button) findViewById(R.id.update_close);
		downloadProgressBar = (ProgressBar) findViewById(R.id.download_progressbar);
		mVersionNameView.setText(updateBean.getVersionName());
		mVersionLogView.setText(updateBean.getVersionLog());
		mDownloadView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask();
				downloadAsyncTask.execute(updateBean.getDownloadUrl());
			}
		});

		if(updateBean.isMustUpdate(ApplicationInfoUtil.getVersionName(mContext))){
			//强制升级
			mCloseView.setVisibility(ViewGroup.VISIBLE);
			mCloseView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//
					exit();
				}
			});
		}
		else{
			//建议升级
			mCancelView.setVisibility(ViewGroup.VISIBLE);
			mCancelView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					UpdateDownloadDialogActivity.this.finish();

				}
			});
		}

	}

	private class DownloadAsyncTask extends AsyncTask<String, Integer, Integer>{
		private Exception exception;
		@Override
		protected void onPreExecute() {
			if(!InternetConnUtil.isHaveInternet(mContext)){
				//				ToastUtil.showToast(mContext, "无法连接网络，请打开wifi或者3g");
				Logger.w("检查更新检测状态：" +"网络已断开"+ TimeUtil.currentTime());
			}
			else{
				super.onPreExecute();
				downloadProgressBar.setVisibility(ViewGroup.VISIBLE);

			}
		}
		@Override
		protected Integer doInBackground(String... params) {
			String saveTemp = SettingConfig.getUpdateFilePath();
			DownSingleUtil downSingleUtil = new DownSingleUtil(params[0], saveTemp);
			downSingleUtil.setOnUpdateProgressListener(new OnUpdateProgressListener() {

				@Override
				public void onUpdateProgress(int doneLength,int contentLength) {
					downloadProgressBar.setMax(contentLength);
					downloadProgressBar.setProgress(doneLength);

				}
			});
			try {
				File file = new File(saveTemp);
				if(!file.exists()){
					file.createNewFile();
				}
				boolean result = downSingleUtil.download();
				if(result){
					return 1;
				}
				else{
					return 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
				exception = e;
				return -1;
			}
		}
		@Override
		protected void onPostExecute(Integer result) {
			downloadProgressBar.setVisibility(ViewGroup.GONE);
			switch (result.intValue()) {
			case 1:
				ToastUtil.showToast(mContext, "下载完成");
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + SettingConfig.getUpdateFilePath()), "application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				UpdateDownloadDialogActivity.this.finish();
				break;

			case 0:
				ToastUtil.showToast(mContext, "下载失败");

				break;
			case -1:
				HttpResponseUtil.justToast(exception, mContext);
				break;
			}
		}

	}

	/**
	 * 退出
	 */
	private void exit(){
		Logger.i("exit");
//		System.exit(0);
		this.sendBroadcast(new Intent("com.wutong.kill"));
		finish();

	}

}
