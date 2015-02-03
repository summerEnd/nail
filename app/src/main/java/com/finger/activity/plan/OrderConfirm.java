package com.finger.activity.plan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.api.AlipayAPI;
import com.finger.entity.AddressSearchBean;
import com.finger.entity.NailInfoBean;
import com.finger.entity.OrderBean;
import com.finger.entity.OrderManager;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.PopupList;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.TextPainUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    TextView  tv_coupon;
    View      choose_coupon;

    float      taxiFee;
    CouponBean mCoupon;
    String     total_fee;
    ArrayList<CouponBean> couponBeans = new ArrayList<CouponBean>();
    PopupList         popupList;
    CouponAdapter     adapter;
    PayMethodFragment payMethod;
    OrderBean         mOrderBean;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirm);
        findIds();
        adapter = new CouponAdapter();
        payMethod = new PayMethodFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, payMethod).commit();
    }

    private void findIds() {
        tv_nail_name = (TextView) findViewById(R.id.tv_nail_name);
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_shop_price = (TextView) findViewById(R.id.tv_shop_price);
        tv_contact = (TextView) findViewById(R.id.tv_contact);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_planTime = (TextView) findViewById(R.id.tv_planTime);
        tv_address = (TextView) findViewById(R.id.tv_address);
        edit_summary = (EditText) findViewById(R.id.edit_summary);
        tv_taxi_fee = (TextView) findViewById(R.id.tv_taxi_fee);
        tv_real_pay = (TextView) findViewById(R.id.tv_real_pay);
        choose_coupon = findViewById(R.id.choose_coupon);
        nail_image = (ImageView) findViewById(R.id.nail_image);

        mOrderBean = OrderManager.getCurrentOrder();
        if (mOrderBean == null) {
            mOrderBean = (OrderBean) getIntent().getSerializableExtra("bean");
        }
        NailInfoBean infoBean = mOrderBean.nailInfoBean;
        TextPainUtil.addDeleteLine(tv_shop_price);

        tv_nail_name.setText(infoBean.name);
        tv_price.setText(getString(R.string.s_price, infoBean.price));
        tv_shop_price.setText(getString(R.string.s_price, infoBean.store_price));
        tv_contact.setText(mOrderBean.contact);
        tv_mobile.setText(mOrderBean.mobile);
        tv_planTime.setText(mOrderBean.planTime);

        tv_address.setText(mOrderBean.address);

        ImageManager.loadImage(infoBean.cover, nail_image);
        calculateCost();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit: {
                if (TextUtils.isEmpty(orderId)) {
                    postOrder();
                } else {
                    showAlipayDialog(orderId);
                }
                break;
            }
            case R.id.choose_coupon: {
                if (popupList == null) {
                    popupList = new PopupList(OrderConfirm.this, adapter, v.getWidth());
                    popupList.getListView().setVerticalScrollBarEnabled(false);
                    popupList.getListView().setPadding(1, 1, 1, 1);
                    popupList.getListView().setBackgroundResource(R.drawable.corner_frame_white_solid);
                    popupList.setOnListItemClickListener(new PopupList.OnListItemClick() {
                        @Override
                        public void onPopupListClick(PopupList v, int position) {
                            v.dismiss();
                            CouponBean selected = couponBeans.get(position);
                            if (position == 0) {
                                mCoupon = null;
                                tv_coupon.setText(selected.title);
                            } else {
                                mCoupon = selected;
                                tv_coupon.setText(mCoupon.title + " ￥" + mCoupon.price);
                            }

                            calculateCost();
                        }
                    });
                }

                if (couponBeans.size() == 0) {
                    getCouponList();
                } else {
                    popupList.showAsDropDown(v);
                }

                break;
            }
        }
        super.onClick(v);
    }

    /**
     * 获取优惠券列表
     */
    void getCouponList() {
        //不使用优惠券
        CouponBean emptyBean = new CouponBean();
        emptyBean.title = getString(R.string.not_use_coupon);
        couponBeans.add(emptyBean);

        RequestParams params = new RequestParams();
        params.put("status", 0);
        FingerHttpClient.post("getCouponList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), CouponBean.class, couponBeans);

                    adapter.notifyDataSetChanged();
                    popupList.showAsDropDown(choose_coupon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 计算费用
     */
    void calculateCost() {

        AddressSearchBean adrBean = mOrderBean.addressSearchBean;

        RequestParams params = new RequestParams();
        params.put("latitude", adrBean.latitude);
        params.put("longitude", adrBean.longitude);
        params.put("mid", mOrderBean.nailInfoBean.mid);
        params.put("product_id", mOrderBean.nailInfoBean.id);
        if (mCoupon != null) {
            params.put("coupon_id", mCoupon.id);
        }
        FingerHttpClient.post("calculateCost", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {

                    JSONObject data = o.getJSONObject("data");
                    taxiFee = (float) data.getDouble("taxiCost");
                    tv_taxi_fee.setText(getString(R.string.s_price, data.getString("taxiCost")));
                    total_fee = data.getString("realPay");
                    tv_real_pay.setText(getString(R.string.real_price_s, total_fee));

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

    }

    /**
     * 提交订单
     */
    public void postOrder() {

        float pay_price = Float.valueOf(mOrderBean.nailInfoBean.price);
        if (mCoupon != null) {

        }

        NailInfoBean infoBean = mOrderBean.nailInfoBean;
        RequestParams params = new RequestParams();
        params.put("product_id", infoBean.id);
        params.put("uid", getApp().getUser().id);
        params.put("mid", infoBean.seller_info.uid);
        if (mCoupon != null)
            params.put("coupon_id", mCoupon.id);
        params.put("remark", edit_summary.getText().toString());
        params.put("type", mOrderBean.type);
        params.put("address", mOrderBean.address);
        params.put("taxi_cost", taxiFee);
        params.put("order_price", mOrderBean.nailInfoBean.price);
        params.put("real_pay", pay_price);
        params.put("pay_method", "1");
        params.put("book_date", mOrderBean.book_date);
        params.put("time_block", mOrderBean.time_block);
        params.put("contact", mOrderBean.contact);
        params.put("phone_num", mOrderBean.mobile);

        FingerHttpClient.post("postOrder", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                OrderManager.cancel();
                if (o.has("data")) {
                    try {
                        orderId = o.getString("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                showAlipayDialog(orderId);

            }
        });
    }

    /**
     * 是否立即支付
     * @param orderId
     */
    private void showAlipayDialog(String orderId) {
        final String finalOrderId = orderId;

        new AlertDialog.Builder(OrderConfirm.this)
                .setTitle(getString(R.string.order_ok))
                .setMessage(getString(R.string.order_msg))
                .setPositiveButton(getString(R.string.pay_now), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlipayAPI(OrderConfirm.this).start(
                                finalOrderId,
                                mOrderBean.nailInfoBean.name,
                                mOrderBean.nailInfoBean.name,
                                total_fee
                        );
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show()
        ;
    }

    class CouponAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return couponBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return couponBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(OrderConfirm.this, R.layout.coupon_list, null);
            }

            CouponBean bean = couponBeans.get(position);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView price = (TextView) convertView.findViewById(R.id.price);

            title.setText(bean.title);
            if (!TextUtils.isEmpty(bean.price)) {
                price.setText(getString(R.string.s_price, bean.price));
            } else {
                price.setText(null);
            }
            return convertView;
        }
    }

}
