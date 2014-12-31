package com.finger.activity.other.info;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.finger.activity.other.plan.OrderConfirm;
import com.finger.activity.other.plan.PlanActivity;
import com.finger.support.entity.ArtistInfoBean;
import com.finger.support.entity.NailInfoBean;
import com.finger.support.entity.OrderBean;
import com.finger.support.entity.OrderManager;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.RatingWidget;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;
import com.sp.lib.util.TextPainUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class NailInfo extends BaseActivity {


    NailInfoBean bean;
    TextView tv_shop_price;
    TextView tv_time_keep;
    TextView tv_time_cost;
    TextView tv_price;
    TextView tv_artist_name;
    TextView tv_info_text;
    TextView tv_info_title;
    TextView tv_comment_num;
    RatingWidget ratingWidget;
    ImageView cover;
    ImageView iv_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nail_info);
        tv_shop_price = (TextView) findViewById(R.id.tv_shop_price);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_time_cost = (TextView) findViewById(R.id.tv_time_cost);
        tv_time_keep = (TextView) findViewById(R.id.tv_time_keep);
        tv_artist_name = (TextView) findViewById(R.id.tv_artist_name);
        tv_info_text = (TextView) findViewById(R.id.tv_info_text);
        tv_info_title = (TextView) findViewById(R.id.tv_info_title);
        tv_comment_num = (TextView) findViewById(R.id.tv_comment_num);
        ratingWidget = (RatingWidget) findViewById(R.id.rating);
        cover = (ImageView) findViewById(R.id.cover);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        TextPainUtil.addDeleteLine(tv_shop_price);
        getData();
    }

    void getData() {
        RequestParams params = new RequestParams();
        params.put("product_id", getIntent().getIntExtra("id",-1));
        FingerHttpClient.post("getProductDetail", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    bean = JsonUtil.get(o.getString("data"), NailInfoBean.class);
                    setData(bean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setData(NailInfoBean bean) {
        if (bean == null) return;
        tv_price.setText(getString(R.string.rmb_s, bean.price));
        tv_shop_price.setText(getString(R.string.rmb_s, bean.store_price));
        tv_time_cost.setText(getString(R.string.time_cost_s, bean.spend_time));
        tv_time_keep.setText(getString(R.string.time_keep_s, bean.keep_date));
        tv_info_title.setText(bean.name);
        tv_info_text.setText(bean.description);
        tv_comment_num.setText(getString(R.string.d_num, bean.comment_num));
        ImageManager.loadImage(bean.cover,cover);

        ArtistInfoBean artist=bean.seller_info;
        setArtistZGS(artist.professional,artist.talk,artist.on_time);
        setStars(ratingWidget,artist.score);
        tv_artist_name.setText(artist.username);

        ImageManager.loadImage(artist.avatar,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                iv_avatar.setImageBitmap(ImageUtil.roundBitmap(loadedImage,getResources().getDimensionPixelSize(R.dimen.avatar_size)));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_nail: {
                OrderBean bean = OrderManager.getCurrentOrder();
                if (bean == null) {
                    bean = OrderManager.createOrder();
                    bean.artist_id = 1;
                    bean.nail_id = 1;
                    startActivity(new Intent(this, PlanActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, OrderConfirm.class));
                }
                break;
            }
        }
        super.onClick(v);
    }




}
