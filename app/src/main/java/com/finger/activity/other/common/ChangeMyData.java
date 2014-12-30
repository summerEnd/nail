package com.finger.activity.other.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.support.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;
import com.sp.lib.util.ImageUtil;

import org.json.JSONObject;

public class ChangeMyData extends BaseActivity{
    EditText edit_nick;
    EditText edit_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chage_my_data);
        edit_nick= (EditText) findViewById(R.id.edit_nick);
        edit_phone= (EditText) findViewById(R.id.edit_phone);
        setUserData();
    }

    void setUserData() {
        RoleBean bean=getApp().getUser();
        //设置头像
        ImageView iv_avatar= (ImageView) findViewById(R.id.iv_avatar);
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        int radius = getResources().getDimensionPixelSize(R.dimen.avatar_center_size) / 2;
        iv_avatar.setImageBitmap(ImageUtil.roundBitmap(avatar, radius));

        edit_nick.setText(bean.name);
        edit_phone.setText(bean.mobile);
        ((TextView) findViewById(R.id.tv_nick_name)).setText(bean.name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.commit:{
                RequestParams params=new RequestParams();
                params.put("",edit_nick.getText().toString());
                params.put("",edit_phone.getText().toString());
                FingerHttpClient.post("",params,new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject o) {

                    }
                });
                break;
            }
        }
        super.onClick(v);
    }
}
