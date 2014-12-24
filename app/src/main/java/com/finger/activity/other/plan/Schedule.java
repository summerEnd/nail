package com.finger.activity.other.plan;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.finger.R;
import com.finger.support.widget.ItemUtil;

import java.util.ArrayList;

public class Schedule extends PopupWindow {
    View contentView;
    ViewPager pager;
    ArrayList<View> views = new ArrayList<View>();
    int column_Num = 4;
    final int item_w = ItemUtil.halfScreen /2-1;
    final int item_h = item_w;
    private Context context;

    public Schedule(Context context) {
        super(context);
        this.context = context;
        contentView = View.inflate(context, R.layout.fragment_schedule, null);
        setContentView(contentView);
        pager = (ViewPager) contentView.findViewById(R.id.pager);
        addSchedule();
        setBackgroundDrawable(new ColorDrawable(0));
        pager.setAdapter(new SchedulePageAdapter());
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    void addSchedule() {
        int w = item_w * 4;
        int h = item_h * 3;
        pager.getLayoutParams().height = h;
        for (int i = 0; i < 4; i++) {
            GridView object = new GridView(context);
            GridView.LayoutParams lp = new AbsListView.LayoutParams(w, h);
            object.setLayoutParams(lp);
            object.setAdapter(new ScheduleItemAdapter());
            object.setNumColumns(column_Num);
            object.setHorizontalSpacing(1);
            object.setVerticalSpacing(1);
            object.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
            object.setVerticalScrollBarEnabled(false);
            views.add(object);
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

        @Override
        public int getCount() {
            return 12;
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
                holder.tv_time.setTextColor(0xffb3b3b3);
            } else {
                convertView.setBackgroundColor(0xfffe5000);
                holder.tv_time.setTextColor(0xffffffff);
            }

            holder.tv_time.setText(String.format("%d:00", position + 10));
            convertView.setTag(holder);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_time;
        TextView tv_state;
    }
}
