package com.finger.activity.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.info.User;
import com.finger.activity.setting.ChangeMyData;
import com.finger.entity.ArtistRole;
import com.finger.entity.RoleBean;
import com.finger.entity.UserRole;
import com.finger.service.LocationService;
import com.finger.support.Constant;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

@User
public class RegisterActivity extends BaseActivity {
    EditText edit_password,
            edit_mobile,
            edit_yzm;
    Button smsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edit_password = (EditText) findViewById(R.id.password);
        edit_mobile = (EditText) findViewById(R.id.mobile);
        edit_yzm = (EditText) findViewById(R.id.yzm);
        smsButton = (Button) findViewById(R.id.getRegisterCode);
    }

    /**
     * 注册
     *
     * @param v
     */
    public void register(View v) {
        final String mobile = getMobile();
        final String password = getPassword();
        final String register_code = edit_yzm.getText().toString();
        RequestParams params = new RequestParams();
        if (TextUtils.isEmpty(mobile)){
            ContextUtil.toast(edit_mobile.getHint());
            return;
        }
        params.put("phone_num", mobile);

        if (TextUtils.isEmpty(password)){
            ContextUtil.toast(edit_mobile.getHint());
            return;
        }
        params.put("password", password);

        if (TextUtils.isEmpty(register_code)){
            ContextUtil.toast(edit_yzm.getHint());
            return;
        }


        params.put("register_code", register_code);
        FingerHttpClient.post("register", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject object) {
                UserRole bean = new UserRole();
                bean.mobile = mobile;
                bean.password = password;
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle(getString(R.string.congratulation));
                builder.setMessage(getString(R.string.congrate_reg));
                builder.setPositiveButton(R.string.login_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(RegisterActivity.this, ChangeMyData.class));
                        setResult(RESULT_OK);
                        finish();
                    }
                });
                builder.show();
                try {
                    bean.id = object.getInt("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getApp().setUser(bean);
            }


        });
    }

    /**
     * 获取
     *
     * @return
     */
    private String getPassword() {
        return edit_password.getText().toString();
    }

    /**
     * @return
     */
    private String getMobile() {
        return edit_mobile.getText().toString();
    }

    /**
     * 获取验证码
     *
     * @param v
     */
    public void getRegisterCode(View v) {
        RequestParams params = new RequestParams();
        params.put("phone_num", getMobile());
        FingerHttpClient.post("getRegisterCode", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject object) {
                startTimer();
                smsButton.setEnabled(false);
            }

        });
    }


    /**
     * 执行登录
     *
     * @param mobile
     * @param password
     */
    public void doLogin(final String mobile, final String password) {

        RequestParams params = new RequestParams();
        params.put("phone_num", mobile);
        params.put("password", password);
        params.put("type", Constant.LOGIN_TYPE_USER);
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

                        bean = JsonUtil.get(user.toString(), UserRole.class);
                        bean.password = password;
                        //设置登录用户信息
                        getApp().setUser(bean);
                    }

                }
        );

    }

    Timer timer;
    final int MAX_TIME = 60;
    int remainTime;

    void startTimer() {
        remainTime = MAX_TIME;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainTime--;
                handler.sendEmptyMessage(remainTime);
            }
        }, 0, 1000);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int time = msg.what;
            if (time <= 0) {
                smsButton.setText(R.string.get_yzm);
                smsButton.setEnabled(true);
                timer.cancel();
            } else {
                smsButton.setText(getString(R.string.remain_time_d, time));
            }
            return false;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
