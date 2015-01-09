package com.sp.lib.support;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class WebJsonHttpHandler {

    public  boolean showDialog=true;

    public abstract void onSuccess(JSONObject object,JSONArray array);

    public abstract void onFail(String msg);
}
