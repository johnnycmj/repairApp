package com.wutong.repair.dictionary;

import com.wutong.repairfjnu.R;

public class RepairOrderDict {

	public static final int ORDER_START_STATUS = 1;
	public static final int START_TO_DEAL_STATUS = 2;
	public static final int FIRST_DEALED_STATUS = 3;
	public static final int DIRECT_END_STATUS = 4;
	public static final int WANNA_REFIX_STATUS = -1;
	public static final int REFIX_DEALED_STATUS = 6; 
	public static final int REFIX_END_STATUS = 7;

	public static final int GROUP_START = 0x101;
	public static final int GROUP_ING = 0x102;
	public static final int GROUP_END = 0x103;
	public static final int GROUP_DEFAULT = 0x500;
	
	public static final String GROUP_START_NAME = "未受理";
	public static final String GROUP_ING_NAME = "维修中";
	public static final String GROUP_END_NAME = "已完成";
	public static final String GROUP_DEFAULT_NAME = "其他";


	public static int groupId(String statusStr){
		int status = Integer.valueOf(statusStr);
		switch (status) {
		case ORDER_START_STATUS:
			return GROUP_START;
		case START_TO_DEAL_STATUS:
			case WANNA_REFIX_STATUS:
			return GROUP_ING;
		case FIRST_DEALED_STATUS:
			case DIRECT_END_STATUS:
			case REFIX_END_STATUS:
			case REFIX_DEALED_STATUS:
			return GROUP_END;
		default:
			return GROUP_DEFAULT;
		}

	}
	
	public static String groupNameByStatus(String statusStr){
		int status = Integer.valueOf(statusStr);
		switch (status) {
		case ORDER_START_STATUS:
			return GROUP_START_NAME;
		case START_TO_DEAL_STATUS:
			case FIRST_DEALED_STATUS:
			case WANNA_REFIX_STATUS:
			return GROUP_ING_NAME;
		case DIRECT_END_STATUS:
			case REFIX_END_STATUS:
			case REFIX_DEALED_STATUS:
			return GROUP_END_NAME;
		default:
			return GROUP_DEFAULT_NAME;
		}

	}
	public static String groupName(String groupIdStr){
		int groupId = Integer.valueOf(groupIdStr);
		switch (groupId) {
		case GROUP_START:
			return GROUP_START_NAME;
		case GROUP_ING:
			return GROUP_ING_NAME;
		case GROUP_END:
			return GROUP_END_NAME;
		default:
			return GROUP_DEFAULT_NAME;
		}

	}

}
