package com.wutong.repair.activity;

import com.wutong.repair.BaseFragmentActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.util.CommonOperateUtil;
import com.wutong.repair.util.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ExtraModularActivity extends BaseFragmentActivity {

	private String outFragmentLabel;
	
	private String mCallFragmentKeyName;
	private int mCallFragmentKeyCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.page_name_activity_extra_modular);
		dataInit();
		intentInit();
		Fragment fragment = application.getModularFragmentManager().getFragmentByLabel(outFragmentLabel);
		Bundle arguments = fragment.getArguments();
		Bundle extras = new Bundle(getIntent().getExtras());
		if(arguments != null){
			extras.putAll(arguments);
		}
		fragment.setArguments(extras);
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.extra_modular_content,fragment).commit();
	}
	
	@Override
	public void onBackPressed() {
		CommonOperateUtil.hideIME(this);
		if(mCallFragmentKeyName != null){
			Intent intent =new Intent();
			intent.putExtra(getString(R.string.call_fragment_key_name), mCallFragmentKeyName);
			intent.putExtra(getString(R.string.call_fragment_key_code), mCallFragmentKeyCode);
			setResult(RESULT_OK,intent);
		}
		super.onBackPressed();
	}

	private void dataInit(){
		getIntent().putExtra("titlebar_back_is_show", true);
	}

	private void intentInit(){
		outFragmentLabel = getIntent().getStringExtra("fragmentLabel");
	}
	
	public void setCallFragmentKeyName(String callFragmentKeyName) {
		this.mCallFragmentKeyName = callFragmentKeyName;
	}

	public void setCallFragmentKeyCode(int callFragmentKeyCode) {
		this.mCallFragmentKeyCode = callFragmentKeyCode;
	}

	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_extra_modular;

		default:
			return R.layout.activity_extra_modular;
		}
	}

}
