package com.finger.activity.info;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.plan.OrderConfirm;
import com.finger.activity.plan.PlanActivity;
import com.finger.entity.ArtistRole;
import com.finger.entity.NailInfoBean;
import com.finger.entity.OrderBean;
import com.finger.entity.OrderManager;
import com.finger.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.DialogUtil;
import com.finger.support.util.JsonUtil;
import com.finger.support.util.ItemUtil;
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
    CheckBox cb_collect;

    public static class SellerInfoBean extends ArtistRole{
        /**
         * 应该使用uid，不要使用id
         */
        public int uid;
    }

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
        cb_collect = (CheckBox) findViewById(R.id.collect);
        cb_collect.setOnClickListener(this);
        TextPainUtil.addDeleteLine(tv_shop_price);
        getData();
    }

    void getData() {
        RequestParams params = new RequestParams();
        params.put("product_id", getIntent().getIntExtra("id", -1));
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

                    @Override
                    public void onFail(JSONObject o) {
                        DialogUtil.showNetFail(NailInfo.this).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                onBackPressed();
                            }
                        });
                    }
                }
        );
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
        ImageManager.loadImage(bean.cover, cover);
        if (!TextUtils.isEmpty(bean.cover))
            cover.setLayoutParams(new LinearLayout.LayoutParams(ItemUtil.halfScreen * 2, ItemUtil.halfScreen * 2));
        cb_collect.setChecked(bean.collection_id != 0);

        SellerInfoBean seller_info = bean.seller_info;
        setArtistZGS(seller_info.professional, seller_info.talk, seller_info.on_time);
        ratingWidget.setScore(seller_info.score);
        tv_artist_name.setText(seller_info.username);

        ImageManager.loadImage(seller_info.avatar, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                iv_avatar.setImageBitmap(ImageUtil.roundBitmap(loadedImage, getResources().getDimensionPixelSize(R.dimen.avatar_size)));
            }
        });
        RoleBean role=getApp().getUser();
        if (role instanceof ArtistRole&&seller_info.uid==role.id){
            findViewById(R.id.choose_nail).setVisibility(View.INVISIBLE);
        }else{
            findViewById(R.id.choose_nail).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_nail: {
                OrderBean bean = OrderManager.getCurrentOrder();
                if (bean == null) {
                    bean = OrderManager.createOrder();
                    bean.nailInfoBean = this.bean;
                    startActivity(new Intent(this, PlanActivity.class));
                    finish();
                } else {
                    bean.nailInfoBean = this.bean;
                    startActivity(new Intent(this, OrderConfirm.class));
                }
                break;
            }
            case R.id.collect: {
                //注意：CheckBox先改变状态，再触发onClick
                if (bean == null) return;
                if (!cb_collect.isChecked()) {
                    cancel(bean.collection_id);
                } else {
                    addCollection(bean.id);
                }
                break;
            }
        }
        super.onClick(v);
    }

    /**
     * @param id 作品的id
     */
    void addCollection(int id) {
        RequestParams params = new RequestParams();
        params.put("product_id", id);
        params.put("uid", id);
        FingerHttpClient.post("addCollection", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    bean.collection_id = o.getInt("data");
                    ContextUtil.toast(getString(R.string.collect_ok));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(JSONObject o) {
                cb_collect.setChecked(false);
            }
        });
    }

    /**
     * @param id 作品的collection_id
     */
    void cancel(int id) {
        RequestParams params = new RequestParams();
        params.put("collection_id", id);
        FingerHttpClient.post("cancelCollection", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                ContextUtil.toast(getString(R.string.cancel_ok));
            }

            @Override
            public void onFail(JSONObject o) {
                cb_collect.setChecked(true);
            }
        });
    }
}
