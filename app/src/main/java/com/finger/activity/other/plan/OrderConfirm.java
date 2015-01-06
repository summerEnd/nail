package com.finger.activity.other.plan;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.activity.other.info.NailInfo;
import com.finger.support.entity.ArtistRole;
import com.finger.support.entity.NailInfoBean;
import com.finger.support.entity.OrderBean;
import com.finger.support.entity.OrderManager;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ImageManager;

import org.json.JSONObject;


public class OrderConfirm extends BaseActivity {
    ImageView nail_image;
    TextView tv_nail_name;
    TextView tv_price;
    TextView tv_shop_price;
    TextView tv_contact;
    TextView tv_mobile;
    TextView tv_planTime;
    TextView tv_address;
    TextView tv_summary;
    TextView tv_taxi_fee;
    TextView tv_real_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirm);
        findIds();

    }

    private void findIds() {
        tv_nail_name = (TextView) findViewById(R.id.tv_nail_name);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_shop_price = (TextView) findViewById(R.id.tv_shop_price);
        tv_contact = (TextView) findViewById(R.id.tv_contact);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_planTime = (TextView) findViewById(R.id.tv_planTime);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_summary = (TextView) findViewById(R.id.tv_summary);
        tv_taxi_fee = (TextView) findViewById(R.id.tv_taxi_fee);
        tv_real_pay = (TextView) findViewById(R.id.tv_real_pay);
        nail_image = (ImageView) findViewById(R.id.nail_image);

        OrderBean order = OrderManager.getCurrentOrder();
        NailInfoBean infoBean = order.nailInfoBean;

        tv_nail_name.setText(infoBean.name);
        tv_price.setText(infoBean.price);
        tv_shop_price.setText(infoBean.store_price);
        tv_contact.setText(order.contact);
        tv_mobile.setText(order.mobile);
        tv_planTime.setText(order.planTime);
        tv_address.setText(order.address);
        tv_summary.setText(order.summary);
        tv_taxi_fee.setText(order.taxi_cost);
        tv_real_pay.setText(getString(R.string.real_price_s, order.realPrice));
        ImageManager.loadImage(infoBean.cover, nail_image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit: {
                postOrder();
                break;
            }
        }
        super.onClick(v);
    }

    public void postOrder() {
        OrderBean order = OrderManager.getCurrentOrder();
        NailInfoBean infoBean = order.nailInfoBean;

        RequestParams params = new RequestParams();
        params.put("product_id", infoBean.id);
        params.put("uid", getApp().getUser().id);
        params.put("mid", infoBean.seller_info.id);
        params.put("coupon_id", "");
        params.put("remark", "");
        params.put("type", "");
        params.put("address", order.address);
        params.put("taxi_cost", "");
        params.put("order_price", order.realPrice);
        params.put("real_pay", "");
        params.put("pay_method", "");
        params.put("book_date", order.book_date);
        params.put("time_block", order.time_block);
        params.put("contact", order.contact);
        params.put("phone_num", order.mobile);

        FingerHttpClient.post("postOrder", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {

            }
        });
    }

}
