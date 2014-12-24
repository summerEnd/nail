package com.sp.lib.support;

import org.json.JSONArray;
import org.json.JSONObject;

public interface WebJsonHttpHandler {

    public boolean showDialog=true;

    public void onSuccess(JSONObject object,JSONArray array);

    public void onFail(String msg);
}
