package com.wutong.androidprojectlibary.file.util;

import java.io.File;
import java.text.DecimalFormat;
import java.io.FileInputStream;

public class FileUtil
{

	private static FileUtil instance;

	public FileUtil()
	{

	}

	public static FileUtil getInstance()
	{
		if (instance == null)
		{
			instance = new FileUtil();
		}
		return instance;
	}


	public long getFileSizes(File f) throws Exception
	{
		long s = 0;
		if (f.exists())
		{
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		}
		else
		{
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}


	public long getFileNumber(File f) throws Exception
	{
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++)
		{
			if (flist[i].isDirectory())
			{
				size = size + getFileNumber(flist[i]);
			}
			else
			{
				size = size + flist.length;
			}
		}
		return size;
	}

	/**
	 * 获取文件夹下文件总大小
	 * @param file
	 * @return
	 */
	public long getDirSize(File file) {     
		//判断文件是否存在     
		if (file.exists()) {     
			//如果是目录则递归计算其内容的总大小    
			if (file.isDirectory()) {     
				File[] children = file.listFiles();     
				long size = 0;     
				for (File f : children) {    
					size += getDirSize(f);   
				}
				return size;     
			} 
			else {//如果是文件则直接返回其大小
				long size = file.length();        
				return size;     
			}     
		} else {     
			System.out.println("文件或者文件夹不存在，请检查路径是否正确！");     
			return 0;     
		}     
	}

	public String FormetFileSize(long fileS)
	{// 转换文件大小
		DecimalFormat df = new DecimalFormat("0.00");
		String fileSizeString = "";
		if(fileS == 0){
			return "缓存为空";
		}
		else if (fileS < 1024)
		{
			fileSizeString = df.format((double) fileS) + "B";
		}
		else if (fileS < 1048576)
		{
			fileSizeString = df.format((double) fileS / 1024) + "K";
		}
		else if (fileS < 1073741824)
		{
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		}
		else
		{
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}


	public long getlist(File f)
	{// 递归求取目录文件个数
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++)
		{
			if (flist[i].isDirectory())
			{
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;
	}

}