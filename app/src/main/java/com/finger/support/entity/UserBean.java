package com.finger.support.entity;

import com.finger.support.Constant;

/**
 * 用户
 */
public class UserBean extends RoleBean {

    public UserBean() {
        type = Constant.LOGIN_TYPE_USER;
    }
}
