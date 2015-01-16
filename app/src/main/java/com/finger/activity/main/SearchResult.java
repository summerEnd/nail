package com.finger.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.finger.activity.FingerApp;
import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.NailInfoBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.util.Logger;
import com.finger.support.util.ItemUtil;
import com.finger.support.widget.NailItem;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class SearchResult extends BaseActivity {
    ListView list;
    public static final String EXTRA_KEY = "keywords";
    public static final String EXTRA_CATEGORY_ID = "category_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_02);
        list = (ListView) findViewById(R.id.list);

        getData();
    }

    void getData() {
        RequestParams params = new RequestParams();
        JSONObject condition = new JSONObject();
        FingerApp app = getApp();
        try {
            Intent intent = getIntent();
            condition.put("city_code", app.getCurCity().city_code);//(百度城市代码)
            condition.put("category_id", intent.getIntExtra(EXTRA_CATEGORY_ID, -1));
            condition.put("keywords", intent.getStringExtra(EXTRA_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FingerHttpClient.post("getProductList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    LinkedList<NailInfoBean> normals = new LinkedList<NailInfoBean>();
                    LinkedList<NailInfoBean> recommends = new LinkedList<NailInfoBean>();
                    JSONObject data = o.getJSONObject("data");
                    JsonUtil.getArray(data.getJSONArray("normal"), NailInfoBean.class, normals);

                    if (data.has("recommend")) {
                        JsonUtil.getArray(data.getJSONArray("recommend"), NailInfoBean.class, recommends);
                    }

                    if (recommends.size() != 0) {
                        showRecommends(recommends);
                    }
                    list.setAdapter(new SearchAdapter(SearchResult.this, normals));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void showRecommends(List<NailInfoBean> recommends) {
        int ids[] = new int[]{
                R.id.item_1,
                R.id.item_2,
                R.id.item_3,
                R.id.item_4,
                R.id.item_5,
                R.id.item_6,
                R.id.item_7,
                R.id.item_8,
                R.id.item_9,
                R.id.item_10
        };
        int size = recommends.size();
        View footer = getLayoutInflater().inflate(R.layout.search_footer, null);
        for (int i = 0; i < ids.length; i++) {
            NailItem item = (NailItem) footer.findViewById(ids[i]);
            if (i < size) {
                item.setVisibility(View.VISIBLE);
                item.setImageSize(ItemUtil.halfScreen);
                item.setInfoBean(recommends.get(i));
            } else {
                item.setVisibility(View.GONE);
            }
        }
        list.addFooterView(footer);
    }


    class SearchAdapter extends BaseAdapter {
        private Context mContext;
        LayoutInflater inflater;
        private LinkedList<NailInfoBean> normals;

        SearchAdapter(Context context, LinkedList<NailInfoBean> normals) {
            this.mContext = context;
            this.normals = normals;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            int size = normals.size();
            return size % 2 == 0 ? size / 2 : size / 2 + 1;
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
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.nail_list_item_2, null);
                holder.item1 = (NailItem) convertView.findViewById(R.id.item_1);
                holder.item2 = (NailItem) convertView.findViewById(R.id.item_2);
                holder.item1.setImageSize(ItemUtil.item_size);
                holder.item2.setImageSize(ItemUtil.item_size);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            int mPosition = position * 2;
            NailInfoBean bean1 = getBean(mPosition);
            NailInfoBean bean2 = getBean(mPosition + 1);
            Logger.i_format("bean" + mPosition + ":" + bean1);
            Logger.i_format("bean" + (mPosition + 1) + ":" + bean2);
            applyData(bean1, holder.item1);
            applyData(bean2, holder.item2);

            convertView.setTag(holder);
            return convertView;
        }

        void applyData(NailInfoBean bean, NailItem item) {
            if (bean == null) {
                item.setVisibility(View.INVISIBLE);
                return;
            }
            item.setInfoBean(bean);
        }


        NailInfoBean getBean(int position) {
            int normalSize = normals.size();

            return position >= normalSize ? null : normals.get(position);
        }
    }

    static class ViewHolder {
        NailItem item1;
        NailItem item2;
    }
}
