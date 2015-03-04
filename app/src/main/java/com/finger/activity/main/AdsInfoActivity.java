package com.finger.activity.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.sp.lib.anim.ActivityAnimator;


public class AdsInfoActivity extends BaseActivity {
    ProgressBar progressBar;
    WebView     webView;
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_URL   = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
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
        setWindowTitle(null);
        String url = getIntent().getStringExtra(EXTRA_URL);
        webView.loadUrl(url);
    }

    void setWindowTitle(String title) {
        if (TextUtils.isEmpty(title)) {

            setTitle(R.string.app_name);
        } else {
            setTitle(title);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                ActivityAnimator.override(this, ActivityAnimator.NO_ANIMATION, ActivityAnimator.OUT_SLIDE_DOWN);
                break;
        }
    }
}
