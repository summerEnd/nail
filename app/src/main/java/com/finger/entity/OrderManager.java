package com.finger.entity;

import android.app.Activity;
import android.widget.ListView;

import com.finger.activity.FingerApp;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import static com.finger.adapter.OrderAdapterFactory.OrderAdapter;

/**
 * Created by acer on 2014/12/25.
 */
public class OrderManager {
    /**
     * 等待服务
     */
    public static int STATUS_WAIT_SERVICE = 1;
    /**
     * 等待评价
     */
    public static int STATUS_WAIT_COMMENT = 2;
    /**
     * 退款
     */
    public static int STATUS_REFUND = 3;
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

    /**
     * @param lv     adapter必须为OrderAdapter的子类
     * @param status 状态 STATUS_WAIT_SERVICE，STATUS_WAIT_COMMENT，STATUS_REFUND
     * @param page
     */
    public static void getOrderList(final ListView lv, int status, int page) {
        RequestParams params = new RequestParams();
        if (status >= 0) {
            params.put("status", status);
        }

        RoleBean bean = FingerApp.getInstance().getUser();

        if (bean instanceof ArtistRole) {
            params.put("mid", bean.id);
        }

        FingerHttpClient.post("getOrderList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    OrderAdapter adapter = (OrderAdapter) lv.getAdapter();
                    JsonUtil.getArray(o.getJSONArray("data"), OrderListBean.class, adapter.getData());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
