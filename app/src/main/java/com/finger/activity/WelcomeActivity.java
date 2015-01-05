package com.finger.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.finger.R;
import com.finger.support.Constant;
import com.finger.support.api.BaiduAPI;
import com.finger.support.entity.ArtistRole;
import com.finger.support.entity.RoleBean;
import com.finger.support.entity.UserRole;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.ItemUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.support.SHttpClient;
import com.sp.lib.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ItemUtil.init(this);
        setContentView(R.layout.activity_welcome);
        locate();
    }

    /**
     * 定位当前位置
     */
    void locate() {
        BaiduAPI.locate(new BaiduAPI.Callback() {
            @Override
            public void onLocated(BDLocation bdLocation) {
                if (bdLocation != null) {
                    getApp().getUser().latitude = bdLocation.getLatitude();
                    getApp().getUser().longitude = bdLocation.getLongitude();
                    getApp().getCurCity().name = bdLocation.getCity();
                    getApp().getCurCity().city_code = bdLocation.getCityCode();
                }
                doLogin();
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
