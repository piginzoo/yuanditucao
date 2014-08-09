package com.shuwang.yuanditucao;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuwang.yuanditucao.entity.TuCao;
import com.shuwang.yuanditucao.util.ImageUtil;
import com.shuwang.yuanditucao.util.Util;

public class TuCaoAdapter extends ArrayAdapter<TuCao> {
	private static class ViewHolder{
		public TextView contentView;
		public ImageView imageView;
		public ImageView playView;
	}
	
	private String TAG = "TuCao.TuCaoAdapter";
	
	
	private final Context context;
	
	private ImageUtil imageUtil;
	public TuCaoAdapter(Context context, List<TuCao> items) {
		super(context, R.layout.brief_row,items);//<----要把数组传入构造函数，这样下面的getItem才能工作
		this.context = context;
		this.imageUtil = new ImageUtil();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final TuCao item = this.getItem(position);
		Log.d(TAG,"position changed:"+position);
		if (convertView == null) {
			Log.d(TAG,"convertView is null!");
			LayoutInflater inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(R.layout.brief_row, parent, false);
			holder = new ViewHolder();
			holder.contentView = (TextView)convertView.findViewById(R.id.tucao_content);
			holder.imageView = (ImageView) convertView.findViewById(R.id.tucao_image);
			holder.playView = (ImageView) convertView.findViewById(R.id.tucao_play);
			holder.playView.setOnClickListener(new OnClickListener() {  
                @Override  
                public void onClick(View v) {
                	String soundFileName = (String)v.getTag();
                	MediaPlayer mp=new MediaPlayer();
                    try {
                    	String soundPath = Util.getAppDir("audio")+"/"+soundFileName;
						mp.setDataSource(soundPath);
						Log.d(TAG,"play sound:"+soundPath);
						mp.prepare();
						mp.start();	
					} catch (IOException e) {
						Log.e(TAG,"Play sound error:"+e.toString());
					}
                }
			});
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Log.d(TAG,"convertView hash:"+convertView.hashCode());

		/** 每次这个convertView呀、holder呀，都当作废物利用，重新清除干净上面的痕迹**/
		holder.imageView.setImageBitmap(null);
		holder.contentView.setText("");
		holder.playView.setVisibility(View.GONE);
		
		//开始重新设置新值
		Log.d(TAG,"Current row contentView text:"+holder.contentView.getText()+",hashcode:"+holder.contentView.hashCode());
		if(item.imagePath!=null){
			Log.d(TAG,"load image:"+item.imagePath);
			Drawable d = this.imageUtil.loadImagePATH(item.imagePath);
			holder.imageView.setImageDrawable(d);		
		}else if(item.content!=null){
			Log.d(TAG,"load content..."+item.content);
			holder.contentView.setText(item.content);
		}else if(item.soundPath!=null){
			//每次重设一下声音路径
			Log.d(TAG,"set the playview tag to:"+item.soundPath);	
			holder.playView.setTag(item.soundPath);
			holder.playView.setVisibility(View.VISIBLE);
			Log.d(TAG,"load sound button:"+item.soundPath);
		}else{
			Log.e(TAG,"All data is null,invalid data");
		}
		return convertView;
	}
	
	
	
}
