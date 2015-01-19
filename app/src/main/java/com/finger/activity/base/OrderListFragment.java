package com.finger.activity.base;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.finger.R;
import com.finger.activity.FingerApp;
import com.finger.activity.info.OrderInfoActivity;
import com.finger.entity.ArtistRole;
import com.finger.entity.OrderListBean;
import com.finger.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.finger.adapter.OrderAdapterFactory.OrderAdapter;

/**
 * 所有订单列表的父类
 */
public class OrderListFragment extends ListFragment implements ListController.Callback {
    String         status;
    ListController controller;

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

    /**
     * 刷新列表数据
     */
    public void clearList() {
        OrderAdapter adapter = (OrderAdapter) getListView().getAdapter();
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
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
        mListView.setDividerHeight(18);
        mListView.setSelector(new ColorDrawable(0));
        view.setBackgroundResource(R.drawable.windowBackground);
        //获取订单列表
        status = getArguments().getString("status");
        controller = new ListController(mListView, this);
        getOrderList(1);
    }

    /**
     * @param page
     */
    public void getOrderList(int page) {
        RequestParams params = new RequestParams();
        if (status != null)
            params.put("status", status);

        RoleBean bean = FingerApp.getInstance().getUser();

        if (bean instanceof ArtistRole) {
            params.put("mid", bean.id);
        }

        params.put("pagesize", controller.getPageSize());
        params.put("page", page);

        FingerHttpClient.post("getOrderList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    OrderAdapter adapter = (OrderAdapter) getListView().getAdapter();
                    JsonUtil.getArray(o.getJSONArray("data"), OrderListBean.class, adapter.getData());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override

    public void onListItemClick(ListView l, View v, int position, long id) {
        OrderAdapter listAdapter = (OrderAdapter) getListAdapter();
        List data = listAdapter.getData();
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
}
