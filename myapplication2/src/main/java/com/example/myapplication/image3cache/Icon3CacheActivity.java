package com.example.myapplication.image3cache;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

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
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMessage: " + Thread.currentThread().getName());
            adapter.updata(mListBean);
            listView.onRefreshComplete();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon3_cache);
        ButterKnife.bind(this);
        initListView();

        downLoad();
    }

    private void initListView() {
        adapter = new MyAdapter(this, mListBean, R.layout.icon_item);
        listView.setAdapter(adapter);

        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                downLoad();
            }
        });
    }

    private void downLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = JokeHttpUtils.request(JokeHttpUtils.httpUrl, JokeHttpUtils.getHttpArg(page));
                Log.i(TAG, "run: " + data);
                JokeBean bean = new Gson().fromJson(data, JokeBean.class);

                mListBean.addAll(bean.getShowapi_res_body().getContentlist());
                mHandler.sendEmptyMessage(0x002);


            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        page++;
        downLoad();
        Log.i(TAG, "onClick: " + page);
    }

    @Override
    protected void onDestroy() {
        listView.setAdapter(null);
        super.onDestroy();
    }

    public static class MyAdapter extends CommonAdapter<JokeBean.ShowapiResBodyBean.ContentlistBean> {
        private final Image3CacheHelper helper;
        IconDownLoadThreadPool iconDownLoadThreadPool;

        public MyAdapter(Context context, List<JokeBean.ShowapiResBodyBean.ContentlistBean> data, int layoutId) {
            super(context, data, layoutId);
            helper = Image3CacheHelper.getInstance();
            iconDownLoadThreadPool = IconDownLoadThreadPool.getInstance(helper, 3, IconDownLoadThreadPool.Type.FIFI);
        }

        @Override
        public void setItemContent(ViewHolder holder, JokeBean.ShowapiResBodyBean.ContentlistBean resultBean) {
            String imageUrl = resultBean.getImg();
            ImageView itemImageView = (ImageView) holder.getViewById(R.id.imageView);
            iconDownLoadThreadPool.imageLoad(itemImageView, imageUrl);

        }

    }

}
