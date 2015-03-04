package com.finger.activity.main.artist.my;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
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
import com.finger.activity.base.BaseActivity;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.DialogUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.util.ItemUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class PlanTimeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    ViewPager  pager;
    RadioGroup rg;
    RadioButton[]            radioButtons = new RadioButton[4];
    ArrayList<View>          views        = new ArrayList<View>();
    ArrayList<ScheduleOfDay> blocks       = new ArrayList<ScheduleOfDay>();

    /**
     * 代表一天的日程
     */
    public static class ScheduleOfDay {
        //9点到11点
        public int time1;
        //11点到13点
        public int time2;
        //13点到15点
        public int time3;
        //15点到17点
        public int time4;
        //17点到19点
        public int time5;
        //19点到21点
        public int time6;

        public Date mDate;
        ArrayList<ScheduleBean> beans ;

        public ScheduleOfDay(){
            if(beans==null){
                beans=new ArrayList<ScheduleBean>();
            }
        }

        public ScheduleOfDay(Date date){
            if(beans==null){
                beans=new ArrayList<ScheduleBean>();
            }
            mDate=date;
        }

        /**
         * 转化成集合,time1,time2..将不再使用
         *
         * @return
         */
        public ArrayList<ScheduleBean> getScheduleList() {

            if(beans==null){
                beans=new ArrayList<ScheduleBean>();
            }

            if (beans.size() == 0) {
                //==0代表闲
                addSchedule(time1 == 0);
                addSchedule(time2 == 0);
                addSchedule(time3 == 0);
                addSchedule(time4 == 0);
                addSchedule(time5 == 0);
                addSchedule(time6 == 0);
            }
            return beans;
        }

        /**
         * 格式：2014-09-09_1_0,2014-09-09_2_0
         * 格式说明:每组数据用逗号分隔，每组数据之中再通过_分隔。第一个为日期，第二个是时间段编号，第三个是设置的状态（0未占用，1已经占用）
         *
         * @return
         */
        public String getTimeBlockStr() {
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < beans.size(); i++) {
                sb.append(format.format(mDate))
                        .append("_")
                        .append(i + 1)
                        .append("_")
                        .append(beans.get(i).free ? 0 : 1)
                        .append(i == 5 ? "" : ",")
                ;
            }

            return sb.toString();
        }

        void addSchedule(boolean free) {
            ScheduleBean bean = new ScheduleBean();
            bean.free = free;
            beans.add(bean);
        }

        /**
         * 将time_block转化为时间段
         *
         * @param time_block
         * @return
         */
        public static String convertTimeBlock(int time_block) {
            int start = time_block * 2 + 7;
            return new StringBuilder()
                    .append(start)
                    .append(":00~")
                    .append(start + 2)
                    .append(":00")
                    .toString();
        }

        public static final int getCurTimeBlock() {
            float time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE)/60f;
            int block;
            if (time < 9) {
                block = -1;
            } else if (time >= 9 && time <= 11) {
                block = 1;
            } else if (time <= 13) {
                block = 2;
            } else if (time <= 15) {
                block = 3;
            } else if (time <= 17) {
                block = 4;
            } else if (time <= 19) {
                block = 5;
            } else if (time <= 21) {
                block = 6;
            } else {
                block = 10;
            }

            return block;
        }

    }

    public static class ScheduleBean {
        public int     time_block;
        public boolean free;
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

    /**
     * 获取时间块
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
                    JsonUtil.getArray(o.getJSONArray("data"), ScheduleOfDay.class, blocks);
                    for (int i=0;i<blocks.size();i++){
                        ScheduleOfDay day=blocks.get(i);
                        Calendar mCal=Calendar.getInstance();
                        mCal.add(Calendar.DAY_OF_YEAR,i);
                        day.mDate=mCal.getTime();
                    }
                    buildViews();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置时间快
     */
    void setTimeBlock() {
        //        2014-09-09_1_0
        StringBuilder time_block_str = new StringBuilder();

        Iterator<ScheduleOfDay> it = blocks.iterator();
        while (it.hasNext()) {
            ScheduleOfDay block = it.next();
            time_block_str.append(block.getTimeBlockStr());
            if (it.hasNext()) {
                time_block_str.append(",");
            }
        }

        RequestParams params = new RequestParams();
        params.put("time_block_str", time_block_str.toString());
        FingerHttpClient.post("setTimeBlock", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), ScheduleOfDay.class, blocks);
                    buildViews();
                    ContextUtil.toast(R.string.update_ok);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.commit: {
                setTimeBlock();
                break;
            }
        }
    }

    /**
     * 用数据构建视图
     */
    void buildViews() {

        //GridView里item的宽度
        final int item_w = ItemUtil.halfScreen - 1;

        //GridView里item的高度
        final int item_h = item_w / 2;

        //GridView的宽度
        int w = item_w * 2;

        //GridView的高度
        int h = item_h * 3;

        //设置ViewPager的高度
        pager.getLayoutParams().height = h;

        //创建4个GridView
        for (int i = 0; i < 4; i++) {
            GridView grid = new GridView(this);
            //设置宽高
            GridView.LayoutParams lp = new AbsListView.LayoutParams(w, h);
            grid.setLayoutParams(lp);
            ScheduleOfDay mSchedule = blocks.get(i);

            //设置点击时间
            grid.setOnItemClickListener(new OnScheduleItemClick(mSchedule));

            //设置Adapter
            grid.setAdapter(new ScheduleItemAdapter(this, item_w, item_h, mSchedule));

            //设置基本参数
            grid.setNumColumns(2);
            grid.setHorizontalSpacing(1);
            grid.setVerticalSpacing(1);
            grid.setBackgroundColor(getResources().getColor(R.color.light_gray));

            //设置标记，在OnScheduleItemClick中根据这个tag找到对应的RadioButton
            grid.setTag(i);

            //隐藏滚动条
            grid.setVerticalScrollBarEnabled(false);
            views.add(grid);
        }
        pager.setAdapter(new SchedulePageAdapter());

        for (int i = 0; i < radioButtons.length; i++) {
            refreshTab(i);
        }
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

    /**
     * ViewPager适配器
     */
    class SchedulePageAdapter extends PagerAdapter {

        @Override
        public CharSequence getPageTitle(int position) {

            return super.getPageTitle(position);
        }

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

    /**
     * 设置tab是忙还是闲
     *
     * @param position tab的位置
     */
    void refreshTab(int position) {
        boolean busy = true;
        for (ScheduleBean time : blocks.get(position).getScheduleList()) {
            if (time.free) {
                busy = false;
            }
        }
        StringBuilder text = new StringBuilder();
        switch (position) {
            case 0: {
                text.append(getString(R.string.day1));
                break;
            }
            case 1: {
                text.append(getString(R.string.day2));
                break;
            }
            case 2: {
                text.append(getString(R.string.day3));
                break;
            }
            case 3: {
                text.append(getString(R.string.day4));
                break;
            }
        }
        text.append("(")
                .append(busy ? getString(R.string.busy) : getString(R.string.free))
                .append(")")
        ;
        radioButtons[position].setText(text);
    }

    /**
     * 封装点击时间
     */
    private class OnScheduleItemClick implements AdapterView.OnItemClickListener {
        private ScheduleOfDay mSchedule;

        OnScheduleItemClick(ScheduleOfDay schedule) {
            this.mSchedule = schedule;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Calendar now = Calendar.getInstance();
            Calendar selectDay=Calendar.getInstance();
            selectDay.setTime(mSchedule.mDate);

            float curTime=now.get(Calendar.HOUR_OF_DAY)+now.get(Calendar.MINUTE)/60.f;

            ArrayList<ScheduleBean> scheduleList = mSchedule.getScheduleList();
            ScheduleBean bean = scheduleList.get(position);

            if (DateUtils.isToday(selectDay.getTimeInMillis())&&curTime>bean.time_block){
                ContextUtil.toast(getString(R.string.not_set_time));
                return;
            }

            bean.free = !bean.free;
            ((ScheduleItemAdapter) parent.getAdapter()).notifyDataSetChanged();

            refreshTab((Integer) parent.getTag());
        }


    }

    class ScheduleItemAdapter extends BaseAdapter {
        private int item_w, item_h;
        private Context                 context;
        private ArrayList<ScheduleBean> beans;

        ScheduleItemAdapter(Context context, int item_w, int item_h, ScheduleOfDay block) {
            this.context = context;
            this.item_h = item_h;
            this.item_w = item_w;
            beans = block.getScheduleList();
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

            holder.tv_time.setText(ScheduleOfDay.convertTimeBlock(position + 1));
            convertView.setTag(holder);
            return convertView;
        }


    }

    static class ViewHolder {
        TextView tv_time;
        TextView tv_state;
    }
}
