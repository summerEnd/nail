package com.finger.activity.artist.my;

import android.os.Bundle;
import android.view.View;

import com.finger.R;
import com.finger.activity.BaseActivity;

public class MyResumeActivity extends BaseActivity {
    View v;
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_resume);
        v=findViewById(R.id.modify);
    }
}
