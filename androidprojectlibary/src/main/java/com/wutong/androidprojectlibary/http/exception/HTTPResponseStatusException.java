package com.wutong.androidprojectlibary.http.exception;

@SuppressWarnings("serial")
public class HTTPResponseStatusException extends RuntimeException {

	public HTTPResponseStatusException() {
		super();
	}

	public HTTPResponseStatusException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public HTTPResponseStatusException(String detailMessage) {
		super(detailMessage);
	}

	public HTTPResponseStatusException(Throwable throwable) {
		super(throwable);
	}

}
