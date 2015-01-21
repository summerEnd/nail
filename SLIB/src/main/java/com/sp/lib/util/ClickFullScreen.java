package com.sp.lib.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ClickFullScreen extends PopupWindow {
    ImageView mImageView;
    View      container;
    String imageUrl;
    public ClickFullScreen(Context context) {
        super(context);
        container = View.inflate(context, R.layout.image_layout, null);
        mImageView = (ImageView) container.findViewById(R.id.image);
        setContentView(container);
        setWidth(MATCH_PARENT);
        setHeight(MATCH_PARENT);
        setFocusable(true);
    }

    public void setNetWorkImage(String url){
        this.imageUrl=url;
    }

    public void setImageResource(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setImageURI(Uri uri) {
        mImageView.setImageURI(uri);
    }

    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    public void setImageBitmap(Bitmap bm) {
        mImageView.setImageBitmap(bm);
    }

    /**
     * PS:
     *
     * @param anchor 弹出位置
     */
    public void showFor(View anchor) {

        //设置背景透明
        setBackgroundDrawable(new ColorDrawable(0));

        //获取anchor在屏幕中的位置
        int location[] = new int[2];
        anchor.getLocationOnScreen(location);
        Rect rect = new Rect(anchor.getLeft(), anchor.getTop(), anchor.getRight(), anchor.getBottom());
        showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0);
        //计算动画参数
        Point p = new Point();
        DisplayUtil.getScreenSize((Activity) anchor.getContext(), p);
        float fromX = anchor.getWidth() / (p.x * 1f);
        float toX = 1f;
        float fromY = anchor.getHeight() / (p.y * 1f);
        float toY = 1f;

        float pivotX = rect.width() / 2 + location[0];
        float pivotY = rect.height() / 2 + location[1];

        //去除anchor缓存
        anchor.setDrawingCacheEnabled(true);
        Bitmap bitmap=Bitmap.createBitmap(anchor.getDrawingCache());
        anchor.setDrawingCacheEnabled(false);

        //根据bitmap宽高比计算mImageView所占空间
        int imageWidth=bitmap.getWidth();
        int imageHeight=bitmap.getHeight();

        //图像宽度满屏，高度自适应
        float scale=p.x/(imageWidth*1.0f);
        int height= (int) (imageHeight*scale);
        mImageView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,height));
        mImageView.setImageBitmap(bitmap);

        Log.i("tag", String.format("fromX:%f toX:%f fromY:%f toY:%f", fromX, toX, fromY, toY));

        ScaleAnimation animation = new ScaleAnimation(fromX, toX, fromY, toY, pivotX, pivotY);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!TextUtils.isEmpty(imageUrl)) {

                    ImageManager.loadImage(imageUrl, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (loadedImage == null) {
                                return;
                            }
                            container.setBackgroundColor(0xff000000);
                            mImageView.setImageBitmap(loadedImage);
                        }
                    }, new FadeInBitmapDisplayer(1500));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        container.startAnimation(animation);
    }

}
