package com.finger.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.finger.R;
import com.finger.activity.FingerApp;
import com.finger.activity.base.BaseActivity;
import com.finger.service.LocationService;
import com.finger.support.Constant;
import com.finger.entity.CityBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.anim.ActivityAnimator;
import com.sp.lib.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocationActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    ListView            listView;
    CheckedTextView     tv_gps_city;
    ArrayList<CityBean> cities;
    private LocationService.LocationConnection conn;
    CityBean gpsCity;

    //定位城市是否在开通服务的范围之内
    boolean isGpsCityInService = false;

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
        tv_gps_city.setOnClickListener(this);
        listView.addHeaderView(headView);

        //加载开通城市
        loadServiceCityList();
    }

    /**
     * 加载开通服务城市列表,这是个异步的方法
     */
    void loadServiceCityList() {

        //先从文件中读取城市列表
        cities = (ArrayList<CityBean>) FileUtil.readFile(this, Constant.FILE_CITIES);

        if (cities != null) {
            //获取gps定位城市
            getGpsCity(cities);
        } else {
            //从网络上获取已开通服务城市列表
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
                        getGpsCity(cities);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    /**
     * 获取gps定位城市
     */
    void getGpsCity(final List<CityBean> cities) {

        BDLocation location = LocationService.mBDLocation;
        if (location != null) {
            setGpsCityBy(cities, location);
        } else {
            conn = new LocationService.LocationConnection() {
                @Override
                public void onLocated(BDLocation location) {
                    setGpsCityBy(cities, location);
                }
            };
            bindService(new Intent(this, LocationService.class), conn, BIND_AUTO_CREATE);
        }

    }

    /**
     * 设置gps城市，
     *
     * @param cities
     * @param location
     */
    void setGpsCityBy(List<CityBean> cities, BDLocation location) {

        if (location != null && !TextUtils.isEmpty(location.getCityCode())) {
            gpsCity = new CityBean();
            gpsCity.name = location.getCity();
            gpsCity.city_code = location.getCityCode();

            //遍历城市列表，获取cityCode 与定位城市相同的
            Iterator<CityBean> it = cities.iterator();
            while (it.hasNext()) {
                CityBean cityBean = it.next();
                if (cityBean.city_code.equals(gpsCity.city_code)) {
                    gpsCity = cityBean;
                    isGpsCityInService = true;
                    break;
                }
            }
        }
        displayCities(cities, gpsCity);
    }


    /**
     * 展示所有城市，必须在加载完gps城市和开通城市后调用
     * 设置用户选择的城市，如果用户没有选择任何城市，则显示定位城市
     */
    void displayCities(List<CityBean> cities, CityBean gpsCity) {

        if (gpsCity != null && !TextUtils.isEmpty(gpsCity.name)) {
            tv_gps_city.setText(gpsCity.name);
        } else {
            tv_gps_city.setText(R.string.location_failed);
        }

        if (cities != null) {
            String[] cityNames = new String[cities.size()];
            for (int i = 0; i < cities.size(); i++) {
                cityNames[i] = cities.get(i).name;
            }
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_city, cityNames));
            listView.setOnItemClickListener(this);
        }

        //check城市
        CityBean selectedCity = FingerApp.getInstance().getCurCity();
        if (TextUtils.isEmpty(selectedCity.name)) {
            //选中gps定位城市
            check(-1);
        } else {
            //遍历城市列表，获取cityCode与所选城市相同的
            for (int i = 0; i < cities.size(); i++) {
                CityBean cityBean = cities.get(i);
                if (cityBean.city_code.equals(selectedCity.city_code)) {
                    check(i);
                    break;
                }
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_gps_city: {

                if (isGpsCityInService) {
                    returnCity(gpsCity);
                } else if (gpsCity != null && !TextUtils.isEmpty(gpsCity.name)) {
                    ContextUtil.toast(getString(R.string.not_open_city, gpsCity.name));
                } else {
                    ContextUtil.toast(getString(R.string.not_choose_this_city));
                }
                break;
            }
            case R.id.title_back: {
                finish();
                ActivityAnimator.override(this, ActivityAnimator.NO_ANIMATION, ActivityAnimator.OUT_SLIDE_DOWN);
                break;
            }
            default:
                super.onClick(v);
        }
    }

    /**
     * @param position -1代表当前GPS定位城市，0~max代表开通城市在列表中的位置
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
            city = gpsCity;
        } else {
            //列表的check事件由sdk自动完成
            tv_gps_city.setChecked(false);
            listView.setItemChecked(position + 1, true);
            city = cities.get(position);
        }
        return city;
    }

    /**
     * 返回所选城市
     *
     * @param bean
     */
    void returnCity(CityBean bean) {
        getApp().setCurCity(bean);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //listView的点击事件是从headView开始的，而check()方法是不包括headView的
        returnCity(check(position - 1));
    }
}
