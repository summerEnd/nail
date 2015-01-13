package com.finger.activity.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.activity.info.ArtistInfo;
import com.finger.entity.AttentionItemBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.RatingWidget;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AttentionList extends BaseActivity implements AdapterView.OnItemClickListener, ListController.Callback {


    ListView    listView;
    CareAdapter adapter;
    TextView    title_delete;
    List<AttentionItemBean> beans = new LinkedList<AttentionItemBean>();
    ListController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_care);
        listView = (ListView) findViewById(R.id.listView);
        title_delete = (TextView) findViewById(R.id.tv_title_delete);
        adapter = new CareAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        controller = new ListController(listView, this);
        getAttentionList(1);
    }


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
            case R.id.tv_title_delete: {

                String titleText = title_delete.getText().toString();
                if (titleText.equals(getString(R.string.delete))) {
                    title_delete.setText(R.string.done);
                    adapter.showDelete(true);
                } else {
                    deleteItem();
                }
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
        LinkedList<AttentionItemBean> deletes = new LinkedList<AttentionItemBean>();
        for (AttentionItemBean bean : beans) {
            if (bean.selected) {
                deletes.add(bean);
            }
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
                beans.clear();
                title_delete.setText(R.string.delete);
                adapter.showDelete(false);
                getAttentionList(1);
            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.delete) {
            AttentionItemBean bean = beans.get(position);
            bean.selected = !bean.selected;
            adapter.notifyDataSetChanged();
        } else {
            startActivity(new Intent(this, ArtistInfo.class).putExtra("id", beans.get(position).uid));
        }
    }

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getAttentionList(nextPage);
    }

    class CareAdapter extends BaseAdapter {
        LayoutInflater inflater;

        CareAdapter(Context context) {
            inflater = LayoutInflater.from(context);
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
                holder.rating = (RatingWidget) convertView.findViewById(R.id.rating);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AttentionItemBean bean = beans.get(position);

            if (delete) {
                holder.iv_delete.setVisibility(View.VISIBLE);
                if (bean.selected) {
                    holder.iv_delete.setImageResource(R.drawable.attention_checked);
                } else {
                    holder.iv_delete.setImageResource(R.drawable.attention_unchecked);
                }
            } else {
                holder.iv_delete.setVisibility(View.GONE);
            }
            holder.tv_nick_name.setText(bean.username);
            holder.tv_average_price.setText(getString(R.string.average_price_s, bean.average_price));
            holder.rating.setScore(bean.score);
            holder.tv_order_number.setText(getString(R.string.order_d_num, bean.total_num));
            ImageManager.loadImage(bean.avatar, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.iv_avatar.setImageBitmap(ImageUtil.roundBitmap(loadedImage, getResources().getDimensionPixelSize(R.dimen.avatar_size) / 2));
                }
            });
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
        RatingWidget rating;
    }
}
