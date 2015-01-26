package com.finger.support.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.main.SearchResult;
import com.finger.support.Constant;
import com.finger.entity.HotTagBean;
import com.finger.support.util.ContextUtil;
import com.sp.lib.util.DisplayUtil;
import com.sp.lib.util.FileUtil;

import java.util.ArrayList;


public class SearchWindow extends PopupWindow implements View.OnTouchListener, View.OnClickListener {
    private Activity activity;
    EditText              edit_key;
    ArrayList<HotTagBean> tags;
    View                  search_title;
    GridView              grid;

    public SearchWindow(Activity context) {
        super(context);
        this.activity = context;
        //加载布局
        View v = LayoutInflater.from(context).inflate(R.layout.layout_search, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        v.findViewById(R.id.search).setOnClickListener(this);
        edit_key = (EditText) v.findViewById(R.id.edit_search);
        search_title = v.findViewById(R.id.search_title);
        //设置背景
        setBackgroundDrawable(new ColorDrawable(Color.argb(0x50, 0, 0, 0)));
        //获取屏幕尺寸
        Point p = new Point();
        DisplayUtil.getScreenSize(context, p);
        //全屏幕显示
        v.setLayoutParams(new ViewGroup.LayoutParams(p.x, p.y));

        setWidth(p.x + 4);
        setHeight(p.y);
        setFocusable(true);
        setContentView(v);
        grid = (GridView) v.findViewById(R.id.grid);
        grid.setOnItemClickListener(onCategoryClicked);
        tags = (ArrayList<HotTagBean>) FileUtil.readFile(context, Constant.FILE_TAGS);
        grid.setOnTouchListener(this);
        if (tags != null){
            grid.setAdapter(new TagAdapter(context, tags));
        }
    }

    public void show(View parent) {
        showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);
        search_title.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.search_title_anim));
    }

    private AdapterView.OnItemClickListener onCategoryClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Context context = view.getContext();
            int category_id = tags == null ? 0 : tags.get(position).id;
            context.startActivity(new Intent(context, SearchResult.class)
                            .putExtra(SearchResult.EXTRA_CATEGORY_ID, category_id)
            );
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_MOVE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search: {

                String keywords = edit_key.getText().toString();

                if (TextUtils.isEmpty(keywords)){
                    ContextUtil.toast(edit_key.getHint());
                    return;
                }

                Context context = v.getContext();
                context.startActivity(new Intent(context, SearchResult.class)
                                .putExtra(SearchResult.EXTRA_KEY, keywords)
                );
                break;
            }
        }
    }

    class TagAdapter extends BaseAdapter {
        ArrayList<HotTagBean> tags;
        Context               context;

        TagAdapter(Context context, ArrayList<HotTagBean> tags) {
            this.context = context;
            this.tags = tags;
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
            if (convertView == null) {
                v = new TextView(context);
                v.setTextColor(0xfffa8faf);
                v.setTextSize(20);
                v.setGravity(Gravity.CENTER);
            } else {
                v = (TextView) convertView;
            }
            HotTagBean bean = tags.get(position);
            v.setText(bean.name);
            return v;
        }
    }

}
