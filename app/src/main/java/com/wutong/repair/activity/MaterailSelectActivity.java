package com.wutong.repair.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.wutong.repair.BaseActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.MaterialBean;
import com.wutong.repair.util.Logger;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

@Deprecated
public class MaterailSelectActivity extends BaseActivity {

	private ImageView titlebarBackView;
	private TextView titlebarTitleView;
	private ImageView titlebarSubmitView;

	private ListView mMaterialAllView;
	private Button mToggleSelectAllView;
	private List<Boolean> checkedStatusList;
	private List<MaterialBean> materialUnSelected;//未使用（显示）
	private List<MaterialBean> materialSelected;//已使用
	private BaseAdapter adapter;

	private boolean outIsLargeCharacter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_materail_select);
		setStatPageName(mContext, R.string.title_activity_materail_select);
		intentInit();
		titlebarInit();
		viewInit();
	}

	@SuppressWarnings("unchecked")
	private void intentInit(){
		final Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		materialUnSelected = (List<MaterialBean>) bundle.get("MaterialUnSelected");
		materialSelected = (List<MaterialBean>) bundle.get("MaterialSelected");
		outIsLargeCharacter = intent.getBooleanExtra("isLargeCharacter",false);
	}

	private void titlebarInit(){
		titlebarTitleView = ((TextView)findViewById(R.id.titlebar_title));
		titlebarTitleView.setText(R.string.title_activity_materail_select);
		titlebarBackView = (ImageView) findViewById(R.id.titlebar_back);
		titlebarBackView.setVisibility(ViewGroup.VISIBLE);
		titlebarBackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MaterailSelectActivity.this.onBackPressed();

			}
		});
		titlebarSubmitView = (ImageView) findViewById(R.id.titlebar_submit);
		titlebarSubmitView.setVisibility(ViewGroup.VISIBLE);
		titlebarSubmitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MaterialBean materialBean;
				int index = 0;
				for(Boolean bool:checkedStatusList){
					if(bool){
						materialBean = materialUnSelected.get(index);
						materialSelected.add(materialBean);
						materialUnSelected.remove(materialBean);
					}
					else{
						index ++;
					}
				}

				Bundle bundle = new Bundle();
				bundle.putSerializable("MaterialUnSelectedResult", (Serializable)materialUnSelected);
				bundle.putSerializable("MaterialSelectedResult", (Serializable)materialSelected);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				MaterailSelectActivity.this.setResult(Activity.RESULT_OK, intent);
				MaterailSelectActivity.this.finish();
			}
		});
	}

	private void viewInit(){
		//初始化
		mMaterialAllView = (ListView) findViewById(R.id.materail_all);
		mToggleSelectAllView = (Button) findViewById(R.id.material_toggle_select_all);


		View emptyView = findViewById(R.id.material_select_empty);
		mMaterialAllView.setEmptyView(emptyView);

		adapter = new MaterailSelectBaseAdapter(materialUnSelected);
		checkedStatusList = new ArrayList<Boolean>();
		for(int i=0;i < materialUnSelected.size();i++){
			checkedStatusList.add(false);
		}
		mMaterialAllView.setAdapter(adapter);
		mToggleSelectAllView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mToggleSelectAllView.setEnabled(false);
				if(isCheckedAll()){
					int size = checkedStatusList.size();
					checkedStatusList.clear();
					for(int i=0;i<size;i++){
						checkedStatusList.add(false);
					}
				}
				else{
					int size = checkedStatusList.size();
					checkedStatusList.clear();
					for(int i=0;i<size;i++){
						checkedStatusList.add(true);
					}
				}
				adapter.notifyDataSetChanged();
				mToggleSelectAllView.setEnabled(true);
			}
		});
		
		//动态设置大小
		
				if(outIsLargeCharacter){
					mToggleSelectAllView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
				}
				else{
					mToggleSelectAllView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
					((TextView)emptyView.findViewById(R.id.empty_tip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				}
	}


	private final class MaterailSelectBaseAdapter extends BaseAdapter{

		private List<MaterialBean> list;

		public MaterailSelectBaseAdapter(List<MaterialBean> list) {
			super();
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position ) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position ) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final MaterialBean materialBean = list.get(position);
			if(outIsLargeCharacter){
				convertView = LayoutInflater.from(MaterailSelectActivity.this).inflate(R.layout.listview_materail_select_large, null);
			}
			else{
				convertView = LayoutInflater.from(MaterailSelectActivity.this).inflate(R.layout.listview_materail_select, null);
			}
			TextView materialNameView = (TextView) convertView.findViewById(R.id.pre_list_material_name);
			final CheckBox checkView = (CheckBox) convertView.findViewById(R.id.pre_list_is_checked);
			TextView textView = (TextView) convertView.findViewById(R.id.pre_list_material_total_number);
			boolean check = checkedStatusList.get(position).booleanValue();
			Logger.i("checkView.setChecked " +"position:" + position+check);
			checkView.setChecked(check);
			checkView.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Logger.i(" onCheckedChang before position:" +position  +"isChecked:" +isChecked);
					checkedStatusList.set(position, isChecked);
					Logger.i("checkedStatusList:" +checkedStatusList.get(position).toString());
				}
			});
			materialNameView.setText(materialBean.getMaterialName());
			textView.setText(getString(R.string.format_material_select_number_unit,materialBean.getTotalNumber().toString(),materialBean.getUnit()));
			return convertView;
		}

	}

	/**
	 * 判断是否全部选中
	 * @return
	 */
	private boolean isCheckedAll(){
		int checkedNumber = 0;
		for(boolean isChecked:checkedStatusList){
			if (isChecked) {
				checkedNumber++;
			}
		}
		return checkedNumber == materialUnSelected.size();
	}

}
