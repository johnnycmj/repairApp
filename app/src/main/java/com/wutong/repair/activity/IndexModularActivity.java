package com.wutong.repair.activity;

import java.util.ArrayList;
import java.util.List;

import com.wutong.androidprojectlibary.widget.util.ToastUtil;
import com.wutong.repair.BaseFragmentActivity;
import com.wutong.repairfjnu.R;
import com.wutong.repair.data.bean.ModularNetworkBean;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IndexModularActivity extends BaseFragmentActivity {
	

	private final static int VIEW_CODE_INDEX = 910;//首页
	private final static int VIEW_CODE_REPAIR = 911;//报修
	private final static int VIEW_CODE_MICRO_SHARE = 912;//微分享
	private final static int VIEW_CODE_COMPLAINT = 9113;//建议
	private final static int VIEW_CODE_NOTICE = 914;//公告
	private final static int VIEW_CODE_WORKBENCH = 915;//工作台

	private final static int VIEW_CODE_MORE = 916;//更多
	private final static int VIEW_CODE_PERSONINFO = 917;//个人信息
	private final static int VIEW_CODE_OFFICE_MATERIAL_APPLY_STATISTIC = 918;//申领管理
	private final static int VIEW_CODE_OFFICE_MATERIAL_APPLY = 919;//行政申领

	private List<TextView> modularSwitcherList;
	private List<ModularNetworkBean> mainModularList;
	private List<Fragment> modularFragmentList;

	private LinearLayout switcherContainerView;

	private int outSwitcherViewId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName(mContext, R.string.page_name_activity_index_modular);
		intentInit();
		dataInit();
		viewInit();
		setupData();
	}
	
	private void intentInit(){
		outSwitcherViewId = getIntent().getIntExtra("switcherViewId", 0);
	}

	private void dataInit(){
		getIntent().putExtra("titlebar_back_is_show", false);
	}



	private void viewInit(){
		switcherContainerView = (LinearLayout) findViewById(R.id.modular_switcher_layout);

		mainModularList = application.getModularNetworkBeanMainList();
		if(mainModularList.isEmpty()){
			ToastUtil.showToast(mContext, R.string.tips_for_empty_modulars);
			return;
		}
		modularSwitcherList = new ArrayList<TextView>();
		modularFragmentList = new ArrayList<Fragment>();
		View switcherView ;
		TextView textview = null;
		Fragment fragment;
		for(ModularNetworkBean modularNetworkBean:mainModularList){
			if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_workbench))){
				//工作台
				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_WORKBENCH), switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_workbench);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_repair_repair_all))
					|| modularNetworkBean.getUrl().equals(getString(R.string.modular_url_applicant_repair_all))
					|| modularNetworkBean.getUrl().equals(getString(R.string.modular_url_repair_repair_all_big))){
				//报修
				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_REPAIR), switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_repair);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_complain))){
				//投诉
				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_COMPLAINT), switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_complaint);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_notice))){
				//公告
				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_NOTICE), switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_notice);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_office_material_apply))){
				//行政申领
				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_OFFICE_MATERIAL_APPLY), switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_office_material_apply);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}

			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_office_material_apply_statistic))){
				//申领统计
				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_OFFICE_MATERIAL_APPLY_STATISTIC), switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_office_material_apply_statistic);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}
			
//			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_room_meter))){
//				//住宅抄表
//				switcherView = LayoutInflater.from(mContext).inflate(R.layout.modular_switcher_room_meter, switcherContainerView,true);
//				textview = (TextView) switcherView.findViewById(R.id.switcher_index_room_meter);
//				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
//				modularSwitcherList.add(textview);
//				modularFragmentList.add(fragment);
//			}
//
//			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_office_meter))){
//				//院内抄表
//				switcherView = LayoutInflater.from(mContext).inflate(R.layout.modular_switcher_office_meter, switcherContainerView,true);
//				textview = (TextView) switcherView.findViewById(R.id.switcher_index_office_meter);
//				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
//				modularSwitcherList.add(textview);
//				modularFragmentList.add(fragment);
//			}

			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_person_info))){
				//个人信息
				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_PERSONINFO), switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_personinfo);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_more))){
				//更多
				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_MORE), switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_more);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}
//			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_hom_page))){
//				//首页
//				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_INDEX), switcherContainerView,true);
//				textview = (TextView) switcherView.findViewById(R.id.switcher_index_home_page);
//				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
//				modularSwitcherList.add(textview);
//				modularFragmentList.add(fragment);
//			}
//			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_micro_share))){
//				//微分享
//				switcherView = LayoutInflater.from(mContext).inflate(selectWithSkinType(VIEW_CODE_MICRO_SHARE), switcherContainerView,true);
//				textview = (TextView) switcherView.findViewById(R.id.switcher_index_micro_share);
//				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
//				modularSwitcherList.add(textview);
//				modularFragmentList.add(fragment);
//
//			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_micro_activity))){
				//微生活
				switcherView = LayoutInflater.from(mContext).inflate(R.layout.modular_switcher_micro_activity, switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_micro_activity);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}
			else if(modularNetworkBean.getUrl().equals(getString(R.string.modular_url_service))){
				//服务
				switcherView = LayoutInflater.from(mContext).inflate(R.layout.spring_horse_modular_switcher_service, switcherContainerView,true);
				textview = (TextView) switcherView.findViewById(R.id.switcher_index_service);
				fragment = application.getModularFragmentManager().getFragmentByLabel(modularNetworkBean.getUrl());
				modularSwitcherList.add(textview);
				modularFragmentList.add(fragment);
			}

			//
			if(textview != null){
				textview.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						switcherChange(paramView);

					}
				});
			}

		}





		//
	}




	private void setupData(){
		if(modularSwitcherList != null && !modularSwitcherList.isEmpty()){
			if(outSwitcherViewId != 0){
				switcherChange(findViewById(outSwitcherViewId));
			}
			else{
				switcherChange(modularSwitcherList.get(0));
			}
		}
		else{

		}
	}
	

	private void switcherChange(View paramView){
		for(TextView textView:modularSwitcherList){
			textView.setEnabled(true);
		}
		//
		paramView.setEnabled(false);
		Fragment fragment = modularFragmentList.get(modularSwitcherList.indexOf(paramView));

		Bundle data = fragment.getArguments();
		if(data == null){
			data = new Bundle();
		}
		data.putAll(getIntent().getExtras());
		fragment.setArguments(data);
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.modular_switch_content,fragment).commit();
	}
	
	


	@Override
	protected void changeSkin() {
		super.changeSkin();
		switch (application.getSkinType()) {
		case 1:
			
			switcherContainerView.setBackgroundResource(R.drawable.spring_horse_tab_switcher_layout_bg);
			break;
		default :
			break;
		}

	}


	private int selectWithSkinType(int viewCode){
		switch (application.getSkinType()) {
		case 1:
			switch (viewCode) {
			case VIEW_CODE_INDEX:
				return R.layout.spring_horse_modular_switcher_home_page;
			case VIEW_CODE_REPAIR:
				return R.layout.spring_horse_modular_switcher_repair;
			case VIEW_CODE_MICRO_SHARE:
				return R.layout.spring_horse_modular_switcher_micro_share;
			case VIEW_CODE_COMPLAINT:
				return R.layout.spring_horse_modular_switcher_complaint;
			case VIEW_CODE_NOTICE:
				return R.layout.spring_horse_modular_switcher_notice;
			case VIEW_CODE_WORKBENCH:
				return R.layout.spring_horse_modular_switcher_workbench;
			case VIEW_CODE_MORE:
				return R.layout.spring_horse_modular_switcher_more;
			case VIEW_CODE_PERSONINFO:
				return R.layout.spring_horse_modular_switcher_personinfo;
			case VIEW_CODE_OFFICE_MATERIAL_APPLY_STATISTIC:
				return R.layout.spring_horse_modular_switcher_office_material_apply_statistic;
			case VIEW_CODE_OFFICE_MATERIAL_APPLY:
				return R.layout.spring_horse_modular_switcher_office_material_apply;
			default:
				break;
			}
			break;

		default:
			switch (viewCode) {
			case VIEW_CODE_INDEX:
				return R.layout.modular_switcher_home_page;
			case VIEW_CODE_REPAIR:
				return R.layout.modular_switcher_repair;
			case VIEW_CODE_MICRO_SHARE:
				return R.layout.modular_switcher_micro_share;
			case VIEW_CODE_COMPLAINT:
				return R.layout.modular_switcher_complaint;
			case VIEW_CODE_NOTICE:
				return R.layout.modular_switcher_notice;
			case VIEW_CODE_WORKBENCH:
				return R.layout.modular_switcher_workbench;
			case VIEW_CODE_MORE:
				return R.layout.modular_switcher_more;
			case VIEW_CODE_PERSONINFO:
				return R.layout.modular_switcher_personinfo;
			case VIEW_CODE_OFFICE_MATERIAL_APPLY_STATISTIC:
				return R.layout.modular_switcher_office_material_apply_statistic;
			case VIEW_CODE_OFFICE_MATERIAL_APPLY:
				return R.layout.modular_switcher_office_material_apply;
			default:
				break;
			}
			break;
		}

		return 0;

	}

	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_index_modular;

		default:
			return R.layout.activity_index_modular;
		}
	}
}
