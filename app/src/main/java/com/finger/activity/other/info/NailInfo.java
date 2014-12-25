package com.finger.activity.other.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.activity.other.plan.OrderConfirm;
import com.finger.activity.other.plan.PlanActivity;
import com.finger.support.entity.OrderBean;
import com.finger.support.entity.OrderManager;


public class NailInfo extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nail_info);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_nail: {
                OrderBean bean= OrderManager.getCurrentOrder();
                if (bean==null){
                    bean=OrderManager.createOrder();
                    bean.artist_id=1;
                    bean.nail_id=1;
                    startActivity(new Intent(this, PlanActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(this, OrderConfirm.class));
                }
                break;
            }
        }
        super.onClick(v);
    }
}
