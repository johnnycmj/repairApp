package com.wutong.repair.activity;


import com.wutong.repair.BaseFragment;
import com.wutong.repair.util.ApplicationInfoUtil;
import com.wutong.repairfjnu.R;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutFragment extends BaseFragment {
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	
	
	private TextView mVersionNameView;
	private TextView mPhoneView;
	private TextView mAppNameView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_about);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_about, container,false);
		setFragmentView(fragmentView);
		//
		titleBarInit();
		viewInit();
		return fragmentView;
	}
	
	private void titleBarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_about);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		boolean isTitlebarBackShow = this.getActivity().getIntent().getBooleanExtra("titlebar_back_is_show", false);
		if(isTitlebarBackShow){
			titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		}
		else{
			titlebarBackView.setVisibility(ViewGroup.GONE);
		}
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AboutFragment.this.getActivity().onBackPressed();

			}
		});


	}
	
	private void viewInit(){
		mVersionNameView = (TextView) findViewById(R.id.version_name);
		mAppNameView = (TextView) findViewById(R.id.app_name);
		String versionName = ApplicationInfoUtil.getVersionName(this.getActivity());
		String version = versionName.split("-")[0];
		mVersionNameView.setText(getString(R.string.format_version_name,version));
		mPhoneView = (TextView) findViewById(R.id.phone);
		mPhoneView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = getString(R.string.tel_customer_service_phone);
				Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phone));  
				startActivity(intent);
			}
		});
		String appName = getString(R.string.app_name);
		mAppNameView.setText(getString(R.string.format_app_name,appName));
		
	}
}
