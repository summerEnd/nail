package com.finger.activity.other.plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.finger.R;
import com.finger.support.entity.OrderBean;
import com.finger.support.entity.OrderManager;

import static com.finger.activity.other.plan.PlanActivity.PlanFragment;

/**
 * Created by acer on 2014/12/18.
 */
public class PlanForOther extends PlanFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plan_for_other, null);
        v.findViewById(R.id.choose_nail_artist).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.choose_nail_artist: {
                OrderBean bean = OrderManager.getCurrentOrder();
                if (bean == null) {
                    bean = OrderManager.createOrder();
                    bean.location = "location";
                }
                bean.location = "location";
                submit();
                break;
            }
        }

    }
}
