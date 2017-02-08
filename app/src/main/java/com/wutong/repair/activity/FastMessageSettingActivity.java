package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseActivity;
import com.wutong.repair.util.Logger;
import com.wutong.repair.util.SettingConfig;
import com.wutong.repairfjnu.R;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class FastMessageSettingActivity extends BaseActivity {

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarAddView;
	
	private AlertDialog alertDialog;
	private AlertDialog operateDialog;
	private AlertDialog editDialog;
	private AlertDialog deleteDialog;
	private Button positiveView;
	private Button editPositiveView;
	private View alertView;
	private View editAlertView;
	private EditText fastMessageView;
	private EditText editMessageView;
	private ListView mFastMessageView;
	private BaseAdapter adapter;
	private List<String> fastMessageList;
	private SharedPreferences preferences;
	private Editor editor;
	private int longPosition;
	private String[] editDialogItems = new String[]{"编辑","删除"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fast_message_setting);
		setStatPageName(mContext, R.string.title_activity_fast_message_setting);
		titleBarInit();
		viewInit();
		setupData();
	}

	private void titleBarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_fast_message_setting);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FastMessageSettingActivity.this.onBackPressed();

			}
		});
		titlebarAddView = (ImageView) findViewById(R.id.titlebar_add);
		titlebarAddView.setVisibility(ViewGroup.VISIBLE);
		titlebarAddView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(alertDialog == null){
					alertDialog = new AlertDialog.Builder(mContext)
					.setTitle("新增快速回复")
					.setView(alertView)
					.setPositiveButton("完成", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String message = fastMessageView.getText().toString();
							fastMessageList.add(0, message);
							editor.clear();
							String msg;
							for(int i = 0; i <fastMessageList.size(); i++){
								msg = fastMessageList.get(i);
								editor.putString(String.format("%1$04d", i), msg);
							}
							editor.commit();
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
				positiveView = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				positiveView.setEnabled(false);
			}
		});
	}

	private void viewInit(){
		preferences = getSharedPreferences(SettingConfig.FAST_MESSAGE_CONFIG, MODE_PRIVATE);
		editor = preferences.edit();
		mFastMessageView = (ListView) findViewById(R.id.fast_message_list);
		mFastMessageView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> paramAdapterView,
					View paramView, int position, long paramLong) {
				Logger.i("onItemLongClick position" +position);
				longPosition = position;
				if(operateDialog == null){
					operateDialog = new AlertDialog.Builder(mContext)
					.setItems(editDialogItems, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							switch (paramInt) {
							case 0:
								edit();
								break;
							case 1:
								delete();
								break;
							default:
								break;
							}
							
						}
						
					})
					.create();
				}
				operateDialog.show();
				return true;
			}
		});
		alertView = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_fast_message, null);
		editAlertView = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_fast_message, null);
		fastMessageView = (EditText) alertView.findViewById(R.id.fast_message_input);
		fastMessageView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(alertDialog.isShowing()){
					if(s.toString().trim().length()==0){
						positiveView.setEnabled(false);
					}
					else{
						positiveView.setEnabled(true);
					}
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		editMessageView = (EditText) editAlertView.findViewById(R.id.fast_message_input);
		editMessageView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(editDialog.isShowing()){
					if(s.toString().trim().length()==0){
						editPositiveView.setEnabled(false);
					}
					else{
						editPositiveView.setEnabled(true);
					}
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		fastMessageList = new ArrayList<String>();
		adapter= new FastMessageBaseAdapter();
		mFastMessageView.setAdapter(adapter);
	}
	
	private void setupData(){
		Map<String, String> usefulExpresstions = (Map<String, String>) preferences.getAll();
		Set<String> keys = new TreeMap<String, String>(usefulExpresstions).keySet();
		for(String key:keys){
			fastMessageList.add(usefulExpresstions.get(key));
		}
		adapter.notifyDataSetChanged();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_fast_message, null);
			TextView textView = (TextView) convertView.findViewById(R.id.fast_message_content);
			textView.setText(fastMessageList.get(position).toString());
			return convertView;
		}
		
	}
	
	private void edit(){
		if(editDialog == null){
			editDialog = new AlertDialog.Builder(mContext)
			.setTitle("编辑快速回复")
			.setView(editAlertView)
			.setPositiveButton("完成", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String message = editMessageView.getText().toString();
					fastMessageList.set(longPosition, message);
					editor.putString(String.valueOf(longPosition), message).commit();
					adapter.notifyDataSetChanged();
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
		editMessageView.setText(fastMessageList.get(longPosition));
		editDialog.show();
		editPositiveView = editDialog.getButton(AlertDialog.BUTTON_POSITIVE);
	}
	
	private void delete(){
		if(deleteDialog == null){
			deleteDialog = new AlertDialog.Builder(mContext)
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
}
