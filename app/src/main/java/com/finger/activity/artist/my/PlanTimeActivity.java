package com.finger.activity.artist.my;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.ItemUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by acer on 2014/12/30.
 */
public class PlanTimeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    ViewPager pager;
    RadioGroup rg;
    RadioButton[] radioButtons = new RadioButton[4];
    ArrayList<View> views = new ArrayList<View>();
    ArrayList<TimeBlock> blocks = new ArrayList<TimeBlock>();

    class TimeBlock {
        int time1;
        int time2;
        int time3;
        int time4;
        int time5;
        int time6;
    }

    class ScheduleBean {
        String time;
        boolean free;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_time);
        pager = (ViewPager) findViewById(R.id.pager);
        rg = (RadioGroup) findViewById(R.id.rg);
        radioButtons[0] = (RadioButton) rg.findViewById(R.id.r1);
        radioButtons[1] = (RadioButton) rg.findViewById(R.id.r2);
        radioButtons[2] = (RadioButton) rg.findViewById(R.id.r3);
        radioButtons[3] = (RadioButton) rg.findViewById(R.id.r4);

        rg.setOnCheckedChangeListener(this);
        pager.setOnPageChangeListener(this);

        getTimeBlock(getApp().getUser().id);
    }

    void getTimeBlock(int mid) {
        RequestParams params = new RequestParams();
        params.put("mid", mid);
        FingerHttpClient.post("getTimeBlock", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), TimeBlock.class, blocks);
                    buildViews();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    /**
     * 用数据构建视图
     */
    void buildViews() {
        final int item_w = ItemUtil.halfScreen - 1;
        final int item_h = item_w / 2;
        int w = item_w * 2;
        int h = item_h * 3;
        pager.getLayoutParams().height = h;
        for (int i = 0; i < 4; i++) {
            GridView object = new GridView(this);
            GridView.LayoutParams lp = new AbsListView.LayoutParams(w, h);
            object.setLayoutParams(lp);
            ScheduleItemAdapter adapter = new ScheduleItemAdapter(this, item_w, item_h, blocks.get(i));
            object.setAdapter(adapter);
            object.setOnItemClickListener(adapter);
            object.setNumColumns(2);
            object.setHorizontalSpacing(1);
            object.setVerticalSpacing(1);
            object.setBackgroundColor(getResources().getColor(R.color.light_gray));
            object.setVerticalScrollBarEnabled(false);
            views.add(object);
        }
        pager.setAdapter(new SchedulePageAdapter());

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < radioButtons.length; i++) {
            if (radioButtons[i].getId() == checkedId) {
                pager.setCurrentItem(i);
            }
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        radioButtons[i].setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    class SchedulePageAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }

    class ScheduleItemAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private int item_w, item_h;
        private Context context;
        private ArrayList<ScheduleBean> beans;

        ScheduleItemAdapter(Context context, int item_w, int item_h, TimeBlock block) {
            this.context = context;
            this.item_h = item_h;
            this.item_w = item_w;
            beans = new ArrayList<ScheduleBean>();
            addSchedule(block.time1 == 0);
            addSchedule(block.time2 == 0);
            addSchedule(block.time3 == 0);
            addSchedule(block.time4 == 0);
            addSchedule(block.time5 == 0);
            addSchedule(block.time6 == 0);
            for (int j = 0; j < beans.size(); j++) {
                ScheduleBean scheduleBean = beans.get(j);
                scheduleBean.time = String.format("%d:00~%d:00", j * 2 + 9, j * 2 + 110);
            }
            notifyDataSetChanged();
        }

        void addSchedule(boolean free) {
            ScheduleBean bean = new ScheduleBean();
            bean.free = free;
            beans.add(bean);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.schedule_grid_item, null);
                GridView.LayoutParams lp = new GridView.LayoutParams(item_w, item_h);
                convertView.setLayoutParams(lp);
                holder.tv_time = (TextView) convertView.findViewById(R.id.time);
                holder.tv_state = (TextView) convertView.findViewById(R.id.time);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ScheduleBean bean = beans.get(position);
            if (bean.free) {
                convertView.setBackgroundColor(0xff96d4a0);
                holder.tv_time.setTextColor(0xffffffff);
            } else {
                convertView.setBackgroundColor(0xffffffff);
                holder.tv_time.setTextColor(0xffb3b3b3);
            }

            holder.tv_time.setText(bean.time);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScheduleBean bean = beans.get(position);
            bean.free = !bean.free;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        TextView tv_time;
        TextView tv_state;
    }
}
