package com.wutong.repair.activity;

import com.wutong.repair.BaseFragmentActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.util.CommonOperateUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class EditInfoActivity extends BaseFragmentActivity {
	
	private String outFragmentLabel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_info);
		setStatPageName(mContext, R.string.page_name_activity_edit_info);
		dataInit();
		intentInit();
		Fragment fragment = application.getModularFragmentManager().getFragmentByLabel(outFragmentLabel);
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.activity_edit_info_layout,fragment).commit();
	}
	@Override
	public void onBackPressed() {
		CommonOperateUtil.hideIME(this);
		super.onBackPressed();
	}
	private void dataInit(){
		getIntent().putExtra("titlebar_back_is_show", true);
	}

	private void intentInit(){
		outFragmentLabel = getIntent().getStringExtra("fragmentLabel");
	}


}
