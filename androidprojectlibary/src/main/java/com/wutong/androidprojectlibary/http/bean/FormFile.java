package com.wutong.androidprojectlibary.http.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 锟较达拷锟侥硷拷
 */
public class FormFile {
	/* 锟较达拷锟侥硷拷锟斤拷锟斤拷锟� */
	private byte[] data;
	private InputStream inStream;
	private File file;
	/* 锟侥硷拷锟斤拷锟� */
	private String filname;
	/* 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷*/
	private String parameterName;
	/* 锟斤拷锟斤拷锟斤拷锟斤拷 */
	private String contentType = "application/octet-stream";
	
	public FormFile(String filname, byte[] data, String parameterName, String contentType) {
		this.data = data;
		this.filname = filname;
		this.parameterName = parameterName;
		if(contentType!=null) this.contentType = contentType;
	}
	
	public FormFile(String filname, File file, String parameterName, String contentType) {
		this.filname = filname;
		this.parameterName = parameterName;
		this.file = file;
		try {
			this.inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(contentType!=null) this.contentType = contentType;
	}
	
	public File getFile() {
		return file;
	}

	public InputStream getInStream() {
		return inStream;
	}

	public byte[] getData() {
		return data;
	}

	public String getFilname() {
		return filname;
	}

	public void setFilname(String filname) {
		this.filname = filname;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
