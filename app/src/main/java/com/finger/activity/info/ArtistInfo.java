package com.finger.activity.info;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.login.LoginActivity;
import com.finger.activity.main.artist.my.MyResumeActivity;
import com.finger.entity.ArtistRole;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.DialogUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.RatingWidget;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 美甲师详细信息的展示
 */
public class ArtistInfo extends BaseActivity {
    ArtistRole bean;
    ImageView  iv_avatar;
    TextView   name;
    TextView   tv_average_price;
    CheckBox   attention;
    RatingWidget rating;
    public static final String EXTRA_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        iv_avatar.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
        tv_average_price = (TextView) findViewById(R.id.tv_average_price);
        attention = (CheckBox) findViewById(R.id.attention);
        rating = (RatingWidget) findViewById(R.id.rating);
        attention.setOnClickListener(this);
        NailInfoListFragment nailInfoListFragment = new NailInfoListFragment();
        Bundle data = new Bundle();
        data.putInt(EXTRA_ID, getIntent().getIntExtra(EXTRA_ID, -1));
        nailInfoListFragment.setArguments(data);
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, nailInfoListFragment).commit();
        getSellerDetail();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attention: {
                //注意：CheckBox先改变状态，再触发onClick

                if(doLoginIfNeed()){
                    attention.setChecked(false);
                    return;
                }


                if (!attention.isChecked()) {
                    cancel(bean.attention_id);
                } else {
                    addAttention(bean.mid);
                }
                break;
            }
            case R.id.iv_avatar: {
                startActivity(new Intent(this, MyResumeActivity.class).putExtra("bean", bean));
                break;
            }
        }
        super.onClick(v);
    }

    /**
     * 获取美甲师详情
     */
    void getSellerDetail() {
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

            @Override
            public void onFail(JSONObject o) {
                DialogUtil.showNetFail(ArtistInfo.this).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
            }
        });
    }

    /**
     * 设置展示信息
     *
     * @param bean
     */
    void setData(ArtistRole bean) {
        ImageManager.loadImage(bean.avatar, iv_avatar,ContextUtil.getAvatarOptions());
        name.setText(bean.username);

        setArtistComment(bean);
        tv_average_price.setText(getString(R.string.average_price_s, bean.average_price));
        setArtistZGS(bean.professional, bean.talk, bean.on_time);
        attention.setChecked(bean.attention_id > 0);
        rating.setScore(bean.score);
    }

    /**
     * @param id 作品的id
     */
    void addAttention(int id) {
        RequestParams params = new RequestParams();
        params.put("mid", id);
        FingerHttpClient.post("addAttention", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    bean.attention_id = o.getInt("data");
                    ContextUtil.toast(getString(R.string.attention_ok));
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
        params.put("attention_id", id);
        FingerHttpClient.post("cancelAttention", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                ContextUtil.toast(R.string.cancel_ok);
            }

            @Override
            public void onFail(JSONObject o) {
                attention.setChecked(true);
            }
        });
    }
}
