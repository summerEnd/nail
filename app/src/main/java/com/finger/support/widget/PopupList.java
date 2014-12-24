package com.finger.support.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.finger.R;


/**
 */
public class PopupList extends PopupWindow implements AdapterView.OnItemClickListener {

    private OnListItemClick onListItemClick;
    BaseAdapter adapter;

    public interface OnListItemClick {
        public void onPopupListClick(PopupList v, int position);
    }

    public void setOnListItemClickListener(OnListItemClick onListItemClick) {
        this.onListItemClick = onListItemClick;
    }

    public PopupList(Context context, BaseAdapter adapter,int width) {
        super(context);
        ListView list = getListView(context, width, adapter);
        setContentView(list);
        setWidth(width);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
    }


    private ListView getListView(Context context, int width, BaseAdapter adapter) {
        ListView list = new ListView(context);
        list.setLayoutParams(new AbsListView.LayoutParams(width, AbsListView.LayoutParams.WRAP_CONTENT));
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setDivider(new ColorDrawable(context.getResources().getColor(R.color.light_gray)));
        list.setDividerHeight(1);
        return list;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onListItemClick != null) onListItemClick.onPopupListClick(this, position);
    }
}
