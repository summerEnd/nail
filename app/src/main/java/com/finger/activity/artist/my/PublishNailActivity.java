package com.finger.activity.artist.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.sp.lib.activity.PhotoAlbumActivity;

import java.io.IOException;

/**
 * Created by acer on 2014/12/25.
 */
public class PublishNailActivity extends BaseActivity {
    ImageView iv_add_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_nail);
        iv_add_image = (ImageView) findViewById(R.id.add_image);
        iv_add_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_image: {
                Intent intent = new Intent(this, PhotoAlbumActivity.class);
                intent.putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_HEIGHT, 40);
                intent.putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_WIDTH, 40);
                startActivityForResult(intent, 1000);
                break;
            }
        }
        super.onClick(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) {
            return;
        }

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            iv_add_image.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
