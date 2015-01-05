package com.finger.support.entity;

import com.finger.activity.other.info.NailInfo;
import com.finger.support.Constant;

import java.io.Serializable;

/**
 * 美甲师
 */
public class ArtistRole extends RoleBean implements Serializable {
    public int score;
    public String average_price;
    public float professional;
    public float talk;
    public float on_time;

    public int collection_id;
    public int attention_id;
    public int comment_good;
    public int comment_normal;
    public int comment_bad;

    public ArtistRole(){
        type= Constant.LOGIN_TYPE_ARTIST;
    }
}
