package com.finger.activity.main.user.order;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.finger.R;
import com.finger.activity.base.AddImageActivity;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.info.CommentListActivity;
import com.finger.entity.OrderListBean;
import com.finger.support.Constant;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class CommentOrder extends AddImageActivity implements RadioGroup.OnCheckedChangeListener {


    class CommentBean {
        public String product_id;

    }

    RadioGroup comment_group;

    RatingBar ratingBar_pro;
    RatingBar ratingBar_talk;
    RatingBar ratingBar_time;
    EditText  content;
    /**
     * 1好评，0中评，-1差评
     */
    String grade = Constant.COMMENT_GOOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_order);
        comment_group = (RadioGroup) findViewById(R.id.comment_group);
        ratingBar_pro = (RatingBar) findViewById(R.id.ratingBar_pro);
        ratingBar_talk = (RatingBar) findViewById(R.id.ratingBar_talk);
        ratingBar_time = (RatingBar) findViewById(R.id.ratingBar_time);
        content = (EditText) findViewById(R.id.content);
        comment_group.setOnCheckedChangeListener(this);
        MAX_IMAGE = 1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                addComment();
                break;
        }
        super.onClick(v);
    }

    public void addComment() {
        final OrderListBean bean = (OrderListBean) getIntent().getSerializableExtra("bean");
        if (bean == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.order_null))
                    .setPositiveButton("ues", null).show();
            return;
        }
        float pro = ratingBar_pro.getRating();
        float talk = ratingBar_talk.getRating();
        float time = ratingBar_time.getRating();
        RequestParams params = new RequestParams();
        params.put("product_id", bean.product_id);
        params.put("mid", bean.mid);
        params.put("grade", grade);
        params.put("professional", pro);
        params.put("talk", talk);
        params.put("ontime", time);
        params.put("content", content.getText().toString());
        params.put("image", image_url.size() > 0 ? image_url.get(0) : null);
        FingerHttpClient.post("addComment", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                startActivity(
                        new Intent(CommentOrder.this, CommentListActivity.class)
                                .putExtra(CommentListActivity.EXTRA_PRODUCT_ID, bean.product_id)
                                .putExtra(CommentListActivity.EXTRA_COMMENT_TYPE, grade)
                                .putExtra(CommentListActivity.EXTRA_MID, bean.mid)
                );
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_good:
                grade = Constant.COMMENT_GOOD;
                break;
            case R.id.rb_normal:
                grade = Constant.COMMENT_MID;
                break;
            case R.id.rb_bad:
                grade = Constant.COMMENT_BAD;
                break;
        }
    }
}
