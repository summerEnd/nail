package com.finger.activity.other.plan;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.finger.BaseActivity;
import com.finger.R;
import com.finger.support.api.BaiduAPI;
import com.finger.support.entity.RoleBean;
import com.finger.support.widget.EditItem;
import com.finger.support.util.ContextUtil;

public class PlanForMe extends Fragment implements View.OnClickListener {
    MapView mMapView;
    EditItem edit_gps;
    EditItem edit_address;
    EditItem edit_plan_time;
    Bitmap mark;
    Overlay mOverlay;

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mMapView != null) mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        if (mMapView != null) mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        if (mMapView != null) mMapView.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plan_for_me, null);
        mMapView = (MapView) v.findViewById(R.id.map);
        edit_plan_time = (EditItem) v.findViewById(R.id.item_plan_time);
        edit_gps = (EditItem) v.findViewById(R.id.item_gps);
        edit_address = (EditItem) v.findViewById(R.id.item_address);

        edit_gps.setOnClickListener(this);
        edit_plan_time.setOnClickListener(this);
        RoleBean bean = ((BaseActivity) getActivity()).getApp().getUser();
        addMark(bean.latitude, bean.longitude);

        return v;
    }

    public void addMark(double latitude, double longitude) {
        BaiduMap map = mMapView.getMap();

        if (mark == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            mark = BitmapFactory.decodeResource(getResources(), R.drawable.tab_main_01, options);
        }

        if (mOverlay != null) {
            mOverlay.remove();
        }

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(mark);

        LatLng myPosition = BaiduAPI.convert(latitude, longitude);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(myPosition);

        map.setMapStatus(update);
        mOverlay = map.addOverlay(new MarkerOptions()
                .position(myPosition)
                .icon(icon));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_gps: {
                ContextUtil.toast_debug("click");
                if (!BaiduAPI.isGpsEnabled(getActivity())) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        getActivity().startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                    }
                } else {
                    BaiduAPI.locate(new BaiduAPI.Callback() {
                        @Override
                        public void onLocated(BDLocation bdLocation) {
                            addMark(bdLocation.getLatitude(), bdLocation.getLongitude());
                        }
                    });
                }
                break;
            }
            case R.id.item_plan_time: {
                ((BaseActivity) getActivity()).onClick(v);
                break;
            }
        }


    }

}
