package com.hiretaxi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hiretaxi.R;
import com.hiretaxi.activity.base.ToolbarBaseActivity;
import com.hiretaxi.config.Constant;

public class WebActivity extends ToolbarBaseActivity {

    private static final String TAG = "WebActivity";
    private String srcUrl, titleKey;
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        setDefacultTitle("浏览");

        titleKey = getIntent().getStringExtra(TITLE_KEY);
        srcUrl = getIntent().getStringExtra(SRC_URL)==null? Constant.DEFACULT_SRC_URL:getIntent().getStringExtra(SRC_URL);
        initView();
    }

    @Override
    protected View getToolbarView() {
        return null;
    }

    private void initView() {
        setDefacultTitle(titleKey==null?"浏览":titleKey);

        webView = findView(R.id.webView);
        progressBar = findView(R.id.progressbar);
        webView.loadUrl(srcUrl);

        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
//        优先使用缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        不使用缓存：
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成
                    progressBar.setVisibility(View.GONE);
                } else {
                    // 加载中
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();//返回上一页面
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public static final String TITLE_KEY = "titleKey";
    public static final String SRC_URL = "srcUrl";

    public static void startActivity(Context context, String title, String srcUrl) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(TITLE_KEY, title);
        intent.putExtra(SRC_URL, srcUrl);
        context.startActivity(intent);
    }

}
