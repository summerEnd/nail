package com.finger.activity.plan;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.finger.R;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.ItemUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.finger.activity.main.artist.my.PlanTimeActivity.*;

public class Schedule extends PopupWindow implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemClickListener {
    View       contentView;
    ViewPager  pager;
    RadioGroup rg;
    /**
     * pager的view
     */
    ArrayList<View> views      = new ArrayList<View>();
    int             column_Num = 2;

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
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });
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
     * 退出
     */
    void exit() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                contentView.clearAnimation();
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentView.startAnimation(animation);

    }

    /**
     * 进入
     */
    void enter() {
        contentView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up_in));
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
    }

    public void show(View anchor) {
        showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0);
        enter();
    }

    /**
     * 设置时间
     *
     * @param blocks
     */
    public void setTimeBlock(List<ScheduleOfDay> blocks) {
        final int halfScreen = ItemUtil.halfScreen;
        int w = halfScreen * 2;
        int h = (int) (halfScreen * 1.5);
        views.clear();
        pager.getLayoutParams().height = h;

        //根据时间blocks创建四个页面，今天、明天、后天、大后天
        if (blocks != null) {
            for (ScheduleOfDay block : blocks) {
                views.add(createPage(block, w, h));
            }
        }

        //不足四个补齐
        while (views.size() < 4) {
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
    View createPage(ScheduleOfDay block, int width, int height) {
        //一个页面就是一个GridView
        GridView view = new GridView(context);
        view.setLayoutParams(new AbsListView.LayoutParams(width, height));
        ScheduleGridAdapter adapter;
        if (block != null) {
            //默认设置为"忙"
            view.setTag(context.getString(R.string.busy));
            ArrayList<ScheduleBean> scheduleBeans = block.getScheduleList();

            //如果有一个时间段是闲，那么今天是可预约的
            for (int i = 0; i < scheduleBeans.size(); i++) {
                if (scheduleBeans.get(i).free) {
                    view.setTag(context.getString(R.string.free));
                }
            }
            adapter = new ScheduleGridAdapter(scheduleBeans);
        } else {
            adapter = new ScheduleGridAdapter();
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

        if (mCallback != null) {

            SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, pager.getCurrentItem());
            String date = sp.format(calendar.getTime());
            int block = position + 1;

            GridView gridView = (GridView) views.get(pager.getCurrentItem());
            ScheduleGridAdapter adapter = (ScheduleGridAdapter) gridView.getAdapter();

            ScheduleBean bean = (ScheduleBean) adapter.getItem(position);

            if (bean != null && !bean.free) {
                ContextUtil.toast(context.getString(R.string.not_valid_blocks));
            } else {
                mCallback.onSelect(date, block);
            }

        }
        dismiss();
    }

    /**
     * ViewPager适配器
     */
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

    /**
     * GridView适配器
     */
    class ScheduleGridAdapter extends BaseAdapter {
        final int item_w = ItemUtil.halfScreen - 1;
        final int item_h = item_w / 2;
        List<ScheduleBean> beans;

        ScheduleGridAdapter() {
        }

        public boolean hasData() {
            return beans != null;
        }

        ScheduleGridAdapter(List<ScheduleBean> beans) {
            this.beans = beans;
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int position) {
            return beans == null ? null : beans.get(position);
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

            holder.tv_time.setText(ScheduleOfDay.convertTimeBlock(position + 1));
            convertView.setTag(holder);
            return convertView;
        }
    }


    /**
     * 根据book_date和time_block得出预约时间
     *
     * @param book_date
     * @param time_block
     * @return
     */
    public static String convertPlanTime(String book_date, int time_block) {
        return book_date + " " + ScheduleOfDay.convertTimeBlock(time_block);
    }

    static class ViewHolder {
        TextView tv_time;
        TextView tv_state;
    }
}
