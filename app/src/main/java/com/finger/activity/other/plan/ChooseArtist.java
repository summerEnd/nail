package com.finger.activity.other.plan;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.activity.other.info.ArtistInfo;
import com.finger.support.widget.RatingWidget;
import com.sp.lib.util.DisplayUtil;

public class ChooseArtist extends BaseActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    PopupWindow orderList;
    int width;
    private  PopListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_artist);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArtistAdapter(this));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (orderList == null) {
            orderList = new PopupWindow();
            width = findViewById(R.id.sort_item).getWidth();
            orderList.setWidth(width);
            orderList.setFocusable(true);
            orderList.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            orderList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            View contentView = View.inflate(this, R.layout.order_list, null);
            ListView listView = (ListView) contentView.findViewById(R.id.listView);
            adapter = new PopListAdapter(this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    orderList.dismiss();
                }
            });
            orderList.setContentView(contentView);
            orderList.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
        }


        String name=null;
        switch (v.getId()) {
            case R.id.sort_distance: {
                name=getString(R.string.distance);
                break;
            }
            case R.id.sort_price: {
                name=getString(R.string.price);
                break;
            }
            case R.id.sort_stars: {
                name=getString(R.string.star_level);
                break;
            }
        }
        if (orderList.isShowing()) {
            orderList.dismiss();
        } else {
            openList(v,name);
        }
        super.onClick(v);
    }



    void openList(View v,String str) {
        int[] l = new int[2];
        v.getLocationOnScreen(l);
        orderList.showAsDropDown(v, 0, 0);
        adapter.setName(str);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(getIntent().setClass(this, ArtistInfo.class));
    }

    class PopListAdapter extends BaseAdapter {
        private Context context;
        private String name;

        PopListAdapter(Context context) {
            this.context = context;
        }

        public void setName(String name) {
            this.name = name;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
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
            if (position == 0) {
                item.setText(getString(R.string.s_low_to_high, name));
            } else {
                item.setText(getString(R.string.s_high_to_low, name));
            }

            return convertView;
        }
    }

    class ArtistAdapter extends BaseAdapter {
        Context context;

        ArtistAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.artist_list_item, null);
            } else {
                holder = (View) convertView.getTag();
            }
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        RatingWidget ratingWidget;
        ImageView iv;
    }
}
