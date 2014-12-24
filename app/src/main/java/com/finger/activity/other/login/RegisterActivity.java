package com.finger.activity.other.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.finger.BaseActivity;
import com.finger.R;
import com.finger.support.annotion.User;
import com.finger.support.Constant;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.util.ContextUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.support.SHttpClient;
import com.sp.lib.support.WebJsonHttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;

@User
public class RegisterActivity extends BaseActivity {
    EditText edit_password,
            edit_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edit_password = (EditText) findViewById(R.id.password);
        edit_mobile = (EditText) findViewById(R.id.mobile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reg: {
                String user = edit_mobile.getText().toString();
                String password = edit_password.getText().toString();
                RequestParams params = new RequestParams();
                params.put("username", user);
                params.put("password", password);
                FingerHttpClient.post("register", params, new WebJsonHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject object, JSONArray array) {

                    }

                    @Override
                    public void onFail(String msg) {

                    }
                });
                break;
            }
        }
        super.onClick(v);
    }
}
