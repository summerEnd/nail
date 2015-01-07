package com.finger.activity.base;

import android.content.Intent;
import android.graphics.Bitmap;

import com.finger.R;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.Logger;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ImageUtil;
import com.sp.lib.widget.AddImageItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class AddImageActivity extends BaseActivity implements AddImageItem.Callback {
    protected AddImageItem addImageItem;
    protected LinkedList<String> image_url=new LinkedList<String>();
    /**
     * 最大图片数量
     */
    protected int MAX_IMAGE=1;
    @Override
    public void onAdd(Bitmap bitmap) {
        if (bitmap==null){
            Logger.e("bitmap null!");
            return;
        }
        String image = ImageUtil.base64Encode(bitmap);
        RequestParams params = new RequestParams();
        params.put("image", image);
        FingerHttpClient.post("uploadImage", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    image_url.add(o.getString("data"));
                    if (image_url.size()>=MAX_IMAGE){
                        addImageItem.stopAdd();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        addImageItem = (AddImageItem) findViewById(R.id.add_image);
        addImageItem.setCallback(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addImageItem.onActivityResult(requestCode, resultCode, data);
    }
}
