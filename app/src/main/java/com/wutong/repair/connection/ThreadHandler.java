package com.wutong.repair.connection;


import com.wutong.repair.util.HttpResponseUtil;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class ThreadHandler extends Handler implements Runnable{
	protected final static int CONN_TIME_OUT = 0x20;
	protected final static int CONN_FAILED = 0x01;
	protected final static int CONN_THROW_EXCEPTION = 0x31;
	protected final static int SUCCESS = 0x11;
	protected final static int FAILED = 0x10;
	protected final static int ZERO = 0x00;
	

	/**
	 * IOException默认处理的方法
	 * @param e
	 */
	protected final void dealWithIOException(Exception exception){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putSerializable("exception", exception);
		msg.setData(data );
		this.sendMessage(msg );
	}
}
