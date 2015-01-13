package com.finger.activity.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.MainActivity;
import com.finger.support.Constant;
import com.finger.api.BaiduAPI;
import com.finger.entity.ArtistRole;
import com.finger.entity.RoleBean;
import com.finger.entity.UserRole;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.util.ItemUtil;
import com.finger.support.util.Logger;
import com.loopj.android.http.RequestParams;
import com.sp.lib.support.SHttpClient;
import com.sp.lib.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class WelcomeActivity extends BaseActivity {
    final String FIRST_LOGIN = "first_login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        ItemUtil.init(this);
        locate();
    }

    /**
     * 定位当前位置
     */
    void locate() {
        Logger.d("locate");
        BaiduAPI.locate(new BaiduAPI.Callback() {
            @Override
            public void onLocated(BDLocation bdLocation) {
                if (bdLocation != null) {
                    getApp().getUser().latitude = bdLocation.getLatitude();
                    getApp().getUser().longitude = bdLocation.getLongitude();
                    getApp().getCurCity().name = bdLocation.getCity();
                    getApp().getCurCity().city_code = bdLocation.getCityCode();
                }
                SharedPreferences sp = getSharedPreferences(FIRST_LOGIN, MODE_PRIVATE);
                boolean isFirst = sp.getBoolean("first", true);
                //如果是第一次进入,就跳转到向导页

                if (isFirst) {
                    startActivity(new Intent(WelcomeActivity.this, GuideActivity.class));
                    sp.edit().putBoolean("first", false).commit();
                    finish();
                } else {
                    doLogin();
                }
            }
        });
    }

    /**
     * 自动登录
     */
    void doLogin() {
        //读取保存的登录信息
        Map<String, String> map = (Map<String, String>) FileUtil.readFile(this, Constant.FILE_ROLE);

        if (map != null) {
            String mobile = map.get("mobile");
            final String password = map.get("password");
            final String type = map.get("type");
            RequestParams params = new RequestParams();
            params.put("phone_num", mobile);
            params.put("password", password);
            params.put("type", type);

            FingerHttpClient.post("login", params, new FingerHttpHandler() {
                @Override
                public void onSuccess(JSONObject o) {
                    RoleBean bean;
                    JSONObject user = null;
                    try {
                        user = o.getJSONObject("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (type.equals(Constant.LOGIN_TYPE_ARTIST)) {
                        bean = JsonUtil.get(user.toString(), ArtistRole.class);
                    } else {
                        bean = JsonUtil.get(user.toString(), UserRole.class);
                    }
                    bean.password = password;
                    getApp().setUser(bean);
                    BDLocation location=BaiduAPI.mBDLocation;
                    if (location!=null){
                        getApp().updatePosition(location.getLatitude(),location.getLongitude());
                    }
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

    /**
     * 进入首页
     */
    void enterMainActivity() {
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //testArtist();//测试美甲师
                //testUser();//测试用户
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
