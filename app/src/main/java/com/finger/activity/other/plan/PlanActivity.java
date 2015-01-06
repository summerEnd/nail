package com.finger.activity.other.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.baidu.location.BDLocation;
import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.activity.artist.my.PlanTimeActivity;
import com.finger.activity.other.info.ArtistList;
import com.finger.support.api.BaiduAPI;
import com.finger.support.entity.OrderBean;
import com.finger.support.entity.OrderManager;
import com.finger.support.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.EditItem;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static com.finger.activity.artist.my.PlanTimeActivity.TimeBlock;
import static com.finger.activity.other.plan.Schedule.Callback;

public class PlanActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    RadioGroup rg_plan;
    PlanForMe mPlanForMe = new PlanForMe();
    PlanForOther mPlanForOther = new PlanForOther();
    int REQUEST_CODE_GPS = 101;
    Schedule mSchedule;
    Callback mScheduleCallback;

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

        switch (v.getId()) {

            default:
                super.onClick(v);

        }
    }


    /**
     * 弹出预约时间
     *
     * @param blocks 为null代表所有时间都可以预约
     */
    void showSchedule(List<TimeBlock> blocks) {

        if (mSchedule == null) {
            mSchedule = new Schedule(this);
        }
        mSchedule.setCallback(mScheduleCallback);
        mSchedule.setTimeBlock(blocks);
        mSchedule.showAtLocation(rg_plan, Gravity.BOTTOM, 0, 0);

    }

    void getTimeBlock(int mid) {
        RequestParams params = new RequestParams();
        params.put("mid", mid);
        FingerHttpClient.post("getTimeBlock", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    ArrayList<TimeBlock> blocks = new ArrayList<TimeBlock>();
                    JsonUtil.getArray(o.getJSONArray("data"), TimeBlock.class, blocks);
                    showSchedule(blocks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //此界面关闭，订单取消
        OrderManager.cancel();
    }

    public void getPlanTime(Callback callback) {
        mScheduleCallback = callback;
        OrderBean order = OrderManager.getCurrentOrder();
        if (order != null && order.artist != null) {
            getTimeBlock(order.artist.id);
        } else {
            showSchedule(null);
        }
    }

    public static class PlanFragment extends Fragment {

        String planTime = null;
        String book_date = null;
        String time_block;

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            Button commit = (Button) view.findViewById(R.id.choose_nail_artist);
            OrderBean bean = OrderManager.getCurrentOrder();
            if (bean == null) {
                commit.setText(R.string.choose_nail_artist);
            } else {
                commit.setText(R.string.next_step);
            }
        }

        /**
         * 提交订单
         */
        public void submit() {

            OrderBean bean = OrderManager.getCurrentOrder();

            if (bean == null) {
                return;
            }

            if (TextUtils.isEmpty(bean.mobile)) {
                ContextUtil.toast(R.string.input_mobile);
                return;
            }
            if (TextUtils.isEmpty(bean.address)) {
                ContextUtil.toast(R.string.input_address);
                return;
            }
            if (TextUtils.isEmpty(bean.contact)) {
                ContextUtil.toast(R.string.input_contact);
                return;
            }
            if (TextUtils.isEmpty(bean.planTime)) {
                ContextUtil.toast(R.string.input_plan_time);
                return;
            }

            if (bean.nailInfoBean == null) {
                startActivity(new Intent(getActivity(), ArtistList.class));
            } else {
                startActivity(new Intent(getActivity(), OrderConfirm.class));
            }
        }

        public String getPlanTimeStr(String book_date, int time_block) {
            return String.format("%s %d:00 ~ %d:00", book_date, time_block * 2 + 7, time_block * 2 + 9);
        }
    }
}
