package com.finger.activity.artist.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.support.util.ContextUtil;
import com.sp.lib.activity.PhotoAlbumActivity;
import com.sp.lib.widget.AddImageItem;

import java.io.IOException;

public class PublishNailActivity extends BaseActivity {
    AddImageItem iv_add_image;
    EditText editTitle, editPrice, edit_time_cost, edit_time_keep, edit_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_nail);
        iv_add_image = (AddImageItem) findViewById(R.id.add_image);
        editTitle = (EditText) findViewById(R.id.edit_title);
        editPrice = (EditText) findViewById(R.id.edit_price);
        edit_time_cost = (EditText) findViewById(R.id.edit_time_cost);
        edit_time_keep = (EditText) findViewById(R.id.edit_time_keep);
        edit_detail = (EditText) findViewById(R.id.edit_detail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish: {
                ContextUtil.toast_debug("publish");
                break;
            }
        }
        super.onClick(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        iv_add_image.onActivityResult(requestCode, resultCode, data);
    }
}
