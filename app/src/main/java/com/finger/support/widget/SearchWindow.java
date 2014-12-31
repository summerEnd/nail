package com.finger.support.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.other.common.SearchResult;
import com.finger.support.Constant;
import com.finger.support.entity.HotTagBean;
import com.sp.lib.util.DisplayUtil;
import com.sp.lib.util.FileUtil;

import java.util.ArrayList;


public class SearchWindow extends PopupWindow implements View.OnTouchListener,View.OnClickListener{
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
        v.findViewById(R.id.search).setOnClickListener(this);
        setBackgroundDrawable(new ColorDrawable(Color.argb(0x50, 0, 0, 0)));
        Point p = new Point();
        DisplayUtil.getScreenSize(context, p);
        v.setLayoutParams(new ViewGroup.LayoutParams(p.x, p.y));
        setWidth(p.x + 4);
        setHeight(p.y);
        setFocusable(true);
        setContentView(v);
        GridView gridView= (GridView) v.findViewById(R.id.grid);
        ArrayList<HotTagBean> tags= (ArrayList<HotTagBean>) FileUtil.readFile(context,Constant.FILE_TAGS);
        gridView.setOnTouchListener(this);
        if (tags!=null)gridView.setAdapter(new TagAdapter(context,tags));
    }

    public void show(View parent) {
        showAsDropDown(parent, -4, 0);
//        showAtLocation(parent, Gravity.TOP,-4,0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return event.getAction()==MotionEvent.ACTION_MOVE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:{
                Context context=v.getContext();
                context.startActivity(new Intent(context, SearchResult.class));
                break;
            }
        }
    }

    class TagAdapter extends BaseAdapter{
        ArrayList<HotTagBean> tags;
        Context context;
        TagAdapter(Context context, ArrayList<HotTagBean> tags) {
            this.context = context;
            this.tags =tags;
        }

        @Override
        public int getCount() {
            return tags.size();
        }

        @Override
        public Object getItem(int position) {
            return tags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v;
            if (convertView==null){
                v=new TextView(context);
                v.setTextColor(0xfffa8faf);
                v.setTextSize(20);
                v.setGravity(Gravity.CENTER);
            }else{
                v= (TextView) convertView;
            }
            HotTagBean bean= tags.get(position);
            v.setText(bean.name);
            return v;
        }
    }

}
