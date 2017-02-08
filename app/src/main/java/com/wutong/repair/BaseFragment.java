package com.wutong.repair;

import com.wutong.repair.util.Logger;
import com.wutong.repair.util.StatUtil;
import com.wutong.repairfjnu.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseFragment extends Fragment {
	protected String mTitle;
	protected boolean isSubFragment;//是否为fragment嵌套中的fragment
	protected String mFragmentPageName = "[父片段]";
	protected RepairApplication application;
	protected Context mContext;
	protected BaseFragmentActivity mActivity;
	protected View mFragmentView;
	protected Dialog mDialog;//数据处理进度暂停弹框
	private TextView progressMessageView;//进度框的文字控件


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		mActivity = (BaseFragmentActivity) this.getActivity();
		application = (RepairApplication) this.getActivity().getApplication();

		//ProgressDialog 效果
		View progressMessageContentView = LayoutInflater.from(mContext).inflate(R.layout.progress_dialog_view, null);
		progressMessageView = (TextView) progressMessageContentView.findViewById(R.id.progress_message);
		mDialog = new Dialog(mContext, R.style.progress_dialog);
		mDialog.setCancelable(false);
		mDialog.setContentView(progressMessageContentView);
	}

	protected View findViewById (int id){
		if(mFragmentView != null){
			return mFragmentView.findViewById(id);
		}
		else{
			Logger.e("mFragmentView is null please setFragmentView(fragmentView) before");
			return null;
		}

	}


	public void setFragmentPageName(String fragmentPageName) {
		this.mFragmentPageName = fragmentPageName;
	}

	public void setFragmentPageName(Context context,int statPageNameId) {
		this.mFragmentPageName = context.getString(statPageNameId);
	}
	protected void showProgressDialog(){
		showProgressDialog(getString(R.string.tips_for_loading));
		
	}
	protected void showProgressDialog(int resId){
		showProgressDialog(getString(resId));
		
	}
	
	protected void showProgressDialog(String message){
		if(!mDialog.isShowing()){
			progressMessageView.setText(message);
			mDialog.show();
		}

	}
	protected void hideProgressDialog(){
		if(mDialog.isShowing()){
			mDialog.dismiss();
		}
	}

	protected void setFragmentView(View fragmentView) {
		this.mFragmentView = fragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Logger.i(" Fragment->onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		Logger.i(" Fragment->onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDestroy() {
		Logger.i(" Fragment->onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		Logger.i(" Fragment->onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		Logger.i(" Fragment->onPause");
		super.onPause();
		if(!isSubFragment){
			StatUtil.onPageEnd(this.getActivity(), mFragmentPageName + this.getClass().getName().substring(26));
			Logger.i("fragment:" +this.getClass().getName() +"onPageEnd:" +mFragmentPageName);
		}
	}

	@Override
	public void onResume() {
		Logger.i(" Fragment->onResume");
		super.onResume();
		if(!isSubFragment){
			StatUtil.onPageStart(this.getActivity(), mFragmentPageName + this.getClass().getName().substring(26));
			Logger.i("fragment:" +this.getClass().getName() +"onPageStart:" +mFragmentPageName);
		}
	}

	@Override
	public void onStart() {
		Logger.i(" Fragment->onStart");
		super.onStart();
		changeBackgroud();
		changeSkin();
	}

	@Override
	public void onStop() {
		Logger.i(" Fragment->onStop");
		super.onStop();
	}
	protected void changeBackgroud(){
		switch (application.getSkinType()) {
		case 1:
			View headTitlebarView = findViewById(R.id.head_titlebar_layout);
			if(headTitlebarView != null){
				headTitlebarView.setBackgroundResource(R.drawable.labor_title_bg);
			}
			break;

		default:
			break;
		}
	}
	
	protected void changeSkin(){
		
	}

}
