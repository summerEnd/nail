package com.finger.activity.plan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.finger.activity.FingerApp;
import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.activity.login.LoginActivity;
import com.finger.entity.AddressSearchBean;
import com.finger.api.BaiduAPI;
import com.finger.entity.OrderBean;
import com.finger.entity.OrderManager;
import com.finger.entity.RoleBean;
import com.finger.support.widget.EditItem;

import static android.app.Activity.RESULT_OK;
import static com.finger.activity.plan.PlanActivity.PlanFragment;

public class PlanForMe extends PlanFragment implements View.OnClickListener {
    MapView           mMapView;
    EditItem          edit_address;
    Bitmap            mark;
    Overlay           mOverlay;
    EditItem          edit_plan_time;
    AddressSearchBean addressSearchBean;

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        if (mMapView != null)
            mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        if (mMapView != null)
            mMapView.onPause();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plan_for_me, null);
        mMapView = (MapView) v.findViewById(R.id.map);
        edit_plan_time = (EditItem) v.findViewById(R.id.item_plan_time_for_me);
        edit_plan_time.setOnClickListener(this);
        edit_address = (EditItem) v.findViewById(R.id.item_address);
        edit_address.setOnClickListener(this);
        v.findViewById(R.id.choose_nail_artist).setOnClickListener(this);
        RoleBean bean = ((BaseActivity) getActivity()).getApp().getUser();
        addMark(bean.latitude, bean.longitude);

        return v;
    }

    public void addMark(double latitude, double longitude) {
        BaiduMap map = mMapView.getMap();

        if (mark == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            mark = BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_mark, options);
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
            case R.id.item_address: {
                startActivityForResult(new Intent(getActivity(), SearchAddress.class), 101);
                break;
            }

            case R.id.choose_nail_artist: {

                if (FingerApp.getInstance().isLogin()) {
                    OrderBean bean = OrderManager.getCurrentOrder();
                    if (bean == null) {
                        bean = OrderManager.createOrder();
                    }
                    RoleBean role = ((FingerApp) getActivity().getApplication()).getUser();
                    bean.mobile = role.mobile;
                    bean.contact = role.username;
                    bean.planTime = planTime;
                    bean.time_block = time_block;
                    bean.book_date = book_date;
                    bean.address =edit_address.getContent();
                    bean.addressSearchBean = addressSearchBean;
                    submit();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }

                break;
            }

            case R.id.item_plan_time_for_me: {
                ((PlanActivity) getActivity()).getPlanTime(new Schedule.Callback() {
                    @Override
                    public void onSelect(String book_date, int time_block) {
                        planTime = getPlanTimeStr(book_date, time_block);
                        edit_plan_time.setContent(planTime);
                    }
                });
                break;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }
        addressSearchBean = (AddressSearchBean) data.getSerializableExtra("bean");
        String address = addressSearchBean == null ? "" : addressSearchBean.address+addressSearchBean.name;
        edit_address.setContent(address);

    }
}
