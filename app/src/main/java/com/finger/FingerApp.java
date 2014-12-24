package com.finger;

import android.app.Application;
import android.content.Intent;

import com.finger.support.entity.RoleBean;
import com.finger.support.net.Http;
import com.finger.support.util.ContextUtil;
import com.sp.lib.Slib;

import com.finger.support.api.BaiduAPI;

public class FingerApp extends Application {

    private RoleBean bean;
    public static final String ACTION_ROLE_CHANGED="finger.role.changed";
    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtil.init(this);
        BaiduAPI.init(this);
        Slib.initialize(this);
        ContextUtil.toast_debug(String.format("type:%s  id=%d",getUser().getType(),getUser().id));
        BaiduAPI.mLocationClient.start();
        Http.init(this);
    }

    public RoleBean getUser() {
        if (bean==null){
            bean=new RoleBean.EmptyRole();
        }
        return bean;
    }

    public void setUser(RoleBean bean) {
        this.bean = bean;
        sendBroadcast(new Intent(ACTION_ROLE_CHANGED).putExtra("role", getUser().getType()));
    }

    public String getLoginType(){
        return getUser().getType();
    }

    public boolean isLogin(){
        return getUser().id!=null;
    }

    public void onExit(){
        BaiduAPI.mLocationClient.stop();
    }
}
