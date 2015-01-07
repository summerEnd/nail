package com.finger.activity.info;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.OrderListBean;
import com.sp.lib.util.ImageManager;

public class OrderInfoActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
    }

    void setData(OrderListBean bean) {
        ImageView nail_image = (ImageView) findViewById(R.id.nail_image);
        TextView create_time = (TextView) findViewById(R.id.create_time),
                tv_nail_name = (TextView) findViewById(R.id.tv_nail_name),
                tv_price = (TextView) findViewById(R.id.tv_price),
                taxi_fee = (TextView) findViewById(R.id.taxi_fee),
                tv_coupon = (TextView) findViewById(R.id.tv_coupon),
                tv_real_pay = (TextView) findViewById(R.id.tv_real_pay),
                tv_contact = (TextView) findViewById(R.id.tv_contact),
                tv_planTime = (TextView) findViewById(R.id.tv_planTime),
                tv_mobile = (TextView) findViewById(R.id.tv_mobile),
                tv_address = (TextView) findViewById(R.id.tv_address),
                tv_summary = (TextView) findViewById(R.id.tv_summary);

    }
}