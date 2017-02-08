package com.wutong.androidprojectlibary.validate.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringValidateUtil {
	/**  
	 * 验证手机号码  
	 * @param mobiles  
	 * @return  true if phone number or false for otherwise 
	 */  
	public static boolean isPhoneNumber(String mobiles){  
		boolean flag = false;  
		try{  
			Pattern p = Pattern.compile("1[0-9]{10}");  
			Matcher m = p.matcher(mobiles);  
			flag = m.matches();  
		}catch(Exception e){  
			flag = false;  
		}  
		return flag;  
	} 

	/**
	 * 验证邮箱
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){  
		boolean flag = false;  
		try{  
			Pattern p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");  
			Matcher m = p.matcher(email);  
			flag = m.matches();  
		}catch(Exception e){  
			flag = false;  
		}  
		return flag;  
	}

	/**
	 * 字符串是否在长度范围内
	 * @param text
	 * @param minLength 最小长度，包括之
	 * @param maxLength 最长长度，包括之
	 * @return
	 */
	public static boolean isBetweenLength(String text,int minLength,int maxLength){
		if(text.length() < minLength){
			return false;
		}
		if(text.length() > maxLength){
			return false;	
		}
		return true;

	}
}
