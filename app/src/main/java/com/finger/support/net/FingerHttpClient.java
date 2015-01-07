package com.finger.support.net;

import com.finger.BuildConfig;
import com.finger.activity.FingerApp;
import com.finger.entity.RoleBean;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.Logger;
import com.loopj.android.http.RequestParams;
import com.sp.lib.support.SHttpClient;
import com.sp.lib.support.WebJsonHttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static com.finger.support.util.ContextUtil.getContext;
import static com.finger.support.util.ContextUtil.getImei;
import static com.finger.support.util.ContextUtil.getVersion;


public class FingerHttpClient {
    private static final String SECRET = "ju34s4&6d567nuwe678l89kjdf56o34iw!e";
    private static final String host = "http://218.244.149.129/pnail/index.php?s=/Home/Api/";
    private static String MIEI = "miei";

    public static void setDialogCreator(SHttpClient.ProgressDialogCreator creator) {
        SHttpClient.setDialogCreator(creator);
    }

    /**
     * @param method  调用的方法名称
     * @param params  参数
     * @param handler
     */
    public static void post(String method, RequestParams params, final FingerHttpHandler handler) {

        String time = String.valueOf(new Date().getTime());
        String uuId = getImei();

        params.put("time", time);
        params.put("secret", SECRET);
        params.put("uuid", uuId);
        params.put("version", getVersion());
        params.put("os", "android");
        RoleBean user=((FingerApp) getContext()).getUser();
//        if (user instanceof ArtistRole){
//            params.put("mid",user.id );
//        }else if(user instanceof UserRole){
//            params.put("uid", user.id);
//        }
        params.put("uid", user.id);
        SHttpClient.post(host + method, params, new WebJsonHttpHandler() {
            @Override
            public void onSuccess(JSONObject object, JSONArray array) {
                JSONObject response = null;
                try {
                    response = object.getJSONObject("response");
                    if (!"100".equals(response.getString("code"))) {
                        handler.onFail(object);

                    }else{
                        handler.onSuccess(response);
                    }
                    String msg = response.getString("msg");
                    ContextUtil.toast(msg);

                } catch (JSONException e) {
                    Logger.w(e.getLocalizedMessage());
                }
            }

            @Override
            public void onFail(String msg) {
                handler.onFail(null);
                ContextUtil.toast(msg);
            }
        }, BuildConfig.DEBUG);
    }


}
