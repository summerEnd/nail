package com.finger.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.support.Constant;
import com.finger.api.BaiduAPI;
import com.finger.entity.CityBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    CheckedTextView tv_gps_city;
    ArrayList<CityBean> cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_city_activity);
        listView = (ListView) findViewById(R.id.listView);
        View headView = View.inflate(this, R.layout.my_location_head, null);
        tv_gps_city = (CheckedTextView) headView.findViewById(R.id.tv_gps_city);
        //拦截headView点击事件
        headView.findViewById(R.id.tv_title_service_city).setOnClickListener(this);
        headView.findViewById(R.id.tv_title_gps).setOnClickListener(this);
        setLocationCity();
        tv_gps_city.setOnClickListener(this);
        listView.addHeaderView(headView);
        cities = (ArrayList<CityBean>) FileUtil.readFile(this, Constant.FILE_CITIES);
        if (cities != null) {
            setListAdapter(cities);
        } else {
            getCities();
        }
    }

    void setLocationCity() {
        BDLocation location = BaiduAPI.mBDLocation;

        if (location != null) {
            tv_gps_city.setText(location.getCity());
            tv_gps_city.setChecked(true);
        } else {
            BaiduAPI.locate(new BaiduAPI.Callback() {
                @Override
                public void onLocated(BDLocation bdLocation) {
                    if (bdLocation == null) {
                        tv_gps_city.setText(getString(R.string.location_failed));
                        tv_gps_city.setChecked(false);
                    } else {
                        tv_gps_city.setText(bdLocation.getCity());
                        tv_gps_city.setChecked(true);
                    }

                }
            });

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
                    cities = new ArrayList<CityBean>();
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
                returnCity(check(-1));
                break;
            }
        }
        super.onClick(v);
    }

    /**
     * @param position -1代表当前定位城市，0~max代表开通城市在列表中的位置
     */
    CityBean check(int position) {
        CityBean city;

        if (position < 0) {
            CheckedTextView checked = (CheckedTextView) listView.getChildAt(listView.getCheckedItemPosition());
            if (checked != null) {
                checked.setChecked(false);
            }
            listView.clearChoices();
            tv_gps_city.setChecked(true);
            city = getApp().getCurCity();
        } else {
            //列表的check事件由sdk自动完成
            tv_gps_city.setChecked(false);

            city = cities.get(position);
        }
        return city;
    }

    void returnCity(CityBean bean) {
        getApp().setCurCity(bean);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //listView的点击事件是从headView开始的，而check()方法是不包括headView的
        returnCity(check(position-1));
    }
}
