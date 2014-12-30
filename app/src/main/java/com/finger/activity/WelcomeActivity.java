package com.finger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.finger.R;
import com.finger.support.entity.ArtistRole;
import com.finger.support.entity.UserRole;
import com.finger.support.util.ContextUtil;
import com.finger.support.widget.ItemUtil;


public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        ItemUtil.init(this);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                testArtist();//测试美甲师
//                testUser();//测试用户
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);
    }

    void testArtist() {
        ArtistRole role = new ArtistRole();
        role.id=6;
        getApp().setUser(role);
        ContextUtil.toast("test artist--->");
    }
    void testUser() {
        UserRole role = new UserRole();
        role.id=6;
        getApp().setUser(role);
        ContextUtil.toast("test user--->");
    }
}
