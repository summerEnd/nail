package com.finger.activity.other.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.support.annotion.User;
import com.finger.support.entity.UserBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;
import com.sp.lib.support.WebJsonHttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
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
                final String mobile = edit_mobile.getText().toString();
                final String password = edit_password.getText().toString();
                RequestParams params = new RequestParams();
                params.put("mobile", mobile);
                params.put("password", password);
                FingerHttpClient.post("register", params, new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject object) {
                        UserBean bean=new UserBean();
                        bean.mobile=mobile;
                        bean.password=password;
                        AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                        builder.setTitle(getString(R.string.congratulation));
                        builder.setMessage(getString(R.string.congrate_reg));
                        builder.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                        try {
                            bean.id=object.getInt("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getApp().setUser(bean);
                    }


                });
                break;
            }
        }
        super.onClick(v);
    }
}
