package com.example.myapplication.webview;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.myapplication.R;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "WebViewActivity";
    private static final String ACTION_TITLE = "WebView的使用";
    private static final String URL_HTTP = "http://";
    @Bind(R.id.layout)
    RelativeLayout layout;
    private View showVideoView;
    @Bind(R.id.et_to_url)
    EditText etToUrl;
    @Bind(R.id.btn_to)
    Button btnTo;
    @Bind(R.id.top_layout)
    LinearLayout topLayout;
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.video_layout)
    FrameLayout videoLayout;
    private WebSettings mWebSettings;
    private String toUrl = "http://www.baidu.com";
    private WebChromeClient.CustomViewCallback mCallback = null;
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            //设置当前加载的进度
            if (newProgress == 100) {
                //加载完成
            } else {
                //正在加载
            }
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            super.onReceivedTouchIconUrl(view, url, precomposed);
            Log.i(TAG, "onReceivedTouchIconUrl: "+url+precomposed);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            //当点击全屏显示时调用该方法
            mCallback = callback;
            showVideoView = view;
            videoLayout.addView(showVideoView);
            videoLayout.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = showVideoView.getLayoutParams();
            layoutParams.height =ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            showVideoView.setLayoutParams(layoutParams);
            mWebView.setVisibility(View.GONE);
            topLayout.setVisibility(View.GONE);
            layout.setSystemUiVisibility(View.INVISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.i(TAG, "onShowCustomView: 当点击全屏显示时调用该方法？");
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            //在全屏情况下 点击小屏幕显示调用该方法；
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            videoLayout.removeView(showVideoView);
            showVideoView = null;
            layout.setSystemUiVisibility(View.VISIBLE);
            videoLayout.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            topLayout.setVisibility(View.VISIBLE);
            Log.i(TAG, "onHideCustomView: 点击小屏幕显示调用该方法？");
        }
    };
    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //设置在webView中显示而不是在第三方浏览器中打开
            Log.i(TAG, "shouldOverrideUrlLoading: url ---> " + url);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            addImageClickListner();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //要加载页面的时候调用该方法；
        }
    };

    private int uiWidth;
    private ActionBar bar;
    WeakReference<Context> re = new WeakReference<Context>(this);
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        uiWidth = getResources().getDisplayMetrics().widthPixels;

        initWebView();
        addListener();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //当前点击的是返回键

            if (showVideoView  != null){
                videoLayout.removeView(showVideoView);
                showVideoView = null;
                mCallback.onCustomViewHidden();
                videoLayout.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                topLayout.setVisibility(View.VISIBLE);

            }else if (mWebView.canGoBack()) {
                mWebView.goBack();

            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addListener() {

        btnTo.setOnClickListener(this);

    }

    private void initWebView() {
        /*mWebView.goBack();   //后退
        mWebView.goForward();//前进
        mWebView.reload();  //刷新*/
        mWebView.loadUrl(toUrl);
        //设置当前在webView中显示
        mWebView.setWebViewClient(mWebViewClient);
        //设置获得当前加载进度；
        mWebView.setWebChromeClient(mWebChromeClient);
        //如果当前显示页面有输入框时可以获得焦点
        mWebView.requestFocus();
        //滚动条风格，为0指滚动条不占用空间，直接覆盖在网页上
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //获得WebView的设置项；
        mWebSettings = mWebView.getSettings();
        //设置编码格式
        mWebSettings.setDefaultTextEncodingName("GBK");//设置字符编码
        //设置WebView是否需要进行缓存
        setWebViewCacheMode(true);
        //设置开启JS
        setWebJS(true);
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void clickOnAndroid(String src) {
                Log.i(TAG, "clickOnAndroid: 是否被点击呢？" + src);
            }
        }, "imagelisetner");
        //把图片加载放在最后来加载渲染
        mWebSettings.setBlockNetworkImage(false);
        //listview,webview中滚动拖动到顶部或者底部时的阴影
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);

    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.clickOnAndroid(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    private void setWebJS(boolean boo) {
        if (boo) {
            Toast.makeText(WebViewActivity.this, "打开JS", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WebViewActivity.this, "关闭JS", Toast.LENGTH_SHORT).show();
        }
        mWebSettings.setJavaScriptEnabled(boo);
        mWebView.reload();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.btn_to://TO按键跳转地址
                toUserUrl();
                break;
        }
    }

    private void toUserUrl() {
        String userUrl = etToUrl.getText().toString().trim();
        if (!userUrl.startsWith(URL_HTTP)) {
            userUrl = URL_HTTP + userUrl;
        }
        mWebView.loadUrl(userUrl);
    }



    public void setWebViewCacheMode(boolean isOpen) {
        if (isOpen) {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            Toast.makeText(WebViewActivity.this, "缓存机制打开", Toast.LENGTH_SHORT).show();
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            Toast.makeText(WebViewActivity.this, "缓存机制关闭", Toast.LENGTH_SHORT).show();
        }
        mWebView.reload();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        if (layout!=null && mWebView!= null) {
            mWebView.setWebChromeClient(null);
            mWebChromeClient =null;
            mWebViewClient = null;
            mWebView.setWebViewClient(null);
            layout.removeView(mWebView);
            mWebView.destroy();
        }
        super.onDestroy();

    }
}
