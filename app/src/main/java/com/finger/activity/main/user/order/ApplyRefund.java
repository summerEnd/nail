package com.finger.activity.main.user.order;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.AddImageActivity;
import com.finger.entity.OrderListBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.DialogUtil;
import com.loopj.android.http.RequestParams;

import junit.framework.Assert;

import org.json.JSONObject;

/**
 * 申请退款
 */
public class ApplyRefund extends AddImageActivity {

    OrderListBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_refund);
        bean= (OrderListBean) getIntent().getSerializableExtra("bean");
        Assert.assertNotNull(bean);
        TextView tv_refund_money = (TextView) findViewById(R.id.tv_refund_money);
        tv_refund_money.setText(getString(R.string.s_price,bean.order_price));
        MAX_IMAGE = 2;
    }

    public void applyRefund(String reason, String imageUrl) {
        RequestParams params = new RequestParams();
        params.put("reason", reason);
        params.put("imageUrl", imageUrl);
        params.put("order_id", bean.id);
        FingerHttpClient.post("applyRefund", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                DialogUtil.alert(ApplyRefund.this, getString(R.string.commit_ok)).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
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
