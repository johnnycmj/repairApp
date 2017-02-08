package com.wutong.repair.util;



import android.app.Activity;
import android.os.Environment;

public class SettingConfig {
	public final static String LOGIN_CONFIG = "config";
	public final static String TEMP_PHOTO = "temp_ph";
	public final static String PHOTO_CACHE_DICT = "photo_cache_dict";
	public final static String TEMP_PHOTO_LAST_TIME = "temp_last_time";
	public final static String SERVICE_LOG = "log";
	public final static String BASE_DIR = "/repair";
	public final static String FAST_MESSAGE_CONFIG = "useful_expresstions";
	public final static String PERMISSION_CONFIG = "BEDD536D9DB65D5BEBB0E6B72A8FCA01";
	/**
	 * 拍照图片文件夹
	 */
	public final static String PHOTO_CACHE_DIR = BASE_DIR + "/photos/";
	/**
	 * 接收图片文件夹
	 */
	public final static String LOAD_IMAGE_CACHE_DIR = BASE_DIR + "/cache/";
	
	public static String getPhotoCacheDir(Activity activity){
		return Environment.getExternalStorageDirectory().getPath() + PHOTO_CACHE_DIR;
//		SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
//		boolean isExternalStorage = preferences.getBoolean(activity.getString(R.string.is_cache_dir_external_storage), false);
//		if(isExternalStorage){
//			return Environment.getExternalStorageDirectory().getPath() + PHOTO_CACHE_DIR;
//		}
//		else{
//			
//			return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + PHOTO_CACHE_DIR;
//		}
	}
	
	public static String getLoadImageCacheDir(Activity activity){
		return Environment.getExternalStorageDirectory().getPath() +LOAD_IMAGE_CACHE_DIR;
//		SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
//		boolean isExternalStorage = preferences.getBoolean(activity.getString(R.string.is_cache_dir_external_storage), false);
//		if(isExternalStorage){
//			return Environment.getExternalStorageDirectory().getPath() + LOAD_IMAGE_CACHE_DIR;
//		}
//		else{
//			return Environment.getRootDirectory().getPath() + LOAD_IMAGE_CACHE_DIR;
//		}
	}
	
	public static String getUpdateFilePath(){
		return Environment.getExternalStorageDirectory().getPath() +"/repair.apk";
	}
	
	public final static class LoginForm{
		public static final String REMEBER_PASSWORD = "8B8FFA6CFD87879EAEB753F098065656";
		public static final String AUTO_SIGN_IN = "D0A34172E460CFE7D901EFBB1A461227";
		public static final String USERNAME_TEXTVIEW = "FB162399DB3673D497BCF9A36662572B";
		public static final String PASSWORD_TEXTVIEW = "910AED06E9B32530CDBA2D0C436A0B33";
	}
	
	public final static class RepairOrderForm{
		public static final String NUMBER_OF_PHOTOES = "293E1449BD91241F3D3F00F9F4D0DE83";
		public static final String LAST_KEY_NAME = "DFE0EA8B0485B113B2B3C30BBF89FA61";
	}
	
//	public final static class DormInfo{
//		public static final String FLOOR_NAME = "15AA5634CDCBD907B48060877A4DE971";
//		public static final String ROOM_NAME = "A3AA34CEE9AE33EFDC9342AAAE85D24B";
//		public static final String ROOM_FLOOR = "2560E3FD469616FB68DB31BF28858AE4";
//		public static final String BED_NODE = "AB8F62A2A3A108170FDAA697E010FF3C";
//		public static final String STUDENT_NAME = "8190128750060A13830360B0B2A6DDA7";
//		//public static final String PHONE = "F9DD946CC89C1F3B41A0EDBE0F36931D";
//		public static final String DORMROOM_ID = "86360F488AD1C63BA58F7BCB19473624";
//		
//	}
	
	public final static class PersonInfo{
		public static final String UID = "E7D22294BDCB7133967C3548ECE982E5";
	}
	
	
	public final static class QuietTime{
		public static final String SLIENT_PERIOD = "slient_period";
		public static final String SLIENT_WEEKDAY = "slient_weekday";
	}
	
	public final static class FastMessage{
		public static final String USEFUL_EXPRESSTIONS = "useful_expressions";
	}

	public final static class FirstRunning{
		public static final String FEEDBACK_USEFUL_EXPRESSION_FLAG = "useful_expression_flag";
	}
	
	public final static class UpdateInfo{
		public static final String LAST_UPDATE_TIME = "last_update_time";
	}
}
