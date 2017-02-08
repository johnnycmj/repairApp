package com.wutong.androidprojectlibary.file.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class CommonSwitcherUtil {

	public static  File getAbsoluteImagePath(Activity activity,Uri uri){  
		// can post image   
		String [] proj={MediaStore.Images.Media.DATA};  
		Cursor cursor =activity.managedQuery( uri,proj,null,null,null);

		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
		cursor.moveToFirst();  
		File file = new File(cursor.getString(column_index));
		return file;  
	} 
}
