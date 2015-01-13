package com.finger.activity.main.artist.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.setting.ChangeMyData;
import com.finger.activity.main.AttentionList;
import com.finger.activity.main.MyCollectionActivity;

public class MyInfoActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_my_info);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.plan_time:{
                Intent intent=new Intent(this, PlanTimeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.change_data:{
                Intent intent=new Intent(this, ChangeMyData.class);
                startActivity(intent);
                break;
            }
            case R.id.my_resume:{
                Intent intent=new Intent(this, MyResumeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.my_collection:{
                Intent intent=new Intent(this, MyCollectionActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.my_attention:{
                Intent intent=new Intent(this, AttentionList.class);
                startActivity(intent);
                break;
            }
        }

        super.onClick(v);
    }
}
