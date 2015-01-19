package com.finger.activity.main;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import com.finger.R;
import com.finger.activity.FingerApp;
import com.finger.activity.base.BaseActivity;
import com.finger.adapter.NailListAdapter;
import com.finger.entity.NailInfoBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.util.Logger;
import com.finger.support.widget.PriceSorter;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by acer on 2015/1/19.
 */
public class NewHotActivity extends BaseActivity implements ListController.Callback {
    ListController controller;
    GridView       gridView;
    List<NailInfoBean> beans = new LinkedList<NailInfoBean>();
    NailListAdapter adapter;
    PriceSorter     priceSorter;
    String          price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_hot);
        gridView = (GridView) findViewById(R.id.grid);
        adapter = new NailListAdapter(this, beans);
        gridView.setAdapter(adapter);
        controller = new ListController(gridView, this);
        getProductList(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_sort:{
                //价格区间
                if (priceSorter == null) {
                    priceSorter = new PriceSorter(this);
                    priceSorter.setPriceSetWatcher(new PriceSorter.OnPriceSet() {
                        @Override
                        public void onPriceSet(String price) {
                            NewHotActivity.this.price = price;
                            beans.clear();
                            adapter.notifyDataSetChanged();
                            getProductList(1);
                        }
                    });
                }
                if (priceSorter.isShowing()) {
                    priceSorter.dismiss();
                } else {
                    int[] l = new int[2];
                    v.getLocationOnScreen(l);
                    priceSorter.showAsDropDown(v, 0, 0);
                }
                break;
            }
        }
        super.onClick(v);
    }

    /**
     * 获取美甲作品列表
     *
     * @param page
     */
    void getProductList(int page) {

        RequestParams params = new RequestParams();
        params.put("page", page);
        params.put("pagesize", controller.getPageSize());

        JSONObject condition = new JSONObject();
        FingerApp app = FingerApp.getInstance();
        try {

            // (40 - 80 之间)
            condition.put("price", price);
            //百度城市代码
            condition.put("city_code", app.getCurCity().city_code);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            //对condition进行URL编码
            params.put("condition", URLEncoder.encode(condition.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        FingerHttpClient.post("getProductList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {

                    JSONObject data = o.getJSONObject("data");
                    JsonUtil.getArray(data.getJSONArray("normal"), NailInfoBean.class, beans);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Logger.w(e.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getProductList(nextPage);
    }
}
