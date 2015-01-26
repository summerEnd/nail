package com.finger.entity;

import java.io.Serializable;

import static com.finger.activity.info.NailInfo.SellerInfoBean;


public class NailInfoBean implements Serializable{
    public int id;
    public String price;
    public String cover;
    public String store_price;
    public String name;
    public String description;
    public int mid;
    public String spend_time;
    public String keep_date;
    public String artist_name;
    public String info_title;
    public String info_text;
    public int star_num;
    public int star_level;
    public int comment_num;
    public int collection_id;
    public SellerInfoBean seller_info;
}
