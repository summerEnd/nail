package com.finger.activity.other.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.support.entity.NailInfoBean;
import com.finger.support.entity.NailItemBean;
import com.finger.support.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.ItemUtil;
import com.finger.support.widget.NailItem;
import com.finger.support.util.Logger;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.LinkedList;

public class MyCollectionActivity extends BaseActivity {

    GridView grid;
    LinkedList<NailItemBean> beans = new LinkedList<NailItemBean>();
    CollectAdapter adapter;
    View v;
    TranslateAnimation in;
    TranslateAnimation out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        v = findViewById(R.id.edit_layout);
        v.setVisibility(View.INVISIBLE);
        grid = (GridView) findViewById(R.id.grid);
        adapter = new CollectAdapter(this);
        grid.setAdapter(adapter);
        getData();
    }

    void getData() {
        RequestParams params = new RequestParams();
        FingerHttpClient.post("getCollectionList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), NailItemBean.class, beans);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Logger.w(e.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_delete: {
                showDelete(true);
                break;
            }
            case R.id.delete: {
                deleteFromWeb();

                break;
            }
            default:
                super.onClick(v);
        }
    }

    private void deleteFromWeb() {
        deleteFromView();
    }

    private void deleteFromView() {
        LinkedList<NailItemBean> deleteBean = new LinkedList<NailItemBean>();
        for (NailItemBean bean : beans) {
            if (bean.selected) {
                deleteBean.add(bean);
            }
        }
        for (NailItemBean bean : deleteBean) {
            beans.remove(bean);
        }
        showDelete(false);
    }

    void showDelete(boolean show) {
        if (adapter.delete == show) {
            return;
        }
        adapter.showDelete(show);
        animate(show);

    }

    void animate(boolean enter) {
        TranslateAnimation animation;
        v.clearAnimation();
        v.setVisibility(View.VISIBLE);
        if (enter) {
            if (in == null) in = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,//from x
                    Animation.RELATIVE_TO_SELF, 0,//to x
                    Animation.RELATIVE_TO_SELF, 1,//from y
                    Animation.RELATIVE_TO_SELF, 0//to y
            );
            animation = in;
        } else {
            if (out == null) out = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,//from x
                    Animation.RELATIVE_TO_SELF, 0,//to x
                    Animation.RELATIVE_TO_SELF, 0,//from y
                    Animation.RELATIVE_TO_SELF, 1//to y
            );
            animation = out;
        }

        animation.setFillAfter(true);
        animation.setDuration(500);
        v.startAnimation(animation);
    }


    private class CollectAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        LayoutInflater inflater;
        private boolean delete;
        DecimalFormat format;

        public void showDelete(boolean delete) {
            this.delete = delete;
            if (delete) {
                for (NailItemBean bean : beans) {
                    bean.selected = false;
                }
            }
            notifyDataSetChanged();
        }

        CollectAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            format = new DecimalFormat();
            format.applyPattern("0.00");
        }

        @Override
        public int getCount() {
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.grid_item_collect, null);
                GridView.LayoutParams lp = new AbsListView.LayoutParams(ItemUtil.item_size, ItemUtil.item_size);
                convertView.setLayoutParams(lp);
                holder.item = (NailItem) convertView.findViewById(R.id.item);
                holder.cb = (CheckBox) convertView.findViewById(R.id.cb_delete);
                holder.cb.setOnCheckedChangeListener(this);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NailItemBean bean = beans.get(position);

            if (delete) {
                holder.cb.setVisibility(View.VISIBLE);
                holder.item.setExtraOnClickListener(this);
            } else {
                holder.cb.setVisibility(View.GONE);
                holder.item.setExtraOnClickListener(null);
            }
            holder.cb.setTag(bean);
            holder.item.getContentView().setTag(bean);
            holder.cb.setChecked(bean.selected);
            holder.item.setInfoBean(bean);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            NailItemBean bean = (NailItemBean) buttonView.getTag();
            bean.selected = isChecked;
        }

        @Override
        public void onClick(View v) {
            Logger.d("v:" + v + " tag:" + v.getTag());
            NailItemBean bean = (NailItemBean) v.getTag();
            bean.selected = !bean.selected;
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        boolean selected = false;
        NailItem item;
        CheckBox cb;
    }
}
