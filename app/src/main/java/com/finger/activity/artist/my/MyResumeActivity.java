package com.finger.activity.artist.my;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.finger.support.entity.ArtistRole;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.RatingWidget;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MyResumeActivity extends BaseActivity {
    View v;
    TextView tv_short_content;
    TextView tv_nick_name;
    TextView tv_order_num;
    ImageView iv_avatar;
    HorizontalScrollView resume_images;
    ArtistRole bean;
    RatingWidget rating;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.modify: {
//                startActivity(new Intent(this, ChangeResume.class).putExtra(ChangeResume.EXTRA_SHORT_TEXT, tv_short_content.getText().toString()));
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.update_resume_notice));
                builder.show();
                break;
            }
        }
        super.onClick(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_resume);
        v = findViewById(R.id.modify);
        tv_short_content = (TextView) findViewById(R.id.tv_short_content);
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
        tv_order_num = (TextView) findViewById(R.id.tv_order_num);
        resume_images = (HorizontalScrollView) findViewById(R.id.resume_images);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        rating = (RatingWidget) findViewById(R.id.rating);

        ArtistRole role = (ArtistRole) getIntent().getSerializableExtra("bean");

        if (role == null) {
            getSellerInfo();
        } else {
            setData(role);
        }
    }

    void getSellerInfo() {
        RequestParams params = new RequestParams();
        params.put("mid", getApp().getUser().id);
        FingerHttpClient.post("getSellerDetail", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    bean = JsonUtil.get(o.getString("data"), ArtistRole.class);
                    setData(bean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setData(ArtistRole bean) {
        //设置头像
        ImageManager.loadImage(bean.avatar, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage == null) {
                    iv_avatar.setImageResource(R.drawable.default_user);
                    return;
                }
                int radius = getResources().getDimensionPixelSize(R.dimen.avatar_center_size) / 2;
                iv_avatar.setImageBitmap(ImageUtil.roundBitmap(loadedImage, radius));
            }
        });

        setArtistZGS(bean.professional, bean.talk, bean.on_time);
        setArtistComment(bean.comment_good, bean.comment_normal, bean.comment_bad);
        rating.setScore(bean.score);
        tv_nick_name.setText(bean.username);
//        tv_order_num.setText(getString(R.string.order_d_num,bean.));
//        tv_short_content.setText(bean.);
    }

}
