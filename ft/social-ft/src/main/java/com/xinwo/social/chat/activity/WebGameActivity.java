package com.xinwo.social.chat.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xinwo.base.BaseActivity;
import com.xinwo.social.R;

import java.util.Map;

public class WebGameActivity extends BaseActivity {
    private final String TAG = "WebGameActivity";
    private WebView webView;
//    String url = "http://demo.ligx.top/fuk/";

    public static final String EXTRA_GAME_URL = "extra_game_url";
    public String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_game);

        hideStatusBar();

        initView();
    }

    @Override
    protected void success(Object bean, int tag) {

    }

    @Override
    protected void error(Throwable e, int tag) {

    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if(intent != null){
            url = intent.getStringExtra(EXTRA_GAME_URL);
        }else{
            Log.e(TAG, "initData --> intent 为空");

        }
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        webView = findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        // 设置WebView支持JavaScript
        settings.setJavaScriptEnabled(true);
        //支持自动适配
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);  //支持放大缩小
        settings.setBuiltInZoomControls(true); //显示缩放按钮
        settings.setBlockNetworkImage(true);// 把图片加载放在最后来加载渲染
        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setSaveFormData(true);
        settings.setGeolocationEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);/// 支持通过JS打开新窗口
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //设置不让其跳转浏览器
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        // 添加客户端支持
        webView.setWebChromeClient(new WebChromeClient());

        //不加这个图片显示不出来
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setBlockNetworkImage(false);

        //允许cookie 不然有的网站无法登陆
        CookieManager mCookieManager = CookieManager.getInstance();
        mCookieManager.setAcceptCookie(true);
        mCookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.loadUrl(url);
    }

    @Override
    public void loadData() {

    }

    @Override
    public Map<String, String> getParams(int tag) {
        return null;
    }
}
