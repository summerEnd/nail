package com.finger.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class UpdatePasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm: {
                EditText tv_init_psw = (EditText) findViewById(R.id.tv_init_psw);
                EditText tv_new_psw = (EditText) findViewById(R.id.tv_new_psw);
                EditText tv_confirm_psw = (EditText) findViewById(R.id.tv_confirm_psw);

                String init_psw = tv_init_psw.getText().toString();
                String new_psw = tv_new_psw.getText().toString();
                String confirm_psw = tv_confirm_psw.getText().toString();
                if (!check(init_psw, new_psw, confirm_psw)) {
                    return;
                }
                RequestParams params = new RequestParams();
                params.put("old_password", init_psw);
                params.put("new_password", new_psw);
                FingerHttpClient.post("changePassword", params, new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject o) {

                    }
                });

                break;
            }
        }
        super.onClick(v);
    }

    boolean check(String init_psw, String new_psw, String confirm_psw) {
        return true;
    }
}