package com.finger.support.net;

import com.finger.support.Constant;
import com.finger.FingerApp;
import com.loopj.android.http.RequestParams;
import com.sp.lib.support.SHttpClient;
import com.sp.lib.support.WebJsonHttpHandler;

import java.util.Date;

import static com.finger.support.util.ContextUtil.getContext;
import static com.finger.support.util.ContextUtil.getImei;
import static com.finger.support.util.ContextUtil.getVersion;


public class FingerHttpClient {
    private static final String SECRET = "ju34s4&6d567nuwe678l89kjdf56o34iw!e";
    private static final String host = "http://192.168.1.142/Manicures/index.php?s=/Home/Api/";
    private static String MIEI = "miei";

    public static void setDialogCreator(SHttpClient.ProgressDialogCreator creator){
        SHttpClient.setDialogCreator(creator);
    }

    /**
     * @param method 调用的方法名称
     * @param params 参数
     * @param handler
     */
    public static void post(String method, RequestParams params, WebJsonHttpHandler handler) {

        String time = String.valueOf(new Date().getTime());
        String uuId = getImei();

        params.put("time", time);
        params.put("secret", SECRET);
        params.put("uuid", uuId);
        params.put("version", getVersion());
        params.put("os", "android");
        params.put("uid", ((FingerApp) getContext()).getUser().id);
        SHttpClient.post(host+method, params, handler);
    }


}
