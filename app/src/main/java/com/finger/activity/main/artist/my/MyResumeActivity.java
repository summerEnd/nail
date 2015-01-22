package com.finger.activity.main.artist.my;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.ArtistRole;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ItemUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.RatingWidget;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.util.ClickFullScreen;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyResumeActivity extends BaseActivity {
    TextView     tv_short_content;
    TextView     tv_nick_name;
    TextView     tv_order_num;
    ImageView    iv_avatar;
    LinearLayout resume_images;
    ArtistRole   bean;
    RatingWidget rating;

    @Override
    public void onClick(View v) {

        //        switch (v.getId()) {
        //            case R.id.modify: {
        //                //                startActivity(new Intent(this, ChangeResume.class).putExtra(ChangeResume.EXTRA_SHORT_TEXT, tv_short_content.getText().toString()));
        //                AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //                builder.setMessage(getString(R.string.update_resume_notice));
        //                builder.setTitle(R.string.warn);
        //                builder.setPositiveButton(R.string.yes, null);
        //                builder.show();
        //                break;
        //            }
        //        }
        super.onClick(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_resume);
        tv_short_content = (TextView) findViewById(R.id.tv_short_content);
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
        tv_order_num = (TextView) findViewById(R.id.tv_order_num);
        resume_images = (LinearLayout) findViewById(R.id.resume_images);
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
                    JSONObject data = o.getJSONObject("data");
                    bean = JsonUtil.get(data.toString(), ArtistRole.class);

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

        ArrayList<String> alum = bean.album;
        int imageSIze = ItemUtil.halfScreen - 20;
        if (alum != null && alum.size() > 0) {
            for (String url : alum) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSIze, imageSIze);
                params.setMargins(4, 4, 4, 4);
                imageView.setLayoutParams(params);
                imageView.setTag(url);
                imageView.setOnClickListener(onImageClick);
                ImageManager.loadImage(url, imageView);
                resume_images.addView(imageView);
            }
        }

        setArtistZGS(bean.professional, bean.talk, bean.on_time);
        setArtistComment(bean);
        rating.setScore(bean.score);
        tv_nick_name.setText(bean.username);
        tv_order_num.setText(getString(R.string.order_d_num, bean.total_num));
        tv_short_content.setText(bean.resume);
    }

    View.OnClickListener onImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ClickFullScreen clickFullScreen = new ClickFullScreen(MyResumeActivity.this);
            clickFullScreen.setNetWorkImage(v.getTag().toString());
            clickFullScreen.showFor(v);
        }
    };

}
