package com.example.myapplication.image3cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by Administrator on 2016/8/18.
 */
public class IconDownLoadThreadPool {
    //创建一个三级缓存；
    private Image3CacheHelper mImage3CacheHelper;

    //创建信号量，控制mThreadPoolHandler初始化；
    private Semaphore mSemaphoreThreadLoopHandler;
    //创建信号量，控制自定义的消息队列；
    private Semaphore mSemaphoreThreadLoopWork;
    //线程池
    private ExecutorService mExecutorService;
    //线程池的数量
    private int account;
    //控制添加工作线程的handler
    private Handler mThreadPoolHandler;
    //控制下载成后，iamgeView显示图片，显示图片必须在主线程完成；
    private Handler mUIHandler;
    //一个标记，是先进先出，还是先进后出；
    public enum  Type{
        FIFI,FIFO;
    }
    private Type mType;
    //创建一个线程队列，用于存储需要工作的Message队列
    private LinkedList<Runnable> mListTaskQueue;

    //用于消息循环的线程；
    private Thread mPoolThread;
    private static IconDownLoadThreadPool pool;


    public static IconDownLoadThreadPool getInstance() {
        return pool;
    }
    public static IconDownLoadThreadPool getInstance(Image3CacheHelper image3CacheHelper,int accountThread, Type mType) {
        if (pool == null){
            synchronized (IconDownLoadThreadPool.class){
                if (pool == null){
                    pool = new IconDownLoadThreadPool(image3CacheHelper,accountThread, mType);
                }
            }
        }
        return pool;
    }
    private IconDownLoadThreadPool(Image3CacheHelper image3CacheHelper,int accountThread, Type mType) {
        this.mImage3CacheHelper =image3CacheHelper;
        this.account = accountThread;
        this.mType = mType;
        mSemaphoreThreadLoopWork = new Semaphore(accountThread);
        mSemaphoreThreadLoopHandler = new Semaphore(1);
        mListTaskQueue = new LinkedList<>();
        mExecutorService = Executors.newFixedThreadPool(accountThread);
        mPoolThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                mThreadPoolHandler =new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //进行处理消息；
                        mExecutorService.execute(addTask());
                        try {
                            mSemaphoreThreadLoopWork.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Looper.loop();
                //mSemaphoreThreadLoopHandler.release();
            }
        };
        mPoolThread.start();
    }

    /**
     * 在子线程执行；
     * @return
     */
    private synchronized Runnable addTask() {

        if (mType.equals(Type.FIFI)&&!mListTaskQueue.isEmpty()){
            //如果是队列形式，那么是先进先出；那么就要获得消息队列的第一项；

            Log.i("mtag", "addTask: "+ mListTaskQueue.size());
            return mListTaskQueue.removeFirst();
        }else if (mType.equals(Type.FIFO)&&!mListTaskQueue.isEmpty()){
            //如果是栈，那么就是后进先出，就需要获得消息队列的最后一个项；
            return mListTaskQueue.removeLast();
        }
        Log.i(TAG, "addTask:添加消息的时候，出现空指针 ");
        return null;
    }

    private static final String TAG = "IconDownLoadThreadPool";
    public void imageLoad(ImageView iv , String imageUrl){
        //在ListView中 滑动时，由于Item是复用的，所以每次显示时需要判断，当前显示的url与下载完的url是否匹配，
        //只有在匹配的情况下才能显示；
        iv.setTag(imageUrl);
        if (mUIHandler == null){
            //对UIHandler进行初始化；
            mUIHandler = new UIHandler();
        }
        if (mImage3CacheHelper.isSaveBitmap(imageUrl)) {
            //如果内存存在该图片，那么就通知刷新UI即可；
            Message msg = Message.obtain();
            ImageBean bean = new ImageBean();
            bean.iv = iv;
            bean.iamgeUrl = imageUrl;
            msg.obj = bean;
            mUIHandler.sendMessage(msg);
            Log.i(TAG, "imageLoad: 判断当前图片在内存中，直接刷新即可");
        }else {
            Runnable runnable =new ImageLoadRunnable(iv ,imageUrl);
            //消息添加到队列中；
            mListTaskQueue.add(runnable);
            //很有可能这个时候mThreadPoolHandler没有初始化完毕，因为这个初始化在子线程进行所以加一个信号量；
             /*  try {
                  mSemaphoreThreadLoopHandler.acquire();
                   } catch (InterruptedException e) {
                 e.printStackTrace();
                  }*/
            //通知线程池该干活了；
            mThreadPoolHandler.sendEmptyMessage(0x110);
        }


    }
    public static class ImageBean {
        ImageView iv;
        String iamgeUrl;
    }

    public class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageBean bean = (ImageBean) msg.obj;
            ImageView imageView = bean.iv;
            String url = bean.iamgeUrl;
            String urlSrc = (String) imageView.getTag();
            if (url.equals(urlSrc)){
                imageView.setImageBitmap(mImage3CacheHelper.getBitmap(url));
            }
            Log.i("mtag", "handleMessage: 在主线程更新UI ");
        }
    }
    public class ImageLoadRunnable implements Runnable{
        ImageView iv;
        String imageUrl;
        private final int READ_TIME_OUT = 10_000;
        private final int CONNECT_TIME_OUT = 10_000;

        public ImageLoadRunnable(ImageView iv, String imageUrl) {
            this.iv = iv;
            this.imageUrl = imageUrl;
        }

        @Override
        public void run() {
            if (mImage3CacheHelper.isSaveBitmap(imageUrl)){
                //内存当中有这张图片；

            }else {
                //内存当中没有这张图片，进行网络请求下载图片；
                downLoad();
            }
            Message message = Message.obtain();
            ImageBean bean = new ImageBean();
            bean.iamgeUrl = imageUrl;
            bean.iv = iv;
            message.obj = bean;

            mUIHandler.sendMessage(message);
            mSemaphoreThreadLoopWork.release();
        }

        private void downLoad() {
            //进行网络下载
            BufferedInputStream bis = null;
            ByteArrayOutputStream baos = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(READ_TIME_OUT);
                conn.setConnectTimeout(CONNECT_TIME_OUT);
                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK){
                    InputStream is =conn.getInputStream();
                    bis = new BufferedInputStream(is);
                    baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[8192];//8*1024;
                    int len =  -1;
                    while ((len = bis.read(buf))!= -1){
                        baos.write(buf,0,len);
                    }
                    Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(),0,baos.size());
                    mImage3CacheHelper.add(imageUrl,bitmap);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (bis != null) {
                    try {
                        bis.close();
                        bis = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (baos != null) {
                    try {
                        baos.close();
                        baos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

}
