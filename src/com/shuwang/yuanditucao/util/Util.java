package com.shuwang.yuanditucao.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

import com.shuwang.yuanditucao.Const;

public class Util {
	private static String TAG = "TuCao.Util";
	
	public static String getAppDir(String subDir) {
		File dir = new File(Environment.getExternalStorageDirectory(),Const.APP_HOME_PATH+"/"+subDir );
		if(dir.exists()) return dir.getAbsolutePath();
		if(!dir.mkdirs()) {
			Log.e(TAG, "Image create failed,Path:" + dir.getPath());
			return null;
		}
		return dir.getAbsolutePath();
	}
	
	public static String generateFileName(String prefix,String subfix) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		return prefix + "_" + timeStamp + "."+subfix;
	}	
	
	
}
