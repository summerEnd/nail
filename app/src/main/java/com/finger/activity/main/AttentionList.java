package com.finger.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.activity.info.ArtistInfo;
import com.finger.entity.AttentionItemBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.RatingWidget;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AttentionList extends BaseActivity implements AdapterView.OnItemClickListener, ListController.Callback {


    ListView    listView;
    CareAdapter adapter;
    List<AttentionItemBean> beans = new LinkedList<AttentionItemBean>();
    ListController controller;
    View           title_done;
    View           title_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_care);
        listView = (ListView) findViewById(R.id.listView);
        title_delete = findViewById(R.id.title_delete);
        title_done = findViewById(R.id.title_done);
        adapter = new CareAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        controller = new ListController(listView, this);
        getAttentionList(1);
    }

    /**
     * 获取关注列表
     *
     * @param page
     */
    void getAttentionList(int page) {
        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("pagesize", controller.getPageSize());
        FingerHttpClient.post("getAttentionList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), AttentionItemBean.class, beans);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_done: {
                deleteItem();
                toggleTitle(false);
                break;
            }
            case R.id.title_delete: {
                adapter.showDelete(true);
                toggleTitle(true);
                break;
            }
        }
        super.onClick(v);
    }

    /**
     * 删除选中的关注
     */
    private void deleteItem() {
        //将选中的item放入一个集合
        final LinkedList<AttentionItemBean> deletes = new LinkedList<AttentionItemBean>();
        for (AttentionItemBean bean : beans) {
            if (bean.selected) {
                deletes.add(bean);
            }
        }
        if (deletes.size() == 0) {
            toggleTitle(false);
            adapter.showDelete(false);
            return;
        }

        //将集合中的id拼接起来
        StringBuilder delete_ids = new StringBuilder();
        Iterator<AttentionItemBean> iterator = deletes.iterator();
        while (iterator.hasNext()) {
            AttentionItemBean bean = iterator.next();
            delete_ids.append(bean.attention_id);
            if (iterator.hasNext()) {
                delete_ids.append(",");
            }
        }

        //向服务器请求删除
        RequestParams params = new RequestParams();
        params.put("attentions", delete_ids.toString());
        FingerHttpClient.post("batchCancelAttention", params, new FingerHttpHandler() {

            @Override
            public void onSuccess(JSONObject o) {
                for (AttentionItemBean item : deletes) {
                    beans.remove(item);
                }
                toggleTitle(false);
                adapter.showDelete(false);
            }
        });
    }


    /**
     * 转换是否为删除状态
     *
     * @param delete
     */
    void toggleTitle(boolean delete) {
        if (delete) {
            title_done.setVisibility(View.VISIBLE);
            title_delete.setVisibility(View.GONE);
        } else {
            title_done.setVisibility(View.GONE);
            title_delete.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.delete) {
            AttentionItemBean bean = beans.get(position);
            bean.selected = !bean.selected;
            ImageView iv = (ImageView) view.findViewById(R.id.iv_delete);
            if (bean.selected) {
                iv.setImageResource(R.drawable.attention_checked);
            } else {
                iv.setImageResource(R.drawable.attention_unchecked);
            }
        } else {
            startActivity(new Intent(this, ArtistInfo.class).putExtra("id", beans.get(position).uid));
        }
    }

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getAttentionList(nextPage);
    }

    class CareAdapter extends BaseAdapter {
        LayoutInflater      inflater;
        DisplayImageOptions options;

        CareAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            options = ContextUtil.getAvatarOptions();
        }

        private boolean delete;

        public void showDelete(boolean delete) {
            this.delete = delete;
            if (delete) {
                for (AttentionItemBean bean : beans) {
                    bean.selected = false;
                }
            }
            notifyDataSetChanged();
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
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_my_care, null);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.tv_nick_name = (TextView) convertView.findViewById(R.id.tv_nick_name);
                holder.tv_average_price = (TextView) convertView.findViewById(R.id.tv_average_price);
                holder.tv_order_number = (TextView) convertView.findViewById(R.id.tv_order_number);
                holder.tv_news = (TextView) convertView.findViewById(R.id.tv_news);
                holder.rating = (RatingWidget) convertView.findViewById(R.id.rating);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AttentionItemBean bean = beans.get(position);
            if (delete) {
                if (bean.selected) {
                    holder.iv_delete.setImageResource(R.drawable.attention_checked);
                } else {
                    holder.iv_delete.setImageResource(R.drawable.attention_unchecked);
                }
                holder.iv_delete.setVisibility(View.VISIBLE);
            } else {
                holder.iv_delete.setVisibility(View.GONE);
            }

            holder.tv_nick_name.setText(bean.username);
            holder.tv_average_price.setText(getString(R.string.average_price_s, bean.average_price));
            holder.rating.setScore(bean.score);
            holder.tv_order_number.setText(getString(R.string.order_d_num, bean.total_num));


            String new_product = bean.new_product;
            if ("0".equals(new_product) || TextUtils.isEmpty(new_product) || "null".equals(new_product)) {
                holder.tv_news.setVisibility(View.GONE);
            } else {
                holder.tv_news.setVisibility(View.VISIBLE);
                holder.tv_news.setText(new_product);

            }

            ImageManager.loadImage(bean.avatar, holder.iv_avatar, options);

            convertView.setTag(holder);
            return convertView;
        }
    }

    class ViewHolder {
        ImageView    iv_delete;
        ImageView    iv_avatar;
        TextView     tv_nick_name;
        TextView     tv_average_price;
        TextView     tv_order_number;
        TextView     tv_news;
        RatingWidget rating;
    }
}
