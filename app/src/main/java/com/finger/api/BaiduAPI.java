package com.finger.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.IBinder;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.finger.service.LocationService;

public class BaiduAPI {

    /**
     * 转化成百度坐标消除误差
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public static LatLng convert(double latitude, double longitude) {
        return convert(new LatLng(latitude, longitude));
    }

    /**
     * 转化成百度坐标消除误差
     *
     * @param latLng
     * @return
     */
    public static LatLng convert(LatLng latLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        converter.coord(latLng);
        return converter.convert();
    }

    /**
     * 判断GPS是否打开
     *
     * @param context
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
