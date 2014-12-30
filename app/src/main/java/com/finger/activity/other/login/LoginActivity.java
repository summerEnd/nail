package com.finger.activity.other.login;

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

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.support.entity.ArtistRole;
import com.finger.support.entity.RoleBean;
import com.finger.support.entity.UserRole;
import com.finger.support.Constant;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.widget.EditItem;
import com.finger.support.util.ContextUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by acer on 2014/12/8.
 */
public class LoginActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    RadioGroup rg_login;
    UserLogin userLogin;
    ArtistLogin ARTIST;
    UserLogin USER;
    FragmentManager manager;


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

                if (type.equals(Constant.LOGIN_TYPE_ARTIST)) {
                    bean = new ArtistRole();
                } else {
                    bean = new UserRole();
                }
                bean.mobile = mobile;
                bean.password = password;
                try {
                    bean.id = o.getInt("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getApp().setUser(bean);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFail(JSONObject o) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.forget_password: {
                ContextUtil.toast_debug("forget_password");
                Intent intent = new Intent(this, FindPassword.class);
                startActivity(intent);
                break;
            }

            case R.id.update_password: {
                ContextUtil.toast_debug("update_password");
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
                    getActivity().startActivity(new Intent(getActivity(), RegisterActivity.class));
                    break;
                }
            }
        }
    }

    public static class ArtistLogin extends Fragment implements View.OnClickListener {
        EditItem edit_phone;
        EditItem edit_password;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_artist_login, null);
            v.findViewById(R.id.login).setOnClickListener(this);
            edit_phone = (EditItem) v.findViewById(R.id.edit_phone);
            edit_password = (EditItem) v.findViewById(R.id.edit_password);
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
}
