package com.finger.activity.other.plan;

import android.os.Bundle;
import android.view.View;

import com.finger.activity.BaseActivity;
import com.finger.R;


public class OrderConfirm extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirm);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit: {
                finish();

                break;
            }
        }
        super.onClick(v);
    }
}
