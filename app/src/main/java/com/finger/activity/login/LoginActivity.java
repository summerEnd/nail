package com.finger.activity.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.baidu.location.BDLocation;
import com.finger.activity.FingerApp;
import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.api.BaiduAPI;
import com.finger.entity.ArtistRole;
import com.finger.entity.RoleBean;
import com.finger.entity.UserRole;
import com.finger.service.LocationService;
import com.finger.support.Constant;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.EditItem;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by acer on 2014/12/8.
 */
public class LoginActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    RadioGroup      rg_login;
    UserLogin       userLogin;
    ArtistLogin     ARTIST;
    UserLogin       USER;
    FragmentManager manager;
    private LocationService.LocationConnection conn;
    final int REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rg_login = (RadioGroup) findViewById(R.id.rg_login);
        rg_login.setOnCheckedChangeListener(this);
        manager = getSupportFragmentManager();
        ARTIST = new ArtistLogin();
        USER = new UserLogin();
        manager.beginTransaction()
                .add(R.id.frag_container, ARTIST)
                .add(R.id.frag_container, USER)
                .hide(ARTIST)
                .commit();
        userLogin = new UserLogin();

    }

    /**
     * @param mobile
     * @param password
     * @param type     登录类型：美甲师、用户
     */
    public void doLogin(final String mobile, final String password, final String type) {


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

                Map<String,Object> map=new HashMap<String, Object>();
                map.put("password",password);
                map.put("mobile",mobile);
                map.put("type",type);
                FileUtil.saveFile(getApplicationContext(),Constant.FILE_ROLE_MAP,map);
                //设置登录用户信息
                getApp().setUser(bean);
                //登录完成，定位当前位置
                if (type.equals(Constant.LOGIN_TYPE_ARTIST)) {
                   final Dialog dialog= ProgressDialog.show(LoginActivity.this, null, getString(R.string.update_location));
                    conn = new LocationService.LocationConnection() {
                        @Override
                        public void onLocated(BDLocation location) {
                            dialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }
                    };


                    //更新位置，LocationService中会自动更新美甲师当前位置
                    bindService(new Intent(LoginActivity.this, LocationService.class), conn, BIND_AUTO_CREATE);

                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }


        });

    }

    public void doRegister() {
        startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            finish();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.forget_password: {
                Intent intent = new Intent(this, FindPassword.class);
                startActivity(intent);
                break;
            }

            case R.id.update_password: {
                Intent intent = new Intent(this, UpdatePasswordActivity.class);
                startActivity(intent);
                break;
            }

            default:
                super.onClick(v);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction ft = manager.beginTransaction();
        switch (checkedId) {
            case R.id.rb_artist: {
                ft.hide(USER).show(ARTIST);
                break;
            }
            case R.id.rb_user: {
                ft.hide(ARTIST).show(USER);
                break;
            }
        }
        ft.commit();
    }

    /**
     * 用户登录
     */
    public static class UserLogin extends Fragment implements View.OnClickListener {
        EditItem edit_phone;
        EditItem edit_password;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_user_login, null);
            v.findViewById(R.id.login).setOnClickListener(this);
            v.findViewById(R.id.register).setOnClickListener(this);
            edit_phone = (EditItem) v.findViewById(R.id.edit_phone);
            edit_password = (EditItem) v.findViewById(R.id.edit_password);
            edit_password.getTextView().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edit_phone.getTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
            return v;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login: {
                    String mobile = edit_phone.getContent();
                    String password = edit_password.getContent();

                    ((LoginActivity) getActivity()).doLogin(mobile, password, Constant.LOGIN_TYPE_USER);

                    break;
                }
                case R.id.register: {
                    ((LoginActivity) getActivity()).doRegister();
                    break;
                }
            }
        }
    }

    /**
     * 美甲师登录
     */
    public static class ArtistLogin extends Fragment implements View.OnClickListener {
        EditItem edit_phone;
        EditItem edit_password;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_artist_login, null);
            v.findViewById(R.id.login).setOnClickListener(this);
            edit_phone = (EditItem) v.findViewById(R.id.edit_phone);
            edit_password = (EditItem) v.findViewById(R.id.edit_password);
            edit_phone.getTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
            edit_password.getTextView().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            return v;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login: {

                    String mobile = edit_phone.getContent();
                    String password = edit_password.getContent();

                    ((LoginActivity) getActivity()).doLogin(mobile, password, Constant.LOGIN_TYPE_ARTIST);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
        }
    }
}
