package com.wutong.repair.activity;



import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.wutong.repair.BaseFragment;
import com.wutong.repair.BaseFragmentActivity.CallFragmentListener;
import com.wutong.repairfjnu.R;

public class PersonInfoFragment extends BaseFragment {
	
	public final static int CALL_CODE_REFRESH = 1;
	
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;


	private TextView mUserNameView;
	private TextView mRealNameView;
	private TextView mDormNameView;
	private TextView mContactView;



	private View changeContactView;
	private View changePasswordView;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_person_info);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.activity_person_info, container,false);
		setFragmentView(fragmentView);
		//
		titlebarInit();
		viewInit();
		callInit();
		setupData();
		return fragmentView;
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_person_info);
		titlebarTitleView.setVisibility(ViewGroup.VISIBLE);
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
				PersonInfoFragment.this.getActivity().onBackPressed();

			}
		});


	}
	
	private void callInit(){
		mActivity.putCallFragment(getString(R.string.call_fragment_person_info_name), new CallFragmentListener() {
			
			@Override
			public void onCall(int callCode) {
				switch (callCode) {
				case CALL_CODE_REFRESH:
					setupData();
					break;
				
				}
			}
		});
	}

	private void viewInit(){
		mUserNameView = (TextView) findViewById(R.id.person_user_name);
		mRealNameView = (TextView) findViewById(R.id.person_real_name);
		mDormNameView = (TextView) findViewById(R.id.person_info_room_info);
		if(application.getLoginInfoBean().getDormRoomId().equals("0")){
			View dormLayout = findViewById(R.id.dorm_layout);
			dormLayout.setVisibility(ViewGroup.GONE);
		}
		mContactView = (TextView) findViewById(R.id.person_info_contact_tel);
		changeContactView = findViewById(R.id.person_info_contact_change);
		changePasswordView = findViewById(R.id.person_info_password_change);
		changeContactView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeContact();

			}
		});
		changePasswordView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changePassword();

			}
		});
	}

	private void setupData(){
		mUserNameView.setText(application.getLoginInfoBean().getUserName());
		mRealNameView.setText(application.getLoginInfoBean().getRealName());
		mDormNameView.setText(application.getLoginInfoBean().getDormRoomName());
		mContactView.setText(application.getLoginInfoBean().getPhone());
	}




	private void changeContact(){
		//弹窗修改联系方式

		Intent intent = new Intent(mContext, EditInfoActivity.class);
		intent.putExtra("fragmentLabel", getString(R.string.fragment_label_change_contact));
		intent.putExtra("contact", mContactView.getText().toString());
		
		startActivityForResult(intent, 0);
	}

	private void changePassword(){
		//跳转修改密码
		Intent intent = new Intent(mContext, EditInfoActivity.class);
		intent.putExtra("fragmentLabel", getString(R.string.fragment_label_change_password));
		startActivityForResult(intent, 0);
	}




	

}
