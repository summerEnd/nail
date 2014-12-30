package com.finger.activity.artist.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.BaseActivity;

public class MyResumeActivity extends BaseActivity {
    View v;
    TextView tv_short_content;
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.modify:{
                startActivity(new Intent(this,ChangeResume.class).putExtra(ChangeResume.EXTRA_SHORT_TEXT,tv_short_content.getText().toString()));
                break;
            }
        }
        super.onClick(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_resume);
        v=findViewById(R.id.modify);
        tv_short_content= (TextView) findViewById(R.id.tv_short_content);
        setArtistZGS(4,3.1f,5);
        setArtistComment(1111,2222,3333);
    }
}
