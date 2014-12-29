package com.finger.activity.user.order;

import android.content.Intent;
import android.os.Bundle;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.sp.lib.widget.AddImageItem;

public class ApplyRefund extends BaseActivity {
    AddImageItem addImageItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_refund);
        addImageItem= (AddImageItem) findViewById(R.id.addImage);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addImageItem.onActivityResult(requestCode,resultCode,data);
    }
}
