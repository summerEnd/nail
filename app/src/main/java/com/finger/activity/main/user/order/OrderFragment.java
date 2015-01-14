package com.finger.activity.main.user.order;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.OrderListFragment;
import com.finger.adapter.OrderAdapterFactory;
import com.finger.activity.info.User;
import com.finger.entity.OrderListBean;
import com.finger.entity.OrderManager;

import java.util.ArrayList;
import java.util.List;

import static com.finger.adapter.OrderAdapterFactory.OrderAdapter;
import static com.finger.adapter.OrderAdapterFactory.OrderType.*;

@User
public class OrderFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    RadioGroup        rg;
    FragmentManager   manager;
    OrderListFragment curFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_user_order, null);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.order_center);
        rg = (RadioGroup) v.findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);

        manager = getChildFragmentManager();
        showFragment(R.id.rb1);
        return v;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            List<Fragment> fragments = manager.getFragments();
            for (Fragment f:fragments){
                ((OrderListFragment) f).clearList();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        showFragment(checkedId);
    }

    void showFragment(int checked_id) {
        OrderListFragment fragment = (OrderListFragment) manager.findFragmentByTag(String.valueOf(checked_id));
        FragmentTransaction ft = manager.beginTransaction();

        if (fragment == null) {
            String status = "0";
            OrderAdapter adapter = null;

            switch (checked_id) {
                case R.id.rb1: {
                    adapter = OrderAdapterFactory.getAdapter(getActivity(), ORDER_TO_PAY, new ArrayList<OrderListBean>());
                    break;
                }
                case R.id.rb2: {
                    adapter = OrderAdapterFactory.getAdapter(getActivity(), WAIT_SERVICE, new ArrayList<OrderListBean>());
                    status = OrderManager.STATUS_WAIT_SERVICE;
                    break;
                }
                case R.id.rb3: {
                    adapter = OrderAdapterFactory.getAdapter(getActivity(), WAIT_COMMENT, new ArrayList<OrderListBean>());
                    status = OrderManager.STATUS_WAIT_COMMENT;
                    break;
                }
                case R.id.rb4: {
                    adapter = OrderAdapterFactory.getAdapter(getActivity(), REFUND, new ArrayList<OrderListBean>());
                    status = OrderManager.STATUS_REFUND;
                    break;
                }
            }
            fragment = OrderListFragment.newInstance(status);
            fragment.setListAdapter(adapter);
            ft.add(R.id.frag_container, fragment, String.valueOf(checked_id));
        }


        ft.show(fragment);

        if (curFragment != null) {
            ft.hide(curFragment);
        }

        ft.commit();
        curFragment = fragment;
    }


}
