package com.wutong.repair.util;

public enum ExceptionErrorEnum {
	
	ConnectException("无法连接服务器"),
	DataInvalidException("出错啦，错误代码[-1]"),
	SocketMessageInvalidException("出错啦，错误代码[-2]"),
	SocketTimeoutException("连接超时"),
	AuthenticationException("无"),
	IOException("其他异常");

	final private String  value;

	ExceptionErrorEnum(final String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

}
