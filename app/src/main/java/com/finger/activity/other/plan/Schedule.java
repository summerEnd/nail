package com.finger.activity.other.plan;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.finger.R;
import com.finger.support.util.Logger;
import com.finger.support.widget.ItemUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.finger.activity.artist.my.PlanTimeActivity.*;

public class Schedule extends PopupWindow implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    View contentView;
    ViewPager pager;
    RadioGroup rg;
    /**
     * pager的view
     */
    ArrayList<View> views = new ArrayList<View>();
    int column_Num = 2;
    /**
     * 每一页有一个TimeBlock
     */
    List<TimeBlock> blocks = new ArrayList<TimeBlock>();
    private Context context;
    RadioButton[] radioButtons = new RadioButton[4];

    public interface Callback {
        public void onSelect(String book_date, int time_block);
    }

    public Schedule(Context context) {
        super(context);
        this.context = context;
        setContentView();
        init();
    }

    /**
     * 设置视图
     */
    void setContentView() {
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

    }

    /**
     * 初始化popupWindow属性
     */
    void init() {
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0x80000000));
        pager.setAdapter(new SchedulePageAdapter());
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setAnimationStyle(android.R.style.Animation_InputMethod);
    }


    /**
     * 设置时间
     *
     * @param blocks
     */
    public void setTimeBlock(List<TimeBlock> blocks) {
        final int halfScreen = ItemUtil.halfScreen;
        int w = halfScreen * 2;
        int h = (int) (halfScreen * 1.5);
        views.clear();
        pager.getLayoutParams().height = h;
        if (blocks != null) {
            for (TimeBlock block : blocks) {
                views.add(createPage(block, w, h));
            }
        }

        while (views.size() < 4) {//不足四个补齐
            views.add(createPage(null, w, h));
        }

        for (int i = 0; i < views.size(); i++) {
            radioButtons[i].setText(getPageTitle(i));
        }

        pager.getAdapter().notifyDataSetChanged();
    }

    /**
     * 创建一个页面
     *
     * @param block
     * @param width
     * @param height
     * @return
     */
    View createPage(TimeBlock block, int width, int height) {

        GridView view = new GridView(context);
        view.setLayoutParams(new AbsListView.LayoutParams(width, height));
        ScheduleItemAdapter adapter;
        if (block != null) {
            view.setTag(context.getString(R.string.busy));
            ArrayList<ScheduleBean> scheduleBeans = block.convert2Schedule();
            //如果有一个时间段是闲，那么今天是可预约的
            for (int i = 0; i < scheduleBeans.size(); i++) {
                if (scheduleBeans.get(i).free) {
                    view.setTag(context.getString(R.string.xian));
                }
            }
            adapter = new ScheduleItemAdapter(scheduleBeans);
        } else {
            adapter = new ScheduleItemAdapter();

        }
        view.setAdapter(adapter);
        view.setNumColumns(column_Num);
        view.setHorizontalSpacing(1);
        view.setVerticalSpacing(1);
        view.setBackgroundColor(0xff808080);
        view.setVerticalScrollBarEnabled(false);
        view.setOnItemClickListener(this);
        return view;
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

    public CharSequence getPageTitle(int position) {
        StringBuilder title = new StringBuilder();
        switch (position) {
            case 0: {
                title.append(context.getString(R.string.day1));
                break;
            }
            case 1: {
                title.append(context.getString(R.string.day2));
                break;
            }
            case 2: {
                title.append(context.getString(R.string.day3));
                break;
            }
            case 3: {
                title.append(context.getString(R.string.day4));
                break;
            }
        }
        Object tag = views.get(position).getTag();
        if (tag != null)
            title.append("(")
                    .append(tag)
                    .append(")")
                    ;
        return title;
    }

    Callback mCallback;

    public Callback getCallback() {
        return mCallback;
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (mCallback != null) {
            SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, pager.getCurrentItem());
            String date = sp.format(calendar.getTime());
            int block = position + 1;
            Logger.d(date + " block:" + block);

            mCallback.onSelect(date,block);
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
        final int item_w = ItemUtil.halfScreen - 1;
        final int item_h = item_w / 2;
        List<ScheduleBean> beans;

        ScheduleItemAdapter() {
        }

        ScheduleItemAdapter(List<ScheduleBean> beans) {
            this.beans = beans;
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

            if (beans == null) {
                convertView.setBackgroundResource(R.drawable.plan_free);
                holder.tv_time.setTextColor(0xffffffff);
            } else {
                ScheduleBean bean = beans.get(position);
                if (bean.free) {
                    convertView.setBackgroundResource(R.drawable.plan_free);
                    holder.tv_time.setTextColor(0xffffffff);

                } else {
                    convertView.setBackgroundColor(0xffffffff);
                    holder.tv_time.setTextColor(0xff808080);
                }
            }

            holder.tv_time.setText(String.format("%d:00~%d:00", position * 2 + 9, position * 2 + 11));
            convertView.setTag(holder);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_time;
        TextView tv_state;
    }
}
