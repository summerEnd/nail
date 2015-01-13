package com.finger.entity;

import com.finger.support.Constant;

import java.io.Serializable;

/**
 * 美甲师
 */
public class ArtistRole extends RoleBean implements Serializable {

    public int    score;
    public int    mid;//美甲师id
    public String average_price;
    public float  professional;
    public float  talk;
    public float  on_time;
    public int    order_num;

    public int    collection_id;
    public int    attention_id;
    public int    comment_good;
    public int    comment_normal;
    public int    comment_bad;
    public int    total_num;//接单数量
    public String resume;//简历内容

    public ArtistRole() {
        type = Constant.LOGIN_TYPE_ARTIST;
    }
}
