package com.finger.activity.setting;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.finger.activity.FingerApp;
import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.entity.BaseInfo;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceAreaActivity extends BaseActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service_area);

        BaseInfo info = getApp().getBaseInfo();

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        if (info != null) {
            webView.loadUrl(info.service_rule);
        } else {
            BaseInfo.reload(this, new FingerHttpHandler() {
                @Override
                public void onSuccess(JSONObject o) {
                    BaseInfo info = getApp().getBaseInfo();
                    webView.loadUrl(info == null ? "" : info.service_rule);
                }
            });
        }
    }


}
