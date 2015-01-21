package com.finger.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;


public class SearchResult extends BaseActivity implements ListController.Callback {
    ListView list;
    public static final String EXTRA_KEY         = "keywords";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    ListController controller;
    //普通作品
    LinkedList<NailInfoBean> normals    = new LinkedList<NailInfoBean>();
    //精彩推荐
    LinkedList<NailInfoBean> recommends = new LinkedList<NailInfoBean>();

    public SearchAdapter adapter;
    View recommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_02);
        list = (ListView) findViewById(R.id.list);

        adapter = new SearchAdapter(SearchResult.this, normals);
        recommend = getLayoutInflater().inflate(R.layout.search_footer, null);
        showRecommends(recommends);
        list.addFooterView(recommend);
        list.setAdapter(adapter);

        controller = new ListController(list, this);

        getSearchList(1);
    }

    /**
     * 获取搜索结果的列表
     *
     * @param page
     */
    void getSearchList(int page) {
        RequestParams params = new RequestParams();
        JSONObject condition = new JSONObject();
        FingerApp app = getApp();
        try {
            Intent intent = getIntent();
            //城市
            condition.put("city_code", app.getCurCity().city_code);//(百度城市代码)

            //分类
            int category = intent.getIntExtra(EXTRA_CATEGORY_ID, -1);
            if (category != -1) {
                condition.put("category_id", category);
            }
            //关键词
            String keywords = intent.getStringExtra(EXTRA_KEY);
            if (!TextUtils.isEmpty(keywords)) {
                condition.put("keywords", keywords);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            //对condition进行URL编码
            params.put("condition", URLEncoder.encode(condition.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //分页相关的参数
        params.put("page", page);
        params.put("pagesize", controller.getPageSize());

        FingerHttpClient.post("getProductList", params, new FingerHttpHandler() {

            @Override
            public void onSuccess(JSONObject o) {
                try {

                    JSONObject data = o.getJSONObject("data");

                    try {
                        //获取普通作品
                        JsonUtil.getArray(data.getJSONArray("normal"), NailInfoBean.class, normals);
                    } catch (Exception e) {

                    }

                    if (normals.size() == 0) {
                        adapter.showEmpty(true);
                    }

                    try {
                        //获取人气作品,如果已经获取过了就不再取
                        if (recommends.size() == 0) {
                            JsonUtil.getArray(data.getJSONArray("recommend"), NailInfoBean.class, recommends);
                            if (recommends.size() != 0) {
                                //展示精彩推荐
                                showRecommends(recommends);
                            }
                        }

                    } catch (Exception e) {

                    }
                    //这里要注意，要先setAdapter，后加footerView
                    //否则java.lang.ClassCastException: android.widget.HeaderViewListAdapter cannot be cast to android.widget.BaseAdapter
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 加载精彩推荐，最多十个。
     * 精彩推荐作为listView的FooterView展示
     *
     * @param recommends
     */
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
        //加载精彩推荐布局

        for (int i = 0; i < ids.length; i++) {
            NailItem item = (NailItem) recommend.findViewById(ids[i]);
            if (i < size) {
                item.setVisibility(View.VISIBLE);
                item.setImageSize(ItemUtil.halfScreen);
                item.setInfoBean(recommends.get(i));
            } else {
                item.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getSearchList(nextPage);
    }

    class SearchAdapter extends BaseAdapter {
        private Context mContext;
        LayoutInflater inflater;
        private LinkedList<NailInfoBean> normals;

        private boolean showEmpty = false;

        SearchAdapter(Context context, LinkedList<NailInfoBean> normals) {
            this.mContext = context;
            this.normals = normals;
            inflater = LayoutInflater.from(mContext);
        }

        public void showEmpty(boolean flag) {
            showEmpty = flag;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            int size = normals.size();
            //如果没有数据加载一个imageView
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
            if (showEmpty) {
                ImageView imageView = new ImageView(mContext);
                imageView.setImageResource(R.drawable.search_failed);
                imageView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                convertView = imageView;
            } else {
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
            }
            return convertView;


        }

        void applyData(NailInfoBean bean, NailItem item) {
            if (bean == null) {
                item.setVisibility(View.INVISIBLE);
                return;
            } else {
                item.setVisibility(View.VISIBLE);
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
