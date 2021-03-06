package com.finger.entity;

import java.io.Serializable;

public class OrderBean implements Serializable {

    public NailInfoBean nailInfoBean;//作品ID
    public int coupon_id;
    public String remark;
    public String type;
    public String address;
    public String realPrice;
    //备注信息
    public String summary;
    public String taxi_cost;
    public String pay_method;
    //联系人手机号
    public String mobile;
    //联系人姓名
    public String contact;
    //预约时间
    public String planTime;

    public String book_date;
    public int time_block;
    public AddressSearchBean addressSearchBean;


}
