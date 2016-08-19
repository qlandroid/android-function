package com.example.myapplication.image3cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/18.
 */
public class Image3CacheHelper {
    private static final String END_NAME_JPG = ".jpg";
    private static final String TAG = "Image3CacheHelper";
    private LruCache<String,Bitmap> mLruCache ;
    private HashMap<String,SoftReference<Bitmap>> mSoftReference;
    private int max_cache = 8;
    private final static String FILE_DIR_NAME = "ImageCache";
    private boolean isSDCache = true;
    private static Image3CacheHelper helper;

    public void destroy(){
        //清除内存
        mLruCache.evictAll();
        mSoftReference.clear();

    }

    public static Image3CacheHelper getInstance() {
        if (helper == null){
            synchronized (Image3CacheHelper.class){
                if (helper == null){
                    helper = new Image3CacheHelper();
                }
            }
        }
        return helper;
    }
    /**
     * 保存图片的内创建为内存最大值的百分比；
     * @param max_cache
     */
    public void setMax_cache(int max_cache){
        this.max_cache = max_cache;
    }

    public Image3CacheHelper() {
        this(8);
    }

    private  Image3CacheHelper(int max_cache) {
        this.max_cache = max_cache;
        init();
    }

    private void init(){
        //获得系统最大内存；
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxBitmapMemory = maxMemory / max_cache;
        mLruCache =  new MyLruCache(maxBitmapMemory);
        mSoftReference = new HashMap<>();
    }

    /**
     * 将图片添加到内存当中；
     * @param imageUrl
     * @param bitmap
     */
    public void add(String imageUrl ,Bitmap bitmap){
        //http://www.zbjuran.com/uploads/allimg/160819/7-160Q91111213A.jpg
        String imageName = getImageNameString(imageUrl);
        Log.i(TAG, "getImageNameString: 传入名字进行切割" + imageName);
        mLruCache.put(imageName,bitmap);
    }

    /**
     * 将字符串切割；
     * @param imageUrl
     * @return
     */
    private String getImageNameString(String imageUrl) {
        //http://www.zbjuran.com/uploads/allimg/160819/7-160Q91111213A.jpg
        String[] split = imageUrl.split("\\/");

        return split[split.length - 1]+ END_NAME_JPG;
    }

    public boolean isSaveBitmap(String imageUrl){

        if (getBitmap(imageUrl)!= null){
            return true;
        }
        return false;
    }

    public Bitmap getBitmap(String bitmapUrl){
        String imageName = getImageNameString(bitmapUrl);
        Bitmap bitmap = null;
        bitmap =mLruCache.get(imageName);
        if (bitmap != null){
            return bitmap;
        }
        SoftReference<Bitmap> softReference =mSoftReference.get(imageName);
        if (softReference != null){
            bitmap = softReference.get();
            if (bitmap != null){
                return bitmap;
            }
        }

        String bitmapSavePath = getBitmapSavePath(imageName);
        bitmap = BitmapFactory.decodeFile(bitmapSavePath);
        return bitmap;
    }

    private String getBitmapSavePath(String bitmapUrl) {
        return createSaveFile(getSaveBaseFile(),bitmapUrl);
    }


    private void setSDCache(boolean isSDCache){
        this.isSDCache = isSDCache;
    }



    private String createSaveFile(String saveFile, String saveBitmapName) {
        String path = saveFile+"/"+FILE_DIR_NAME +"/"+saveBitmapName;
        return path;
    }


    /**
     * 当内存不足时，将图片保存到本地；
     * @param oldValue 内存要清除的bitmap;
     * @param saveFile 保存的路径；
     * @return
     */
    private boolean saveBitmap(Bitmap oldValue, String saveFile ,String imageName) {
        File saveFileDocument = new File(saveFile,"/"+imageName);

        FileOutputStream fos = null;
        try {
            if (!saveFileDocument.exists()){
                saveFileDocument.getParentFile().mkdirs();
                saveFileDocument.createNewFile();
            }
            fos = new FileOutputStream(saveFileDocument);
            oldValue.compress(Bitmap.CompressFormat.JPEG,70,fos);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null){
                try {
                    fos.close();
                    fos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 判断保存到哪个路径，是手机自带的SD卡还是外置内存卡；
     * @return
     */
    public String getSaveBaseFile() {
        String basePath = "";
        if (isSDCache){
            basePath =Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return basePath ;
    }

    public  class MyLruCache extends LruCache<String,Bitmap>{
        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public MyLruCache(int maxSize) {
            super(maxSize);

        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            //返回当前图片的大小；
            return value.getByteCount();
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            //evicted 为true证明系统内存不足需要进行清除内存；
            if (evicted){
                //系统内存不足需要进行清理内存，可以将图片保存到弱引用中，并保存到手机内存中；
                mSoftReference.put(key,new SoftReference<Bitmap>(oldValue));
                //并保存到手机内存基本路径
                String saveFile = getSaveBaseFile();
                //获得lruCache的key也就是图片的名称iamgeName；
                String saveBitmapName = key;
                saveFile = saveFile +"/"+FILE_DIR_NAME +"/";

                boolean isSuccess = saveBitmap(oldValue, saveFile,saveBitmapName);
                Log.i(TAG, "entryRemoved: isSuccess =" +isSuccess );
            }
        }
    }
}
