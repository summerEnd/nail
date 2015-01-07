package com.finger.support.api;

import android.content.Context;
import android.location.LocationManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.finger.support.util.Logger;

/**
 * Created by acer on 2014/12/8.
 */
public class BaiduAPI {

    public static LocationClient mLocationClient;
    private static L l;
    private static Callback mCallback;
    public static BDLocation mBDLocation;

    /**
     * 定位的时间间隔
     */
    public static final int scanSpan = 10 * 1000 * 60;

    public interface Callback {
        public void onLocated(BDLocation bdLocation);
    }

    public static void init(Context context) {

        SDKInitializer.initialize(context);

        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(scanSpan);
        mLocationClient = new LocationClient(context);
        l = new L();
        mLocationClient.registerLocationListener(l);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

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

    public static boolean isGpsEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static String getCityCode() {

        if (mBDLocation != null) {
            return mBDLocation.getCityCode();
        } else {
            return null;
        }
    }


    /**
     * 计算两点间的距离
     *
     * @param l1
     * @param l2
     * @return
     */
    public static final Double getDistance(LatLng l1, LatLng l2) {
        Double distance = DistanceUtil.getDistance(l1, l2);
        return Math.floor(distance);
    }


    public static final void locate(Callback callback) {
        mCallback = callback;
        mLocationClient.requestLocation();
    }

    static class L implements BDLocationListener {


        public void onReceiveLocation(BDLocation location) {
            mBDLocation = location;
            debug(location);

            if (mCallback != null) mCallback.onLocated(location);
        }


        public void onReceivePoi(BDLocation bdLocation) {

        }

        void debug(final BDLocation location) {
            Logger.debug(new Runnable() {
                @Override
                public void run() {
                    if (location == null)
                        return;

                    StringBuffer sb = new StringBuffer(256);
                    sb.append("cityCode : ");
                    sb.append(location.getCityCode());
                    sb.append("\ntime : ");
                    sb.append(location.getTime());
                    sb.append("\nerror code : ");
                    sb.append(location.getLocType());
                    sb.append("\nlatitude : ");
                    sb.append(location.getLatitude());
                    sb.append("\nlontitude : ");
                    sb.append(location.getLongitude());
                    sb.append("\nradius : ");
                    sb.append(location.getRadius());
                    if (location.getLocType() == BDLocation.TypeGpsLocation) {
                        sb.append("\nspeed : ");
                        sb.append(location.getSpeed());
                        sb.append("\nsatellite : ");
                        sb.append(location.getSatelliteNumber());
                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                        sb.append("\naddr : ");
                        sb.append(location.getAddrStr());
                    }
                    Logger.d("--->" + sb.toString());
                }
            });
        }
    }
}
