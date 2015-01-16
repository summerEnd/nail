package com.finger.entity;

import com.finger.activity.main.user.my.MyDiscountActivity;

import java.io.Serializable;

import static com.finger.activity.main.user.my.MyDiscountActivity.CouponBean;

public class OrderListBean implements Serializable {
    public int    id;
    public int    status;
    public int    product_id;
    public int    mid;
    public String create_time;
    public String order_price;
    public String product_cover;
    public String product_name;
    public String username;
    public String contact;
    public String phone_num;

    //1已领优惠券，0未领
    public String get_coupon;

    public String     uid;
    public String     type;
    public String     remark;
    public String     taxi_cost;
    public String     coupon_id;
    public CouponBean coupon;
    public String     real_pay;
    public String     pay_method;
    public String     book_date;
    public int        time_block;
    public String     latitude;
    public String     longitude;
    public String     address;
    public String     is_delete;
    public String     avatar;
}
