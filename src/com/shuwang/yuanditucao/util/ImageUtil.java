package com.shuwang.yuanditucao.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.shuwang.yuanditucao.Const;

public class ImageUtil {

	private String SDPATH;
	private final static String TAG = "ImageUtil";
	private HashMap<Object, SoftReference<Drawable>> imageCache;
	private static ImageUtil instance;

	public ImageUtil(Context context) {
		this();
		imageCache = new HashMap<Object, SoftReference<Drawable>>();
	}

	public ImageUtil() {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
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
		return dir;
	}

	public File creatSDFile(File path, String fileName) throws IOException {
		File file = new File(path, fileName);
		file.createNewFile();
		return file;
	}

	public File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = Const.JPEG_FILE_PREFIX + timeStamp + "_";
		File imageF = File.createTempFile(imageFileName,
				Const.JPEG_FILE_SUFFIX, new File(getAlbumDir()));
		return imageF;
	}

	private static String albumDir = null;
	public static String getAlbumDir() {
		if(albumDir!=null) return albumDir;
		File dir = new File(Environment.getExternalStorageDirectory(),
				Const.APP_HOME_PATH + "/" + Const.IMAGE_PATH);
		if (dir != null) {
			if (!dir.mkdirs()) {
				if (!dir.exists()) {
					Log.e(TAG, "Image Path:" + dir.getPath());
					return null;
				}
			}
		}
		albumDir = dir.getAbsolutePath();
		return albumDir;
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
				Log.e(TAG, "图片锟斤拷锟斤拷失锟杰ｏ拷图片锟斤拷锟斤拷锟节ｏ拷" + image.getAbsolutePath());
				return null;
			}
			FileInputStream is = new FileInputStream(image);

			Drawable d = Drawable.createFromStream(is, "src");
			// 锟斤拷锟斤拷OOM锟斤拷锟解，锟斤拷锟斤拷锟斤拷锟斤拷锟絟ttp://www.cnblogs.com/RayLee/archive/2010/11/09/1872856.html
			// | http://yangguangfu.iteye.com/blog/1050445
			// BitmapFactory.Options opts = new BitmapFactory.Options();
			// opts.inJustDecodeBounds = true;
			// opts.inSampleSize = computeSampleSize(opts, -1, 128*128);
			// opts.inJustDecodeBounds = false;
			// Drawable d = null;
			// try{
			// d = Drawable.createFromResourceStream(null,null, is,"src", opts);
			// } catch (OutOfMemoryError err) {
			// Log.e(TAG,"锟接憋拷锟截硷拷锟斤拷图片锟斤拷锟斤拷锟节达拷锟斤拷锟斤拷锟斤拷锟�+imageFileName);
			// return null;
			// }finally{
			// //锟斤拷锟斤拷一锟斤拷要锟斤拷锟斤拷锟斤拷锟矫伙拷false锟斤拷锟斤拷为之前锟斤拷锟角斤拷锟斤拷锟斤拷锟矫筹拷锟斤拷true
			// opts.inJustDecodeBounds = false;
			// }

			if (d == null) {
				Log.e(TAG, "锟斤拷锟斤拷图片失锟杰ｏ拷锟斤拷锟轿猲ull锟斤拷" + imageFileName);
				return null;
			}

			is.close();
			return d;
		} catch (Exception e) {
			Log
					.e(TAG, "锟接憋拷锟截硷拷锟斤拷图片[" + imageFileName + "]失锟杰ｏ拷" + e.toString());
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
			Log.e(TAG,"图锟今缓筹拷锟終ey值为NULL");
			return null;
		}
		Drawable drawable = null;
		if (imageCache.containsKey(key)) {
			SoftReference<Drawable> softReference = imageCache.get(key);
			drawable = softReference.get();
			Log.d(TAG, "锟矫碉拷锟斤拷锟斤拷锟叫的讹拷锟斤拷 = "+drawable);
			Log.d(TAG, "锟矫碉拷锟斤拷锟斤拷锟叫的讹拷锟斤拷锟斤拷锟斤拷 = "+type);
			if (drawable != null) {
				Log.d(TAG, "锟斤拷示锟斤拷锟斤拷锟叫碉拷图片");
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

    public boolean initFilePath() {
            File path = new File(getAlbumDir());
            try {
                creatSDDir(path);
                return true;
            } catch (Exception e) {
            	e.printStackTrace();
                Log.e(TAG,"锟斤拷SD锟斤拷锟斤拷写锟侥硷拷失锟杰ｏ拷dir:["+ getAlbumDir()+"] :"+e.getMessage());
                return false;
            }
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