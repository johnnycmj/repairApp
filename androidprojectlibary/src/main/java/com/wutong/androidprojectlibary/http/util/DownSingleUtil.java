package com.wutong.androidprojectlibary.http.util;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import com.wutong.androidprojectlibary.log.util.Logger;

public class DownSingleUtil {
	
	private String path;
	private String targetFile;
	private OnUpdateProgressListener mUpdateProgressListener;
	
	
	private int fileSize;

	public DownSingleUtil(String path, String targetFile) {
		super();
		this.path = path;
		this.targetFile = targetFile;
	}
	
	
	public boolean download() throws Exception{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0(Linux;U;Android 2.3.3;en-us;Nexus One Build.FRG83) "
		+"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
		conn.setRequestProperty("Connection", "Keep-Alive");
		//
		InputStream inputStream = conn.getInputStream();
		int contentLength = conn.getContentLength();
		Logger.i("长度："+contentLength);
		RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
		file.setLength(fileSize);
		byte[] buffer = new byte[1024];
		int hasRead = 0;
		int doneLength = 0;
		while((hasRead = inputStream.read(buffer)) != -1){
			file.write(buffer,0,hasRead);
			doneLength +=buffer.length;
			mUpdateProgressListener.onUpdateProgress(doneLength,contentLength);
		}
		file.close();
		inputStream.close();
		return true;
	}
	
	public void setOnUpdateProgressListener(
			OnUpdateProgressListener onUpdateProgressListener) {
		this.mUpdateProgressListener = onUpdateProgressListener;
	}

	public interface OnUpdateProgressListener {
		public void onUpdateProgress(int doneLength, int contentLength);
	}

}
