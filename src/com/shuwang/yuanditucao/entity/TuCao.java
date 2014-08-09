package com.shuwang.yuanditucao.entity;

import java.util.Date;

import com.shuwang.yuanditucao.TuCaoApplication;

public class TuCao {
	public Date createdAt;
	public String content;//发表的吐槽文本
	public String imageUrl;//远程的七牛的图片URL
	public String imagePath;//本地的图片路径，只保存文件名，剩下的路径都约定好
	public String soundPath;//录音的文件路径，只保存文件名，剩下的路径都约定好
	public double latitude;
	public double longitude;
	
	public TuCao(){
		this.latitude = TuCaoApplication.position_latitude;
		this.longitude = TuCaoApplication.position_longitude;
	}
	
	public TuCao(Date createdAt, String content, String imageUrl,
			double latitude, double longitude) {
		this();
		this.createdAt = createdAt;
		this.content = content;
		this.imageUrl = imageUrl;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String toString(){
		StringBuffer sb =new StringBuffer();
		sb.append("{");
		sb.append(createdAt);
		sb.append(",");
		sb.append(content);
		sb.append(",");
		sb.append(imageUrl);
		sb.append(",");
		sb.append(latitude);
		sb.append(",");
		sb.append(longitude);
		sb.append("}");
		return sb.toString();
	}
}
