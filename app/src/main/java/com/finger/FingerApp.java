package com.finger;

import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.finger.support.Constant;
import com.finger.support.entity.ArtistGrade;
import com.finger.support.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.Http;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.Logger;
import com.sp.lib.Slib;

import com.finger.support.api.BaiduAPI;
import com.sp.lib.support.SHttpClient;
import com.sp.lib.util.FileUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class FingerApp extends Application {

    private RoleBean bean;
    public static final String ACTION_ROLE_CHANGED = "finger.role.changed";

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtil.init(this);
        BaiduAPI.init(this);
        Slib.initialize(this);
        FingerHttpClient.setDialogCreator(new SHttpClient.ProgressDialogCreator() {
            @Override
            public Dialog onCreateDialog() {
                ProgressDialog dialog = new ProgressDialog(getApplicationContext());
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage(getString(R.string.loading));
                dialog.show();
                return dialog;
            }
        });

        BaiduAPI.mLocationClient.start();
        Http.init(this);
        loadGradeFromXml();
    }

    void loadGradeFromXml() {
        XmlResourceParser parser = getResources().getXml(R.xml.grade);
        LinkedList<ArtistGrade> products = new LinkedList<ArtistGrade>();
        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG
                        && parser.getName().equalsIgnoreCase("item")) {
                    ArtistGrade product = new ArtistGrade();
                    product.value = parser.getAttributeIntValue(0, 0);
                    product.from = parser.getAttributeIntValue(1, 0);
                    product.to = parser.getAttributeIntValue(2, 0);
                    products.add(product);
                }
                eventType = parser.next();
            }
            FileUtil.saveFile(this, Constant.FILE_ARTIST_GRADE, products);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RoleBean getUser() {
        if (bean == null) {
            bean = new RoleBean.EmptyRole();
        }
        return bean;
    }

    public void setUser(RoleBean bean) {
        this.bean = bean;
        sendBroadcast(new Intent(ACTION_ROLE_CHANGED).putExtra("role", getUser().getType()));
    }

    public String getLoginType() {
        return getUser().getType();
    }

    public boolean isLogin() {
        return getUser().id != null;
    }

    public void onExit() {
        BaiduAPI.mLocationClient.stop();
    }
}
