package com.finger.activity.main.user.order;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.AddImageActivity;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class ApplyRefund extends AddImageActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_refund);
        TextView tv_refund_money = (TextView) findViewById(R.id.tv_refund_money);
        tv_refund_money.setText(getString(R.string.s_price, "998"));
        MAX_IMAGE = 1;
    }

    public void applyRefund(String reason, String imageUrl) {
        RequestParams params = new RequestParams();
        params.put("reason", reason);
        params.put("imageUrl", imageUrl);
        FingerHttpClient.post("applyRefund", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit: {
                TextView edit_refund_reason = (EditText) findViewById(R.id.edit_refund_reason);
                applyRefund(edit_refund_reason.getText().toString(), image_url.size() > 0 ? image_url.get(0) : null);
                break;
            }
        }
        super.onClick(v);
    }
}
