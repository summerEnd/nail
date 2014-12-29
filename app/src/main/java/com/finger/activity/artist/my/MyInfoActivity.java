package com.finger.activity.artist.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.finger.activity.other.common.ChangeMyData;
import com.finger.activity.other.common.MyCareActivity;
import com.finger.activity.other.common.MyCollectionActivity;

/**
 * Created by acer on 2014/12/29.
 */
public class MyInfoActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_my_info);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                Intent intent=new Intent(this, MyCareActivity.class);
                startActivity(intent);
                break;
            }
        }

        super.onClick(v);
    }
}
