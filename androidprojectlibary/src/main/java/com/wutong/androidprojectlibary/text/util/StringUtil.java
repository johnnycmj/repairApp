package com.wutong.androidprojectlibary.text.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;


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
	 * 将包含回车的一段文字进行段落化,回车替换为"回车"+“2个全角空格”
	 * @return
	 */
	public static String paragraph(String content){
		content = content.replaceAll("\n", "\n　　");
		return "　　"+content;
	}

	/**
	 * setError中文字的背景白色，边框红色，防止颜色混淆，导致看不见
	 * <pre>
	 * </pre>
	 * @param content
	 * @return
	 */
	public static Spanned errorHtml(String content){
		SpannableStringBuilder errorContent = new SpannableStringBuilder(content+"　　");
		errorContent.setSpan(new ForegroundColorSpan(Color.RED),0,content.length()-1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return errorContent; 
	}

	/**
	 * 字符串首尾空格去掉
	 * @param s
	 * @return
	 */
	public static String trim(String s) {
		int i = s.length();// 字符串最后一个字符的位置
		int j = 0;// 字符串第一个字符
		int k = 0;// 中间变量
		char[] arrayOfChar = s.toCharArray();// 将字符串转换成字符数组
		while ((j < i) && (arrayOfChar[(k + j)] <= ' '))
			++j;// 确定字符串前面的空格数
		while ((j < i) && (arrayOfChar[(k + i - 1)] <= ' '))
			--i;// 确定字符串后面的空格数
		return (((j > 0) || (i < s.length())) ? s.substring(j, i) : s);// 返回去除空格后的字符串
	}

	public static String splitAndFilterString(String input, int length) {
		if (input == null || input.trim().equals("")) {
			return "";
		}
		// 去掉所有html元素(优雅)
		String str = input.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "").replaceAll("</[a-zA-Z]+[1-9]?>", "");
		str = str.replaceAll("[(/>)<]", "");
		int len = str.length();
		if (len <= length) {
			return str;
		} else {
			str = str.substring(0, length);
			str += "......";
		}
		return str;
	}
}
