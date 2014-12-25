package com.finger.activity.other.plan;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.finger.R;
import com.finger.support.adapter.NailListAdapter;
import com.finger.support.entity.OrderBean;
import com.finger.support.util.ContextUtil;
import com.sp.lib.util.DisplayUtil;

/**
 * Created by acer on 2014/12/19.
 */
public class NailItemListFragment extends Fragment implements View.OnClickListener {
    GridView gridView;
    PopupWindow orderList;
    PopupWindow price_area;
    View layout;
    int width;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_nail_list, null);
        gridView = (GridView) layout.findViewById(R.id.grid);
        gridView.setAdapter(new NailListAdapter(getActivity()));
        layout.findViewById(R.id.order_item).setOnClickListener(this);
        layout.findViewById(R.id.sort_item).setOnClickListener(this);

        return layout;
    }

    @Override
    public void onClick(View v) {
        ContextUtil.toast_debug(v);
        if (width == 0) {
            width = layout.findViewById(R.id.select_layout).getWidth();
        }

        switch (v.getId()) {
            case R.id.order_item: {
                if (orderList == null) {
                    orderList = new PopupWindow();

                    orderList.setWidth(width);
                    orderList.setFocusable(true);
                    orderList.setBackgroundDrawable(new ColorDrawable(0xffffffff));
                    orderList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    View contentView = View.inflate(getActivity(), R.layout.order_list, null);
                    ListView listView = (ListView) contentView.findViewById(R.id.listView);
                    listView.setAdapter(new OrderListAdapter(getActivity(), getResources().getStringArray(R.array.orders)));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((OrderListAdapter) parent.getAdapter()).select(position);
                            orderList.dismiss();
                        }
                    });
                    orderList.setContentView(contentView);
                    orderList.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            rotate(false);
                        }
                    });
                }
                if (orderList.isShowing()) {
                    orderList.dismiss();
                } else {
                    openOrder(v);
                }


                break;
            }
            case R.id.sort_item: {
                if (price_area == null) {
                    price_area = new PopupWindow(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                    price_area.setBackgroundDrawable(new ColorDrawable(0xffffffff));
                    price_area.setFocusable(true);

                    View contentView = View.inflate(getActivity(), R.layout.price_area, null);
                    price_area.setContentView(contentView);

                }
                if (price_area.isShowing()) {
                    price_area.dismiss();
                } else {
                    int[] l = new int[2];
                    v.getLocationOnScreen(l);
                    price_area.showAsDropDown(v, 0, 0);
                }

                break;
            }
        }
    }

    void openOrder(View v) {
        rotate(true);

        int[] l = new int[2];
        v.getLocationOnScreen(l);
        orderList.showAsDropDown(v, 0, 0);
    }

    void rotate(boolean open) {
        View v = layout.findViewById(R.id.iv_order);
        v.clearAnimation();
        RotateAnimation rotateAnimation;
        if (open) {
            rotateAnimation = new RotateAnimation(0, -180, v.getWidth() / 2, v.getHeight() / 2);
        } else {
            rotateAnimation = new RotateAnimation(-180, 0, v.getWidth() / 2, v.getHeight() / 2);
        }
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        v.startAnimation(rotateAnimation);
    }

    class OrderListAdapter extends BaseAdapter {
        private Context context;
        int selected;
        String[] items;
        final int selectedColor = 0xffac3c2e;
        final int unSelectedColor = 0xff808080;

        OrderListAdapter(Context context, String items[]) {
            this.items = items;
            this.context = context;
        }

        public void select(int selected) {
            this.selected = selected;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView item;
            if (convertView == null) {
                item = new TextView(context);
                item.setTextSize(15);
                int verticalPadding = (int) DisplayUtil.dp(9, getResources());
                item.setPadding(0, verticalPadding, 0, verticalPadding);

                convertView = item;
            } else {
                item = (TextView) convertView;
            }
            item.setText(items[position]);
            if (selected == position) {
                item.setTextColor(selectedColor);
            } else {
                item.setTextColor(unSelectedColor);
            }


            return convertView;
        }
    }
}
