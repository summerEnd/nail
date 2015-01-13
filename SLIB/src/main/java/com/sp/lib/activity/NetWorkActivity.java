package com.sp.lib.activity;

import com.loopj.android.http.RequestParams;
import com.sp.lib.support.SHttpClient;
import com.sp.lib.support.WebJsonHttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 1.加载网络数据
 * 2.加载失败页面
 * 3.加载进度条
 */
public abstract class NetWorkActivity extends BaseActivity {


    WebJsonHttpHandler handler = new WebJsonHttpHandler() {
        @Override
        public void onSuccess(JSONObject object, JSONArray array) {
            NetWorkActivity.this.onSuccess(object);
        }

        @Override
        public void onFail(String msg) {
            NetWorkActivity.this.onFail(msg);
        }
    };

    /**
     * 开始网络任务
     */
    protected void start(NetworkInfo info) {
        handler.showDialog = info.showDialog;
        SHttpClient.post(info.url, info.params, handler, info.showLog);
    }

    protected abstract void onSuccess(JSONObject object);

    protected void onFail(String msg) {

    }
}
