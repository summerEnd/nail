package com.finger.activity.other.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.activity.other.plan.OrderConfirm;
import com.finger.activity.other.plan.PlanActivity;
import com.finger.support.entity.OrderBean;
import com.finger.support.entity.OrderManager;
import com.finger.support.widget.RatingWidget;
import com.sp.lib.util.TextPainUtil;


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

    public static class NailInfoBean {
        String price;
        String shop_price;
        String time_cost;
        String time_keep;
        String artist_name;
        String info_title;
        String info_text;
        int star_num;
        int star_level;
        int comment_num;
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
        setArtistZGS(3, 3, 3);
        TextPainUtil.addDeleteLine(tv_shop_price);
        getData();
    }

    void getData() {
        bean = new NailInfoBean();
        bean.shop_price = "123123";
        bean.price = "12";
        bean.artist_name = "奥巴马";
        bean.time_cost = "两个礼拜";
        bean.time_keep = "20000个礼拜";
        bean.info_text="锄禾日当午，锄禾日当午，锄禾日当午，锄禾日当午，锄禾日当午，锄禾日当午，锄禾日当午，";
        bean.info_title="超级指甲";
        bean.comment_num=1231;
        setData(bean);
    }

    public void setData(NailInfoBean bean) {
        if (bean == null) return;
        tv_price.setText(getString(R.string.rmb_s, bean.price));
        tv_shop_price.setText(getString(R.string.rmb_s, bean.shop_price));
        tv_time_cost.setText(getString(R.string.time_cost_s, bean.time_cost));
        tv_time_keep.setText(getString(R.string.time_keep_s, bean.time_keep));
        tv_artist_name.setText(bean.artist_name);
        tv_info_title.setText(bean.info_title);
        tv_info_text.setText(bean.info_title);
        ratingWidget.setNum_star(bean.star_num);
        ratingWidget.setStarRecourseId(R.drawable.star2);
        tv_comment_num.setText(bean.comment_num+"");
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
