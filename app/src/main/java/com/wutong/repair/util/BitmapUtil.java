package com.wutong.repair.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore;

public class BitmapUtil {

	private static Options option;

	private static Bitmap mBitmap;
	private static int scale = 1;
	private static final int REQUIRED_SIZE = 100;
	private static final int uploadHeight = 2048;//这里设置高度为1600f
	private static final int uploadWidth = 2048;//这里设置宽度为1200f
	private static final int showHeight = 800;//这里设置高度为1600f
	private static final int showWidth = 480;//这里设置宽度为1200f


	/**
	 * 图片压缩方法实现
	 * @param srcPath
	 * @return
	 */
	public static void photoCompress(String srcPath,OutputStream output) {
		option =new Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath,option);//只是获取图片的宽高，放在Options里
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		scale = 1;//scale=1表示不缩放
		if (option.outWidth > option.outHeight && option.outWidth > uploadWidth) {//如果宽度大的话根据宽度固定大小缩放
			scale = option.outWidth / uploadWidth;
		}
		else if (option.outWidth < option.outHeight && option.outHeight > uploadHeight) {//如果高度高的话根据高度固定大小缩放
			scale = option.outHeight / uploadHeight;
		}
		if (scale <= 0){
			scale = 1;
		}
		else{
			scale++;
		}
		option.inSampleSize = scale;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		option.inJustDecodeBounds = false;
		mBitmap = BitmapFactory.decodeFile(srcPath, option);
		compressQuality(mBitmap,output);//压缩好比例大小后再进行质量压缩
	}

	/**
	 * 图片压缩方法实现
	 * @param srcPath
	 * @return
	 */
	public static Bitmap compressImage(byte[] data) {
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		option =new Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length,option);
		Logger.i("compressImage option.outWidth:" + option.outWidth +"option.outHeight:" + option.outHeight);
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		scale = 1;//scale=1表示不缩放
		if (option.outWidth > option.outHeight && option.outWidth > showWidth) {//如果宽度大的话根据宽度固定大小缩放
			scale = option.outWidth / showWidth;
		}
		else if (option.outWidth < option.outHeight && option.outHeight > showHeight) {//如果高度高的话根据高度固定大小缩放
			scale = option.outHeight / showHeight;
		}
		if (scale <= 0){
			scale = 1;
		}
		else{
			scale++;
		}
		option.inSampleSize = scale;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		option.inJustDecodeBounds = false;
		Logger.i("width:" + option.outWidth +"height:" +option.outHeight);
		mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length,option);
		return mBitmap;
	}


	/**  
	 * 根据byte数组，生成文件  
	 * @throws IOException 
	 */  
	public static void byteArray2File(byte[] bfile, String filePath) throws IOException {   
		BufferedOutputStream bos = null;   
		FileOutputStream fos = null;   
		File file = null;   

		File dir = new File(filePath);   
		if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在   
			dir.mkdirs();   
		}   
		file = new File(filePath);   
		fos = new FileOutputStream(file);   
		bos = new BufferedOutputStream(fos);   
		bos.write(bfile);   
		bos.close();
		fos.close();
	}   



	/**
	 * 质量压缩
	 * @param image
	 * @return
	 */
	private static Bitmap compressQuality(Bitmap image,OutputStream output) {

		image.compress(Bitmap.CompressFormat.JPEG, 30, output);

		return image;
	}

	/**
	 * 将后台图片文件缩放到宽和高都大于100px的缩略图
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Bitmap convertBitmap(File file) throws IOException {
		option =new Options();
		option.inJustDecodeBounds = true;
		FileInputStream fis = new FileInputStream(file.getAbsolutePath());
		BitmapFactory.decodeStream(fis, null, option);
		fis.close();
		int width_tmp = option.outWidth, height_tmp = option.outHeight;
		Logger.i("convertBitmap option.outWidth:" + option.outWidth +"option.outHeight:" + option.outHeight);
		scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}
		option.inSampleSize = scale;
		fis = new FileInputStream(file.getAbsolutePath());
		option.inJustDecodeBounds = false;
		mBitmap = BitmapFactory.decodeStream(fis, null, option);
		fis.close();
		Logger.i("convertBitmap before return option.outWidth:" + option.outWidth +"option.outHeight:" + option.outHeight);
		return mBitmap;
	}
	
	public static Bitmap convertUri2Bitmap(Context context,Uri uri){
		try {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
