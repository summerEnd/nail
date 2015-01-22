package com.finger.activity.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.finger.R;
import com.finger.activity.base.BaseActivity;


public class BaseInfoActivity extends BaseActivity {
    ProgressBar progressBar;
    WebView     webView;
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_URL   = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setWindowTitle(title);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressBar.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
        setWindowTitle(getIntent().getStringExtra(EXTRA_TITLE));
        webView.loadUrl(getIntent().getStringExtra(EXTRA_URL));
    }

    void setWindowTitle(String title) {
        if (TextUtils.isEmpty(title)) {

            setTitle(R.string.app_name);
        } else {
            setTitle(title);
        }
    }
}
