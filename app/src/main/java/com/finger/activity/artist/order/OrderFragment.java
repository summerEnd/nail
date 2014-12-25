package com.finger.activity.artist.order;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.finger.R;
import com.finger.support.adapter.OrderAdapterFactory;
import com.finger.support.annotion.Artist;

import java.util.ArrayList;

import static com.finger.support.adapter.OrderAdapterFactory.OrderType.ORDER_NOTICE;
import static com.finger.support.adapter.OrderAdapterFactory.OrderType.ORDER_TO_PAY;
import static com.finger.support.adapter.OrderAdapterFactory.OrderType.REFUND;
import static com.finger.support.adapter.OrderAdapterFactory.OrderType.REFUND_NOTICE;
import static com.finger.support.adapter.OrderAdapterFactory.OrderType.WAIT_COMMENT;
import static com.finger.support.adapter.OrderAdapterFactory.OrderType.WAIT_SERVICE;

@Artist
public class OrderFragment extends Fragment implements RadioGroup.OnCheckedChangeListener{

    RadioGroup rg;
    ListView listView;
    FragmentManager manager;
    OrderListFragment curFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_artist_order, null);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.order_center);
        rg = (RadioGroup) v.findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);
        listView = (ListView) v.findViewById(R.id.listView);

        manager = getChildFragmentManager();
        showFragment(R.id.rb1);

        return v;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        showFragment(checkedId);
    }

    void showFragment(int checked_id) {
        OrderListFragment fragment = (OrderListFragment) manager.findFragmentByTag(String.valueOf(checked_id));
        FragmentTransaction ft = manager.beginTransaction();

        if (fragment == null) {
            fragment = new OrderListFragment();
            ft.add(R.id.frag_container, fragment, String.valueOf(checked_id));

        }

        OrderAdapterFactory.OrderAdapter adapter = (OrderAdapterFactory.OrderAdapter) fragment.getListAdapter();
        if (adapter == null) {
            switch (checked_id) {
                case R.id.rb1: {
                    adapter = OrderAdapterFactory.getAdapter(getActivity(), ORDER_NOTICE, new ArrayList());
                    break;
                }
                case R.id.rb2: {
                    adapter = OrderAdapterFactory.getAdapter(getActivity(), REFUND_NOTICE, new ArrayList());
                    break;
                }
            }
            fragment.setListAdapter(adapter);
        }
        ft.show(fragment);

        if (curFragment != null) {
            ft.hide(curFragment);
        }

        ft.commit();
        curFragment = fragment;
    }

    public static class OrderListFragment extends ListFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getListView().setDivider(new ColorDrawable(0));
            getListView().setDividerHeight(18);
        }
    }

}
