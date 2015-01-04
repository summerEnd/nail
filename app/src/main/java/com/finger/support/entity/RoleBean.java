package com.finger.support.entity;

import com.finger.support.Constant;

import java.io.Serializable;

/**
 * 当前登录的角色,使用的逻辑是：
 * 调用{@link com.finger.FingerApp}的getRole方法获取{@link com.finger.support.entity.RoleBean}。
 * 返回的RoleBean不为空，类型为{@link UserRole} 、{@link ArtistRole}、{@link com.finger.support.entity.RoleBean.EmptyRole}
 * 例如：
 *
 */
public class RoleBean implements Serializable{
    public Integer id;
    protected String type;
    public String username;
    public String avatar;
    public String mobile;
    public String password;
    public double latitude;
    public double longitude;
    public String getType() {
        return type;
    }

    /**
     * 没有登录
     */
    public static class EmptyRole extends RoleBean{
        public EmptyRole() {
            type= Constant.LOGIN_TYPE_EMPTY;
        }
    }
}
