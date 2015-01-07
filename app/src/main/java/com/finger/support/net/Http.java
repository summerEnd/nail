package com.finger.support.net;


import android.content.Context;
import android.telephony.TelephonyManager;

import com.finger.activity.FingerApp;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.Logger;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.HttpUtil;
import com.sp.lib.util.Md5;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

public class Http {
    private static Context context;

    private static final String SECRET = "ju34s4&6d567nuwe678l89kjdf56o34iw!e";
    private static final String host = "http://192.168.1.142/Manicures/index.php?s=/Home/Api/";
    private static String uuId;

    private static String UUID_KEY = "miei";

    public static void init(Context context){
        Http.context=context;
        uuId=getImei();
    }

    public static void post(String methodName, RequestParams params) {
        String time = String.valueOf(new Date().getTime());
        FingerApp app = (FingerApp) context.getApplicationContext();
        params.put("time", time);
        params.put("uuid", uuId);
        params.put("secret", Md5.md5(new String(uuId + time + SECRET)));
        params.put("version", ContextUtil.getVersion());
        params.put("uid", app.isLogin() ? app.getUser().id : 0);
        params.put("os", "android");
        HttpUtil.post(host + methodName, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Logger.d(response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Logger.d(response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                Logger.d(responseString);
            }

        });
    }

    /**
     * 设备id。如果device获取为空，测从preference取出. 如果preference为空就随机生成一个，并保存到preference.
     *
     * @return
     */
    public static String getImei() {

        TelephonyManager telephonemanage = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String miei = telephonemanage.getDeviceId();

        if (miei != null && miei.length() == 14) {
            miei += "1";
        } else if (miei == null) {
            miei = context.getSharedPreferences(UUID_KEY, Context.MODE_PRIVATE).getString(UUID_KEY, null);
        }
        if (miei == null) {
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 15; i++) {
                sb.append(random.nextInt(10));
            }
            miei = sb.toString();
            context.getSharedPreferences(UUID_KEY, Context.MODE_PRIVATE).edit().putString(UUID_KEY, miei).commit();
        }
        return miei;
    }
}
