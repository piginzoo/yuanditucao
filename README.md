Yuan Di Tu Cao (Android)
===========

LBS social app.

Socket.io API:  
http://socket.io/docs/

Baidu Ditu API:  
http://developer.baidu.com/map/wiki/index.php?title=androidsdk/guide/introduction

Restify API:  
http://mcavage.me/node-restify/

Node.js API:  
http://nodejs.org/documentation/

Qi Niu storage API:
- For Client Side:
http://developer.qiniu.com/docs/v6/sdk/android-sdk.html
- For NodeJS Server authentication side:  
http://developer.qiniu.com/docs/v6/sdk/nodejs-sdk.html

## Development Logs:
20140809 

ListVIew有个问题，新的视图在最下面，而最新的值在最上面，因此，必须先清掉每个视图中，也就是holder中的文本显示，图片啥的，否则就会有残留。也就是说在getView中，你就当这个convertView是个废物利用的东西，里面holder也是个废物利用的东西，把上边的值按照getItem的内容重新设置一圈。  

用标准API照相完毕后，三星的相机的照片的元数据是躺着的，所以在显示的时候需要rotate90度，优化地讲应该保存的时候直接rotate，回头再优化吧。


解决了个坑，空指针导致调试和开发工具ADT死掉。
后来发现不是，是因为图片太大了，导致debug图片溢出了，
我理解是decode吧图片从jpg还原成bmp到内存中的时候，会非常大导致内存溢出，OOM，
解决办法就是加载的时候把图片重新采样加载，采样比例是按照800像素才养出来的，
```Java
http://my.oschina.net/huhs/blog/75186
//inJustDecodeBounds = true
//通过设置BitmapFactory.Options的inJustDecodeBounds属性设置为true，
//可以解码避免内存分配，返回的Bitmap为空，但返回outWidth，outHeight和outMimeType。
//这种技术使您可以读取的图像数据的尺寸和类型在内存分配之前。
BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
bmpFactoryOptions.inJustDecodeBounds = true;
//获得大小
BitmapFactory.decodeFile(this.getPhotoFullName(imageFileName), bmpFactoryOptions);

//我们需要的是800作为基准，甭管最大是多大的，都缩成800
int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/800);
int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/800);
if (heightRatio > 0 && widthRatio > 0){ 
if (heightRatio > widthRatio) {  
// Height ratio is larger, scale according to it  
bmpFactoryOptions.inSampleSize = heightRatio; 
} else {  // Width ratio is larger, scale according to it  
bmpFactoryOptions.inSampleSize = widthRatio;
}
}
Log.d(TAG, "Resize ratio is:"+bmpFactoryOptions.inSampleSize);
```

我在代码中监听了OnTouch,并且这个控件有个onTouchEvent(,这两者的前后顺序和关系是？
http://ipjmc.iteye.com/blog/1694146
从源码中，我们可以看出View的onTouchListener和onTouchEvent都是在这里被调用的。如果View的touchListener返回true，dispatchTouchEvent()直接就返回，连onTouchEvent都不会被调用了。只有View没有设置onTouchListener，或者touchListener.onTouch()返回false，才会调用onTouchEvent()。 