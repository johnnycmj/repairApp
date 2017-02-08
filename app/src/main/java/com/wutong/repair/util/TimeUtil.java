package com.wutong.repair.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间显示工具类
 * @author Jolly
 *
 */
public class TimeUtil {
	public static final String SQL_PATTERN_TIME = "yyyy-MM-dd HH:mm.S";
	private static final String PATTERN_TIME = "yyyy-MM-dd HH:mm";
	private static final String PATTERN_DATE = "yyyy-MM-dd";
	private static final String PATTERN_DATE_LITE = "MM-dd";
	private static final String ONLY_TIME = "HH:mm:ss";
	private static final String ONLY_TIME_LITE = "HH:mm";
	private static final String TODAY = "今天";
	private static final String YESTODAY = "昨天";
	private static final String Before_YESTODAY = "前天";
	private static final String SECOND_UNIT = "秒";
	private static final String MINE_UNIT = "分";
	private static final String HOUR_UNIT = "时";
	private static final String BLANKS = "  ";/*多个空格*/
	/**
	 * 时间秒数转换时分秒显示
	 * @param dur
	 * @return
	 */
	public static String timeNum2Show(long dur){
		if(dur < 60){
			//一分钟内用秒
			return dur+SECOND_UNIT;
		}
		else if (dur < 3600){
			//一小时内用分+秒

			long sec = dur%60;
			long mine = (dur-sec)/60;
			return mine + MINE_UNIT + sec +SECOND_UNIT;
		}
		else if(dur <86400){
			//一天内用时+分+秒
			long sec = dur%60;
			long mine = (dur-sec)%60;
			long hour = (dur-sec-mine)/3600;
			return hour + HOUR_UNIT + mine + MINE_UNIT + sec +SECOND_UNIT;
		}
		else {
			return "大于一天";
		}
	}
	/**
	 * 格式化时间
	 * @return
	 */
	public static String beautifulTime(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_TIME);
		return sdf.format(date);
	}
	
	/**
	 * 格式化时间
	 * @return
	 */
	public static String beautifulDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_DATE_LITE);
		return sdf.format(date);
	}
	
	/**
	 * 根据格式，格式化时间
	 * @param timestamp 时间
	 * @param pattern 格式
	 * @return
	 */
	public static String beautifulTime(Date date,String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * 友好的时间提示
	 * @param time
	 * @return
	 */
	public static String friendlyTime(Date time){
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
		Date today = calendar.getTime();
		calendar.add(Calendar.DATE, -1);
		Date yesToday = calendar.getTime();
		
		
		calendar.add(Calendar.DATE, -1);
		Date beforeYesToday = calendar.getTime();
		if(time.after(today)){
			//今天
			return beautifulTime(time, ONLY_TIME);
		}
		else{
			if(time.after(yesToday)){
				//昨天 
				return YESTODAY + BLANKS + beautifulTime(time, ONLY_TIME);
			}
			else if(time.after(beforeYesToday)){
				//前天
				return Before_YESTODAY + BLANKS + beautifulTime(time, ONLY_TIME);
			}
			else{
				return beautifulTime(time,PATTERN_DATE);
			}
		}
	}
	
	/**
	 * 友好的时间提示
	 * @param time
	 * @return
	 */
	public static String friendlyDate(Date time){
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
		Date today = calendar.getTime();
		calendar.add(Calendar.DATE, -1);
		Date yesToday = calendar.getTime();
		
		
		calendar.add(Calendar.DATE, -1);
		Date beforeYesToday = calendar.getTime();
		if(time.after(today)){
			//今天
			return beautifulTime(time, ONLY_TIME_LITE);
		}
		else{
			if(time.after(yesToday)){
				//昨天 
				return YESTODAY + BLANKS + beautifulTime(time, ONLY_TIME_LITE);
			}
			else if(time.after(beforeYesToday)){
				//前天
				return Before_YESTODAY + BLANKS + beautifulTime(time, ONLY_TIME_LITE);
			}
			else{
				return beautifulDate(time);
			}
		}
	}
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String currentTime(){
		Calendar calendar = Calendar.getInstance();
		return beautifulTime(calendar.getTime());
	}

}
