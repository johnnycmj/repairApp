package com.wutong.repair.activity;

import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.content.Context;
import android.content.Intent;

public class WelcomeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		isNeedLogin = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		setStatPageName(mContext, R.string.page_name_activity_welcome);
		checkIsLogin();
	}



	private void checkIsLogin(){
		StartInitAsyncTask startInitAsyncTask = new StartInitAsyncTask();
		startInitAsyncTask.execute();
	}


	private class StartInitAsyncTask extends AsyncTask<Void, Void, Integer>{

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			Intent intent = new Intent(mContext,LoginActivity.class);
			startActivity(intent);
			WelcomeActivity.this.finish();
		}
	}


	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			ImageView splash = (ImageView)findViewById(R.id.splash);
			if(splash != null){
				//Fix分支
					splash.setImageResource(R.drawable.graduation_splash);

			}
			break;

		default:
			break;
		}
	}

}
