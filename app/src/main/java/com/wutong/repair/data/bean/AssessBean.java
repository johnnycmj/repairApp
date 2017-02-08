package com.wutong.repair.data.bean;

import com.wutong.repair.fragment.util.SingleSelectDialogFragment.SingleSelectable;


/**
 * 评价项
 * @author Jolly
 * 创建时间：2013-7-20下午4:29:59
 *
 */
public class AssessBean implements SingleSelectable{

	private String assessTypeCode;
	private String repairFormId;
	private String assessTypeName;
	public String getAssessTypeCode() {
		return assessTypeCode;
	}
	public void setAssessTypeCode(String assessTypeCode) {
		this.assessTypeCode = assessTypeCode;
	}
	public String getRepairFormId() {
		return repairFormId;
	}
	public void setRepairFormId(String repairFormId) {
		this.repairFormId = repairFormId;
	}
	public String getAssessTypeName() {
		return assessTypeName;
	}
	public void setAssessTypeName(String assessTypeName) {
		this.assessTypeName = assessTypeName;
	}
	@Override
	public String getTextName() {
		return assessTypeName;
	}
	@Override
	public void setTextName(String textName) {
		
	}
	@Override
	public String getValue() {
		return assessTypeCode;
	}
	@Override
	public void setValue(String value) {
		
	}
	@Override
	public String getExtra() {
		return null;
	}
	@Override
	public void setExtra(String extra) {
		
	}
	
	
}
