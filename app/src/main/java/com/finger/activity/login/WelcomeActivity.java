package com.finger.activity.login;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.MainActivity;
import com.finger.service.LocationService;
import com.finger.support.Constant;
import com.finger.entity.ArtistRole;
import com.finger.entity.RoleBean;
import com.finger.entity.UserRole;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.support.SHttpClient;
import com.sp.lib.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.finger.service.LocationService.*;


public class WelcomeActivity extends BaseActivity {
    final String FIRST_LOGIN = "first_login";
    private LocationService.LocationBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getLocation();
    }

    /**
     * 定位当前位置
     */
    void getLocation() {
        Intent service = new Intent(this, LocationService.class);

        //启动定位服务，别的地方只需要bind就可以了
        startService(service);

        if (SHttpClient.isConnect(this)) {
            bindService(service, cnn, BIND_AUTO_CREATE);
        } else {
            UserRole userRole= (UserRole) FileUtil.readFile(this,Constant.FILE_ROLE);
            getApp().setUser(userRole);
            enterMainActivity();
        }

    }


    /**
     * 链接服务
     */
    private LocationConnection cnn = new LocationConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            super.onServiceConnected(name, service);
            mBinder = (LocationService.LocationBinder) service;
        }

        @Override
        public void onLocated(BDLocation location) {

            if (location != null) {
                getApp().getUser().latitude = location.getLatitude();
                getApp().getUser().longitude = location.getLongitude();

                //这里只保存city_code，因为百度定位的城市名可能与我们后台的不一样
                getApp().getCurCity().city_code = location.getCityCode();
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


    };

    /**
     * 自动登录
     */
    void doLogin() {
        //读取保存的登录信息
        Map<String, String> map = (Map<String, String>) FileUtil.readFile(this, Constant.FILE_ROLE_MAP);

        if (map != null && map.containsKey("mobile")) {
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
                    BDLocation location = LocationService.mBDLocation;
                    if (location != null) {
                        mBinder.updateArtistPosition(bean.id);
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
                overridePendingTransition(R.anim.stand_still, R.anim.stand_still);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cnn != null) {
            unbindService(cnn);
        }
    }
}
