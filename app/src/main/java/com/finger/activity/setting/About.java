package com.finger.activity.setting;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.BaseInfo;
import com.finger.support.net.FingerHttpHandler;

import org.json.JSONObject;

public class About extends BaseActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        BaseInfo info = getApp().getBaseInfo();
        if (info != null) {
            webView.loadUrl(info.about_us);
        } else {
            BaseInfo.reload(this, new FingerHttpHandler() {
                @Override
                public void onSuccess(JSONObject o) {
                    BaseInfo info = getApp().getBaseInfo();
                    webView.loadUrl(info == null ? "" : info.about_us);
                }
            });
        }
    }
}
