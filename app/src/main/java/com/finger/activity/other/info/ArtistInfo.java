package com.finger.activity.other.info;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.finger.activity.artist.my.MyResumeActivity;
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
    CheckBox attention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        iv_avatar.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
        tv_average_price = (TextView) findViewById(R.id.tv_average_price);
        attention = (CheckBox) findViewById(R.id.attention);
        attention.setOnClickListener(this);
        NailItemListFragment nailItemListFragment = new NailItemListFragment();
        Bundle data = new Bundle();
        data.putInt("id", getIntent().getIntExtra("id", -1));
        nailItemListFragment.setArguments(data);
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, nailItemListFragment).commit();
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attention: {
                //注意：CheckBox先改变状态，再触发onClick
                if (!attention.isChecked()) {
                    cancel(bean.attention_id);
                } else {
                    addAttention(bean.id);
                }
                break;
            }
            case R.id.iv_avatar: {
                startActivity(new Intent(this, MyResumeActivity.class).putExtra("bean",bean));
                break;
            }
        }
        super.onClick(v);
    }

    void getData() {
        RequestParams params = new RequestParams();
        params.put("mid", getIntent().getIntExtra("id", -1));
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
        setArtistComment(bean.comment_good, bean.comment_normal, bean.comment_bad);
        tv_average_price.setText(getString(R.string.average_price_s, bean.average_price));
        setArtistZGS(bean.professional, bean.talk, bean.on_time);
    }

    /**
     * @param id 作品的id
     */
    void addAttention(int id) {
        RequestParams params = new RequestParams();
        params.put("product_id", id);
        FingerHttpClient.post("", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    bean.collection_id = o.getInt("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(JSONObject o) {
                attention.setChecked(false);
            }
        });
    }

    /**
     * @param id 作品的collection_id
     */
    void cancel(int id) {
        RequestParams params = new RequestParams();
        params.put("collection_id", id);
        FingerHttpClient.post("", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {

            }

            @Override
            public void onFail(JSONObject o) {
                attention.setChecked(true);
            }
        });
    }
}
