package com.finger.activity.plan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.AddressSearchBean;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.Logger;
import com.sp.lib.util.ListController;

import java.util.ArrayList;
import java.util.List;

public class SearchAddress extends BaseActivity implements ListController.Callback, AdapterView.OnItemClickListener {
    EditText  edit_address;
    PoiSearch mPoiSearch;
    ListView  list;
    List<PoiInfo> allPoi = new ArrayList<PoiInfo>();
    ListController controller;
    AddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        edit_address = (EditText) findViewById(R.id.edit_address);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(this);
        adapter = new AddressAdapter();
        list.setAdapter(adapter);
        controller = new ListController(list, this);
        controller.setPageSize(20);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button3)
            scale(v, new Runnable() {
                @Override
                public void run() {

                    allPoi.clear();
                    adapter.notifyDataSetChanged();
                    startSearch(0);

                }
            });

        super.onClick(v);
    }


    void startSearch(int page) {
        String keywords = edit_address.getText().toString();

        String city = getApp().getCurCity().name;
        Logger.d("开始搜索：" + city + " " + keywords);

        mPoiSearch.searchInCity(
                new PoiCitySearchOption()
                        .city(city)
                        .keyword(keywords)
                        .pageNum(page)
                        .pageCapacity(controller.getPageSize())
        );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }


    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                //详情检索失败
                // result.error请参考SearchResult.ERRORNO
                ContextUtil.toast(getString(R.string.search_failed));
            } else {
                //检索成功
                allPoi.addAll(result.getAllPoi());
                //list.setAdapter(new ArrayAdapter<String>(SearchAddress.this, android.R.layout.simple_list_item_1, data));
                adapter.notifyDataSetChanged();
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {

            //获取POI检索结果
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                //详情检索失败
                // result.error请参考SearchResult.ERRORNO
                Logger.w("onGetPoiDetailResult failed：" + result.error);
            } else {
                //检索成功
                Logger.w("onGetPoiDetailResult success:" + result.getLocation());

            }
        }
    };

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        startSearch(nextPage - 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PoiInfo info = allPoi.get(position);

        if (info.location == null) {
            ContextUtil.toast(info.name + "不是一个精确的位置。");
            return;
        }

        AddressSearchBean bean = new AddressSearchBean();
        bean.name = info.name;
        bean.address = info.address;
        LatLng location = info.location;
        bean.latitude = location.latitude;
        bean.longitude = location.longitude;
        Intent data = new Intent();
        data.putExtra("bean", bean);
        setResult(RESULT_OK, data);
        finish();

    }

    /**
     * 点击缩放
     *
     * @param v
     * @param runnable
     */
    void scale(View v, final Runnable runnable) {
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale_click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                runnable.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(animation);
    }

    class AddressAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allPoi.size();
        }

        @Override
        public Object getItem(int position) {
            return allPoi.get(position);
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
                convertView = View.inflate(SearchAddress.this, R.layout.list_item_address, null);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                holder.name = (TextView) convertView.findViewById(R.id.name);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PoiInfo info = allPoi.get(position);
            holder.address.setText(info.address);
            holder.name.setText(info.name);
            convertView.setTag(holder);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView address;
        TextView name;
    }

}
