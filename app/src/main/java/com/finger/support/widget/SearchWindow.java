package com.finger.support.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.finger.R;
import com.sp.lib.util.DisplayUtil;


public class SearchWindow extends PopupWindow {
    private Activity activity;

    public SearchWindow(Activity context) {
        super(context);
        this.activity = context;
        View v = LayoutInflater.from(context).inflate(R.layout.layout_search, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setBackgroundDrawable(new ColorDrawable(Color.argb(0x50,0,0,0)));
        Point p = new Point();
        DisplayUtil.getScreenSize(context, p);
        v.setLayoutParams(new ViewGroup.LayoutParams(p.x, p.y));
        setWidth(p.x + 4);
        setHeight(p.y);
        setFocusable(true);
        setContentView(v);
    }

    public void show(View parent) {
        showAsDropDown(parent,-4,0);
//        showAtLocation(parent, Gravity.TOP,-4,0);
    }
}
