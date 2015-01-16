package com.finger.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.finger.BuildConfig;
import com.finger.activity.FingerApp;
import com.finger.activity.info.Artist;
import com.finger.entity.ArtistRole;
import com.finger.entity.RoleBean;
import com.finger.support.Constant;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.util.Logger;
import com.loopj.android.http.RequestParams;
import com.sp.lib.support.SHttpClient;
import com.sp.lib.support.WebJsonHttpHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileDescriptor;

/**
 * 定位服务
 */
public class LocationService extends Service {

    private       LocationBinder mBinder;
    public        LocationClient mLocationClient;
    public static BDLocation     mBDLocation;
    private       Callback       mCallback;


    @Override
    public IBinder onBind(Intent intent) {
        debug("onBind");
        if (mBinder == null) {
            mBinder = new LocationBinder();
        }

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        debug("onStartCommand");
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        debug("onUnbind");
        return true;

    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        debug("onRebind");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        debug("onCreate");
        mLocationClient = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//        option.setScanSpan(10 * 1000 * 60);//定位时间间隔
        option.setTimeOut(10000);
        FingerLocationListener l = new FingerLocationListener();
        mLocationClient.registerLocationListener(l);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        debug("onDestroy");
    }


    void debug(String msg) {
        Logger.e("---<>", msg);
    }

    public class LocationBinder extends Binder {
        public BDLocation getLocation() {
            return mBDLocation;
        }

        /**
         * 更新美甲师的位置
         */
        public void updateArtistPosition(int mid) {

            if (mBDLocation != null) {
                updatePosition(mid, mBDLocation.getLatitude(), mBDLocation.getLongitude());
            } else {
                updatePosition(mid, 0, 0);
            }
        }

        public void requestLocation(Callback callback) {
            mCallback = callback;
            mLocationClient.requestLocation();
        }

    }

    /**
     * 服务一启动就自动定位
     */
    public static class LocationConnection implements ServiceConnection, Callback {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof LocationBinder) {
                LocationBinder mBinder = (LocationBinder) service;
                mBinder.requestLocation(this);
            } else {
                throw new IllegalStateException(getClass() + " is only support Service which onBinder() return a LocationBinder");
            }
        }

        /**
         * 定位后回调这个方法
         *
         * @param location
         */
        public void onLocated(BDLocation location) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    public interface Callback {
        public void onLocated(BDLocation bdLocation);
    }

    private class FingerLocationListener implements BDLocationListener {


        public void onReceiveLocation(BDLocation location) {
            mBDLocation = location;
            if (mCallback != null) {
                mCallback.onLocated(location);
            }
            RoleBean user = FingerApp.getInstance().getUser();
            user.latitude = location.getLatitude();
            user.longitude = location.getLongitude();

            if (user instanceof ArtistRole) {
                updatePosition(user.id, location.getLatitude(), location.getLongitude());
            }
            debug(location);
        }

        void debug(final BDLocation location) {
            Logger.debug(new Runnable() {
                @Override
                public void run() {
                    if (location == null)
                        return;

                    StringBuffer sb = new StringBuffer();
                    sb.append("cityCode : ")
                            .append(location.getCityCode())
                            .append("\ntime : ")
                            .append(location.getTime())
                            .append("\nlatitude : ")
                            .append(location.getLatitude())
                            .append("\nlontitude : ")
                            .append(location.getLongitude());
                    Logger.d("--->" + sb.toString());
                }
            });
        }
    }

    /**
     * 更新美甲师的位置
     *
     * @param latitude
     * @param longitude
     */
    public void updatePosition(int mid, double latitude, double longitude) {


        RequestParams params = new RequestParams();
        params.put("longitude", latitude);
        params.put("latitude", latitude);
        params.put("mid", mid);

        WebJsonHttpHandler handler = new WebJsonHttpHandler() {
            @Override
            public void onSuccess(JSONObject object, JSONArray array) {

            }

            @Override
            public void onFail(String msg) {

            }
        };
        handler.showDialog = false;
        SHttpClient.post(FingerHttpClient.host + "updatePosition", params, handler, BuildConfig.DEBUG);


    }

}
