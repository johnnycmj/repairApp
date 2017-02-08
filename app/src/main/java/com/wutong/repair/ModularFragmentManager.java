package com.wutong.repair;


import com.wutong.repair.activity.OfficeMaterialApplyDetailActivity;
import com.wutong.repair.fragment.AboutFragment;
import com.wutong.repair.fragment.ChangeContactFragment;
import com.wutong.repair.fragment.ChangePasswordFragment;
import com.wutong.repair.fragment.ComplaintFragment;
import com.wutong.repair.fragment.ContributeListFragment;
import com.wutong.repair.fragment.DormChangeApplyFragment;
import com.wutong.repair.fragment.DormChangeStatusFragment;
import com.wutong.repair.fragment.FastMessageSettingFragment;
import com.wutong.repair.fragment.FeedbackUsFragment;
import com.wutong.repair.fragment.HelpFoundListFragment;
import com.wutong.repair.fragment.HomePageFragment;
import com.wutong.repair.fragment.MaterialInfoFragment;
import com.wutong.repair.fragment.MeterChangeRecordFragment;
import com.wutong.repair.fragment.MicroActivityFragment;
import com.wutong.repair.fragment.MicroActivityListFragment;
import com.wutong.repair.fragment.MicroShareFragment;
import com.wutong.repair.fragment.MoreFragment;
import com.wutong.repair.fragment.MyPublishLostFoundListFragment;
import com.wutong.repair.fragment.NotDisturbSettingFragment;
import com.wutong.repair.fragment.NoticeListFragment;
import com.wutong.repair.fragment.OfficeMaterialApplyFragment;
import com.wutong.repair.fragment.OfficeMaterialStatisticFragment;
import com.wutong.repair.fragment.OfficeMeterFragment;
import com.wutong.repair.fragment.PersonInfoFragment;
import com.wutong.repair.fragment.PushMessageCategoryListFragment;
import com.wutong.repair.fragment.PushMessageListFragment;
import com.wutong.repair.fragment.RepairFragment;
import com.wutong.repair.fragment.RepairmanLikeListFragment;
import com.wutong.repair.fragment.RoomMeterFragment;
import com.wutong.repair.fragment.WorkbenchFragment;
import com.wutong.repairfjnu.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ModularFragmentManager {

	private Context mContext;

	public ModularFragmentManager(Context context) {
		super();
		this.mContext = context;
	}

	public Fragment getFragmentByLabel(String fragmentLabel){
		Fragment fragment;
		if(fragmentLabel.equals(mContext.getString(R.string.modular_url_workbench))){
			fragment = new WorkbenchFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_applicant_repair_all))){
			fragment = new RepairFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_repair_repair_all))){
			fragment = new RepairFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_repair_repair_all_big))){
			//维修工报修大字版
			fragment = new RepairFragment();
			Bundle bundle = new Bundle();
			bundle.putBoolean("isLargeCharacter", true);
			fragment.setArguments(bundle);
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_notice))){
			fragment = new NoticeListFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_complain))){
			fragment = new ComplaintFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_more))){
			fragment = new MoreFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_person_info))){
			fragment = new PersonInfoFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_feedbackus))){
			fragment = new FeedbackUsFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_fast_message_setting))){
			fragment = new FastMessageSettingFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_not_disturb_setting))){
			fragment = new NotDisturbSettingFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_material_info))){
			fragment = new MaterialInfoFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_about))){
			fragment = new AboutFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.fragment_label_change_password))){
			fragment = new ChangePasswordFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.fragment_label_change_contact))){
			fragment = new ChangeContactFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_office_material_apply))){
			fragment = new OfficeMaterialApplyFragment();
			Bundle bundle = new Bundle();
			bundle.putInt("detailPermissionType", OfficeMaterialApplyDetailActivity.PERMISSION_DIVISION);
			fragment.setArguments(bundle);
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_office_material_apply_statistic))){
			fragment = new OfficeMaterialStatisticFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_hom_page))){
			fragment = new HomePageFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_micro_share))){
			fragment = new MicroShareFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_room_meter))){
			fragment = new RoomMeterFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_office_meter))){
			fragment = new OfficeMeterFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.fragment_url_contribute_list))){
			fragment = new ContributeListFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_help_found_list))){
			fragment = new HelpFoundListFragment();
			Bundle bundle = new Bundle();
			bundle.putString("type", HelpFoundListFragment.TYPE_FOUND);
			fragment.setArguments(bundle);
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_help_lost))){
			fragment = new HelpFoundListFragment();
			Bundle bundle = new Bundle();
			bundle.putString("type", HelpFoundListFragment.TYPE_LOST);
			fragment.setArguments(bundle);
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_repairman_like))){
			fragment = new RepairmanLikeListFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_my_publish_lost_or_found))){
			fragment = new MyPublishLostFoundListFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_micro_activity))){
			fragment = new MicroActivityFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.fragment_label_micro_activity_list))){
			fragment = new MicroActivityListFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.fragment_label_push_category_list))){
			fragment = new PushMessageCategoryListFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.fragment_label_push_message_list))){
			fragment = new PushMessageListFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_meter_change))){
			fragment = new MeterChangeRecordFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.modular_url_dorm_change))){
			fragment = new DormChangeStatusFragment();
		}
		else if(fragmentLabel.equals(mContext.getString(R.string.fragment_url_dorm_change_apply))){
			fragment = new DormChangeApplyFragment();
		}
		else{
			fragment = null;
		}
		return fragment;
	}

}
