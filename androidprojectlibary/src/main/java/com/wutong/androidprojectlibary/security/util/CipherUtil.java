package com.wutong.androidprojectlibary.security.util;

import android.util.Base64;

public class CipherUtil {

//	public static class DESCipher{
//		public static byte[] key = null;
////		public static String encript(String data){
////			try {
////				if(key == null){
////					key = DESedeCoder.initKey();
////				}
////				return StringUtil.byte2hex(DESedeCoder.encrypt(data.getBytes(), key));
////			} catch (Exception e) {
////				e.printStackTrace();
////				return null;
////			}
////		}
////		
////		public static String decript(String data){
////			try {
////				if(key == null){
////					key = DESedeCoder.initKey();
////				}
////				return new String(StringUtil.hex2byte(new String(DESedeCoder.decrypt(data.getBytes(), key))));
////			} catch (Exception e) {
////				e.printStackTrace();
////				return null;
////			}
////		}
//		
//		public static String encript(String data){
//			return data;
//		}
//		
//		public static String decript(String data){
//			return data;
//		}
//	}
	
	public static class Base64Cipher{
		public static String encript(String data){
			return Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
		}
		
		public static String decript(String data){
			return new String(Base64.decode(data, Base64.DEFAULT));
		}
	}
}
