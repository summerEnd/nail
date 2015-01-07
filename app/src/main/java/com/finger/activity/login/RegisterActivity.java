package com.finger.activity.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.info.User;
import com.finger.entity.UserRole;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg: {
                final String mobile = edit_mobile.getText().toString();
                final String password = edit_password.getText().toString();
                final String register_code = edit_yzm.getText().toString();
                RequestParams params = new RequestParams();
                params.put("phone_num", mobile);
                params.put("password", password);
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
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                break;
            }
            case R.id.getRegisterCode: {
                RequestParams params = new RequestParams();
                params.put("phone_num", edit_mobile.getText().toString());
                FingerHttpClient.post("getRegisterCode", params, new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject object) {
                        startTimer();
                        smsButton.setEnabled(false);
                    }

                });
                break;
            }
        }
        super.onClick(v);
    }

    Timer timer;
    final int MAX_TIME = 100;
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