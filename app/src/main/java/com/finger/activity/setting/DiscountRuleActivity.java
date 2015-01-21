package com.finger.activity.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.BaseInfo;


public class DiscountRuleActivity extends BaseActivity{
    TextView coupon_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_rule);
        coupon_text= (TextView) findViewById(R.id.coupon_text);
        BaseInfo info=getApp().getBaseInfo();
        coupon_text.setText(info.coupon_rule);
    }
}
