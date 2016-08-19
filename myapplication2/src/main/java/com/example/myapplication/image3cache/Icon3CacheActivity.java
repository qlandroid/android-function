package com.example.myapplication.image3cache;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.BaseActvity;
import com.example.myapplication.CommonAdapter;
import com.example.myapplication.R;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Icon3CacheActivity extends BaseActvity implements View.OnClickListener {
    private static final String TAG = "Icon3CacheActivity";
    @Bind(R.id.list_view)
    PullToRefreshListView listView;
    private int page = 1;



    private List<JokeBean.ShowapiResBodyBean.ContentlistBean> mListBean = new ArrayList<>();
    private MyAdapter adapter;
    private int lastEndIndex = -1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMessage: " + Thread.currentThread().getName());
            //由于重新加载图片，需要还原初始化；
            adapter.setIsFirst(true);
            //不需要开启adapter中imageView的初始化；
            adapter.setImageInIt(false);
            //更新适配器
            adapter.updata(mListBean);
            //取消加载提示条；
            listView.onRefreshComplete();


        }
    };
    private Thread downLoadThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon3_cache);
        ButterKnife.bind(this);
        initListView();
        //下载网络数据
        downLoad();
    }

    private void initListView() {
        adapter = new MyAdapter(this, mListBean, R.layout.icon_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Icon3CacheActivity.this, position +"", Toast.LENGTH_SHORT).show();
            }
        });
        //设置滑动监听，在滑动状态取消加载；
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int startIndex ;
            int endIndex;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                adapter.setIsFirst(false);
                //滑动状态需要开启item的初始化；
                adapter.setImageInIt(true);
                switch (scrollState){
                    case SCROLL_STATE_IDLE://停止滑动状态
                        loadingImage();
                        break;
                }
            }

            private void loadingImage() {
                if (startIndex < 0 ){
                    startIndex = 0;
                }
                if (endIndex >= mListBean.size()){
                    endIndex = mListBean.size();
                }
                for (; startIndex < endIndex ; startIndex++) {
                    String imageUrl = mListBean.get(startIndex).getImg();
                    ImageView iv = (ImageView) listView.findViewWithTag(imageUrl);

                    if (imageUrl != null&&iv != null)
                        IconDownLoadThreadPool.getInstance().imageLoad(iv,imageUrl);
                }
                lastEndIndex = endIndex;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                startIndex = firstVisibleItem-1;
                if (visibleItemCount+firstVisibleItem < totalItemCount) {
                    endIndex = firstVisibleItem + visibleItemCount;
                }else {
                    endIndex = totalItemCount;
                }
                Log.i(TAG, "onScroll: " + firstVisibleItem +"----" + endIndex + " listdata---->" +mListBean.size());
            }
        });
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                downLoad();
            }
        });
    }

    private void downLoad() {
        downLoadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String data = JokeHttpUtils.request(JokeHttpUtils.httpUrl, JokeHttpUtils.getHttpArg(page));
                Log.i(TAG, "run: " + data);
                JokeBean bean = new Gson().fromJson(data, JokeBean.class);
                if (bean == null){
                    return;
                }

                JokeBean.ShowapiResBodyBean showapiResBodyBean = bean.getShowapi_res_body();
                if (showapiResBodyBean == null){
                    return;
                }
                List<JokeBean.ShowapiResBodyBean.ContentlistBean> list = showapiResBodyBean.getContentlist();
                if (list == null){
                    return;
                }
                mListBean.addAll(list);
                mHandler.sendEmptyMessage(0x002);


            }
        });
        downLoadThread.start();

    }

    @Override
    public void onClick(View v) {
        page++;
        downLoad();
        Log.i(TAG, "onClick: " + page);
    }

    @Override
    protected void onDestroy() {
        mHandler = null;
        downLoadThread = null;
        listView.setOnScrollListener(null);
        listView.setAdapter(null);
        Image3CacheHelper.getInstance().destroy();
        super.onDestroy();
    }

    public static class MyAdapter extends CommonAdapter<JokeBean.ShowapiResBodyBean.ContentlistBean> {
        //异步处理
        IconDownLoadThreadPool iconDownLoadThreadPool;
        //判断当前是否是需要开启异步加载显示图片；
        private boolean isFirst = true;
        //用于判断是否需要初始化imageView；
        private boolean isImageInIt = true;

        public void setImageInIt(boolean imageInIt) {
            isImageInIt = imageInIt;
        }

        public void setIsFirst(boolean isFirst){
            this.isFirst = isFirst;
        }
        public MyAdapter(Context context, List<JokeBean.ShowapiResBodyBean.ContentlistBean> data, int layoutId) {
            super(context, data, layoutId);
            //创建异步处理对象，
            iconDownLoadThreadPool = IconDownLoadThreadPool.getInstance(Image3CacheHelper.getInstance(), 3, IconDownLoadThreadPool.Type.FIFO);
        }


        @Override
        public void setItemContent(ViewHolder holder, JokeBean.ShowapiResBodyBean.ContentlistBean resultBean, int position) {

            String imageUrl = resultBean.getImg();
            ImageView itemImageView = (ImageView) holder.getViewById(R.id.imageView);
            itemImageView.setTag(imageUrl);


            if (isImageInIt) {
                //需要初始化，需要初始化的状态为数据已经加载完毕，item可以复用；
                itemImageView.setImageResource(R.mipmap.ic_launcher);
            }
            if (isFirst){
                iconDownLoadThreadPool.imageLoad(itemImageView,imageUrl);
            }
        }

    }

}
