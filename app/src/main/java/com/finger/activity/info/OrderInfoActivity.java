package com.finger.activity.info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.main.user.my.MyDiscountActivity;
import com.finger.activity.plan.Schedule;
import com.finger.entity.NailInfoBean;
import com.finger.entity.OrderListBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.DialogUtil;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ImageManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.finger.activity.main.user.my.MyDiscountActivity.CouponBean;

public class OrderInfoActivity extends BaseActivity {

    OrderInfoBean bean;

    class OrderInfoBean extends OrderListBean {
        NailInfoBean product;
    }

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
                    bean = JsonUtil.get(o.getString("data"), OrderInfoBean.class);
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

    void setData(OrderInfoBean bean) {
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
        //作品封面
        ImageManager.loadImage(bean.product.cover, nail_image);

        //订单时间
        create_time.setText(getString(R.string.order_date_s, bean.create_time));

        //作品名称
        tv_nail_name.setText(bean.product.name);

        //订单价格
        tv_price.setText(getString(R.string.price_s, bean.order_price));

        //车费
        taxi_fee.setText(getString(R.string.s_price, bean.taxi_cost));

        //优惠券
        CouponBean coupon = bean.coupon;
        tv_coupon.setText(coupon ==null
                ? "无"
                : (coupon.title+" ￥"+coupon.price));
        //实付
        tv_real_pay.setText(getString(R.string.s_price, bean.real_pay));

        //联系人
        tv_contact.setText(bean.contact);

        //预约时间
        tv_planTime.setText(Schedule.convertPlanTime(bean.book_date,bean.time_block));

        //联系人电话
        tv_mobile.setText(bean.phone_num);

        //地址
        tv_address.setText(bean.address);

        //备注
        tv_summary.setText(bean.remark);
    }
}