package com.finger.activity.main.artist.my;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.finger.R;
import com.finger.activity.base.AddImageActivity;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.NailItemBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.DialogUtil;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.activity.PhotoAlbumActivity;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PublishNailActivity extends BaseActivity {
    EditText editTitle, editPrice, edit_time_cost, edit_time_keep, edit_detail, edit_store_price;
    public static final String EXTRA_BEAN = "bean";
    NailItemBean bean;
    ImageView    add_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_nail);
        editTitle = (EditText) findViewById(R.id.edit_title);
        editPrice = (EditText) findViewById(R.id.edit_price);
        edit_time_cost = (EditText) findViewById(R.id.edit_time_cost);
        edit_time_keep = (EditText) findViewById(R.id.edit_time_keep);
        edit_detail = (EditText) findViewById(R.id.edit_detail);
        edit_store_price = (EditText) findViewById(R.id.edit_store_price);
        add_image = (ImageView) findViewById(R.id.add_image);
        add_image.setOnClickListener(this);
        bean = (NailItemBean) getIntent().getSerializableExtra(EXTRA_BEAN);
        if (bean != null) {
            editTitle.setText(bean.name);
            editPrice.setText(bean.price);
            edit_store_price.setText(bean.store_price);
            edit_time_cost.setText(bean.spend_time);
            edit_time_keep.setText(bean.keep_date);
            edit_detail.setText(bean.description);
            add_image.setContentDescription(bean.cover);
            ImageManager.loadImage(bean.cover, add_image);
            setTitle(getString(R.string.modify_product));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish: {
                publish();
                break;
            }
            case R.id.add_image: {
                startActivityForResult(new Intent(this, PhotoAlbumActivity.class)
                                .putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_WIDTH, 90)
                                .putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_HEIGHT, 90)
                        , 112
                );
                break;
            }
        }
        super.onClick(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode && requestCode == 112) {
            Bitmap bitmap = null;
            try {
                // 读取uri所在的图片
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String image = ImageUtil.base64Encode(bitmap);
            RequestParams params = new RequestParams();
            params.put("image", image);
            FingerHttpClient.post("uploadImage", params, new FingerHttpHandler() {
                        @Override
                        public void onSuccess(JSONObject o) {
                            try {
                                String url = o.getString("data");
                                add_image.setContentDescription(url);
                                ImageManager.loadImage(url, add_image);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }
    }

    /**
     * 发布作品
     */
    void publish() {

        CharSequence imageUrl = add_image.getContentDescription();

        RequestParams params = new RequestParams();

        if (isTextEmpty(params, editTitle, "name")
                || isTextEmpty(params, editPrice, "price")
                || isTextEmpty(params, edit_store_price, "store_price")
                || isTextEmpty(params, edit_time_cost, "spend_time")
                || isTextEmpty(params, edit_time_keep, "keep_date")
                || isTextEmpty(params, edit_detail, "description")
                )
            return;
        if (TextUtils.isEmpty(imageUrl)) {
            ContextUtil.toast(getString(R.string.please_input_image));
            return;
        }
        if (bean != null) {
            params.put("product_id", bean.id);
        }
        params.put("cover", imageUrl);
        params.put("mid", getApp().getUser().id);


        FingerHttpClient.post("addProduct", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {

                String msg;
                if (bean == null) {
                    msg = getString(R.string.publish_ok);
                } else {
                    msg = getString(R.string.modify_ok);
                }
                ContextUtil.toast(msg);
                finish();

            }
        });
    }

    boolean isTextEmpty(RequestParams params, EditText edit_text, String params_key) {
        String input = edit_text.getText().toString();
        if (TextUtils.isEmpty(input)) {
            ContextUtil.toast(edit_text.getHint());
            return true;
        }

        if(input.length()>15){
            ContextUtil.toast("标题不能超过15字");
            return true;
        }


        params.put(params_key, input);
        return false;
    }

}
