package com.finger.activity.base;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.finger.activity.info.OrderInfoActivity;
import com.finger.entity.OrderManager;


public class OrderListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDivider(new ColorDrawable(0));
        getListView().setDividerHeight(18);
        getListView().setSelector(new ColorDrawable(0));
        OrderManager.getOrderList(getListView(), getArguments().getInt("status", -1), 1);
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(getActivity(), OrderInfoActivity.class));
    }
}
