package com.finger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.finger.R;
import com.finger.activity.artist.my.ChangeResume;
import com.finger.support.widget.ItemUtil;


public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        ItemUtil.init(this);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
//                startActivity(new Intent(WelcomeActivity.this, ChangeResume.class));
                finish();
            }
        }, 1500);
    }
}
