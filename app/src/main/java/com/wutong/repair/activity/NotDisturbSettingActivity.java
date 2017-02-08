package com.wutong.repair.activity;



import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.RepairApplication;
import com.wutong.repair.util.SettingConfig;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;

import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class NotDisturbSettingActivity extends BaseActivity {

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;

	private CheckBox mMondayView;
	private CheckBox mTuesdayView;
	private CheckBox mWednesdayView;
	private CheckBox mThursdayView;
	private CheckBox mFridayView;
	private CheckBox mSaturdayView;
	private CheckBox mSundayView;

	private SeekBar mStartTimeBarView;
	private SeekBar mContinuedTimeBarView;

	private TextView mFullShowView;
	private TextView mStartTimeShowView;
	private TextView mContinuedTimeShowView;


	private SharedPreferences preferences;
	private Editor edit;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_not_disturb_setting);
		setStatPageName(mContext, R.string.title_activity_not_disturb_setting);
		titleBarInit();
		viewInit();
		dataInit();
	}

	private void titleBarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_not_disturb_setting);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NotDisturbSettingActivity.this.onBackPressed();

			}
		});

		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveQuietTime();
			}
		});
	}

	private void viewInit(){
		preferences = ((RepairApplication)getApplicationContext()).getDefaultPreferences();
		edit = preferences.edit();
		mMondayView = (CheckBox)findViewById(R.id.cb_monday);
		mTuesdayView = (CheckBox)findViewById(R.id.cb_tuesday);
		mWednesdayView = (CheckBox)findViewById(R.id.cb_wednesday);
		mThursdayView = (CheckBox)findViewById(R.id.cb_thursday);
		mFridayView = (CheckBox)findViewById(R.id.cb_friday);
		mSaturdayView = (CheckBox)findViewById(R.id.cb_saturday);
		mSundayView = (CheckBox)findViewById(R.id.cb_sunday);
		mStartTimeBarView = (SeekBar) findViewById(R.id.start_time_bar);
		mContinuedTimeBarView = (SeekBar) findViewById(R.id.continued_time_bar);
		mStartTimeShowView = (TextView) findViewById(R.id.start_time_show);
		mContinuedTimeShowView = (TextView) findViewById(R.id.continued_time_show);
		mFullShowView = (TextView) findViewById(R.id.time_full_show);
		mStartTimeBarView.setMax(23);
		mStartTimeBarView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mStartTimeShowView.setText(getString(R.string.format_quiet_start_time, progress));
				mFullShowView.setText(calcQuiteTimeRange(progress, mContinuedTimeBarView.getProgress()));
			}
		});
		mContinuedTimeBarView.setMax(24);
		mContinuedTimeBarView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mContinuedTimeShowView.setText(getString(R.string.format_quiet_continued_time, progress));
				mFullShowView.setText(calcQuiteTimeRange(mStartTimeBarView.getProgress(), progress));
			}
		});
	}

	private String calcQuiteTimeRange(int startTime,int continuedTime){
		String show;
		if(continuedTime == 0){
			//没有免打扰
			show = getString(R.string.quiet_zero_time);
		}
		else if(continuedTime == 24){
			//全天免打扰
			show = getString(R.string.quiet_full_time);
		}
		else if(startTime + continuedTime >= 24){
			//次日
			show = getString(R.string.format_quiet_time_full_show_start_time, startTime) +"-" + getString(R.string.format_quiet_time_full_show_tommrow_time, (startTime + continuedTime -24));
		}
		else{
			//当日
			show = getString(R.string.format_quiet_time_full_show_start_time, startTime) +"-" + getString(R.string.format_quiet_time_full_show_today_end_time, (startTime + continuedTime));
		}
		return show;
	}

	private void saveQuietTime(){
		edit.clear();
		saveWeekday();
		saveQuietPeriod();
		ToastUtil.showToast(mContext, "免打扰时间已经更新");
	}


	private void saveQuietPeriod(){
		//获取时间段
		int startTime = mStartTimeBarView.getProgress();
		int continuedTime = mContinuedTimeBarView.getProgress();
		edit.putString(SettingConfig.QuietTime.SLIENT_PERIOD, startTime + "," + continuedTime);
		edit.commit();
	}

	private void saveWeekday(){
		//获取星期
		String weekdayChecked = "";
		weekdayChecked += mSundayView.isChecked() == true ?"1":"0";
		weekdayChecked += mMondayView.isChecked() == true ?"1":"0";
		weekdayChecked += mTuesdayView.isChecked() == true ?"1":"0";
		weekdayChecked += mWednesdayView.isChecked() == true ?"1":"0";
		weekdayChecked += mThursdayView.isChecked() == true ?"1":"0";
		weekdayChecked += mFridayView.isChecked() == true ?"1":"0";
		weekdayChecked += mSaturdayView.isChecked() == true ?"1":"0";
		edit.putString(SettingConfig.QuietTime.SLIENT_WEEKDAY, weekdayChecked);
		edit.commit();
	}


	private void dataInit(){
		String slientPeriod = preferences.getString(SettingConfig.QuietTime.SLIENT_PERIOD, null);
		if(slientPeriod != null){
			String[] timeArray = slientPeriod.split(",");
			if(timeArray.length == 2){
				int startTime = Integer.valueOf(timeArray[0]);
				int continuedTime = Integer.valueOf(timeArray[1]);
				mStartTimeBarView.setProgress(startTime);
				mContinuedTimeBarView.setProgress(continuedTime);
			}
			else{
				// TODO
				//数据不正常
				defaultPeriodInit();
			}
		}
		else{
			//数据不正常
			defaultPeriodInit();
		}
		String slientWeekday = preferences.getString(SettingConfig.QuietTime.SLIENT_WEEKDAY, null);
		if(slientWeekday != null && slientWeekday.toString().trim().length() == 7){
			String[] slientWeekdayArray = slientWeekday.split("");
			if(slientWeekdayArray.length == 8){
				mSundayView.setChecked(slientWeekdayArray[1].equals("1")?true:false);
				mMondayView.setChecked(slientWeekdayArray[2].equals("1")?true:false);
				mTuesdayView.setChecked(slientWeekdayArray[3].equals("1")?true:false);
				mWednesdayView.setChecked(slientWeekdayArray[4].equals("1")?true:false);
				mThursdayView.setChecked(slientWeekdayArray[5].equals("1")?true:false);
				mFridayView.setChecked(slientWeekdayArray[6].equals("1")?true:false);
				mSaturdayView.setChecked(slientWeekdayArray[7].equals("1")?true:false);
			}
			else{
				defaultWeekdayInit();
			}
		}
		else{
			//数据不正常
			defaultWeekdayInit();
		}
	}

	private void defaultPeriodInit(){
		mStartTimeBarView.setProgress(23);
		mContinuedTimeBarView.setProgress(8);
		saveQuietPeriod();
	}
	private void defaultWeekdayInit(){
		mSundayView.setChecked(true);//星期天打勾
		saveQuietTime();
	}
}
