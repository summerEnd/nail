package com.finger;

import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.WindowManager;

import com.finger.support.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.Http;
import com.finger.support.util.ContextUtil;
import com.sp.lib.Slib;

import com.finger.support.api.BaiduAPI;
import com.sp.lib.support.SHttpClient;

public class FingerApp extends Application {

    private RoleBean bean;
    public static final String ACTION_ROLE_CHANGED="finger.role.changed";
    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtil.init(this);
        BaiduAPI.init(this);
        Slib.initialize(this);
        FingerHttpClient.setDialogCreator(new SHttpClient.ProgressDialogCreator() {
            @Override
            public Dialog onCreateDialog() {
                ProgressDialog dialog=new ProgressDialog(getApplicationContext(),android.R.style.Theme_Holo_Light);
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage(getString(R.string.loading));
                dialog.show();
                return dialog;
            }
        });
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
