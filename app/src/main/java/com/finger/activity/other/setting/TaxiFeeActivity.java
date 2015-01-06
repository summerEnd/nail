package com.finger.activity.other.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.DialogUtil;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class TaxiFeeActivity extends BaseActivity {

    ArrayList<TaxiCostBean> beans = new ArrayList<TaxiCostBean>();

    class TaxiCostBean {
        String begin;
        String end;
        String price;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_fee);
        getTaxiCost();
    }

    void getTaxiCost() {
        RequestParams params = new RequestParams();
        FingerHttpClient.post("getTaxiCost", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), TaxiCostBean.class, beans);
                    showData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(JSONObject o) {
                super.onFail(o);
                DialogUtil.alert(TaxiFeeActivity.this, getString(R.string.get_taxi_failed))
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
            }
        });
    }

    void showData() {
        TextView[] tvs = new TextView[5];
        tvs[0] = (TextView) findViewById(R.id.tv_fee_0_5);
        tvs[1] = (TextView) findViewById(R.id.tv_fee_5_10);
        tvs[2] = (TextView) findViewById(R.id.tv_fee_10_15);
        tvs[3] = (TextView) findViewById(R.id.tv_fee_15_20);
        tvs[4] = (TextView) findViewById(R.id.tv_fee_20_);
        for (int i = 0; i < tvs.length; i++) {
            tvs[i].setText(beans.get(i).price);
        }
    }
}
