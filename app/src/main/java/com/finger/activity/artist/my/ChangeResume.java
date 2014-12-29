package com.finger.activity.artist.my;

import android.content.Intent;
import android.os.Bundle;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.sp.lib.widget.AddImageItem;

public class ChangeResume extends BaseActivity {
    AddImageItem imageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_resume);
        imageItem = (AddImageItem) findViewById(R.id.add_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageItem.onActivityResult(requestCode, resultCode, data);
    }
}
