package com.finger;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.finger.support.util.Logger;
import com.sp.lib.activity.DEBUGActivity;

import com.finger.support.api.BaiduAPI;

import static com.baidu.mapapi.utils.CoordinateConverter.CoordType;


public class MyActivity extends Activity {
    LocationClient mLocationClient;

    TextView text;
    EditText latitude, longitude;
    LatLng myLatLng;
    RoutePlanSearch mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mLocationClient = BaiduAPI.mLocationClient;
        text = (TextView) findViewById(R.id.text);
        latitude = (EditText) findViewById(R.id.editText2);
        longitude = (EditText) findViewById(R.id.editText3);
        mLocationClient.registerLocationListener(l);
        mLocationClient.start();
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(listener);
    }

    BDLocationListener l = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordType.GPS);
            converter.coord(new LatLng(latitude, longitude));
            myLatLng = converter.convert();

            Logger.d(myLatLng + " ==== latitude:" + latitude + " longitude:" + longitude);

            text.setText(bdLocation.getCity() + " " + bdLocation.getAddrStr()
                    + " \nlatitude=" + latitude
                    + " \nlongitude=" + longitude
                    + " \n to(0,0)" + BaiduAPI.getDistance(myLatLng, new LatLng(latitude, longitude - 1)));

        }
    };

    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
            TextView tv = (TextView) findViewById(R.id.tv_walk);
            tv.setText(walkingRouteResult.getRouteLines().get(0).getDistance() + "m");
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            TextView tv = (TextView) findViewById(R.id.tv_bus);
            tv.setText(transitRouteResult.getRouteLines().get(0).getDistance() + "m");
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            TextView tv = (TextView) findViewById(R.id.tv_dri);
            tv.setText(drivingRouteResult.getRouteLines().get(0).getDistance() + "m");

        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button: {
//                double longitudeValue = Double.valueOf(longitude.getText().toString());
//                double latitudeValue = Double.valueOf(latitude.getText().toString());
                PlanNode stNode = PlanNode.withCityNameAndPlaceName("南京", longitude.getText().toString());
                PlanNode enNode = PlanNode.withCityNameAndPlaceName("南京", latitude.getText().toString());

                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));
//                text.setText(BaiduAPI.getDistanceStr(myLatLng, new LatLng(latitudeValue, longitudeValue)));
                break;
            }
            case R.id.button2: {
                Intent intent = new Intent(this, DEBUGActivity.class);
                startActivity(intent);
                break;
            }
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.unRegisterLocationListener(l);
        mLocationClient.stop();

    }
}
