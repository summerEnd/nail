package com.finger.activity.user.my;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.finger.activity.BaseActivity;
import com.finger.R;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams;

/**
 * Created by acer on 2014/12/16.
 */
public class MyDiscountActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener {
    ViewPager pager;
    ArrayList<View> views = new ArrayList<View>();
    RadioGroup rg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_discount);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(this);
        addView();

        DiscountPageAdapter adapter = new DiscountPageAdapter();
        pager.setAdapter(adapter);

        rg= (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);

    }

    private void addView() {

        {
            ListView listView = new ListView(this);
            listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            listView.setDivider(new ColorDrawable(0));
            listView.setSelector(new ColorDrawable(0));
            DiscountAdapter adapter = new DiscountAdapter(this);
            adapter.isExpire = false;
            listView.setAdapter(adapter);
            views.add(listView);
        }
        {
            ListView listView = new ListView(this);
            listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            listView.setDivider(new ColorDrawable(0));
            DiscountAdapter adapter = new DiscountAdapter(this);
            adapter.isExpire = true;
            listView.setAdapter(adapter);
            views.add(listView);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_remain: {
                pager.setCurrentItem(0);
                break;
            }
            case R.id.rb_expired: {
                pager.setCurrentItem(1);
                break;
            }
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i==0){
            rg.check( R.id.rb_remain);

        }else{
            rg.check( R.id.rb_expired);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    class DiscountPageAdapter extends PagerAdapter {

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

    class DiscountAdapter extends BaseAdapter {
        LayoutInflater inflater;
        boolean isExpire;

        DiscountAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

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
                convertView = inflater.inflate(R.layout.list_item_discount, null);
                holder.tv_expire = (TextView) convertView.findViewById(R.id.tv_is_expired);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_expire.setVisibility(isExpire ? View.VISIBLE : View.INVISIBLE);

            convertView.setTag(holder);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_expire;
    }
}
