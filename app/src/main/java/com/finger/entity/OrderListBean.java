package com.finger.entity;

import java.io.Serializable;

public class OrderListBean implements Serializable {
    public int    id;
    public int    status;
    public int    product_id;
    public int    mid;
    public String create_time;
    public String order_price;
    public String product_cover;
    public String product_name;

    public String uid;
    public String type;
    public String remark;
    public String taxi_cost;
    public String coupon_id;
    public String real_pay;
    public String pay_method;
    public String book_date;
    public String time_block;
    public String latitude;
    public String longitude;
    public String address;
    public String is_delete;
}
