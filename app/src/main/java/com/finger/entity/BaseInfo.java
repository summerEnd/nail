package com.finger.entity;

import android.content.Context;

import com.finger.activity.FingerApp;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by acer on 2015/1/19.
 */
public class BaseInfo implements Serializable {
    public String service_tel;
    public String about_us;
    public String coupon_rule;
    public String taxi_rule;
    public String service_rule;

    /**
     * 从网络加载基础信息
     * @param context
     */
    public static void reload(final Context context,final FingerHttpHandler handler) {
        RequestParams params = new RequestParams();
        FingerHttpClient.post("getBaseInfo", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    BaseInfo baseInfo = JsonUtil.get(o.getString("data"), BaseInfo.class);
                    FingerApp.getInstance().setBaseInfo(baseInfo);
                    FileUtil.saveFile(context, "baseInfo", baseInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.onSuccess(o);

            }

            @Override
            public void onFail(JSONObject o) {
                super.onFail(o);

                BaseInfo info = (BaseInfo) FileUtil.readFile(context, "baseInfo");
                FingerApp.getInstance().setBaseInfo(info);
                handler.onFail(o);
            }
        });
    }
}
