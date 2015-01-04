package com.finger.activity.other.info;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.finger.activity.other.plan.NailItemListFragment;
import com.finger.support.entity.ArtistRole;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ArtistInfo extends BaseActivity {
    ArtistRole bean;
    ImageView iv_avatar;
    TextView name;
    TextView tv_average_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        name = (TextView) findViewById(R.id.name);
        tv_average_price = (TextView) findViewById(R.id.tv_average_price);

        NailItemListFragment nailItemListFragment = new NailItemListFragment();
        Bundle data=new Bundle();
        data.putInt("id",getIntent().getIntExtra("id",-1));
        nailItemListFragment.setArguments(data);
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, nailItemListFragment).commit();
        getData();
    }

    void getData() {
        RequestParams params = new RequestParams();
        params.put("mid", getIntent().getIntExtra("id",-1));
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

    void setData(ArtistRole bean) {
        ImageManager.loadImage(bean.avatar, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                iv_avatar.setImageBitmap(ImageUtil.roundBitmap(loadedImage, getResources().getDimensionPixelSize(R.dimen.avatar_size)));
            }
        });
        name.setText(bean.username);
        setArtistComment(bean.comment_good,bean.comment_normal,bean.comment_bad);
        tv_average_price.setText(getString(R.string.average_price_s, bean.average_price));
        setArtistZGS(bean.professional, bean.talk, bean.on_time);
    }
}
