package com.finger.entity;

import java.io.Serializable;

/**
 * Created by acer on 2015/1/7.
 */
public class OrderListBean implements Serializable{
    public int id;
    public int status;
    public int product_id;
    public int mid;
    public String create_time;
    public String order_price;
    public String product_cover;
    public String product_name;
}
