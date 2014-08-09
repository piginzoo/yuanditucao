package com.shuwang.yuanditucao.communication;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.io.IO;
import com.qiniu.io.PutExtra;
import com.qiniu.utils.QiniuException;

@SuppressWarnings("deprecation")
public class WebSocketHelper {

	private String token;
	private long EXPIRE_TIME = 3000*1000; //50分钟的过期时间
	private Gson gson = new Gson();
	private SocketIOClient ioClient;
	
	/*
	 * 周期性去取得token
	 */
	private void periodlyRequestToken(){
		new Timer().schedule(new TimerTask(){   
	        public void run() {  
	        	Log.d("TuCao","timer request token from business server.");
	        	ioClient.emit("qiniu.token",new JSONArray());
	        	token = null;//取得下一次token前，清空旧的token
	        }  
	    },0,EXPIRE_TIME);//每隔50分钟就发送请求
	}
	
	@SuppressWarnings("deprecation")
	public void connect(){
		
		 Future<SocketIOClient> future = SocketIOClient.connect(
				AsyncHttpClient.getDefaultInstance(), 
				"http://192.168.1.205:3900", 
				new ConnectCallback() {
				    @SuppressWarnings("deprecation")
					@Override
				    public void onConnectCompleted(Exception ex, SocketIOClient client) {
				        if (ex != null) {
				            Log.e("TuCao","Error happened when connecting...:" + ex.toString());
				            return;
				        }
				        
				        
				        
				        //保存一下socket.io client的引用
				        ioClient = client;
				        
				        //连接后自动启动timer去定期取得token
				        periodlyRequestToken();
				        
//				        client.on("data", new EventCallback() {
//				            @Override
//				            public void onEvent(JSONArray argument, Acknowledge acknowledge) {
//				            	
//					            TuCao[] tucao = gson.fromJson(argument.toString(), new TypeToken<TuCao[]>(){}.getType());
//					            for(TuCao t : tucao){
//					            	Log.d("TuCao","return tocao:"+t);
//					            }
//				            }
//				        });
		        
		       
				        client.on("qiniu.token",new EventCallback(){
				        	@Override 
				        	public void onEvent(JSONArray arg, Acknowledge ack){
				        			Log.d("TuCao","got server generated QiNiu token:"+ arg.toString());
				        			token = arg.toString();//获得最新的七牛授权token
				        	}
				        });
		    }
				    
		});		 
	}
	
	/**
	 * 上传文件
	 * @param uri
	 */
	public void upload(Context context,Uri uri) {
		if(this.token==null){
			Log.e("TuCao","token does not exist, upload failed!");
			return;
		}
		
		String key = IO.UNDEFINED_KEY; // 自动生成key
		PutExtra extra = new PutExtra();
		extra.params = new HashMap<String, String>();
		IO.putFile(context, this.token, key, uri, extra, new JSONObjectRet() {
			@Override
			public void onProcess(long current, long total) {
				
			}

			@Override
			public void onSuccess(JSONObject resp) {
				Log.i("TuCao","file was uploaded to qiniu!");
			}

			@Override
			public void onFailure(QiniuException ex) {
				Log.e("TuCao","file uploading to qiniu failed:"+ex.toString());
			}
		});
	}
	
	
	public static void  main(String s[]){
		WebSocketHelper  helper = new WebSocketHelper();
		helper.connect();
	}
}


/**
 * 使用WebScoket方式连接Socket.IO服务器
AsyncHttpClient.getDefaultInstance().websocket(
		"http://192.168.1.203:3900",
		null, 
		new WebSocketConnectCallback() {
		    @Override
		    public void onCompleted(Exception ex, WebSocket webSocket) {
		        if (ex != null) {
		        	Log.e("TuCao","Socket.IO connect failed: "+ex.toString());
		            //ex.printStackTrace();
		            return;
		        }
		        Log.i("TuCao","Socket.IO client connected!!!");
		        
		        webSocket.send("Hello , I am client {Hello World!}");
		        
		        webSocket.setStringCallback(new StringCallback() {
		            public void onStringAvailable(String s) {
		                Log.d("TuCao", "received data from server:"+s);
		            }
		        });
		        
		    }
		});	
*/

//        client.setStringCallback(new StringCallback() {
//        @Override
//        public void onString(String string,Acknowledge acknowledge) {
//          TuCao tucao = gson.fromJson(string, TuCao.class);
//          Log.d("TuCao","return tocao:"+tucao);
//        }
//    });
//    client.setJSONCallback(new JSONCallback() {
//    @Override
//    public void onJSON(JSONObject json,Acknowledge acknowledge) {
//    	gson.fromJson(json, typeOfT)
//    	System.out.println("json: " + json.toString());
//    }
//});