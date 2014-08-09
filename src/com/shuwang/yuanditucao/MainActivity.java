package com.shuwang.yuanditucao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.shuwang.yuanditucao.RecordImageView.OnFinishedRecordListener;
import com.shuwang.yuanditucao.entity.TuCao;
import com.shuwang.yuanditucao.util.ImageUtil;
import com.shuwang.yuanditucao.util.Util;

//EB:2E:75:86:D9:22:B3:A2:A3:C6:57:38:EE:10:91:59:BD:C7:E7:58,com.shuwang.yuanditucao
public class MainActivity extends Activity implements OnClickListener,OnTouchListener{

	MapView mMapView = null;
	BaiduMap mBaiduMap;
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        ImageUtil.screenWidth = metric.widthPixels; // 屏幕宽度（像素）
        ImageUtil.screenHeight = metric.heightPixels; // 屏幕高度（像素）

		
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
		
		
		this.items = new ArrayList<TuCao>();
		ListView listView = (ListView)findViewById(R.id.listview);
		tucaoAdapter = new TuCaoAdapter(this,items);
		listView.setAdapter(tucaoAdapter);
		
		this.recordView = (RecordImageView)findViewById(R.id.footer_btn_record);
		
		initRecordButton();
		
		addFooterButtonsListener();
	}
	
	private List<TuCao> items;
	
	
	private TuCaoAdapter tucaoAdapter;
	private RecordImageView recordView;
	
	private void addFooterButtonsListener(){
		findViewById(R.id.footer_btn_capture).setOnClickListener(this);
		findViewById(R.id.footer_btn_mood).setOnClickListener(this);
		findViewById(R.id.footer_btn_record).setOnTouchListener(this);		
	}
	
	private ImageUtil fileUtil;
	private String TAG = "TuCao";
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult 调用");
		if (requestCode == Const.ACTIVITY_ACTION_TAKE_PHOTO) {
			if (resultCode == RESULT_OK) {
				Log.i(TAG, "照完了："+System.currentTimeMillis());
				TuCao tucao = new TuCao();
				tucao.imagePath = this.mImageFileName;
				this.tucaoAdapter.insert(tucao,0);//插入到头部
				this.updateListView();
			}
		}
	}
	
	private void updateListView(){
		this.tucaoAdapter.notifyDataSetChanged();//通知界面刷新
		
		for(TuCao t : this.items){
			Log.d(TAG,"baidu is displaying PIN:("+t.latitude+","+t.longitude+")");
			
			LatLng llA = new LatLng(t.latitude, t.longitude);
			LayoutInflater flater = LayoutInflater.from(this);
			View view = flater.inflate(R.layout.baidu_pin, null);
			BitmapDescriptor bdA = BitmapDescriptorFactory.fromView(view);
			OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA)
					.zIndex(9);
			Marker mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
			
			
		}
		
	}
	
	private String mImageFileName;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//录音的按钮
		if(v.getId() == R.id.footer_btn_record &&
			event.getAction() == MotionEvent.ACTION_DOWN){//第一次按下的时候
			
			String soundFileName = Util.generateFileName("audio","amr");
			String path = Util.getAppDir("audio") + "/"+ soundFileName;
			Log.d(TAG,"record file path:"+path);
			this.recordView.setSavePath(path);
		}
		return false;//一定要返回false，否则控件本身的onTouchEvent不会被调用
	}
	
	@Override
	public void onClick(View v) {
		
		//按照相的按钮
		if(v.getId() == R.id.footer_btn_capture){

			this.mImageFileName = Util.generateFileName("img","jpg");
            String imageFullPath = Util.getAppDir("pictures") + "/"+ this.mImageFileName;
            
    		File imageFile = new File(imageFullPath);
    		Uri imageFileUri = Uri.fromFile(imageFile);
    		
   			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
   			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,imageFileUri);
   			startActivityForResult(intent, Const.ACTIVITY_ACTION_TAKE_PHOTO);

		}
		
		//发表评论文字
		if(v.getId() == R.id.footer_btn_mood){
			LinearLayout ll = (LinearLayout)this.findViewById(R.id.MainView);
	
			if(findViewById(R.id.footer_text)==null){
				//将评论的footer吹起来
				LayoutInflater flater = LayoutInflater.from(this);
				View view = flater.inflate(R.layout.footer_text, null);
				ll.addView(view);
				//别忘了加上事件响应
				findViewById(R.id.footer_button).setOnClickListener(this);
			}
			
			//3个按钮的footer，换成评论的footer
			findViewById(R.id.footer_text).setVisibility(View.VISIBLE);
			findViewById(R.id.footer).setVisibility(View.GONE);
		}
		
		//发表文本的按钮的时候，整个footer替换成另外一个视图（发文本视图），
		//发表完毕后，再切换回3个按钮的视图
		if(v.getId() == R.id.footer_button){
			EditText contentET = (EditText)findViewById(R.id.footer_content);
			String content = contentET.getText().toString();
			if(content == null || "".equals(content.trim())){
				Toast.makeText(this,"请输入内容", Toast.LENGTH_SHORT).show();
				return;
			}
			
			//更新界面
			TuCao t = new TuCao();
			t.content = content;
			this.tucaoAdapter.insert(t,0);
			this.updateListView();
			//隐藏键盘
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	           imm.hideSoftInputFromWindow(contentET.getWindowToken(),0);
			
			//隐藏文本输入视图，展现3个按钮视图
			findViewById(R.id.footer_text).setVisibility(View.GONE);
			findViewById(R.id.footer).setVisibility(View.VISIBLE);		
		}
	}
	
	/*
	 * 准备录音的那个按钮
	 */
	private void initRecordButton(){
			recordView.setOnFinishedRecordListener(new OnFinishedRecordListener() {
				@Override
				public void onFinishedRecord(String audioPath) {
					Log.i(TAG, "Record finished! save to "	+ audioPath);
					final TuCao tucao = new TuCao();
					String fileName = audioPath.substring(audioPath.lastIndexOf("/")+1);
					Log.d(TAG,"sound file name:"+fileName);
					tucao.soundPath = fileName;//这里仅保存文件名就可以了，不存全路径
					MainActivity.this.tucaoAdapter.insert(tucao,0);
				}
			});	
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
			
			//更新全局的经纬度
			TuCaoApplication.position_latitude = location.getLatitude();
			TuCaoApplication.position_longitude = location.getLongitude();
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}	
	
}
