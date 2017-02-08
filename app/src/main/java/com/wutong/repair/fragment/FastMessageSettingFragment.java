package com.wutong.repair.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.common.widget.CustomDialog;
import com.wutong.repair.BaseFragment;
import com.wutong.repairfjnu.R;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;

public class FastMessageSettingFragment extends BaseFragment {
	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarAddView;
	
	private CustomDialog alertDialog;
	private CustomDialog operateDialog;
	private CustomDialog editDialog;
	private CustomDialog deleteDialog;
	
	private OperateWheelAdapter operateWheelAdapter;
	private View operateContentView;
	private WheelView operateWv;
	
	private View alertView;
	private View editAlertView;
	private EditText fastMessageView;
	private EditText editMessageView;
	private ListView mFastMessageView;
	private FastMessageBaseAdapter adapter;
	private List<String> fastMessageList;
	private SharedPreferences preferences;
	private Editor editor;
	private int longPosition;
	private String[] editDialogItems = new String[]{"编辑","删除"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragmentPageName(mContext, R.string.page_name_fragment_fast_message_setting);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(selectedContentView(), container,false);
		setFragmentView(fragmentView);
		//
		titlebarInit();
		viewInit();
		setupData();
		return fragmentView;
	}
	
	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_fast_message_setting);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FastMessageSettingFragment.this.getActivity().onBackPressed();

			}
		});
		titlebarAddView = (ImageView) findViewById(R.id.titlebar_add);
		titlebarAddView.setVisibility(ViewGroup.VISIBLE);
		titlebarAddView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(alertDialog == null){
					alertDialog = new CustomDialog.Builder(mContext)
					.setTitle("新增快速回复")
					.setView(alertView)
					.setPositiveButton("完成", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showProgressDialog(R.string.tips_for_collecting_for_submit);
							String message = fastMessageView.getText().toString();
							if(message.trim().length() == 0){
								ToastUtil.showToast(mContext, "内容不能为空哦");
								return;
							}
							fastMessageList.add(0, message);
							editor.clear();
							String msg;
							for(int i = 0; i <fastMessageList.size(); i++){
								msg = fastMessageList.get(i);
								editor.putString(String.format("%1$04d", i), msg);
							}
							editor.commit();
							adapter.notifyDataSetChanged();
							hideProgressDialog();
							dialog.dismiss();
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
						}
					})
					.create();
				}
				fastMessageView.setText("");
				alertDialog.show();
			}
		});
	}

	private void viewInit(){
		preferences = mContext.getSharedPreferences(SettingConfig.FAST_MESSAGE_CONFIG, Context.MODE_PRIVATE);
		editor = preferences.edit();
		mFastMessageView = (ListView) findViewById(R.id.fast_message_list);
		View emptyView = findViewById(R.id.fast_message_empty);
		mFastMessageView.setEmptyView(emptyView);
		mFastMessageView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> paramAdapterView,
					View paramView, int position, long paramLong) {
				Logger.i("onItemLongClick position" +position);
				longPosition = position;
				operateDialog.show();
				return true;
			}
		});
		alertView = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_fast_message, null);
		editAlertView = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_fast_message, null);
		fastMessageView = (EditText) alertView.findViewById(R.id.fast_message_input);
		editMessageView = (EditText) editAlertView.findViewById(R.id.fast_message_input);
		fastMessageList = new ArrayList<String>();
		adapter= new FastMessageBaseAdapter();
		mFastMessageView.setAdapter(adapter);
		
		
		operateContentView = LayoutInflater.from(mContext).inflate(R.layout.common_single_list_select_no_button, null);

		operateWv = (WheelView) operateContentView.findViewById(R.id.select_item_wheel);
		operateWv.setBackgroundResource(R.drawable.common_wheel_bg);
		operateWv.setCenterDrawable(getResources().getDrawable(R.drawable.common_wheel_val));
		operateWheelAdapter = new OperateWheelAdapter();
		operateWv.setViewAdapter(operateWheelAdapter);
		
		if(operateDialog == null){
			operateDialog = new CustomDialog.Builder(mContext)
			.setView(operateContentView)
			.setTitle("选择操作")
			.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int index = operateWv.getCurrentItem();
					switch (index) {
					case 0:
						edit();
						break;
					case 1:
						delete();
						break;
					default:
						break;
					}
					dialog.dismiss();
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			})
			.create();
		}
	}
	
	private void setupData(){
		innerFastMessagesInit();
		Map<String, String> usefulExpresstions = (Map<String, String>) preferences.getAll();
		Set<String> keys = new TreeMap<String, String>(usefulExpresstions).keySet();
		for(String key:keys){
			fastMessageList.add(usefulExpresstions.get(key));
		}
		adapter.notifyDataSetChanged();
	}
	
	private void innerFastMessagesInit(){
		//检查反馈
		SharedPreferences defaultPreferences = application.getDefaultPreferences();
		boolean usefulExpresstionFlag = defaultPreferences.getBoolean(SettingConfig.FirstRunning.FEEDBACK_USEFUL_EXPRESSION_FLAG, false);
		if(!usefulExpresstionFlag){
			//初始化反馈的快速回复默认项
			//提取已经自定义的值
			List<String> fastMessageList = new ArrayList<String>();
			SharedPreferences fastMessagePreferences = FastMessageSettingFragment.this.getActivity().getSharedPreferences(SettingConfig.FAST_MESSAGE_CONFIG, Context.MODE_PRIVATE);
			Map<String, String> usefulExpresstions = (Map<String, String>) fastMessagePreferences.getAll();

			Set<String> keys = new TreeMap<String, String>(usefulExpresstions).keySet();
			for(String key:keys){
				fastMessageList.add(usefulExpresstions.get(key));
			}
			String[] innerExpressions = getResources().getStringArray(R.array.inner_useful_expresstions);
			for(String expresstion:innerExpressions){
				fastMessageList.add(expresstion);
			}
			//
			String msg;
			Editor editor = fastMessagePreferences.edit();
			for(int i = 0; i <fastMessageList.size(); i++){
				msg = fastMessageList.get(i);
				editor.putString(String.format("%1$04d", i), msg);
			}
			editor.commit();
			//修改flag为true
			defaultPreferences.edit().putBoolean(SettingConfig.FirstRunning.FEEDBACK_USEFUL_EXPRESSION_FLAG, true).commit();
		}
	}
	
	static class FastMessageViewholder{
		TextView textView;
	}
	
	private class FastMessageBaseAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return fastMessageList.size();
		}

		@Override
		public Object getItem(int position) {
			return fastMessageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FastMessageViewholder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(selectedItemLayout(), null);
				holder = new FastMessageViewholder();
				holder.textView = (TextView) convertView.findViewById(R.id.fast_message_content);
				convertView.setTag(holder);
			}
			holder = (FastMessageViewholder) convertView.getTag();
			holder.textView.setText(fastMessageList.get(position).toString());
			return convertView;
		}
		
	}
	
	private void edit(){
		if(editDialog == null){
			editDialog = new CustomDialog.Builder(mContext)
			.setTitle("编辑快速回复")
			.setView(editAlertView)
			.setPositiveButton("完成", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String message = editMessageView.getText().toString();
					if(message.trim().length() == 0){
						ToastUtil.showToast(mContext, "内容不能为空哦");
						return;
					}
					fastMessageList.set(longPosition, message);
					editor.putString(String.format("%1$04d", longPosition), message).commit();
					adapter.notifyDataSetChanged();
					dialog.dismiss();
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			})
			.create();
		}
		String message = fastMessageList.get(longPosition);
		editMessageView.setText(message);
		editMessageView.setSelection(message.length());
		editDialog.show();
	}
	
	private void delete(){
		if(deleteDialog == null){
			deleteDialog = new CustomDialog.Builder(mContext)
			.setTitle("删除快速回复")
			.setMessage("确定删除该快速回复？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String message = fastMessageList.remove(longPosition);
							editor.clear();
							String msg;
							for(int i = 0; i <fastMessageList.size(); i++){
								msg = fastMessageList.get(i);
								editor.putString(String.format("%1$04d", i), msg);
							}
							editor.commit();
							adapter.notifyDataSetChanged();
							ToastUtil.showToast(mContext, "快速回复[" + message +"]已被删除");
							dialog.dismiss();
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
						}
					})
			.create();
		}
		deleteDialog.show();
	}
	
	private class OperateWheelAdapter implements WheelViewAdapter{

		@Override
		public int getItemsCount() {
			return editDialogItems.length;
		}

		@Override
		public View getItem(int index, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.wheel_list_item_common, null);
			TextView textView = (TextView) convertView.findViewById(R.id.wheel_item_text); 
			String name = editDialogItems[index];
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
	
	private int selectedItemLayout(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_listview_fast_message;

		default:
			return R.layout.listview_fast_message;
		}
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_fast_message_setting;

		default:
			return R.layout.activity_fast_message_setting;
		}
	}
}
