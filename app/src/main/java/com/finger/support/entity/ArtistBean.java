package com.finger.support.entity;

import com.finger.support.Constant;

/**
 * 美甲师
 */
public class ArtistBean extends RoleBean {
    public String honor;

    public ArtistBean(){
        type= Constant.LOGIN_TYPE_ARTIST;
    }
}
