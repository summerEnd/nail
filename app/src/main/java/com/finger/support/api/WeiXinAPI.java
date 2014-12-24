package com.finger.support.api;

import android.content.Context;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by acer on 2014/12/8.
 */
public class WeiXinAPI {
    private static IWXAPI api;
    private static String APP_ID = "";
    private static String app_secret = "";
    private static String app_key = "";
    private static String partner_key = "";

    public static final void init(Context context) {
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        api.registerApp(APP_ID);
    }

    public static final void pay() {

    }

}
