package com.wutong.repair.activity;


import com.wutong.repair.BaseFragmentActivity;
import com.wutong.repair.fragment.AboutFragment;
import com.wutong.repairfjnu.R;

import android.os.Bundle;

public class AboutActivity extends BaseFragmentActivity {

	private AboutFragment aboutFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);
		setStatPageName(mContext, R.string.title_activity_about);
		//
		if(aboutFragment == null){
			aboutFragment = new AboutFragment();
			aboutFragment.setArguments(getIntent().getExtras());
		}

		getSupportFragmentManager().beginTransaction()
		.replace(R.id.activity_about_layout,aboutFragment).commit();

	}



}
