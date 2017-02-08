package com.wutong.repair.dictionary;

public class QueryMaterialDict {

	public static final String UNQUERY = "1";
	public static final String QUERYED = "2";
	
	public static String getStatusName(String status){
		if(status == null){
			return "异常的状态";
		}
		else if(status.equals(UNQUERY)){
			return "未确认";
		}
		else if(status.equals(QUERYED)){
			return "已确认";
		}
		else{
			return "意外的状态";
		}
	}
}
