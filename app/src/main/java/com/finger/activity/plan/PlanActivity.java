package com.finger.activity.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.activity.info.ArtistInfoList;
import com.finger.activity.main.artist.my.PlanTimeActivity;
import com.finger.entity.OrderBean;
import com.finger.entity.OrderManager;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.finger.activity.main.artist.my.PlanTimeActivity.ScheduleOfDay;
import static com.finger.activity.plan.Schedule.Callback;

public class PlanActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    RadioGroup rg_plan;
    PlanForMe    mPlanForMe       = new PlanForMe();
    PlanForOther mPlanForOther    = new PlanForOther();
    int          REQUEST_CODE_GPS = 101;
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
            case R.id.rb_for_me: {
                ft.setCustomAnimations(R.anim.stand_still, R.anim.slide_down_out).hide(mPlanForOther).show(mPlanForMe);
                break;
            }
            case R.id.rb_for_other: {
                if (!mPlanForOther.isAdded()) {
                    ft.add(R.id.frag_container, mPlanForOther);
                }

                ft.setCustomAnimations(R.anim.slide_up_in, R.anim.stand_still).hide(mPlanForMe).show(mPlanForOther);
                break;
            }
        }
        ft.commit();
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
    void showSchedule(List<ScheduleOfDay> blocks) {

        if (mSchedule == null) {
            mSchedule = new Schedule(this);
        }
        mSchedule.setCallback(mScheduleCallback);
        mSchedule.setTimeBlock(blocks);
        mSchedule.show(rg_plan);

    }

    /**
     * 获取美甲师时间块
     *
     * @param mid
     */
    void getTimeBlock(int mid) {
        RequestParams params = new RequestParams();
        params.put("mid", mid);
        FingerHttpClient.post("getTimeBlock", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    ArrayList<ScheduleOfDay> blocks = new ArrayList<ScheduleOfDay>();
                    JsonUtil.getArray(o.getJSONArray("data"), ScheduleOfDay.class, blocks);
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

    /**
     * 获取预约时间
     *
     * @param callback
     */

    public void getPlanTime(Callback callback) {
        mScheduleCallback = callback;
        OrderBean order = OrderManager.getCurrentOrder();
        if (order != null && order.nailInfoBean != null) {
            getTimeBlock(order.nailInfoBean.mid);
        } else {
            List<ScheduleOfDay> scheduleOfDays = new ArrayList<ScheduleOfDay>();
            int curTime = ScheduleOfDay.getCurTimeBlock();
            Calendar today = Calendar.getInstance();
            ScheduleOfDay todaySchedule = new ScheduleOfDay(today.getTime());
            todaySchedule.time1 = curTime >= 1 ? 1 : 0;
            todaySchedule.time2 = curTime >= 2 ? 1 : 0;
            todaySchedule.time3 = curTime >= 3 ? 1 : 0;
            todaySchedule.time4 = curTime >= 4 ? 1 : 0;
            todaySchedule.time5 = curTime >= 5 ? 1 : 0;
            todaySchedule.time6 = curTime >= 6 ? 1 : 0;

            scheduleOfDays.add(todaySchedule);

            for (int i = 1; i <= 3; i++) {
                Calendar mDay = Calendar.getInstance();
                mDay.add(Calendar.DAY_OF_YEAR, i);
                ScheduleOfDay day = new ScheduleOfDay(mDay.getTime());
                day.time1 = 0;
                day.time2 = 0;
                day.time3 = 0;
                day.time4 = 0;
                day.time5 = 0;
                day.time6 = 0;
                scheduleOfDays.add(day);
            }

            showSchedule(scheduleOfDays);
        }
    }

    public static class PlanFragment extends Fragment {

        String planTime  = null;
        String book_date = null;
        int time_block;
        String TYPE_FOR_ME    = "0";
        String TYPE_FOR_OTHER = "1";

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
            if (bean.addressSearchBean == null || TextUtils.isEmpty(bean.address)) {
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
                startActivity(new Intent(getActivity(), ArtistInfoList.class));
            } else {
                startActivity(new Intent(getActivity(), OrderConfirm.class));
            }
        }

        public String getPlanTimeStr(String book_date, int time_block) {
            this.book_date = book_date;
            this.time_block = time_block;
            return String.format("%s %d:00 ~ %d:00", book_date, time_block * 2 + 7, time_block * 2 + 9);
        }
    }
}
