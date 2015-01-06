package com.finger.activity.artist.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.finger.R;
import com.finger.activity.AddImageActivity;
import com.finger.activity.BaseActivity;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;
import com.sp.lib.widget.AddImageItem;

import org.json.JSONObject;

/**
 * 修改简历
 */
public class ChangeResume extends AddImageActivity {
    public static final String EXTRA_SHORT_TEXT = "short_text";
    public static final String EXTRA_SHORT_IMAGE = "short_image";
    EditText edit_resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_resume);
        edit_resume = (EditText) findViewById(R.id.edit_resume);
        MAX_IMAGE=3;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                String resume = edit_resume.getText().toString();
                RequestParams params=new RequestParams();
                params.put("content",resume);
                FingerHttpClient.post("updateResume", params, new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject o) {

                    }
                });

                break;
        }
        super.onClick(v);

    }
}
