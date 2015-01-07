package com.finger.activity.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.widget.EditItem;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class FindPassword extends BaseActivity {
    EditItem edit_password,
            edit_mobile,
            edit_yzm;
    Button smsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        edit_mobile= (EditItem) findViewById(R.id.edit_mobile);
        edit_password= (EditItem) findViewById(R.id.edit_password);
        edit_yzm= (EditItem) findViewById(R.id.edit_yzm);
        smsButton= (Button) findViewById(R.id.getRegisterCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done: {
                final String mobile = edit_mobile.getContent();
                final String password = edit_password.getContent();
                final String register_code = edit_yzm.getContent();
                RequestParams params = new RequestParams();
                params.put("phone_num", mobile);
                params.put("password", password);
                params.put("code", register_code);
                FingerHttpClient.post("findPassword", params, new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject object) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(FindPassword.this);
                        builder.setMessage(getString(R.string.find_ok));
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.show();

                    }


                });
                break;
            }
            case R.id.getRegisterCode: {
                RequestParams params = new RequestParams();
                params.put("phone_num", edit_mobile.getContent());
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
