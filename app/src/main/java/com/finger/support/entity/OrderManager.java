package com.finger.support.entity;

import android.app.Activity;

import java.util.LinkedList;

/**
 * Created by acer on 2014/12/25.
 */
public class OrderManager {
    private static OrderBean bean;
    private static LinkedList<Activity> activities = new LinkedList<Activity>();

    /**
     * 创建一个订单
     *
     * @return
     */
    public static OrderBean createOrder() {
        bean = new OrderBean();
        return bean;
    }

    /**
     * 取消一个订单
     */
    public static void cancel() {
        bean = null;
        for (Activity a : activities) {
            a.finish();
        }
    }

    /**
     * 获取当前正在创建的订单
     *
     * @return
     */
    public static OrderBean getCurrentOrder() {
        return bean;
    }

    /**
     * 将activity添加在一个集合里，订单完成时，全部关闭。
     *
     * @param activity
     */
    public static void addOrderActivity(Activity activity) {
        activities.add(activity);
    }

}
