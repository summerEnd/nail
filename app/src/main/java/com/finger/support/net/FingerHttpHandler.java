package com.finger.support.net;

import org.json.JSONObject;

public abstract class FingerHttpHandler {
    /**
     * 当返回数据中code 为100
     * @param o
     */
    public abstract void onSuccess(JSONObject o);

    /**
     * 当网络异常，或者返回数据code不为100
     * @param o
     */
    public  void onFail(JSONObject o){

    };
}
