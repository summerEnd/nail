package com.finger.activity.other.plan;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.finger.R;
import com.finger.support.widget.ItemUtil;

import java.util.ArrayList;

public class Schedule extends PopupWindow implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    View contentView;
    ViewPager pager;
    RadioGroup rg;
    ArrayList<View> views = new ArrayList<View>();
    int column_Num = 2;

    private Context context;
    RadioButton[] radioButtons = new RadioButton[4];

    public Schedule(Context context) {
        super(context);
        this.context = context;
        contentView = View.inflate(context, R.layout.fragment_schedule, null);
        setContentView(contentView);
        pager = (ViewPager) contentView.findViewById(R.id.pager);
        rg = (RadioGroup) contentView.findViewById(R.id.rg);
        radioButtons[0] = (RadioButton) rg.findViewById(R.id.r1);
        radioButtons[1] = (RadioButton) rg.findViewById(R.id.r2);
        radioButtons[2] = (RadioButton) rg.findViewById(R.id.r3);
        radioButtons[3] = (RadioButton) rg.findViewById(R.id.r4);

        rg.setOnCheckedChangeListener(this);
        pager.setOnPageChangeListener(this);
        addSchedule();
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        pager.setAdapter(new SchedulePageAdapter());
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    void addSchedule() {
        final int halfScreen = ItemUtil.halfScreen;
        int w = halfScreen * 2;
        int h = (int) (halfScreen * 1.5);
        pager.getLayoutParams().height = h;
        for (int i = 0; i < 4; i++) {
            GridView object = new GridView(context);
            GridView.LayoutParams lp = new AbsListView.LayoutParams(w, h);
            object.setLayoutParams(lp);
            object.setAdapter(new ScheduleItemAdapter());
            object.setNumColumns(column_Num);
            object.setHorizontalSpacing(1);
            object.setVerticalSpacing(1);
            object.setBackgroundColor(0xff808080);
            object.setVerticalScrollBarEnabled(false);
            views.add(object);
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < radioButtons.length; i++) {
            if (radioButtons[i].getId() == checkedId) {
                pager.setCurrentItem(i);
            }
        }
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

    class ScheduleItemAdapter extends BaseAdapter {
        final int item_w = ItemUtil.halfScreen-1 ;
        final int item_h = item_w/2;

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

            if (position % 3 == 0) {
                convertView.setBackgroundColor(0xffffffff);
                holder.tv_time.setTextColor(0xff808080);
            } else {
                convertView.setBackgroundColor(Color.rgb(0x96,0xd4,0xa0));
                holder.tv_time.setTextColor(0xffffffff);
            }

            holder.tv_time.setText(String.format("%d:00~%d:00", position*2 + 9,position*2+11));
            convertView.setTag(holder);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_time;
        TextView tv_state;
    }
}
