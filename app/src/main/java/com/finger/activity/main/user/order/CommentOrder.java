package com.finger.activity.main.user.order;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.finger.R;
import com.finger.activity.base.AddImageActivity;
import com.finger.activity.base.BaseActivity;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class CommentOrder extends AddImageActivity implements RadioGroup.OnCheckedChangeListener {


    class CommentBean {
        public String product_id;
        public String order_id;
        public String grade;

        public String mid;
    }

    RadioGroup comment_group;

    RatingBar ratingBar_pro;
    RatingBar ratingBar_talk;
    RatingBar ratingBar_time;
    EditText  content;

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
        float pro = ratingBar_pro.getRating();
        float talk = ratingBar_talk.getRating();
        float time = ratingBar_time.getRating();
        RequestParams params = new RequestParams();
        params.put("professional", pro);
        params.put("talk", talk);
        params.put("ontime", time);
        params.put("content", content.getText().toString());
        params.put("", "");
        FingerHttpClient.post("addComment", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_good:
                break;
            case R.id.rb_normal:
                break;
            case R.id.rb_bad:
                break;
        }
    }
}
