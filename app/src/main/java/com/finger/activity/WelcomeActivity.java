package com.finger.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.finger.R;
import com.finger.support.Constant;
import com.finger.support.entity.ArtistRole;
import com.finger.support.entity.RoleBean;
import com.finger.support.entity.UserRole;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.widget.ItemUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;


public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ItemUtil.init(this);
        setContentView(R.layout.activity_welcome);
        doLogin();
    }

    void doLogin() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        final String mobile = sp.getString("mobile", null);
        final String password = sp.getString("password", null);
        final String type = sp.getString("type", null);
        if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(password)) {

            RequestParams params = new RequestParams();
            params.put("phone_num", mobile);
            params.put("password", password);
            params.put("type", type);

            FingerHttpClient.post("login", params, new FingerHttpHandler() {
                @Override
                public void onSuccess(JSONObject o) {
                    RoleBean bean;
                    if (type.equals(Constant.LOGIN_TYPE_ARTIST)) {
                        bean = new ArtistRole();
                    } else {
                        bean = new UserRole();
                    }
                    bean.mobile = mobile;
                    bean.password = password;
                    try {
                        bean.id = o.getInt("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getApp().setUser(bean);
                    enterMainActivity();
                }

                @Override
                public void onFail(JSONObject o) {
                    enterMainActivity();
                }
            });
        } else {
            enterMainActivity();
        }
    }

    void enterMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                            testArtist();//测试美甲师
//                            testUser();//测试用户
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }, 1000);
    }

    void testArtist() {
        ArtistRole role = new ArtistRole();
        role.id = 2;
        getApp().setUser(role);
        ContextUtil.toast("test artist--->");
    }

    void testUser() {
        UserRole role = new UserRole();
        role.id = 6;
        getApp().setUser(role);
        ContextUtil.toast("test user--->");
    }
}
