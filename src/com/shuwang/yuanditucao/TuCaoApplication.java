package com.shuwang.yuanditucao;


import android.app.Application;

public class TuCaoApplication extends Application {
	
	public static double position_latitude;//当前位置的纬度
	public static double position_longitude;//当前位置的经度
	
	private final String TAG = "TuCao.App";
	
	@Override
	public void onCreate() {
		super.onCreate();
	}	
}
