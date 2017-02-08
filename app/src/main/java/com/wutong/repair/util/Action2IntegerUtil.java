package com.wutong.repair.util;

public class Action2IntegerUtil {
	
	public static final String ACTION_SEND_HEART = "com.wutong.repair.sendheart";
	public static final int ACTION_SEND_HEART_INT = 1;
	public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	public static final int ACTION_BOOT_COMPLETED_INT = 2;
	public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final int ACTION_CONNECTIVITY_CHANGE_INT = 3;
	public static final String ACTION_LOGIN = "com.wutong.repair.receiver.login";
	public static final int ACTION_LOGIN_INT = 4;
	public static final String ACTION_OPEN_NOTICE_SERVICE = "com.wutong.repair.receiver.opennoticeservice";
	public static final int ACTION_OPEN_NOTICE_SERVICE_INT = 5;
	public static final String ACTION_CLOSE_NOTICE_SERVICE = "com.wutong.repair.receiver.closenoticeservice";
	public static final int ACTION_CLOSE_NOTICE_SERVICE_INT = 6;
	public static final String ACTION_RESTART_NOTICE_SERVICE = "com.wutong.repair.receiver.restartnoticeservice";
	public static final int ACTION_RESTART_NOTICE_SERVICE_INT = 7;
	public static final String ACTION_CHECK_UPDATE_CHECK = "com.wutong.repair.receiver.checkupdatecheck";
	public static final int ACTION_CHECK_UPDATE_CHECK_INT = 8;
	public static final String ACTION_CHECK_UPDATE_UPDATE = "com.wutong.repair.receiver.checkupdateupdate";
	public static final int ACTION_CHECK_UPDATE_UPDATE_INT = 9;
	public static final String ACTION_BOOT_COMPLETED_BROADCAST = "com.wutong.repair.receiver.bootcompletedbroadcast";
	public static final int ACTION_BOOT_COMPLETED_BROADCAST_INT = 10;
	
	public static int action2Int(String action){
		if(action.equalsIgnoreCase(ACTION_SEND_HEART)){
			return ACTION_SEND_HEART_INT;
		}
		else if(action.equalsIgnoreCase(ACTION_BOOT_COMPLETED)){
			return ACTION_BOOT_COMPLETED_INT;
		}
		else if(action.equalsIgnoreCase(ACTION_CONNECTIVITY_CHANGE)){
			return ACTION_CONNECTIVITY_CHANGE_INT;
		}
		else if(action.equalsIgnoreCase(ACTION_LOGIN)){
			return ACTION_LOGIN_INT;
		}
//		else if(action.equalsIgnoreCase(ACTION_OPEN_NOTICE_SERVICE)){
//			return ACTION_OPEN_NOTICE_SERVICE_INT;
//		}
//		else if(action.equalsIgnoreCase(ACTION_CLOSE_NOTICE_SERVICE)){
//			return ACTION_CLOSE_NOTICE_SERVICE_INT;
//		}
//		else if(action.equalsIgnoreCase(ACTION_RESTART_NOTICE_SERVICE)){
//			return ACTION_RESTART_NOTICE_SERVICE_INT;
//		}
		else {
			return 0;
		}
	}

}
