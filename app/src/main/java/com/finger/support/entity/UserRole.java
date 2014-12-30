package com.finger.support.entity;

import com.finger.support.Constant;

/**
 * 用户
 */
public class UserRole extends RoleBean {

    public UserRole() {
        type = Constant.LOGIN_TYPE_USER;
    }
}
