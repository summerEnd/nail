package com.finger.activity.main.artist.my;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.finger.R;
import com.finger.activity.FingerApp;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.ArtistRole;
import com.finger.entity.NailItemBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.ItemUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.util.Logger;
import com.finger.support.widget.NailItem;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 作品管理
 */
public class ProductManage extends BaseActivity implements ListController.Callback {
    GridView grid;
    LinkedList<NailItemBean> beans = new LinkedList<NailItemBean>();
    ProductAdapter     adapter;
    View               v;
    TranslateAnimation in;
    TranslateAnimation out;
    ListController     controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manage);
        v = findViewById(R.id.edit_layout);
        v.setVisibility(View.INVISIBLE);
        grid = (GridView) findViewById(R.id.grid);
        adapter = new ProductAdapter(this);
        grid.setAdapter(adapter);
        controller = new ListController(grid, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        beans.clear();
        adapter.notifyDataSetChanged();
        getProductList(1);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_delete: {
                showDelete(true);
                break;
            }
            case R.id.delete: {

                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle(R.string.warn)
                        .setMessage(getString(R.string.confirm_delete))
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFromWeb();
                            }
                        })
                        .show();


                break;
            }
            case R.id.title_add: {
                startActivity(new Intent(this, PublishNailActivity.class));
                break;
            }
            case R.id.cancel:{
                //隐藏删除页面
                showDelete(false);
                adapter.notifyDataSetChanged();
                break;
            }
            default:
                super.onClick(v);
        }
    }

    @Override
    public void onBackPressed() {
        if(adapter.delete){
            showDelete(false);
            adapter.notifyDataSetChanged();
        }else{
            super.onBackPressed();
        }
    }

    /**
     * 向服务端请求删除
     */
    private void deleteFromWeb() {
        //先把要删除的放入一个集合
        final LinkedList<NailItemBean> deleteBean = new LinkedList<NailItemBean>();
        for (NailItemBean bean : beans) {
            if (bean.selected) {
                deleteBean.add(bean);
            }
        }

        if (deleteBean.size() == 0) {
            showDelete(false);
            return;
        }

        StringBuilder delete_ids = new StringBuilder();
        Iterator<NailItemBean> it = deleteBean.iterator();
        //将要删除的id拼接成一个字符串用逗号隔开
        while (it.hasNext()) {
            NailItemBean next = it.next();
            delete_ids.append(next.id);
            if (it.hasNext()) {
                delete_ids.append("_");
            }
        }

        //请求网络
        RequestParams params = new RequestParams();
        params.put("products", delete_ids.toString());
        FingerHttpClient.post("batchDeleteProduct", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                //隐藏删除页面
                showDelete(false);

                //删除成功，重新获取数据
                for (NailItemBean item : deleteBean) {
                    beans.remove(item);
                }
                ContextUtil.toast(getString(R.string.delete_ok));
                adapter.notifyDataSetChanged();
            }
        });

    }


    /**
     * @param show true 弹出删除 false隐藏
     */
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
            if (in == null)
                in = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0,//from x
                        Animation.RELATIVE_TO_SELF, 0,//to x
                        Animation.RELATIVE_TO_SELF, 1,//from y
                        Animation.RELATIVE_TO_SELF, 0//to y
                );
            animation = in;
        } else {
            if (out == null)
                out = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0,//from x
                        Animation.RELATIVE_TO_SELF, 0,//to x
                        Animation.RELATIVE_TO_SELF, 0,//from y
                        Animation.RELATIVE_TO_SELF, 1//to y
                );
            animation = out;
        }

        animation.setFillAfter(true);
        animation.setDuration(400);
        v.startAnimation(animation);
    }

    void getProductList(int page) {
        FingerApp app = getApp();
        ArtistRole role = (ArtistRole) app.getUser();
        RequestParams params = new RequestParams();
        JSONObject condition = new JSONObject();
        try {
            condition.put("mid", role.id);// (美甲师id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            params.put("condition", URLEncoder.encode(condition.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        params.put("page", page);
        params.put("pagesize", controller.getPageSize());
        FingerHttpClient.post("getProductList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JSONObject data = o.getJSONObject("data");
                    JsonUtil.getArray(data.getJSONArray("normal"), NailItemBean.class, beans);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    //提示还没有发布作品
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getProductList(nextPage);
    }


    private class ProductAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
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

        ProductAdapter(Context context) {
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
                holder.item.setExtraOnClickListener(onEditClick);
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
            ((CheckBox) ((View) v.getParent().getParent()).findViewById(R.id.cb_delete)).setChecked(bean.selected);
        }

        /**
         * 点击进入编辑页面
         */
        private View.OnClickListener onEditClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                context.startActivity(new Intent(context, PublishNailActivity.class)
                        .putExtra(PublishNailActivity.EXTRA_BEAN, (Serializable) v.getTag()));
            }
        };

    }

    class ViewHolder {
        NailItem item;
        CheckBox cb;
    }
}
