package com.sp.lib.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sp.lib.R;
import com.sp.lib.activity.PhotoAlbumActivity;
import com.sp.lib.util.DisplayUtil;

/**
 * Created by acer on 2014/12/26.
 */
public class AddImageItem extends HorizontalScrollView {

    /**
     * 图片的宽度 dp
     */
    private int next_image_width = 0;
    /**
     * 图片的高度dp
     */
    private int next_image_height = 0;
    private final int REQUEST_CODE = 7715;
    LinearLayout layout;
    ImageView add;

    public AddImageItem(Context context) {
        this(context, null);
    }

    public AddImageItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddImageItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        layout = new LinearLayout(context);
        addView(layout);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        add = new ImageView(context);
        int width= (int) DisplayUtil.dp(100,getResources());
        int height= (int) DisplayUtil.dp(100,getResources());
        add.setLayoutParams(new ViewGroup.LayoutParams(width,height));
        add.setScaleType(ImageView.ScaleType.CENTER_CROP);
        add.setImageResource(R.drawable.iv_add_image);
        add.setOnClickListener(iv_add_click_listener);
        layout.addView(add);
    }

    public int getNext_image_width() {
        return next_image_width;
    }

    public void setNext_image_width(int next_image_width) {
        this.next_image_width = next_image_width;
    }

    public int getNext_image_height() {
        return next_image_height;
    }

    public void setNext_image_height(int next_image_height) {
        this.next_image_height = next_image_height;
    }

    private OnClickListener iv_add_click_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Activity context = (Activity) getContext();
            context.startActivityForResult(
                    new Intent(context, PhotoAlbumActivity.class)
                            .putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_HEIGHT, 80)
                            .putExtra(PhotoAlbumActivity.EXTRA_CAMERA_OUTPUT_WIDTH, 80)
                    , REQUEST_CODE);

        }
    };

    private ImageView addImage(Context context, Uri uri) {
        ImageView imageView = new ImageView(context);
        imageView.setImageURI(uri);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(add.getWidth(),add.getHeight()));
        imageView.setPadding(4,4,4,4);
        layout.addView(imageView, 0);
        return imageView;
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE != requestCode) return;

        if (Activity.RESULT_OK == resultCode) {
            Toast.makeText(getContext(), "ok", Toast.LENGTH_SHORT).show();
            addImage(getContext(), data.getData());

        } else {
            Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();
        }
    }
}
