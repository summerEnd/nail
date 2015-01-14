package com.finger.activity.plan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.activity.main.user.my.MyDiscountActivity;
import com.finger.entity.AddressSearchBean;
import com.finger.entity.NailInfoBean;
import com.finger.entity.OrderBean;
import com.finger.entity.OrderManager;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.TextPainUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.finger.activity.main.user.my.MyDiscountActivity.CouponBean;


public class OrderConfirm extends BaseActivity {
    ImageView nail_image;
    TextView  tv_nail_name;
    TextView  tv_price;
    TextView  tv_shop_price;
    TextView  tv_contact;
    TextView  tv_mobile;
    TextView  tv_planTime;
    TextView  tv_address;
    TextView  edit_summary;
    TextView  tv_taxi_fee;
    TextView  tv_real_pay;
    TextView  tv_choose_coupon;

    float      taxiFee;
    CouponBean mCoupon;

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
        edit_summary = (EditText) findViewById(R.id.edit_summary);
        tv_taxi_fee = (TextView) findViewById(R.id.tv_taxi_fee);
        tv_real_pay = (TextView) findViewById(R.id.tv_real_pay);
        tv_choose_coupon = (TextView) findViewById(R.id.choose_coupon);
        nail_image = (ImageView) findViewById(R.id.nail_image);

        OrderBean order = OrderManager.getCurrentOrder();
        if (order == null) {
            order = (OrderBean) getIntent().getSerializableExtra("bean");
        }
        NailInfoBean infoBean = order.nailInfoBean;
        TextPainUtil.addDeleteLine(tv_shop_price);

        tv_nail_name.setText(infoBean.name);
        tv_price.setText(getString(R.string.s_price, infoBean.price));
        tv_shop_price.setText(getString(R.string.s_price, infoBean.store_price));
        tv_contact.setText(order.contact);
        tv_mobile.setText(order.mobile);
        tv_planTime.setText(order.planTime);

        tv_address.setText(order.address);


        tv_real_pay.setText(getString(R.string.real_price_s, order.realPrice));
        ImageManager.loadImage(infoBean.cover, nail_image);
        AddressSearchBean adrBean = order.addressSearchBean;
        calculateTaxiCost(adrBean.latitude, adrBean.longitude, order.nailInfoBean.mid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit: {
                postOrder();
                break;
            }
            case R.id.btn_choose: {
                startActivityForResult(new Intent(this, MyDiscountActivity.class), 100);
                break;
            }
        }
        super.onClick(v);
    }

    void calculateTaxiCost(double latitude, double longitude, int mid) {
        RequestParams params = new RequestParams();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("mid", mid);
        FingerHttpClient.post("calculateTaxiCost", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    String data = o.getString("data");
                    taxiFee = Float.valueOf(data);
                    tv_taxi_fee.setText(getString(R.string.s_price, data));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode)
            return;
        mCoupon = (CouponBean) data.getSerializableExtra("bean");
        findViewById(R.id.btn_choose).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.choose_coupon)).setText(mCoupon.title);
    }

    public void postOrder() {
        OrderBean order = OrderManager.getCurrentOrder();
        NailInfoBean infoBean = order.nailInfoBean;
        RequestParams params = new RequestParams();
        params.put("product_id", infoBean.id);
        params.put("uid", getApp().getUser().id);
        params.put("mid", infoBean.seller_info.id);
        if (mCoupon != null)
            params.put("coupon_id", mCoupon.id);
        params.put("remark", edit_summary.getText().toString());
        params.put("type", order.type);
        params.put("address", order.address);
        params.put("taxi_cost", taxiFee);
        params.put("order_price", order.nailInfoBean.price);
        params.put("real_pay", order.realPrice);
        params.put("pay_method", "");
        params.put("book_date", order.book_date);
        params.put("time_block", order.time_block);
        params.put("contact", order.contact);
        params.put("phone_num", order.mobile);

        FingerHttpClient.post("postOrder", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                OrderManager.cancel();
                new AlertDialog.Builder(OrderConfirm.this)
                        .setTitle(getString(R.string.commit_ok))
                        .setMessage(getString(R.string.order_msg))
                        .setPositiveButton(getString(R.string.pay_now), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show()
                ;

            }
        });
    }

}
