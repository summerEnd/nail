package com.finger.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.finger.R;
import com.finger.support.Constant;
import com.finger.support.api.BaiduAPI;
import com.finger.support.entity.AdsBean;
import com.finger.support.entity.CityBean;
import com.finger.support.entity.HotTagBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.util.Logger;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    CheckedTextView tv_gps_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_city_activity);
        listView = (ListView) findViewById(R.id.listView);
        View headView = View.inflate(this, R.layout.my_location_head, null);
        tv_gps_city = (CheckedTextView) headView.findViewById(R.id.tv_gps_city);
        BDLocation location = BaiduAPI.mBDLocation;
        //拦截headView点击事件
        headView.findViewById(R.id.tv_title_service_city).setOnClickListener(this);
        headView.findViewById(R.id.tv_title_gps).setOnClickListener(this);
        if (location != null) {
            tv_gps_city.setText(location.getCity());
            tv_gps_city.setChecked(true);
        } else {
            tv_gps_city.setText(getString(R.string.location_failed));
            tv_gps_city.setChecked(false);
        }
        tv_gps_city.setOnClickListener(this);
        listView.addHeaderView(headView);
        ArrayList<CityBean> cities = (ArrayList<CityBean>) FileUtil.readFile(this, Constant.FILE_CITIES);
        if (cities != null) {
            setListAdapter(cities);
        } else {
            getCities();
        }
    }

    void setListAdapter(ArrayList<CityBean> cities) {
        String[] cityNames = new String[cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            cityNames[i] = cities.get(i).name;
        }
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_city, cityNames));
        listView.setOnItemClickListener(this);

    }

    void getCities() {

        RequestParams params = new RequestParams();
        FingerHttpClient.post("getIndex", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    ArrayList<CityBean> cities = new ArrayList<CityBean>();
                    JSONObject data = o.getJSONObject("data");
                    JSONArray json_cityList = data.getJSONArray("city_list");
                    JsonUtil.getArray(json_cityList, CityBean.class, cities);
                    FileUtil.saveFile(getApplicationContext(), Constant.FILE_CITIES, cities);
                    setListAdapter(cities);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_gps_city: {
                CheckedTextView checked = (CheckedTextView) listView.getChildAt(listView.getCheckedItemPosition());
                if (checked != null) {
                    checked.setChecked(false);
                }
                listView.clearChoices();

                tv_gps_city.setChecked(true);
                break;
            }
        }
        super.onClick(v);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        tv_gps_city.setChecked(false);
        Logger.d("checked:" + listView.getCheckedItemPosition());
    }
}
