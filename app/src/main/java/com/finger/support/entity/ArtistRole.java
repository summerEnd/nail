package com.finger.support.entity;

import com.finger.support.Constant;

/**
 * 美甲师
 */
public class ArtistRole extends RoleBean {
    public String honor;

    public ArtistRole(){
        type= Constant.LOGIN_TYPE_ARTIST;
    }
}
