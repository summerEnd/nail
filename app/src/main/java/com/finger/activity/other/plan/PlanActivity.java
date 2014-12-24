package com.finger.activity.other.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioGroup;

import com.baidu.location.BDLocation;
import com.finger.BaseActivity;
import com.finger.R;
import com.finger.support.api.BaiduAPI;
import com.finger.support.entity.RoleBean;
import com.finger.support.util.ContextUtil;

public class PlanActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    RadioGroup rg_plan;
    PlanForMe mPlanForMe = new PlanForMe();
    PlanForOther mPlanForOther = new PlanForOther();
    int REQUEST_CODE_GPS = 101;
    Schedule mSchedule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        rg_plan = (RadioGroup) findViewById(R.id.rg_plan);
        rg_plan.setOnCheckedChangeListener(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag_container, mPlanForMe)
                .commit();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (checkedId) {
            case R.id.rb_user: {
                ft.hide(mPlanForOther).show(mPlanForMe);
                break;
            }
            case R.id.rb_artist: {
                if (!mPlanForOther.isAdded()) {
                    ft.add(R.id.frag_container, mPlanForOther);
                }

                ft.hide(mPlanForMe).show(mPlanForOther);
                break;
            }
        }
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GPS) {
            BaiduAPI.locate(new BaiduAPI.Callback() {
                @Override
                public void onLocated(BDLocation bdLocation) {
                    RoleBean bean = getApp().getUser();
                    mPlanForMe.addMark(bean.latitude, bean.longitude);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        ContextUtil.toast_debug("click:"+v);
        switch (v.getId()) {

            case R.id.item_plan_time: {
                if (mSchedule==null){
                    mSchedule=new Schedule(this);
                }
                mSchedule.showAtLocation(v, Gravity.BOTTOM,0,0);
                break;
            }

            default:
                super.onClick(v);

        }
    }
}
