package com.wutong.androidprojectlibary.file.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.wutong.androidprojectlibary.log.util.Logger;

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

		image.compress(CompressFormat.JPEG, 30, output);

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
			e.printStackTrace();
			return null;
		}
	}

	public static boolean saveBitmap2file(Bitmap bmp,File saveFile){
		CompressFormat format= CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(saveFile);
			return bmp.compress(format, quality, stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap){
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(outBitmap);
		final int color =0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPX = bitmap.getWidth()/2;
		paint.setAntiAlias(true);
		canvas.drawARGB(0,0,0,0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return outBitmap;
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024>100) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 图片压缩成，固定宽度
	 * @param image
	 * @return
	 */
	public static Bitmap getComp(Bitmap image,float hh,float ww) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		image.compress(CompressFormat.JPEG, 100, baos);
		if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出	
			baos.reset();//重置baos即清空baos
			image.compress(CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Options newOpts = new Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		//float hh = 800f;//这里设置高度为800f
		//float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}

	// 获取指定路径的图片  
	public static Bitmap getImage(String urlpath)  
			throws Exception {  
		URL url = new URL(urlpath);  
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
		conn.setRequestMethod("POST");  
		conn.setConnectTimeout(5 * 1000);  
		Bitmap bitmap = null;  
		if (conn.getResponseCode() == 200) {  
			InputStream inputStream = conn.getInputStream();  
			bitmap = BitmapFactory.decodeStream(inputStream);  
		}  
		return bitmap;  
	}  

	/**
	 * 图片实现圆角特效
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap source, int radius) {  

		int width = source.getWidth();
        int height = source.getHeight();
 
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(android.graphics.Color.WHITE);
 
        Bitmap clipped = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(clipped);
        canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius,
                paint);
        
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        
        source.recycle();
 
        return clipped;
	}
	/**
	 * 图片实现圆角特效
	 * @param context
	 * @param ResId
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Context context,int ResId, int radius){
		Drawable drawable = context.getResources().getDrawable(ResId);
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return toRoundCorner(bd.getBitmap(), radius);
	}
}
