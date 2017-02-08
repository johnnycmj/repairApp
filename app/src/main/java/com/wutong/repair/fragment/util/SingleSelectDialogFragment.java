package com.wutong.repair.fragment.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;

import com.wutong.repairfjnu.R;
import com.wutong.repair.util.Logger;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SingleSelectDialogFragment extends DialogFragment{
	
	protected View mFragmentView;
	protected Context mContext;
	private TextView mTitleView;
	private String mTitle;
	private WheelView mSingleSelectWv;
	
	private Button mCancelBtn;
	private Button mOkBtn;
	
	private List<SingleSelectable> mSingleSelectBeanList;
	
	private SingleSelectWheelAdapter mSingleSelectWheelAdapter;
	
	private DataChangeListener mDataChangeListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		this.setStyle(STYLE_NO_TITLE, getTheme());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_single_select, container, false);
		setFragmentView(fragmentView);
		//
		commonInit();
		argumentsInit();
		dataInit();
		viewInit();
		setupData();
		return fragmentView;
	}
	
	private void commonInit(){
		mSingleSelectBeanList = new ArrayList<SingleSelectable>();
	}
	private void argumentsInit(){
		Bundle arguments = getArguments();
		if(arguments != null){
			List<SingleSelectable> tempList = (List<SingleSelectable>) arguments.getSerializable("dataList");
			if(tempList != null){
				mSingleSelectBeanList.clear();
				mSingleSelectBeanList.addAll(tempList);
			}
			mTitle = arguments.getString("title");
		}
		
	}

	private void dataInit(){

	}
	private void viewInit(){

		if(mTitle != null){
			mTitleView = (TextView) findViewById(R.id.title);
			mTitleView.setText(mTitle);
		}

		mSingleSelectWv = (WheelView) findViewById(R.id.single_select_item_wheel);
		mSingleSelectWv.setBackgroundResource(R.drawable.common_wheel_bg);
		mSingleSelectWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		mSingleSelectWheelAdapter = new SingleSelectWheelAdapter();
		mSingleSelectWv.setViewAdapter(mSingleSelectWheelAdapter);
		
		mOkBtn = (Button) findViewById(R.id.positiveButton);
		mCancelBtn = (Button) findViewById(R.id.negativeButton);
		
		mOkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				int index = mSingleSelectWv.getCurrentItem();
				SingleSelectable singleSelectBean = mSingleSelectBeanList.get(index);
				if(mDataChangeListener != null){
					mDataChangeListener.onDataChange(singleSelectBean);
				}
				if(SingleSelectDialogFragment.this.getDialog().isShowing()){
					SingleSelectDialogFragment.this.getDialog().dismiss();
				}
			}
		});
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(SingleSelectDialogFragment.this.getDialog().isShowing()){
					SingleSelectDialogFragment.this.getDialog().dismiss();
				}
			}
		});
		
		
	}
	private void setupData(){
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
	
	public void setDataChangeListener(DataChangeListener dataChangeListener) {
		this.mDataChangeListener = dataChangeListener;
	}

	public interface SingleSelectable  extends Serializable{
		public String getTextName() ;
		public void setTextName(String textName);
		public String getValue();
		public void setValue(String value);
		public String getExtra();
		public void setExtra(String extra);
	}
	
	public static class SimpleSelect implements SingleSelectable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3413213702349321721L;
		private String textName;
		private String value;
		private String extra;
		@Override
		public String getTextName() {
			return textName;
		}
		@Override
		public void setTextName(String textName) {
			this.textName = textName;
		}
		@Override
		public String getValue() {
			return value;
		}
		@Override
		public void setValue(String value) {
			this.value = value;
		}
		@Override
		public String getExtra() {
			return extra;
		}
		@Override
		public void setExtra(String extra) {
			this.extra = extra;
		}
		public SimpleSelect(String textName, String value) {
			super();
			this.textName = textName;
			this.value = value;
		}
		public SimpleSelect(String textName, String value, String extra) {
			super();
			this.textName = textName;
			this.value = value;
			this.extra = extra;
		}
		public SimpleSelect() {
			super();
		}

		
		
	}
	
	private class SingleSelectWheelAdapter implements WheelViewAdapter{

		@Override
		public int getItemsCount() {
			return mSingleSelectBeanList.size();
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.wheel_list_item_common, null);
			TextView textView = (TextView) convertView.findViewById(R.id.wheel_item_text); 
			String name = mSingleSelectBeanList.get(index).getTextName();
			textView.setText(name);
			return convertView;
		}

		@Override
		public View getEmptyItem(View convertView, ViewGroup parent) {
			return null;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {

		}

	}
	
	public interface DataChangeListener{
		public void onDataChange(SingleSelectable itemBean);
	}
}
