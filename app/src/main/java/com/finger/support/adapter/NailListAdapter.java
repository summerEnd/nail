package com.finger.support.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.finger.support.widget.ItemUtil;
import com.finger.support.widget.NailItem;

/**
 * Created by acer on 2014/12/24.
 */
public class NailListAdapter extends BaseAdapter {
    private Context context;

    public NailListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 7;
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
        NailItem item;
        if (convertView == null) {
            item = new NailItem(context);
            item.setImageSize(ItemUtil.item_size);
            convertView = item;
        } else {
            item = (NailItem) convertView;
        }
        item.setPrice("200" + position);
        item.setTitle("hi" + position);
        return convertView;
    }
}

