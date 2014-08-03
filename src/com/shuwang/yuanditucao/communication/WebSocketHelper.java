package com.shuwang.yuanditucao.communication;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpClient.WebSocketConnectCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.WebSocket.StringCallback;
public class WebSocketHelper {

	public void connect(){
		//final AsyncHttpGet get = new AsyncHttpGet();
        
		AsyncHttpClient.getDefaultInstance().websocket(
				"http://192.168.1.101:3900",
				null, 
				new WebSocketConnectCallback() {
				    @Override
				    public void onCompleted(Exception ex, WebSocket webSocket) {
				        if (ex != null) {
				            ex.printStackTrace();
				            return;
				        }
				        webSocket.send("a string");
				        webSocket.setStringCallback(new StringCallback() {
				            public void onStringAvailable(String s) {
				                System.out.println("I got a string: " + s);
				            }
				        });
				        webSocket.setDataCallback(new DataCallback() {
							@Override
							public void onDataAvailable(DataEmitter emitter,
									ByteBufferList bb) {
								System.out.println("I got some bytes!");
				                // note that this data has been read
				                bb.recycle();
							}
				        });
				    }
				});	
	}

}
