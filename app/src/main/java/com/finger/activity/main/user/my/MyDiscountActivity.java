package com.finger.activity.main.user.my;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
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
    List<CouponBean> usedCoupons  = new ArrayList<CouponBean>();
    List<CouponBean> freshCoupons = new ArrayList<CouponBean>();
    DiscountAdapter usedAdapter;
    DiscountAdapter freshAdapter;
    final int PAGE=15;
    /**
     * Intent参数，是否为选择优惠券，true 选择优惠券 ，默认是false
     */
    public static final String EXTRA_FOR_PICK = "for_pick_coupon";

    /**
     * 已使用
     */
    final int STATUS_USED  = 1;
    /**
     * 未使用
     */
    final int STATUS_FRESH = 0;

    public static class CouponBean implements Serializable {
        public int    id;
        public String title;
        public String start_time;
        public String stop_time;
        public String price;
        public int    status;
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

        getFreshCoupons(1);
    }

    void getFreshCoupons(final int page) {
        RoleBean user = getApp().getUser();
        RequestParams params = new RequestParams();
        params.put("uid", user.id);
        params.put("page", page);
        params.put("pagesize", PAGE);
        params.put("status", STATUS_FRESH);
        FingerHttpClient.post("getCouponList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    if (page<=1){
                        freshCoupons.clear();
                    }
                    JsonUtil.getArray(o.getJSONArray("data"), CouponBean.class, freshCoupons);
                    freshAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取过期优惠券
     */
    void getUsedCoupons(final int page) {
        RoleBean user = getApp().getUser();
        RequestParams params = new RequestParams();
        params.put("uid", user.id);
        params.put("status", STATUS_USED);
        params.put("page", page);
        params.put("pagesize", PAGE);
        FingerHttpClient.post("getCouponList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    if (page<=1){
                        freshCoupons.clear();
                    }
                    JsonUtil.getArray(o.getJSONArray("data"), CouponBean.class, usedCoupons);
                    usedAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addView() {
        usedAdapter = new DiscountAdapter(this, usedCoupons);
        usedAdapter.STATUS=STATUS_USED;
        freshAdapter = new DiscountAdapter(this, freshCoupons);
        freshAdapter.STATUS=STATUS_FRESH;
        {
            FrameLayout layout=new FrameLayout(this);
            View empty=getLayoutInflater().inflate(R.layout.empty_vuew,null);
            empty.setBackgroundResource(R.drawable.ic_discount_bg);
            ((TextView) empty.findViewById(R.id.empty_text)).setText(getString(R.string.empty_coupon));


            ListView listView = new ListView(this);
            listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            listView.setDivider(new ColorDrawable(0));
            listView.setSelector(new ColorDrawable(0));
            listView.setAdapter(freshAdapter);
            listView.setBackgroundResource(R.drawable.windowBackground);
            new ListController(listView,new ListController.Callback() {
                @Override
                public void onLoadMore(AbsListView listView, int nextPage) {
                    getFreshCoupons(nextPage);
                }
            }).setPageSize(PAGE);
            layout.addView(empty);
            layout.addView(listView);
            views.add(layout);
        }

        {
            ListView listView = new ListView(this);
            listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            listView.setDivider(new ColorDrawable(0));
            listView.setSelector(new ColorDrawable(0));
            listView.setAdapter(usedAdapter);
            new ListController(listView,new ListController.Callback() {
                @Override
                public void onLoadMore(AbsListView listView, int nextPage) {
                    getUsedCoupons(nextPage);
                }
            }).setPageSize(PAGE);
            views.add(listView);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_not_used: {
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
            rg.check(R.id.rb_not_used);
            if (freshCoupons.size() == 0) {
                getFreshCoupons(1);
            }
        } else {
            rg.check(R.id.rb_expired);
            if (usedCoupons.size() == 0) {
                getUsedCoupons(1);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * ViewPager的Adapter
     */
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

    /**
     * 优惠券列表
     */
    class DiscountAdapter extends BaseAdapter {
        LayoutInflater   inflater;
        List<CouponBean> beans;
        public int STATUS;
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
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            CouponBean bean = beans.get(position);
            holder.tv_expire.setVisibility(STATUS == STATUS_USED ? View.VISIBLE : View.INVISIBLE);
            holder.tv_title.setText(bean.title);
            holder.tv_price.setText(getString(R.string.price_s, bean.price));
            holder.tv_date.setText(getString(R.string.date_limit, bean.start_time + "~" + bean.stop_time));
            convertView.setTag(holder);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_expire;
        TextView tv_title;
        TextView tv_date;
        TextView tv_price;
    }
}
