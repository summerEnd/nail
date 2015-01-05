package com.finger.activity.other.common;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.support.Constant;
import com.finger.support.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.DialogUtil;
import com.finger.support.util.Logger;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.activity.PhotoAlbumActivity;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ChangeMyData extends BaseActivity {
    EditText edit_nick;
    TextView edit_phone;
    ImageView iv_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chage_my_data);
        edit_nick = (EditText) findViewById(R.id.edit_nick);
        edit_phone = (TextView) findViewById(R.id.edit_phone);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);

        setUserData();
    }

    void setUserData() {
        RoleBean bean = getApp().getUser();
        //设置头像
        loadAvatar(bean.avatar);
        iv_avatar.setContentDescription(bean.avatar);
        edit_nick.setText(bean.username);
        edit_phone.setText(bean.mobile);
        ((TextView) findViewById(R.id.tv_nick_name)).setText(bean.username);
    }

    void loadAvatar(String uri) {
        ImageManager.loadImage(uri, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage == null) {
                    loadedImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_user);
                }
                setImage(loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.default_user);
                setImage(avatar);
            }

            void setImage(Bitmap bitmap) {
                int radius = getResources().getDimensionPixelSize(R.dimen.avatar_center_size) / 2;
                iv_avatar.setImageBitmap(ImageUtil.roundBitmap(bitmap, radius));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit: {
                updateInfo();
                break;
            }
            case R.id.iv_avatar: {

                startActivityForResult(
                        new Intent(this, PhotoAlbumActivity.class)
                                .putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_HEIGHT, 90)
                                .putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_WIDTH, 90)
                        , 100);
                break;
            }
        }
        super.onClick(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) return;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            if (bitmap == null) {
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
                        String avatar = o.getString("data");
                        iv_avatar.setContentDescription(avatar);
                        loadAvatar(avatar);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateInfo() {
        RequestParams params = new RequestParams();
        final String username = edit_nick.getText().toString();
        final String avatar = (String) iv_avatar.getContentDescription();
        final RoleBean user = getApp().getUser();

        params.put("username", username);
        params.put("avatar", avatar);
        params.put("uid", user.id);
        params.put("type", user.getType());

        FingerHttpClient.post("updateBaseInfo", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                user.username = username;
                ContextUtil.toast(getString(R.string.modify_ok));
                if (Constant.LOGIN_TYPE_ARTIST.equals(user.getType())) {
                    DialogUtil.alert(ChangeMyData.this, getString(R.string.artist_avatar_upload));
                } else {
                    user.avatar = avatar;
                }
                setUserData();

            }
        });
    }
}
