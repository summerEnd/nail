package com.finger.activity.artist.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.finger.activity.AddImageActivity;
import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.loopj.android.http.RequestParams;
import com.sp.lib.activity.PhotoAlbumActivity;
import com.sp.lib.widget.AddImageItem;

import org.json.JSONObject;

import java.io.IOException;

public class PublishNailActivity extends AddImageActivity {
    EditText editTitle, editPrice, edit_time_cost, edit_time_keep, edit_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_nail);
        editTitle = (EditText) findViewById(R.id.edit_title);
        editPrice = (EditText) findViewById(R.id.edit_price);
        edit_time_cost = (EditText) findViewById(R.id.edit_time_cost);
        edit_time_keep = (EditText) findViewById(R.id.edit_time_keep);
        edit_detail = (EditText) findViewById(R.id.edit_detail);
        MAX_IMAGE = 1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        addImageItem.setNext_image_height(200);
        addImageItem.setNext_image_width(200);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish: {
                publish();
                break;
            }
        }
        super.onClick(v);
    }

    void publish() {

        if (image_url.size() == 0) {
            ContextUtil.toast(getString(R.string.please_input_image));
            return;
        }
        RequestParams params = new RequestParams();

        if (isTextEmpty(params, editTitle, "name")
                || isTextEmpty(params, editPrice, "price")
                || isTextEmpty(params, edit_time_cost, "spend_time")
                || isTextEmpty(params, edit_time_keep, "keep_date")
                || isTextEmpty(params, edit_detail, "description")
                ) return;

        params.put("cover", image_url.get(0));
        params.put("mid", getApp().getUser().id);


        FingerHttpClient.post("addProduct", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {

            }
        });
    }

    boolean isTextEmpty(RequestParams params, EditText edit_text, String params_key) {
        String input = edit_text.getText().toString();
        if (TextUtils.isEmpty(input)) {
            ContextUtil.toast(edit_text.getHint());
            return true;
        }
        params.put(params_key, input);
        return false;
    }

}
