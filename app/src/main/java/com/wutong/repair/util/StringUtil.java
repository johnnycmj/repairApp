package com.wutong.repair.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.Html;
import android.text.Spanned;


public class StringUtil {
	private static final String PATTERN_TIME = "yyyy-MM-dd HH:mm";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_TIME);
	
	public static byte[] hex2byte(String s) {  
	    byte[] src = s.toLowerCase().getBytes();  
	    byte[] ret = new byte[src.length / 2];  
	    for (int i = 0; i < src.length; i += 2) {  
	        byte hi = src[i];  
	        byte low = src[i + 1];  
	        hi = (byte) ((hi >= 'a' && hi <= 'f') ? 0x0a + (hi - 'a')  
	                : hi - '0');  
	        low = (byte) ((low >= 'a' && low <= 'f') ? 0x0a + (low - 'a')  
	                : low - '0');  
	        ret[i / 2] = (byte) (hi << 4 | low);  
	    }  
	    return ret;  
	}
	
	/** 
	 * 格式化byte 
	 *  
	 * @param b 
	 * @return 
	 */  
	public static String byte2hex(byte[] b) {  
	    char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',  
	            'B', 'C', 'D', 'E', 'F' };  
	    char[] out = new char[b.length * 2];  
	    for (int i = 0; i < b.length; i++) {  
	        byte c = b[i];  
	        out[i * 2] = Digit[(c >>> 4) & 0X0F];  
	        out[i * 2 + 1] = Digit[c & 0X0F];  
	    }  
	    return new String(out);  
	}  
	
	public static String time2DateString(String time){
		Date date = new Date(Long.valueOf(time));
		return TimeUtil.friendlyDate(date);
	}
//	public static String time2FullString(String time){
//		Date date = new Date(Long.valueOf(time));
//		return TimeUtil.beautifulTime(date);
//	}
	
	public static String time2FullStringByStringType(String time) {
		Date date;
		try {
			date = simpleDateFormat.parse(time);
			return TimeUtil.beautifulTime(date);
		} catch (ParseException e) {
			return "时间异常";
		}
	}
	public static String time2FullStringByStringType(String time,String pattern) {
		Date date;
		try {
			date = simpleDateFormat.parse(time);
			return TimeUtil.beautifulTime(date,pattern);
		} catch (ParseException e) {
			return "时间异常";
		}
	}
	
	/**
	 * 
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static String time2DateString(String time,String pattern) {
		Date date;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			date = sdf.parse(time);
			return TimeUtil.friendlyDate(date);
		} catch (ParseException e) {
			return "时间异常";
		}
	}
	public static Spanned errorHtml(String content){
		return Html.fromHtml("<font color='#CC0000'>"+content+"</font>"); 
	}
}
