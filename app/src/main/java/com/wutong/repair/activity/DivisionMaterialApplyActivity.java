package com.wutong.repair.activity;

import com.wutong.repair.BaseFragmentActivity;
import com.wutong.repair.fragment.OfficeMaterialApplyFragment;
import com.wutong.repairfjnu.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 科室下的申领单列表
 * @author Jolly
 * 创建时间：2014年2月24日下午3:17:49
 *
 */
public class DivisionMaterialApplyActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(selectedContentView());
		setStatPageName("科室下的申领单列表");
		viewInit();
	}

	
	private void viewInit(){
		Fragment fragment = new OfficeMaterialApplyFragment();

		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.division_material_apply_area,fragment).commit();
	}
	
	private int selectedContentView(){
		switch (application.getSkinType()) {
		case 1:
			return R.layout.spring_horse_activity_division_material_apply;

		default:
			return R.layout.activity_division_material_apply;
		}
	}
}
