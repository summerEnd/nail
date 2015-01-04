package com.finger.support.entity;

import com.finger.support.Constant;

import java.io.Serializable;

/**
 * 用户
 */
public class UserRole extends RoleBean implements Serializable{

    public UserRole() {
        type = Constant.LOGIN_TYPE_USER;
    }
}
