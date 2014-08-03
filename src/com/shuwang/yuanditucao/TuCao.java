package com.shuwang.yuanditucao;

import java.util.Date;

public class TuCao {
	private Date createdAt;
	private String content;
	private String imageUrl;
	private double latitude;
	private double longitude;
	
	
	public TuCao(Date createdAt, String content, String imageUrl,
			double latitude, double longitude) {
		super();
		this.createdAt = createdAt;
		this.content = content;
		this.imageUrl = imageUrl;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}
}
