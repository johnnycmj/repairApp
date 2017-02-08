package com.wutong.repair.activity;

import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.util.CommonOperateUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HowRepairActivity extends BaseActivity {

	private ImageView mTitlebarBackIv;
	private TextView mTitlebarTitleTv;
	private ImageView mTitlebarSubmitIv;

	private EditText mHowRepairContentEt;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_how_repair);
		titlebarInit();
		viewInit();
	}
	
	@Override
	public void onBackPressed() {
		CommonOperateUtil.hideIME(this);
		super.onBackPressed();
	}

	private void titlebarInit(){
		mTitlebarBackIv = (ImageView) findViewById(R.id.titlebar_back);
		mTitlebarBackIv.setVisibility(ViewGroup.VISIBLE);
		mTitlebarBackIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HowRepairActivity.this.onBackPressed();
			}
		});

		mTitlebarTitleTv = (TextView) findViewById(R.id.titlebar_title);
		mTitlebarTitleTv.setText(R.string.title_activity_how_repair);
		mTitlebarSubmitIv = (ImageView) findViewById(R.id.titlebar_submit);
		mTitlebarSubmitIv.setVisibility(ViewGroup.VISIBLE);
		mTitlebarSubmitIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();
			}
		});
	}


	private void viewInit(){
		mHowRepairContentEt = (EditText) findViewById(R.id.how_repair_content);

	}

	private void submit(){
		String content = mHowRepairContentEt.getText().toString();
		Intent intent = new Intent();
		intent.putExtra("data", content);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_how_repair;

		default:
			return R.layout.activity_how_repair;
		}
	}
}
