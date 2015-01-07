package com.finger.activity.main.user.my;

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

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams;

/**
 * Created by acer on 2014/12/16.
 */
public class MyDiscountActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    ViewPager pager;
    ArrayList<View> views = new ArrayList<View>();
    RadioGroup rg;
    List<CouponBean> usedCoupons = new ArrayList<CouponBean>();
    List<CouponBean> freshCoupons = new ArrayList<CouponBean>();
    DiscountAdapter usedAdapter;
    DiscountAdapter freshAdapter;
    /**
     * 已使用
     */
    final int STATUS_USED = 1;
    /**
     * 未使用
     */
    final int STATUS_FRESH = 0;

    class CouponBean {
        int id;
        String title;
        String start_time;
        String stop_time;
        String price;
        int status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_discount);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(this);
        addView();

        DiscountPageAdapter adapter = new DiscountPageAdapter();
        pager.setAdapter(adapter);

        rg = (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);
        getFreshCoupons();
    }

    void getFreshCoupons() {
        RoleBean user = getApp().getUser();
        RequestParams params = new RequestParams();
        params.put("uid", user.id);
        params.put("status", STATUS_FRESH);
        FingerHttpClient.post("getCouponList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), CouponBean.class, freshCoupons);
                    freshAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void getUsedCoupons() {
        RoleBean user = getApp().getUser();
        RequestParams params = new RequestParams();
        params.put("uid", user.id);
        params.put("status", STATUS_USED);
        FingerHttpClient.post("getCouponList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), CouponBean.class, usedCoupons);
                    usedAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addView() {
        usedAdapter=new DiscountAdapter(this,usedCoupons);
        freshAdapter=new DiscountAdapter(this,freshCoupons);

        {
            ListView listView = new ListView(this);
            listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            listView.setDivider(new ColorDrawable(0));
            listView.setSelector(new ColorDrawable(0));
            listView.setAdapter(freshAdapter);
            views.add(listView);
        }
        {
            ListView listView = new ListView(this);
            listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            listView.setDivider(new ColorDrawable(0));
            listView.setAdapter(usedAdapter);
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
        if (i == 0) {
            rg.check(R.id.rb_remain);
            if (freshCoupons.size() == 0) {
                getFreshCoupons();
            }
        } else {
            rg.check(R.id.rb_expired);
            if (usedCoupons.size() == 0) {
                getUsedCoupons();
            }
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
        List<CouponBean> beans;

        DiscountAdapter(Context context, List<CouponBean> beans) {
            this.beans = beans;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return beans.size();
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
            CouponBean bean = beans.get(position);
            holder.tv_expire.setVisibility(bean.status == STATUS_USED ? View.VISIBLE : View.INVISIBLE);

            convertView.setTag(holder);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_expire;
    }
}