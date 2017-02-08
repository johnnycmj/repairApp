package com.wutong.repair.fragment;



import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wutong.repair.BaseFragment;
import com.wutong.repair.activity.ExtraModularActivity;
import com.wutong.repairfjnu.R;

/**
 * 微生活
 * @author Jolly
 * 创建时间：2014年5月4日下午3:24:22
 *
 */
public class MicroActivityFragment extends BaseFragment {


	private TextView titlebarTitleView;

	private ImageView mCurrentPosterView;//海报
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_micro_activity, container,false);
		setFragmentView(fragmentView);
		dataInit();
		argumentInit();
		titlebarInit();
		viewInit();
		return fragmentView;
	}

	

	private void dataInit(){
		
	}

	private void argumentInit(){

	}

	private void titlebarInit(){
		titlebarTitleView = (TextView) findViewById(R.id.titlebar_title);
		titlebarTitleView.setText(getString(R.string.title_fragment_micro_activity));
	}

	private void viewInit(){
		setFragmentPageName(mContext, R.string.title_fragment_micro_activity);
		
		mCurrentPosterView = (ImageView) findViewById(R.id.micro_activity_current_poster);
		
		mCurrentPosterView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ExtraModularActivity.class);
				intent.putExtra("fragmentLabel", getString(R.string.fragment_label_micro_activity_list));
				startActivity(intent);
			}
		});
	}



	

}
