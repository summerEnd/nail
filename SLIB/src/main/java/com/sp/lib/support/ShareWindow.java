package com.sp.lib.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sp.lib.R;
import com.sp.lib.util.DisplayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class ShareWindow extends PopupWindow implements AdapterView.OnItemClickListener {
    List<ResolveInfo> apps;
    OnShareItemClick  onShareItemClick;
    int width;
    TextView tv_title;
    public interface OnShareItemClick {
        public void onShare(ResolveInfo info);
    }

    public void setOnShareItemClick(OnShareItemClick onShareItemClick) {
        this.onShareItemClick = onShareItemClick;
    }

    public ShareWindow(Context context) {
        super(context);
        apps = IntentFactory.getShareAPPs(context);
        final Point p = new Point();
        DisplayUtil.getScreenSize((Activity)context,p);
        width=p.x-100;
        setWidth(width);
        setHeight(WRAP_CONTENT);
        setFocusable(true);
        View v = View.inflate(context, R.layout.share_window, null);
        tv_title= (TextView) v.findViewById(R.id.title);
        GridView gridView = (GridView) v.findViewById(R.id.grid);
        SimpleAdapter adapter = new SimpleAdapter(context, createData(context), R.layout.share_item, new String[]{"icon", "name"}, new int[]{R.id.icon, R.id.app_name});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView) {
                    ImageView iv = (ImageView) view;
                    iv.setLayoutParams(new LinearLayout.LayoutParams(width/3,width/3));
                    iv.setImageDrawable((Drawable) data);
                    return true;
                }
                return false;
            }
        });
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        setContentView(v);

        setAnimationStyle(R.style.popup_window_animation);
    }

    public void setTitle(String title){
        tv_title.setText(title);
    }

    private List createData(Context context) {

        ArrayList data = new ArrayList();
        if (apps == null || apps.size() == 0) {

        } else {
            PackageManager manager = context.getPackageManager();
            for (ResolveInfo info : apps) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("icon", info.loadIcon(manager));
                item.put("name", info.loadLabel(manager).toString());
                data.add(item);
                Log.d("--->",info.toString());
            }



//            data.remove(0);
        }

        return data;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (onShareItemClick != null) {
            ResolveInfo info = apps.get(position);

            onShareItemClick.onShare(info);
        }

    }
}
