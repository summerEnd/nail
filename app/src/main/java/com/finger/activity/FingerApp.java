package com.finger.activity;

import android.app.Application;
import android.content.Intent;
import android.content.res.XmlResourceParser;

import com.finger.R;
import com.finger.api.BaiduAPI;
import com.finger.entity.ArtistGrade;
import com.finger.entity.ArtistRole;
import com.finger.entity.BaseInfo;
import com.finger.entity.CityBean;
import com.finger.entity.RoleBean;
import com.finger.entity.UserRole;
import com.finger.support.Constant;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.Logger;
import com.sp.lib.Slib;
import com.sp.lib.util.FileUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class FingerApp extends Application {

    private RoleBean bean;
    public static final String ACTION_ROLE_CHANGED = "finger.role.changed";
    private static FingerApp mApp;
    /**
     * 当前城市
     */
    private CityBean curCity  = new CityBean();
    private BaseInfo baseInfo = new BaseInfo();

    public BaseInfo getBaseInfo() {
        return baseInfo = (baseInfo == null ? new BaseInfo() : baseInfo);
    }

    public void setBaseInfo(BaseInfo baseInfo) {

        this.baseInfo = baseInfo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        ContextUtil.init(this);
        BaiduAPI.init(this);
        Slib.initialize(this);

        loadGradeFromXml();


    }

    public static FingerApp getInstance() {
        return mApp;
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

        String type;
        if (bean instanceof UserRole) {
            type = Constant.LOGIN_TYPE_USER;
        } else if (bean instanceof ArtistRole) {
            type = Constant.LOGIN_TYPE_ARTIST;
        } else {
            type = Constant.LOGIN_TYPE_EMPTY;
        }
        Map<String, String> map = new HashMap<String, String>();
        if (bean != null) {
            Logger.i_format("mobile:%s password:%s type:%s", bean.mobile, bean.password, type);

            map.put("mobile", bean.mobile);
            map.put("password", bean.password);
            map.put("type", type);
        }
        FileUtil.saveFile(this, Constant.FILE_ROLE, map);
        sendBroadcast(new Intent(ACTION_ROLE_CHANGED).putExtra("role", getUser().getType()));
    }

    /**
     * 返回一个城市，非定位获得的，由用户设定
     *
     * @return
     */
    public CityBean getCurCity() {
        return curCity;
    }

    public void setCurCity(CityBean curCity) {
        this.curCity = curCity;
    }

    public String getLoginType() {
        return getUser().getType();
    }

    public boolean isLogin() {
        return getUser().id != null;
    }


}
