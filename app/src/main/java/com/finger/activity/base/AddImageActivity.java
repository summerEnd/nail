package com.finger.activity.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import com.finger.R;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.Logger;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ImageUtil;
import com.sp.lib.widget.AddImageItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;


/**
 * 封装了一个AddImageItem,需要在布局文件中加入，并且id为R.id.add_image。
 * 每当添加图片时，自动调用“uploadImage”接口上传图片，并将返回的url放在LinkedList<String> image_url中
 */
public class AddImageActivity extends BaseActivity implements AddImageItem.Callback {
    protected AddImageItem addImageItem;
    protected LinkedList<String> image_url = new LinkedList<String>();
    /**
     * 最大图片数量
     */
    protected int                MAX_IMAGE = 1;

    //上传失败的图片
    Bitmap failed_bitmap;

    @Override
    public void onAdd(Bitmap bitmap) {
        if (bitmap == null) {
            Logger.e("bitmap null!");
            return;
        }
        failed_bitmap=bitmap;
        String image = ImageUtil.base64Encode(bitmap);
        RequestParams params = new RequestParams();
        params.put("image", image);
        FingerHttpClient.post("uploadImage", params, new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject o) {
                        failed_bitmap=null;
                        try {
                            image_url.add(o.getString("data"));
                            if (image_url.size() >= MAX_IMAGE) {
                                addImageItem.stopAdd();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(JSONObject o) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddImageActivity.this);
                        builder.setTitle(R.string.warn);
                        builder.setMessage(R.string.upload_failed);
                        builder.setPositiveButton(getString(R.string.re_upload), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    onAdd(failed_bitmap);
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    addImageItem.removeImage(image_url.size());
                            }
                        });
                        builder.show();
                    }
                }
        );
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
