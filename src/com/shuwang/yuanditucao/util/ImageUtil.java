package com.shuwang.yuanditucao.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.shuwang.yuanditucao.Const;

public class ImageUtil {

	private String SDPATH;
	private final static String TAG = "TuCao.ImageUtil";
	private HashMap<Object, SoftReference<Drawable>> imageCache;
	private static ImageUtil instance;
	public static int screenWidth;
	public static int screenHeight;

	public ImageUtil(Context context) {
		this();
	}

	public ImageUtil() {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
		imageCache = new HashMap<Object, SoftReference<Drawable>>();
	}

	public static ImageUtil getInstance(Context context) {
		if (instance == null) {
			synchronized (ImageUtil.class) {
				if (instance == null) {
					instance = new ImageUtil(context);
				}
			}
		}
		return instance;
	}

	public File creatSDDir(File dirName) {
		File dir = new File(dirName, SDPATH);
		dir.mkdirs();
		Log.i(TAG,"image dir was created.");
		return dir;
	}

	public File creatSDFile(File path, String fileName) throws IOException {
		File file = new File(path, fileName);
		file.createNewFile();
		return file;
	}

	
	

	public String getSDPATH() {
		return SDPATH;
	}

	public boolean exist(String fileName) {
		File albumDir = new File(getAlbumDir());
		File file = new File(albumDir, fileName);
		return file.exists();
	}

	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);

		return file.exists();
	}

	public void deleteImages() {
		File albumDir = new File(getAlbumDir());
		File[] images = albumDir.listFiles();
		for (File image : images) {
			image.delete();
		}
		Log.i(TAG, "目录[" + albumDir.getPath() + "]锟铰碉拷锟斤拷锟斤拷图片锟斤拷删锟斤拷");
	}

	/**
	 * 锟斤拷锟芥到SD锟斤拷锟较ｏ拷路锟斤拷锟角既讹拷锟侥ｏ拷锟斤拷Const锟斤拷锟斤拷锟斤拷锟斤拷 一锟斤拷锟斤拷/mnt/sdcard/Pictures/paozhoumo/
	 * 
	 * @param bitmap
	 * @param iszoom
	 * @return
	 */
	public String savePhotoFromCamera(Bitmap bitmap, boolean iszoom) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, Const.JPEG_QUALITY, baos);

		byte[] mBitmapBytes = baos.toByteArray();
		ByteArrayInputStream mBitmapInputStream = new ByteArrayInputStream(
				mBitmapBytes);

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS")
				.format(new Date());
		String rawName = Const.JPEG_FILE_PREFIX + "_" + timeStamp
				+ Const.JPEG_FILE_SUFFIX;
		String imageFileName;
		if (iszoom) {
			imageFileName = Const.JPEG_FILE_SMALL_PIC + rawName;
		} else {
			imageFileName = Const.JPEG_FILE_ORIGIN_PIC + rawName;
		}
		if (!this.write2SD(imageFileName, mBitmapInputStream))
			return null;

		return imageFileName;
	}

	/**
	 * 锟斤拷锟脚筹拷小图
	 * 
	 * @param bitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public String zoomPhotoFromCamera(Bitmap bitmap, int newWidth,
			int newHeight, boolean isSmall) {

		float scaleWidth = ((float) newWidth) / bitmap.getWidth();
		float scaleHeight = ((float) newHeight) / bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap smallBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return savePhotoFromCamera(smallBitmap, isSmall);
	}

	private String imagePath = null;
	private String getAlbumDir(){
		if(this.imagePath==null)
			this.imagePath = Util.getAppDir("pictures");
		return this.imagePath;
	}
	
	public boolean write2SD(String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		File path = new File(getAlbumDir());
		try {

			creatSDDir(path);
			file = creatSDFile(path, fileName);
			output = new FileOutputStream(file);

			byte[] buffer = new byte[4 * 1024];

			int len = 0;
			len = input.read(buffer);
			while (len != -1) {
				output.write(buffer, 0, len);
				len = input.read(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "锟斤拷SD锟斤拷锟斤拷写锟侥硷拷失锟杰ｏ拷dir:" + path + ", file:" + fileName
					+ "] :" + e.getMessage());
			return false;
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				Log.e(TAG, "锟截憋拷SD锟斤拷锟较碉拷锟侥硷拷锟斤拷锟�" + e.getMessage());
				return false;
			}
		}
		return true;
	}


	/**
	 * 锟斤拷锟斤拷没锟絀d锟斤拷锟斤拷头锟斤拷
	 * 
	 * @param userId
	 * @return
	 */
	// public Drawable loadAvatar(int userId){
	// User user = PaoDAO.getInstance(this.context).findUserById(userId);
	// if(user==null) {
	// //Log.e(TAG,"锟睫凤拷锟揭碉拷锟矫伙拷锟斤拷锟斤拷锟斤拷默锟斤拷头锟斤拷锟矫伙拷id:"+userId);
	// return defaultAvatar();
	// }
	// return this.loadAvatar(user);
	// }

	public Drawable loadAvatar(int userId) {
		return this.setImageDrawable(userId, Const.AVATAR_DARWABLE);
	}
	

	public Drawable loadImage(String imageFileName){
		return setImageDrawable(imageFileName,Const.IMAGE_DRAWABLE);
	}
	
	public Drawable loadImagePATH(String imageFileName) {
		try {
			File image = new File(getAlbumDir(), imageFileName);
			if (!image.exists()) {
				Log.e(TAG, "The picture does not exist:" + image.getAbsolutePath());
				return null;
			}

			//inJustDecodeBounds = true
			//通过设置BitmapFactory.Options的inJustDecodeBounds属性设置为true，
			//可以解码避免内存分配，返回的Bitmap为空，但返回outWidth，outHeight和outMimeType。
			//这种技术使您可以读取的图像数据的尺寸和类型在内存分配之前。
			BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
	    	bmpFactoryOptions.inJustDecodeBounds = true;
	    	//获得大小
	    	BitmapFactory.decodeFile(this.getPhotoFullName(imageFileName), bmpFactoryOptions);

            
	    	//我们需要的是800作为基准，甭管最大是多大的，都缩成800
	    	int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/ImageUtil.screenHeight);
	    	int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/ImageUtil.screenWidth);
	    	if (heightRatio > 0 && widthRatio > 0){ 
	    		if (heightRatio > widthRatio) {  
	    			// Height ratio is larger, scale according to it  
	    			bmpFactoryOptions.inSampleSize = heightRatio; 
	    		} else {  // Width ratio is larger, scale according to it  
	    			bmpFactoryOptions.inSampleSize = widthRatio;
	    		}
	    	}
	    	Log.d(TAG, "Resize ratio is:"+bmpFactoryOptions.inSampleSize);
	    	
	    	//
	    	bmpFactoryOptions.inJustDecodeBounds = false;
	    	Bitmap bmp = BitmapFactory.decodeFile(this.getPhotoFullName(imageFileName), bmpFactoryOptions);
	    	
	    	ExifInterface ei = new ExifInterface(image.getAbsolutePath());
	    	int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	    	if(orientation==ExifInterface.ORIENTATION_ROTATE_90){ 
	    		Log.d(TAG,"the picture need to rotate 90'");
		    	Matrix matrix = new Matrix();
		    	matrix.postRotate(90);	
		    	Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),bmp.getHeight(),matrix,false);
		    	bmp.recycle();//别忘了释放了，防止内存泄漏
		    	bmp = rotatedBMP;
	    	}
	    	return new BitmapDrawable(bmp);
		} catch (Exception e) {
			Log
					.e(TAG, "Exception when loading image[" + imageFileName + "]:" + e.toString());
			return null;
		}
	}

	public Bitmap loadavatar(String imageFilename) {

		return null;
	}

	// 图片锟斤拷锟截凤拷锟斤拷
	/**********************************************************************************************************/

	/***
	 * 锟斤拷锟斤拷图片
	 * 
	 * @param imageUrl
	 *            图片锟斤拷址
	 * @param imageCallback
	 *            锟截碉拷锟接匡拷
	 * @return
	 */
	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback, final Integer type) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};
		// 锟斤拷锟斤拷锟竭程硷拷锟截憋拷锟斤拷图片
		// AsyncTask锟斤拷锟斤拷锟斤拷锟斤拷10锟斤拷锟斤拷锟斤拷,锟斤拷锟斤拷同时锟斤拷锟斤拷10锟斤拷锟斤拷锟斤拷图片
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = null;
				if (type == Const.IMAGE_DRAWABLE) {
					drawable = ImageUtil.this.loadImageFromUrl(imageUrl);
				} else if (type == Const.AVATAR_DARWABLE) {
					//drawable = ImageUtil.this.loadImageFromId(imageUrl);
				}
				// 锟斤拷锟斤拷锟截碉拷图片锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
				if(drawable!=null)
					imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}
	
	/***
	 * 锟斤拷锟斤拷图片
	 * 
	 * @param imageUrl
	 *            图片锟斤拷址
	 * @param imageCallback
	 *            锟截碉拷锟接匡拷
	 * @return
	 */
	public void clearCache(final int userId, final Integer type) {
		if (imageCache.containsKey(userId)) {
			imageCache.remove(userId);
		}
	}


	public Drawable loadImageFromUrl(String imageURL) {
		Drawable drawable = this.loadImagePATH(imageURL);
		Log.d(TAG, "loadImageFromUrl == " + imageURL);
		return drawable;

	}

	// 锟截碉拷锟接匡拷
	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

	public Drawable getDrawable(ImageUtil ImageUtil, String imageUrl, int type,
			final View listView) {
		Drawable drawable = ImageUtil.loadDrawable(imageUrl,
				new ImageCallback() {
					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						ImageView imageViewByTag = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageViewByTag != null)
							imageViewByTag.setImageDrawable(imageDrawable);

					}
				}, type);
		return drawable;
	}

	public void setImageDrawable(ImageView imageView, Object userId, int type,
			View listView) {
		imageView.setTag(userId);
		Drawable avatarDrawable = getDrawable(this, userId + "", type, listView);
		imageView.setImageDrawable(avatarDrawable);
	}

	/**
	 * @param key 	图锟斤拷锟斤拷锟�	 * @param type	
	 * @return
	 */
	public Drawable setImageDrawable(Object key, int type) {
		if(key==null) {
			Log.e(TAG,"key值为NULL");
			return null;
		}
		Drawable drawable = null;
		if (imageCache.containsKey(key)) {
			SoftReference<Drawable> softReference = imageCache.get(key);
			drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		if (type == Const.IMAGE_DRAWABLE) {
			drawable = loadImageFromUrl(key.toString());
		} else if (type == Const.AVATAR_DARWABLE) {
			//drawable = loadImageFromId(key.toString());
		}
		if(drawable!=null)
			imageCache.put(key, new SoftReference<Drawable>(drawable));
		return drawable;
	}
	
    public String getPhotoFullName(String imageFileName) {
        return getAlbumDir()+"/"+imageFileName;
    }

    

    public Bitmap loadPhotoFromSD(String imageFileName) {
    	BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
    	bmpFactoryOptions.inJustDecodeBounds = true;
    	Bitmap bmp = BitmapFactory.decodeFile(this.getPhotoFullName(imageFileName), bmpFactoryOptions);
    	
    	//锟斤拷锟斤拷800锟斤拷锟斤拷锟斤拷小锟斤拷锟斤拷锟斤拷蟪け锟斤拷锟斤拷锟�600锟斤拷
    	int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/800);
    	int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/800);
    	Log.d(TAG,"锟斤拷片锟斤拷锟斤拷 锟斤拷锟�"+bmpFactoryOptions.outWidth+"锟竭讹拷="+bmpFactoryOptions.outHeight);
    	Log.d(TAG,"锟斤拷锟斤拷压锟斤拷 "+widthRatio +" or  "+heightRatio);
    	if (heightRatio > 0 && widthRatio > 0){ 
    		if (heightRatio > widthRatio) {  
    			// Height ratio is larger, scale according to it  
    			bmpFactoryOptions.inSampleSize = heightRatio; 
    		} else {  // Width ratio is larger, scale according to it  
    			bmpFactoryOptions.inSampleSize = widthRatio;
    		}
    	}
    	Log.d(TAG, "锟斤拷锟斤拷压锟斤拷锟斤拷锟斤拷 1/"+bmpFactoryOptions.inSampleSize);
    	 // 锟斤拷锟斤拷图片
    	bmpFactoryOptions.inJustDecodeBounds = false;
    	bmp = BitmapFactory.decodeFile(this.getPhotoFullName(imageFileName), bmpFactoryOptions);
    	Log.d(TAG, bmp.getWidth()+"锟斤拷"+bmp.getHeight()+"锟斤拷");
    	return bmp;
    }

}