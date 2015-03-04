package com.finger.activity.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.finger.R;
import com.finger.activity.FingerApp;
import com.finger.activity.MainActivity;
import com.finger.activity.info.OrderInfoActivity;
import com.finger.activity.info.PayInfoActivity;
import com.finger.activity.main.artist.order.OrderFragment;
import com.finger.activity.main.user.order.ApplyRefund;
import com.finger.activity.main.user.order.CommentOrder;
import com.finger.entity.ArtistRole;
import com.finger.entity.OrderListBean;
import com.finger.entity.OrderManager;
import com.finger.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.FingerDialog;
import com.loopj.android.http.RequestParams;
import com.sp.lib.support.IntentFactory;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.finger.adapter.OrderAdapterFactory.OrderAdapter;

/**
 * 所有订单列表的父类
 */
public class OrderListFragment extends ListFragment implements ListController.Callback, OrderListCallback {
    String         status;
    ListController controller;
    OrderAdapter   mAdapter;

    /**
     * @param status 状态 STATUS_WAIT_SERVICE，STATUS_WAIT_COMMENT，STATUS_REFUND
     * @return
     */
    public static OrderListFragment newInstance(String status) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle b = new Bundle();
        b.putString("status", status);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void setListAdapter(ListAdapter adapter) {
        mAdapter = (OrderAdapter) adapter;
        super.setListAdapter(mAdapter);
    }

    /**
     * 刷新列表数据
     */
    public void clearList() {
        mAdapter.getData().clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            clearList();
            getOrderList(1);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView mListView = getListView();
        mListView.setDivider(new ColorDrawable(0));
        mListView.setDividerHeight(15);
        mListView.setSelector(new ColorDrawable(0));
        view.setBackgroundResource(R.drawable.windowBackground);
        //获取订单列表
        status = getArguments().getString("status");
        controller = new ListController(mListView, this);
    }

    /**
     * @param page
     */
    public void getOrderList(int page) {
        RequestParams params = new RequestParams();
        if (status != null)
            params.put("status", status);

        final RoleBean bean = FingerApp.getInstance().getUser();
        if (bean instanceof ArtistRole) {
            params.put("mid", bean.id);
        }

        params.put("pagesize", controller.getPageSize());
        params.put("page", page);

        FingerHttpClient.post("getOrderList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {


                try {
                    JsonUtil.getArray(o.getJSONArray("data"), OrderListBean.class, mAdapter.getData());
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override

    public void onListItemClick(ListView l, View v, int position, long id) {
        List data = mAdapter.getData();
        if (data == null) {
            return;
        }

        OrderListBean bean = (OrderListBean) data.get(position);
        Intent intent = new Intent(getActivity(), OrderInfoActivity.class);
        intent.putExtra("id", bean.id);
        startActivity(intent);
    }

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getOrderList(nextPage);
    }


    @Override
    public void onResume() {
        super.onResume();

        Fragment parent = getParentFragment();
        if (parent instanceof com.finger.activity.main.user.order.OrderFragment
                && ((com.finger.activity.main.user.order.OrderFragment) parent).goToComment
            //                &&!OrderManager.STATUS_WAIT_COMMENT.equals(getTag())
               &&(!OrderManager.STATUS_WAIT_COMMENT.equals(status)) ) {
            //do nothing

        } else {
            mAdapter.getData().clear();
            mAdapter.notifyDataSetChanged();
            getOrderList(1);
        }
    }

    @Override
    public void onPay(OrderListBean bean) {
        startActivity(
                new Intent(getActivity(), PayInfoActivity.class)
                        .putExtra(PayInfoActivity.EXTRA_ID, bean.id)
        );
    }

    @Override
    public void onApplyRefund(OrderListBean bean) {
        startActivity(
                new Intent(getActivity(), ApplyRefund.class)
                        .putExtra("bean", bean));
    }


    @Override
    public void onConfirmPay(final OrderListBean bean) {

        new FingerDialog(getActivity(), new FingerDialog.Listener() {
            @Override
            public void onDialogYesPressed(final DialogInterface dialog) {
                RequestParams params = new RequestParams();
                params.put("order_id", bean.id);
                FingerHttpClient.post("confirmPay", params, new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject o) {
                        mAdapter.getData().clear();
                        mAdapter.notifyDataSetChanged();
                        getOrderList(1);
                        ContextUtil.toast(getString(R.string.confirm_ok));
                        dialog.dismiss();
                    }

                    @Override
                    public void onFail(JSONObject o) {
                        super.onFail(o);

                    }
                });
            }

            @Override
            public void onDialogNoPressed(DialogInterface dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onGetCoupon(final OrderListBean bean) {

        FingerDialog dialog = new FingerDialog(getActivity(), new FingerDialog.Listener() {
            @Override
            public void onDialogYesPressed(DialogInterface dialog) {
                dialog.dismiss();
                MainActivity activity = (MainActivity) getActivity();
                activity.setGetCouponOrderId(bean.id);
                activity.startActivityForResult(IntentFactory.share(
                                activity.getString(R.string.app_name), "http://218.244.149.129/pnail/download/aiyou.apk")
                        ,
                        MainActivity.REQUEST_SHARE);
            }

            @Override
            public void onDialogNoPressed(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        dialog.setTitle(getString(R.string.get_coupon));
        dialog.setMessage(getString(R.string.share_coupon_notice));
        dialog.setNoText(getString(R.string.cancel));
        dialog.setYesText(getString(R.string.share));
        dialog.show();
    }

    @Override
    public void onComment(OrderListBean bean) {
        startActivityForResult(new Intent(getActivity(), CommentOrder.class).putExtra("bean", bean), 100);
        Fragment parent = getParentFragment();
        if (parent instanceof com.finger.activity.main.user.order.OrderFragment) {
            ((com.finger.activity.main.user.order.OrderFragment) parent).goToComment = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.getData().clear();
        mAdapter.notifyDataSetChanged();
        getOrderList(1);
    }
}
