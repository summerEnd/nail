package com.finger.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.finger.entity.NailInfoBean;
import com.finger.support.util.ItemUtil;
import com.finger.support.widget.NailItem;

import java.util.List;

/**
 * Created by acer on 2014/12/24.
 */
public class NailListAdapter extends BaseAdapter {
    private Context context;
    private List<NailInfoBean> beans;

    public NailListAdapter(Context context, List<NailInfoBean> beans) {
        this.context = context;
        this.beans = beans;
    }

    @Override
    public int getCount() {
        return beans.size();
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
            ViewGroup.LayoutParams lp=new AbsListView.LayoutParams(ItemUtil.item_size,ItemUtil.item_size);
            item.setLayoutParams(lp);
            convertView = item;
        } else {
            item = (NailItem) convertView;
        }
        NailInfoBean bean=beans.get(position);
        item.setInfoBean(bean);
        return convertView;
    }


}

