package com.wutong.repair.activity;

import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;

import android.app.Activity;
import android.os.Bundle;

public class FullExitActivity  extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setStatPageName(mContext, R.string.page_name_activity_full_exit);
		this.finish();
	}

}
