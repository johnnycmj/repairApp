package com.wutong.repair;
/**
 * 权限重复异常
 * <pre>
 * 防止权限重复添加，被替换后导致权限异常引起不可控的错误
 * </pre>
 * @author Jolly
 * 创建时间：2013-8-31下午3:37:05
 *
 */
@SuppressWarnings("serial")
public class PermissionRepeatException extends RuntimeException{

	public PermissionRepeatException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PermissionRepeatException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public PermissionRepeatException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public PermissionRepeatException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
