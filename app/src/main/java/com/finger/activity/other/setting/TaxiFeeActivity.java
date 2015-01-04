package com.finger.activity.other.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.BaseActivity;


public class TaxiFeeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_fee);
        showData();
    }

    void showData() {
        TextView tv5 = (TextView) findViewById(R.id.tv_fee_0_5);
        TextView tv10 = (TextView) findViewById(R.id.tv_fee_5_10);
        TextView tv15 = (TextView) findViewById(R.id.tv_fee_10_15);
        TextView tv20 = (TextView) findViewById(R.id.tv_fee_15_20);
        TextView tv20_ = (TextView) findViewById(R.id.tv_fee_20_);
    }
}
