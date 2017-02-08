package com.wutong.repair.activity;

import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.util.CommonOperateUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QueryRedoActivity extends BaseActivity {
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;
	
	private EditText mReasonView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.title_activity_query_redo);
		titlebarInit();
		viewInit();
	}
	
	@Override
	public void onBackPressed() {
		CommonOperateUtil.hideIME(this);
		super.onBackPressed();
	}
	
	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_query_redo);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				QueryRedoActivity.this.onBackPressed();

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
		mReasonView = (EditText) findViewById(R.id.query_redo_reason);
	}

	private void submit(){
		String content = mReasonView.getText().toString();
		
		Intent intent = new Intent();
		intent.putExtra("reason", content);
		setResult(RESULT_OK, intent);
		finish();
	}


	@Override
	protected void changeSkin() {
		super.changeSkin();
		((LinearLayout)mReasonView.getParent()).setBackgroundResource(R.drawable.spring_horse_edittext_backgroud);
		mReasonView.setTextColor(getResources().getColor(R.color.spring_horse_repair_order_text_area_text_color));
	}
	
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_query_redo;

		default:
			return R.layout.activity_query_redo;
		}
	}
	
}
