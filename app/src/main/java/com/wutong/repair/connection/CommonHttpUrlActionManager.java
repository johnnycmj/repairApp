package com.wutong.repair.connection;


import com.wutong.repairfjnu.R;

import android.content.Context;

public class CommonHttpUrlActionManager {

	private String domainUrl;//服务器ip和端口以及项目名称
	private String loginUrl;
	private String studentDormInfoUrl;//获取学生宿舍信息
	private String repairOrderStudentAllLoadUrl;//学生获取全部
	private String repairOrderRepairmanAllLoadUrl;//维修工获取全部
	private String repairOrderStudentDormLoadUrl;//获取报修单列表-学生-宿舍
	private String repairOrderStudentPublicInnerLoadUrl;//获取报修单列表-学生-公共室内
	private String repairOrderStudentPublicOutterLoadUrl;//获取报修单列表-学生-公共室外
	private String repairOrderRepairmanDormAndPublicInnerLoadUrl;//获取报修单列表-维修工-公共室内和宿舍
	private String repairOrderRepairmanPublicOutterLoadUrl;//获取报修单列表-维修工-公共室外
	private String repairOrderDetailDormUrl;//获取报修单详细信息（基础）
	private String repairOrderDetailPublicInnerUrl;//获取报修单详细信息（基础）
	private String repairOrderDetailPublicOutterUrl;//获取报修单详细信息（基础）
	private String assetListLoadUrl;//获取报修类型(父)
	private String troubleListLoadUrl;//获取报修项目(子)
	private String buildingListLoadUrl;//获取建筑
	private String roomListLoadUrl;//获取宿舍
	private String streetListLoadUrl;//获取街道
	private String landmarkLoadUrl;//获取标志性建筑
	private String checkSameOrderUrl;
	private String repairOrderSubmitUrl;
	private String studentContactChangeUrl;//修改学生联系方式
	private String repairmanContactChangeUrl;//修改维修工联系方式
	private String changePasswordUrl;//修改密码
	private String repairmanMaterialListUrl;//维修工身上的材料
	private String repairOrderMaterialUsedUrl;//宿舍资产列表
	private String repairOrderMaterialAddUrl;//报修单的材料增加
	private String roomAssetListUrl;//宿舍资产列表
	private String repairOrderPhotoUploadUrl;//报修单的图片提交
	private String feedbackListUrl;//反馈列表
	private String feedbackTextAddUrl;//反馈文本
	private String feedbackRecordAddUrl;//反馈录音
	private String assessListUrl;//评价项列表
	private String assessAddUrl;//添加评价
	private String repairOrderImageListUrl;
	private String repairOrderAcceptUrl;//受理
	private String repairOrderRefixUrl;//返修
	private String repairOrderFinishUrl;//完成
	private String repairOrderRefixFinishUrl;//返修完成
	private String repairOrderAffirmUrl;//确认
	private String repairOrderRefixAffirmUrl;//返修确认
	private String repairOrderMuchMoneyForwardUrl;//大额维修单转发
	private String checkVersionUrl;//版本更新检查
	private String noticeListUrl;//通知列表
	private String noticeDetailUrl;//通知详情
	private String noticeIsReadSendUrl;//通知已读状态发送
	private String noticeIsReadSendForHomePageUrl;//通知已读状态发送
	private String feedbackUsSendUrl;//意见反馈
	private String complaintListUrl;//投诉（留言板）列表
	private String complaintSubListUrl;//投诉（留言板）回复列表
	private String complaintPublishOrReplyUrl;//投诉（留言板）发表或回复
	private String complaintDetailUrl;//投诉（留言板）详情
	private String materialGrantListUrl;//材料发放列表
	private String materialGrantQueryUrl;//材料发放，维修工确认
	private String materialGrantDetailUrl;//材料发放详情

	private String repairOrderAssignmentDormAndPublicInnerLoadUrl;//获取报修单列表-派发头-公共室内和宿舍
	private String repairOrderAssignmentPublicOutterLoadUrl;//获取报修单列表-派发头-公共室外
	private String repairmanListLoadUrl;
	private String assignmentSubmitUrl;

	private String statRepairLoadUrl;//统计报修单
	private String statMaterialListLoadUrl;//统计材料
	private String statEmploymentListLoadUrl;//统计维修工维修情况列表
	private String statEmploymentRepairOrderGroupListUrl;//维修工下按三种情况分，不区分宿舍报修、公共室内、公共室外
	private String statEmploymentRepairOrderGroupListUrlFJMK;//维修工下按三种情况分，不区分宿舍报修、公共室内、公共室外
	private String remindEmploymentUrl;//催办

	private String officeMaterialApplyLoadList;//申请记录列表（状态分类）
	private String officeMaterialApplyLoadDetail;//申请记录详细
	private String officeMaterialApplyAdd;//添加申请记录
	private String officeMaterialApplyCancel;//申请记录撤销
	private String officeMaterialApplyQuery;//申请记录确认
	private String officeMaterialApplyLoadMaterialList;//申请记录下的材料列表

	private String officeDivisionLoadList;//科室列表
	private String officeEmploymentLoadList;//人员列表
	private String officeDivisionMaterialUsedLoadList;//科室下办公材料使用列表

	private String officeDivisionMaterialAppliedLoadList;//科室下办公材料申领列表

	private String officeEmploymentMaterialUsedLoadList;//人员下办公材料使用列表

	//	private String officeEmploymentMaterialAppliedLoadList;//人员下办公材料申领列表

	private String officeOfficeMaterialListStatisticList;//办公材料统计列表
	private String officeOfficeMaterialStatisticDivisionList;//办公材料下按科室列表
	private String officeOfficeMaterialStatisticEmploymentList;//办公材料下按人员列表
	private String officeRepairMaterialStatisticDivisionList;//办公材料下按科室列表
	private String homeProgressLoadList;//首页进展
	private String microShareLoad;//微分享
	private String complaintCategoryList;//建议的分类
	private String meterBuildingList;
	private String roomMeterList;
	private String officeMeterList;
	private String submitRoomMeter;//提交住宅抄表
	private String officeMeterBuildingList;
	private String officeMeterDivisionList;
	private Context mContext;
	/**
	 * 
	 * @param domainUrl
	 */
	public CommonHttpUrlActionManager(Context context,String domainUrl) {
		super();
		this.domainUrl = domainUrl;
		mContext = context;
		init();
	};
	/**
	 * 对url进行初始化
	 */
	private void init(){
		refreshInit();
	}

	private void refreshInit(){
		loginUrl = mContext.getString(R.string.http_url_load_login,domainUrl);
		repairOrderStudentDormLoadUrl = mContext.getString(R.string.http_url_load_repair_order_list_student_dorm,domainUrl);
		studentDormInfoUrl = mContext.getString(R.string.http_url_load_student_dorm_info,domainUrl);
		repairOrderDetailDormUrl = mContext.getString(R.string.http_url_load_repair_order_detail_dorm,domainUrl);
		repairOrderDetailPublicInnerUrl = mContext.getString(R.string.http_url_load_repair_order_detail_public_inner,domainUrl);
		repairOrderDetailPublicOutterUrl = mContext.getString(R.string.http_url_load_repair_order_detail_public_outter,domainUrl);
		repairOrderStudentPublicInnerLoadUrl = mContext.getString(R.string.http_url_load_repair_order_list_student_public_inner,domainUrl);
		repairOrderStudentPublicOutterLoadUrl = mContext.getString(R.string.http_url_load_repair_order_list_student_public_outter,domainUrl);
		repairOrderRepairmanDormAndPublicInnerLoadUrl = mContext.getString(R.string.http_url_load_repair_order_list_repairman_dorm_and_public_inner,domainUrl);
		repairOrderRepairmanPublicOutterLoadUrl = mContext.getString(R.string.http_url_load_repair_order_list_repairman_public_outter,domainUrl);
		assetListLoadUrl = mContext.getString(R.string.http_url_load_repair_order_asset_list,domainUrl);
		troubleListLoadUrl = mContext.getString(R.string.http_url_load_repair_order_trouble_list,domainUrl);
		buildingListLoadUrl = mContext.getString(R.string.http_url_load_repair_order_building_list,domainUrl);
		roomListLoadUrl = mContext.getString(R.string.http_url_load_repair_order_room_list,domainUrl);
		streetListLoadUrl = mContext.getString(R.string.http_url_load_repair_order_street_list,domainUrl);
		landmarkLoadUrl = mContext.getString(R.string.http_url_load_repair_order_landmark_list,domainUrl);
		checkSameOrderUrl = mContext.getString(R.string.http_url_load_repair_order_check_same,domainUrl);
		repairOrderSubmitUrl = mContext.getString(R.string.http_url_load_repair_order_submit,domainUrl);
		studentContactChangeUrl = mContext.getString(R.string.http_url_load_student_contact_change,domainUrl);
		repairmanContactChangeUrl = mContext.getString(R.string.http_url_load_repairman_contact_change,domainUrl);
		changePasswordUrl = mContext.getString(R.string.http_url_load_change_password,domainUrl);
		repairmanMaterialListUrl = mContext.getString(R.string.http_url_load_repairman_material_list,domainUrl);
		repairOrderMaterialUsedUrl = mContext.getString(R.string.http_url_load_repair_order_material_used,domainUrl);
		repairOrderMaterialAddUrl = mContext.getString(R.string.http_url_load_repair_order_material_add,domainUrl);
		roomAssetListUrl = mContext.getString(R.string.http_url_load_room_asset_list,domainUrl);
		repairOrderPhotoUploadUrl = mContext.getString(R.string.http_url_load_repair_order_photo_upload,domainUrl);
		feedbackListUrl = mContext.getString(R.string.http_url_load_repair_order_feedback_list,domainUrl);
		feedbackTextAddUrl = mContext.getString(R.string.http_url_load_repair_order_feedback_text_add,domainUrl);
		feedbackRecordAddUrl = mContext.getString(R.string.http_url_load_repair_order_feedback_record_add,domainUrl);
		assessListUrl = mContext.getString(R.string.http_url_load_repair_order_assess_list,domainUrl);
		assessAddUrl = mContext.getString(R.string.http_url_load_repair_order_assess_add,domainUrl);
		repairOrderImageListUrl = mContext.getString(R.string.http_url_load_repair_order_image_list,domainUrl);
		repairOrderAcceptUrl = mContext.getString(R.string.http_url_load_repair_order_accept,domainUrl);//受理
		repairOrderRefixUrl = mContext.getString(R.string.http_url_load_repair_order_need_refix,domainUrl);//返修
		repairOrderFinishUrl = mContext.getString(R.string.http_url_load_repair_order_finish,domainUrl);//完成
		repairOrderRefixFinishUrl = mContext.getString(R.string.http_url_load_repair_order_refix_finish,domainUrl);//返修完成
		repairOrderAffirmUrl = mContext.getString(R.string.http_url_load_repair_order_affirm,domainUrl);//确认
		repairOrderRefixAffirmUrl = mContext.getString(R.string.http_url_load_repair_order_refix_affirm,domainUrl);//返修确认
		repairOrderMuchMoneyForwardUrl = mContext.getString(R.string.http_url_load_repair_order_much_money_forward,domainUrl);//大额维修单转发
		checkVersionUrl = mContext.getString(R.string.http_url_load_check_version,domainUrl);
		noticeListUrl = mContext.getString(R.string.http_url_load_notice_list,domainUrl);
		noticeDetailUrl = mContext.getString(R.string.http_url_load_notice_detail,domainUrl);
		noticeIsReadSendUrl = mContext.getString(R.string.http_url_load_notice_is_read_send,domainUrl);
		noticeIsReadSendForHomePageUrl = mContext.getString(R.string.http_url_home_page_notice_is_read,domainUrl);
		feedbackUsSendUrl = mContext.getString(R.string.http_url_load_feedback_us_send,domainUrl);
		complaintListUrl = mContext.getString(R.string.http_url_load_complaint_list,domainUrl);
		complaintSubListUrl = mContext.getString(R.string.http_url_load_complaint_sub_list,domainUrl);
		complaintPublishOrReplyUrl = mContext.getString(R.string.http_url_load_complaint_publish_or_reply,domainUrl);
		complaintDetailUrl = mContext.getString(R.string.http_url_load_complaint_detail,domainUrl);
		materialGrantListUrl = mContext.getString(R.string.http_url_load_material_apply_and_grant_list,domainUrl);
		materialGrantQueryUrl = mContext.getString(R.string.http_url_load_material_apply_and_grant_query,domainUrl);
		materialGrantDetailUrl = mContext.getString(R.string.http_url_load_material_apply_and_grant_detail,domainUrl);
		repairOrderAssignmentDormAndPublicInnerLoadUrl = mContext.getString(R.string.http_url_load_repair_order_list_assignment_dorm_and_public_inner,domainUrl);
		repairOrderAssignmentPublicOutterLoadUrl = mContext.getString(R.string.http_url_load_repair_order_list_assignment_public_outter,domainUrl);
		repairmanListLoadUrl = mContext.getString(R.string.http_url_load_repairman_list,domainUrl);
		assignmentSubmitUrl = mContext.getString(R.string.http_url_load_submit_assignmemt,domainUrl);
		statRepairLoadUrl = mContext.getString(R.string.http_url_load_stat_repair,domainUrl);
		statMaterialListLoadUrl = mContext.getString(R.string.http_url_load_stat_material_list,domainUrl);
		statEmploymentListLoadUrl = mContext.getString(R.string.http_url_load_stat_employment_list,domainUrl);
		statEmploymentRepairOrderGroupListUrl = mContext.getString(R.string.http_url_load_stat_employment_repair_order_group_list,domainUrl);
		statEmploymentRepairOrderGroupListUrlFJMK = mContext.getString(R.string.http_url_load_stat_employment_repair_order_group_list_fjmk,domainUrl);

		remindEmploymentUrl = mContext.getString(R.string.http_url_load_remind_employment,domainUrl);
		repairOrderStudentAllLoadUrl = mContext.getString(R.string.http_url_load_repair_order_list_student_all,domainUrl);
		repairOrderRepairmanAllLoadUrl = mContext.getString(R.string.http_url_load_repair_order_list_repairman_all,domainUrl);
		officeMaterialApplyLoadList = mContext.getString(R.string.http_url_load_office_material_apply_list,domainUrl);
		officeMaterialApplyLoadDetail = mContext.getString(R.string.http_url_load_office_material_apply_detail,domainUrl);
		officeMaterialApplyAdd = mContext.getString(R.string.http_url_load_office_material_apply_add,domainUrl);
		officeMaterialApplyCancel = mContext.getString(R.string.http_url_load_office_material_apply_cancel,domainUrl);
		officeMaterialApplyQuery = mContext.getString(R.string.http_url_load_office_material_apply_query,domainUrl);
		officeMaterialApplyLoadMaterialList = mContext.getString(R.string.http_url_load_office_materials_list,domainUrl);
		officeDivisionLoadList = mContext.getString(R.string.http_url_load_office_division_list,domainUrl);
		officeDivisionMaterialUsedLoadList = mContext.getString(R.string.http_url_load_office_division_material_list,domainUrl);
		officeDivisionMaterialAppliedLoadList = mContext.getString(R.string.http_url_load_office_division_material_list_apply,domainUrl);
		officeOfficeMaterialListStatisticList = mContext.getString(R.string.http_url_load_office_material_list_statistic_list,domainUrl);
		officeOfficeMaterialStatisticDivisionList = mContext.getString(R.string.http_url_load_office_material_list_statistic_detail_group_by_division,domainUrl);
		officeRepairMaterialStatisticDivisionList = mContext.getString(R.string.http_url_load_repair_material_list_statistic_detail_group_by_division,domainUrl);
		homeProgressLoadList = mContext.getString(R.string.http_url_load_home_page_progress_list,domainUrl);
		microShareLoad = mContext.getString(R.string.http_url_load_micro_share,domainUrl);
		complaintCategoryList = mContext.getString(R.string.http_url_load_complaint_category,domainUrl);

		meterBuildingList = mContext.getString(R.string.http_url_load_meter_building_list,domainUrl);
		roomMeterList = mContext.getString(R.string.http_url_load_room_meter_list,domainUrl);
		officeMeterList = mContext.getString(R.string.http_url_load_office_meter_list,domainUrl);
		submitRoomMeter = mContext.getString(R.string.http_url_submit_room_meter,domainUrl);
		officeMeterBuildingList = mContext.getString(R.string.http_url_load_office_meter_building_list,domainUrl);
		officeMeterDivisionList = mContext.getString(R.string.http_url_load_office_meter_division_list,domainUrl);
		officeEmploymentLoadList = mContext.getString(R.string.http_url_load_office_employment_list,domainUrl);

		officeEmploymentMaterialUsedLoadList = mContext.getString(R.string.http_url_load_office_employment_material_list,domainUrl);
		officeOfficeMaterialStatisticEmploymentList = mContext.getString(R.string.http_url_load_office_material_list_statistic_detail_group_by_employment,domainUrl);
	}


	public String getOfficeOfficeMaterialStatisticEmploymentList() {
		return officeOfficeMaterialStatisticEmploymentList;
	}
	public String getOfficeEmploymentMaterialUsedLoadList() {
		return officeEmploymentMaterialUsedLoadList;
	}
	public String getOfficeEmploymentLoadList() {
		return officeEmploymentLoadList;
	}
	public String getOfficeMeterDivisionList() {
		return officeMeterDivisionList;
	}
	public String getOfficeMeterBuildingList() {
		return officeMeterBuildingList;
	}
	public String getSubmitRoomMeter() {
		return submitRoomMeter;
	}
	public String getNoticeIsReadSendForHomePageUrl() {
		return noticeIsReadSendForHomePageUrl;
	}
	public String getComplaintCategoryList() {
		return complaintCategoryList;
	}
	public String getMicroShareLoad() {
		return microShareLoad;
	}
	public String getStatEmploymentRepairOrderGroupListUrlFJMK() {
		return statEmploymentRepairOrderGroupListUrlFJMK;
	}
	public String getOfficeRepairMaterialStatisticDivisionList() {
		return officeRepairMaterialStatisticDivisionList;
	}
	public String getOfficeOfficeMaterialStatisticDivisionList() {
		return officeOfficeMaterialStatisticDivisionList;
	}
	public String getOfficeOfficeMaterialListStatisticList() {
		return officeOfficeMaterialListStatisticList;
	}
	public String getOfficeDivisionMaterialAppliedLoadList() {
		return officeDivisionMaterialAppliedLoadList;
	}
	public String getOfficeDivisionMaterialUsedLoadList() {
		return officeDivisionMaterialUsedLoadList;
	}
	public String getOfficeDivisionLoadList() {
		return officeDivisionLoadList;
	}
	public String getOfficeMaterialApplyLoadDetail() {
		return officeMaterialApplyLoadDetail;
	}
	public String getOfficeMaterialApplyAdd() {
		return officeMaterialApplyAdd;
	}
	public String getOfficeMaterialApplyCancel() {
		return officeMaterialApplyCancel;
	}
	public String getOfficeMaterialApplyQuery() {
		return officeMaterialApplyQuery;
	}
	public String getOfficeMaterialApplyLoadMaterialList() {
		return officeMaterialApplyLoadMaterialList;
	}
	public String getOfficeMaterialApplyLoadList() {
		return officeMaterialApplyLoadList;
	}
	public String getMaterialGrantListUrl() {
		return materialGrantListUrl;
	}
	public String getStatEmploymentRepairOrderGroupListUrl() {
		return statEmploymentRepairOrderGroupListUrl;
	}
	public String getStatMaterialListLoadUrl() {
		return statMaterialListLoadUrl;
	}
	public String getMaterialGrantQueryUrl() {
		return materialGrantQueryUrl;
	}
	public String getRemindEmploymentUrl() {
		return remindEmploymentUrl;
	}
	public String getStatRepairLoadUrl() {
		return statRepairLoadUrl;
	}
	public String getStatEmploymentListLoadUrl() {
		return statEmploymentListLoadUrl;
	}
	public String getMaterialGrantDetailUrl() {
		return materialGrantDetailUrl;
	}
	public void setDomainUrl(String domainUrl) {
		this.domainUrl = domainUrl;
		refreshInit();
	}
	public String getRepairmanListLoadUrl() {
		return repairmanListLoadUrl;
	}
	public String getAssignmentSubmitUrl() {
		return assignmentSubmitUrl;
	}
	public String getComplaintListUrl() {
		return complaintListUrl;
	}
	public String getRepairOrderAssignmentDormAndPublicInnerLoadUrl() {
		return repairOrderAssignmentDormAndPublicInnerLoadUrl;
	}
	public String getRepairOrderAssignmentPublicOutterLoadUrl() {
		return repairOrderAssignmentPublicOutterLoadUrl;
	}
	public String getComplaintSubListUrl() {
		return complaintSubListUrl;
	}
	public String getComplaintPublishOrReplyUrl() {
		return complaintPublishOrReplyUrl;
	}
	public String getComplaintDetailUrl() {
		return complaintDetailUrl;
	}
	public String getFeedbackUsSendUrl() {
		return feedbackUsSendUrl;
	}
	public String getNoticeIsReadSendUrl() {
		return noticeIsReadSendUrl;
	}
	public String getNoticeListUrl() {
		return noticeListUrl;
	}
	public String getNoticeDetailUrl() {
		return noticeDetailUrl;
	}
	public String getCheckVersionUrl() {
		return checkVersionUrl;
	}
	public String getAssessAddUrl() {
		return assessAddUrl;
	}
	public String getRepairOrderImageListUrl() {
		return repairOrderImageListUrl;
	}
	public String getAssessListUrl() {
		return assessListUrl;
	}
	public String getFeedbackRecordAddUrl() {
		return feedbackRecordAddUrl;
	}
	public String getFeedbackListUrl() {
		return feedbackListUrl;
	}
	public String getFeedbackTextAddUrl() {
		return feedbackTextAddUrl;
	}
	public String getRepairOrderPhotoUploadUrl() {
		return repairOrderPhotoUploadUrl;
	}
	public String getRepairOrderMaterialUsedUrl() {
		return repairOrderMaterialUsedUrl;
	}
	public String getRoomAssetListUrl() {
		return roomAssetListUrl;
	}
	public String getRepairOrderMaterialAddUrl() {
		return repairOrderMaterialAddUrl;
	}
	public String getRepairmanMaterialListUrl() {
		return repairmanMaterialListUrl;
	}
	public String getChangePasswordUrl() {
		return changePasswordUrl;
	}
	public String getStudentContactChangeUrl() {
		return studentContactChangeUrl;
	}
	public String getRepairmanContactChangeUrl() {
		return repairmanContactChangeUrl;
	}
	public String getRepairOrderSubmitUrl() {
		return repairOrderSubmitUrl;
	}
	public String getCheckSameOrderUrl() {
		return checkSameOrderUrl;
	}
	public String getLoginUrl() {
		return loginUrl;
	}

	public String getAssetListLoadUrl() {
		return assetListLoadUrl;
	}
	public String getTroubleListLoadUrl() {
		return troubleListLoadUrl;
	}
	public String getBuildingListLoadUrl() {
		return buildingListLoadUrl;
	}
	public String getRoomListLoadUrl() {
		return roomListLoadUrl;
	}
	public String getStreetListLoadUrl() {
		return streetListLoadUrl;
	}
	public String getLandmarkLoadUrl() {
		return landmarkLoadUrl;
	}
	public String getRepairOrderStudentPublicInnerLoadUrl() {
		return repairOrderStudentPublicInnerLoadUrl;
	}
	public String getRepairOrderStudentPublicOutterLoadUrl() {
		return repairOrderStudentPublicOutterLoadUrl;
	}
	public String getRepairOrderRepairmanDormAndPublicInnerLoadUrl() {
		return repairOrderRepairmanDormAndPublicInnerLoadUrl;
	}
	public String getRepairOrderRepairmanPublicOutterLoadUrl() {
		return repairOrderRepairmanPublicOutterLoadUrl;
	}
	public String getRepairOrderStudentDormLoadUrl() {
		return repairOrderStudentDormLoadUrl;
	}
	public String getStudentDormInfoUrl() {
		return studentDormInfoUrl;
	}

	public String getDomainUrl() {
		return domainUrl;
	}
	public String getRepairOrderAcceptUrl() {
		return repairOrderAcceptUrl;
	}
	public String getRepairOrderRefixUrl() {
		return repairOrderRefixUrl;
	}
	public String getRepairOrderFinishUrl() {
		return repairOrderFinishUrl;
	}
	public String getRepairOrderRefixFinishUrl() {
		return repairOrderRefixFinishUrl;
	}
	public String getRepairOrderAffirmUrl() {
		return repairOrderAffirmUrl;
	}
	public String getRepairOrderRefixAffirmUrl() {
		return repairOrderRefixAffirmUrl;
	}
	public String getRepairOrderMuchMoneyForwardUrl() {
		return repairOrderMuchMoneyForwardUrl;
	}
	public Context getmContext() {
		return mContext;
	}
	public String getRepairOrderDetailDormUrl() {
		return repairOrderDetailDormUrl;
	}
	public String getRepairOrderDetailPublicInnerUrl() {
		return repairOrderDetailPublicInnerUrl;
	}
	public String getRepairOrderDetailPublicOutterUrl() {
		return repairOrderDetailPublicOutterUrl;
	}
	public String getRepairOrderStudentAllLoadUrl() {
		return repairOrderStudentAllLoadUrl;
	}
	public String getRepairOrderRepairmanAllLoadUrl() {
		return repairOrderRepairmanAllLoadUrl;
	}
	public String getHomeProgressLoadList() {
		return homeProgressLoadList;
	}
	public String getMeterBuildingList() {
		return meterBuildingList;
	}
	public String getRoomMeterList() {
		return roomMeterList;
	}
	public String getOfficeMeterList() {
		return officeMeterList;
	}




}
