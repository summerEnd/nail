package com.finger.activity.base;

import com.finger.entity.OrderListBean;

/**
 * 订单列表按钮回调
 */
public interface OrderListCallback {
    /**
     * 点击付款触发事件
     */
    public void onPay(OrderListBean bean);

    /**
     * 申请退款
     *
     * @param bean
     */
    public void onApplyRefund(OrderListBean bean);

    /**
     * 确认付款
     *
     * @param bean
     */
    public void onConfirmPay(OrderListBean bean);

    /**
     * 获取优惠券
     *
     * @param bean
     */
    public void onGetCoupon(OrderListBean bean);

    /**
     * 评价订单
     *
     * @param bean
     */
    public void onComment(OrderListBean bean);
}
