package com.shuwang.yuanditucao;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.shuwang.yuanditucao.util.ImageUtil;

//EB:2E:75:86:D9:22:B3:A2:A3:C6:57:38:EE:10:91:59:BD:C7:E7:58,com.shuwang.yuanditucao
public class MainActivity extends Activity implements OnClickListener{

	MapView mMapView = null;
	BaiduMap mBaiduMap;
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatus mapStatus = new MapStatus.Builder().zoom(20).build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
		mBaiduMap.setMapStatus(mapStatusUpdate);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		
		mLocClient.start();
		
		
		//new WebSocketHelper().connect();
		
		fileUtil = new ImageUtil();
		
		
		List<TuCao> items = new ArrayList<TuCao>();
		items.add(new TuCao(new Date(),"xxxxx","url",122.12121,12.121212));
		items.add(new TuCao(new Date(),"xxxxx","url",122.12121,12.121212));
		items.add(new TuCao(new Date(),"xxxxx","url",122.12121,12.121212));
		items.add(new TuCao(new Date(),"xxxxx","url",122.12121,12.121212));
		items.add(new TuCao(new Date(),"xxxxx","url",122.12121,12.121212));
		items.add(new TuCao(new Date(),"xxxxx","url",122.12121,12.121212));
		items.add(new TuCao(new Date(),"xxxxx","url",122.12121,12.121212));
		items.add(new TuCao(new Date(),"xxxxx","url",122.12121,12.121212));
			
		
		ListView listView = (ListView)findViewById(R.id.listview);
		listView.setAdapter(new TuCaoAdapter(this,items));
		
		addListener();
	}
	
	private void addListener(){
		ImageView cTV = (ImageView) findViewById(R.id.footer_btn_capture);
		cTV.setOnClickListener(this);
		
		ImageView textViewRenew = (ImageView) findViewById(R.id.footer_btn_record);
		textViewRenew.setOnClickListener(this);		
		
		ImageView bmTV = (ImageView)findViewById(R.id.footer_btn_mood);
		bmTV.setOnClickListener(this);
	}
	
	private ImageUtil fileUtil;

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.footer_btn_capture){

    		this.fileUtil.initFilePath();
    		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
            String rawName = Const.JPEG_FILE_PREFIX +"_"+ timeStamp + Const.JPEG_FILE_SUFFIX;
            String mImageFileName =Const.JPEG_FILE_Large_PIC  + rawName;
            
    		File imageFile = new File(this.fileUtil.getPhotoFullName(mImageFileName));
    		Uri imageFileUri = Uri.fromFile(imageFile);
    		
   			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
   			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,imageFileUri);
   			startActivityForResult(intent, Const.ACTIVITY_ACTION_TAKE_PHOTO);

		}
		if(v.getId() == R.id.footer_btn_record){
		}
		if(v.getId() == R.id.footer_btn_mood){
		}
		
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}	
	
}
