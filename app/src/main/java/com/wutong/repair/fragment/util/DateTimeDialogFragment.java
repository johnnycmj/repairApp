package com.wutong.repair.fragment.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;






import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repairfjnu.R;
import com.wutong.repair.util.Logger;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 时间选择器
 * @author Jolly
 * 创建时间：2014年3月18日下午1:40:25
 *
 */
public class DateTimeDialogFragment extends DialogFragment{
	protected View mFragmentView;
	protected Context mContext;
	private Calendar initCalendar;

	private WheelView mYearWv;
	private WheelView mMonthWv;
	private WheelView mDayWv;
	
	private WheelView mHourWv;
	private WheelView mMinuteWv;
	

	private Button mCancelBtn;
	private Button mOkBtn;

	private int currentYear;
	private int minYear = 1900;
	private int maxDay = 30;

	private DateListener mDateListener;
	
	public static SimpleDateFormat sdf;
	
	private int mCurrentYear;
	private int mCurrentMonth;
	
	private int outSelectType = 0;//0为都可选，1为给定时间之前，2为给定时间之后

	public void setDateListener(DateListener dateListener) {
		this.mDateListener = dateListener;
	}
	
	static{
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		this.setStyle(STYLE_NO_TITLE, getTheme());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_date_time, container, false);
		setFragmentView(fragmentView);
		//
		argumentsInit();
		dataInit();
		viewInit();
		setupData();
		return fragmentView;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return super.onCreateDialog(savedInstanceState);
	}

	private void argumentsInit(){
		if(this.getArguments() != null){
			String datetime = this.getArguments().getString("datetime");
			boolean isAfter = this.getArguments().getBoolean("isAfter");
			boolean isBefore = this.getArguments().getBoolean("isBefore");
			outSelectType =isAfter?2:0;
			if(outSelectType == 0){
				outSelectType =isBefore?1:0;
			}
			if(datetime != null){
				try {
					Date date = sdf.parse(datetime);
					initCalendar = Calendar.getInstance();
					initCalendar.setTime(date);
				} catch (ParseException e) {
					initCalendar = Calendar.getInstance();
				}

			}
		}
	}

	private void dataInit(){

	}
	private void viewInit(){
		

		currentYear = Calendar.getInstance().get(Calendar.YEAR);
		mYearWv = (WheelView) findViewById(R.id.date_time_year);
		mMonthWv = (WheelView) findViewById(R.id.date_time_month);
		mDayWv = (WheelView) findViewById(R.id.date_time_day);
		mHourWv = (WheelView) findViewById(R.id.date_time_hour);
		mMinuteWv = (WheelView) findViewById(R.id.date_time_minus);
		
		mYearWv.setBackgroundResource(R.drawable.common_wheel_bg);
		mYearWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		mMonthWv.setBackgroundResource(R.drawable.common_wheel_bg);
		mMonthWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		mDayWv.setBackgroundResource(R.drawable.common_wheel_bg);
		mDayWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		mHourWv.setBackgroundResource(R.drawable.common_wheel_bg);
		mHourWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		mMinuteWv.setBackgroundResource(R.drawable.common_wheel_bg);
		mMinuteWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		
		mYearWv.setViewAdapter(new DateTimeNumericWheelAdapter(mContext, currentYear, currentYear+80));
		mMonthWv.setViewAdapter(new DateTimeNumericWheelAdapter(mContext, 1, 12,"%02d"));
		mDayWv.setViewAdapter(new DateTimeNumericWheelAdapter(mContext, 1, maxDay,"%02d"));
		mHourWv.setViewAdapter(new DateTimeNumericWheelAdapter(mContext, 0, 23,"%02d时"));
		mMinuteWv.setViewAdapter(new DateTimeNumericWheelAdapter(mContext, 0, 59,"%02d分"));
		
		
		mOkBtn = (Button) findViewById(R.id.positiveButton);
		mCancelBtn = (Button) findViewById(R.id.negativeButton);
		
		mOkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int year = currentYear + mYearWv.getCurrentItem();
				int month = mMonthWv.getCurrentItem();
				int day = mDayWv.getCurrentItem()+1;
				int hour = mHourWv.getCurrentItem();
				int minute = mMinuteWv.getCurrentItem();
				Calendar selectCalendar = Calendar.getInstance();
				//检验当前时间
				switch (outSelectType) {
				case 2://必须是之后的时候
					selectCalendar.set(year, month, day, hour, minute, 0);
					if(!selectCalendar.after(Calendar.getInstance())){
						ToastUtil.showToast(mContext, "时间必须是当前时间之后");
						return;
					}
					
				case 1:
					selectCalendar.set(year, month, day, hour, minute, 0);
					if(!selectCalendar.before(Calendar.getInstance())){
						ToastUtil.showToast(mContext, "时间必须是当前时间之前");
						return;
					}
				default:
					break;
				}
				
				Logger.i("year:" +year+"month:" + month +"day:" +day+"hour:" + hour +"minute:" +minute);
				mDateListener.onDateSelect(year, month, day,hour,minute);
				if(DateTimeDialogFragment.this.getDialog().isShowing()){
					DateTimeDialogFragment.this.getDialog().dismiss();
				}
			}
		});
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(DateTimeDialogFragment.this.getDialog().isShowing()){
					DateTimeDialogFragment.this.getDialog().dismiss();
				}
			}
		});
		
		mYearWv.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mCurrentYear = minYear + newValue;
				offsetDayMaxValue();
			}
		});
		mMonthWv.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				mCurrentMonth = newValue +1;
				offsetDayMaxValue();
			}
		});
	}
	private void setupData(){
		if(initCalendar != null){
			mYearWv.setCurrentItem(initCalendar.get(Calendar.YEAR)-minYear);
			mMonthWv.setCurrentItem(initCalendar.get(Calendar.MONTH));
			mDayWv.setCurrentItem(initCalendar.get(Calendar.DAY_OF_MONTH)-1);
			
			mHourWv.setCurrentItem(initCalendar.get(Calendar.HOUR_OF_DAY));
			mMinuteWv.setCurrentItem(initCalendar.get(Calendar.MINUTE));
		}
	}




	protected View findViewById(int id) {
		if (mFragmentView != null) {
			return mFragmentView.findViewById(id);
		} else {
			Logger.e("mFragmentView is null please setFragmentView(fragmentView) before");
			return null;
		}

	}


	protected void setFragmentView(View fragmentView) {
		this.mFragmentView = fragmentView;
	}


	class BirthdayException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6162052059865024150L;

		public BirthdayException() {
			super();
		}

		public BirthdayException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

		public BirthdayException(String detailMessage) {
			super(detailMessage);
		}

		public BirthdayException(Throwable throwable) {
			super(throwable);
		}

	}
	
	public interface DateListener{
		public void onDateSelect(int year, int month, int day, int hour, int minute);
	}
	
	/**
	 * 偏正
	 */
	private void offsetDayMaxValue(){
		switch (mCurrentMonth) {
		case 1:case 3:case 5:case 7:case 8:case 10:case 12:
			maxDay = 31;
			break;
		case 4:case 6:case 9:case 11:
			maxDay = 30;
			break;
		case 2:
			maxDay = 28 +((mCurrentYear % 400 == 0 || mCurrentYear % 100 != 0 && mCurrentYear % 4 == 0)?1:0);
			
			
			
			break;
		default:
			break;
		}
		int oldDayIndex = mDayWv.getCurrentItem();
		mDayWv.setViewAdapter(new DateTimeNumericWheelAdapter(mContext, 1, maxDay,"%02d"));
		if(oldDayIndex >= maxDay){
			mDayWv.setCurrentItem(maxDay-1);
		}
	}
	
	private class DateTimeNumericWheelAdapter extends NumericWheelAdapter{

		public DateTimeNumericWheelAdapter(Context context) {
			super(context);
			setTextColor(getResources().getColor(R.color.spring_horse_common_normal_level_text_color));
			setTextSize(getResources().getDimensionPixelSize(R.dimen.date_time_wheel_view_item_text_size));
		}

		public DateTimeNumericWheelAdapter(Context context, int minValue,
				int maxValue, String format) {
			super(context, minValue, maxValue, format);
			setTextColor(getResources().getColor(R.color.spring_horse_common_normal_level_text_color));
			setTextSize(getResources().getDimensionPixelSize(R.dimen.date_time_wheel_view_item_text_size));
		}

		public DateTimeNumericWheelAdapter(Context context, int minValue,
				int maxValue) {
			super(context, minValue, maxValue);
			setTextColor(getResources().getColor(R.color.spring_horse_common_normal_level_text_color));
			setTextSize(getResources().getDimensionPixelSize(R.dimen.date_time_wheel_view_item_text_size));
		}
		
	}
}
