package com.finger.activity.other.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.finger.support.entity.NailInfoBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.util.Logger;
import com.finger.support.widget.ItemUtil;
import com.finger.support.widget.NailItem;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ImageManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class SearchResult extends BaseActivity {
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_02);
        list = (ListView) findViewById(R.id.list);
        getData();
    }

    void getData() {
        RequestParams params = new RequestParams();

        FingerHttpClient.post("getProductList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    LinkedList<NailInfoBean> normals = new LinkedList<NailInfoBean>();
                    LinkedList<NailInfoBean> recommends = new LinkedList<NailInfoBean>();
                    JSONObject data = o.getJSONObject("data");
                    JsonUtil.getArray(data.getJSONArray("normal"), NailInfoBean.class, normals);
                    JsonUtil.getArray(data.getJSONArray("recommend"), NailInfoBean.class, recommends);
                    list.setAdapter(new SearchAdapter(SearchResult.this, normals, recommends));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class SearchAdapter extends BaseAdapter {
        private Context mContext;
        LayoutInflater inflater;
        private LinkedList<NailInfoBean> normals;
        private LinkedList<NailInfoBean> recommends;

        SearchAdapter(Context context, LinkedList<NailInfoBean> normals, LinkedList<NailInfoBean> recommends) {
            this.mContext = context;
            this.normals = normals;
            this.recommends = recommends;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            int extra = recommends.size() > 0 ? 1 : 0;
            return getSize(normals.size()) + getSize(recommends.size()) + extra;
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
                holder.title = convertView.findViewById(R.id.title);
                holder.item1 = (NailItem) convertView.findViewById(R.id.item_1);
                holder.item2 = (NailItem) convertView.findViewById(R.id.item_2);
                holder.item1.setImageSize(ItemUtil.item_size);
                holder.item2.setImageSize(ItemUtil.item_size);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == getSize(normals.size())) {//显示人气推荐
                holder.title.setVisibility(View.VISIBLE);
                holder.item1.setVisibility(View.GONE);
                holder.item2.setVisibility(View.GONE);
            } else {
                holder.title.setVisibility(View.GONE);
                holder.item1.setVisibility(View.VISIBLE);
                holder.item2.setVisibility(View.VISIBLE);
                int mPosition = position * 2;
                NailInfoBean bean1 = getBean(mPosition);
                NailInfoBean bean2 = getBean(mPosition + 1);
                Logger.i_format("bean"+mPosition+":"+bean1);
                Logger.i_format("bean"+(mPosition+1)+":"+bean2);
                applyData(bean1, holder.item1);
                applyData(bean2, holder.item2);
            }

            convertView.setTag(holder);
            return convertView;
        }

        void applyData(NailInfoBean bean, NailItem item) {
            if (bean==null){
                item.setVisibility(View.INVISIBLE);
                return;
            }
            item.setInfoBean(bean);
        }

        int getSize(int size) {
            return size % 2 == 0 ? size / 2 : size / 2 + 1;
        }

        NailInfoBean getBean(int position) {
            int normalSize = normals.size();
            int fixedNormal =normalSize+ (normalSize % 2 == 0 ? 2 : 3);//普通之后要空出来的数量
            int recommendSize = recommends.size();

            if (position < normalSize) {//普通作品
                return normals.get(position);
            } else if (position >=fixedNormal&&position<fixedNormal+recommendSize) {//人气推荐
                Logger.i_format("position:%d normalSize:%d count:%d", position, normalSize, getCount());
                return recommends.get(position-fixedNormal );
            } else {
                return null;
            }
        }
    }

    static class ViewHolder {
        NailItem item1;
        NailItem item2;
        View title;
    }
}
