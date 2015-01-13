package com.finger.activity.info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.OrderListBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.DialogUtil;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ImageManager;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderInfoActivity extends BaseActivity {

    OrderListBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        getOrderDetail(getIntent().getIntExtra("id", -1));
    }

    void getOrderDetail(int orderId) {
        if (orderId == -1) {

            return;
        }
        RequestParams params = new RequestParams();
        params.put("id", orderId);
        FingerHttpClient.post("getOrderDetail", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    bean = JsonUtil.get(o.getString("data"), OrderListBean.class);
                    setData(bean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(JSONObject o) {
                DialogUtil.alert(OrderInfoActivity.this, getString(R.string.order_not_exist))
                        .setOnDismissListener(
                                new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        finish();
                                    }
                                });
            }
        });
    }

    void setData(OrderListBean bean) {
        if (bean == null) {
            DialogUtil.alert(this, getString(R.string.order_not_exist));
            return;
        }
        ImageView nail_image = (ImageView) findViewById(R.id.nail_image);
        TextView create_time = (TextView) findViewById(R.id.create_time),
                tv_nail_name = (TextView) findViewById(R.id.tv_nail_name),
                tv_price = (TextView) findViewById(R.id.tv_price),
                taxi_fee = (TextView) findViewById(R.id.taxi_fee),
                tv_coupon = (TextView) findViewById(R.id.tv_coupon),
                tv_real_pay = (TextView) findViewById(R.id.tv_real_pay),
                tv_contact = (TextView) findViewById(R.id.tv_contact),
                tv_planTime = (TextView) findViewById(R.id.tv_planTime),
                tv_mobile = (TextView) findViewById(R.id.tv_mobile),
                tv_address = (TextView) findViewById(R.id.tv_address),
                tv_summary = (TextView) findViewById(R.id.tv_summary);
        ImageManager.loadImage(bean.product_cover, nail_image);
        create_time.setText(getString(R.string.order_date_s, bean.create_time));
        tv_nail_name.setText(bean.product_name);
        tv_price.setText(getString(R.string.price_s, bean.order_price));
        taxi_fee.setText(getString(R.string.s_price, bean.order_price));
        tv_coupon.setText(getString(R.string.s_price, bean.order_price));//优惠券价值
        tv_real_pay.setText(getString(R.string.s_price, bean.order_price));//实付
        //tv_contact.setText(bean.);//联系人
        tv_planTime.setText(bean.time_block);//预约时间
        //tv_mobile.setText(bean.);//联系人电话
        tv_address.setText(bean.address);
        tv_summary.setText(bean.remark);//备注
    }
}