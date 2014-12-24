package com.finger.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.finger.R;
import com.finger.support.util.Logger;

public class RatingWidget extends View {
    public static final int STAR_PADDING = 2;
    /**
     * 皇冠数量
     */
    private int starRecourseId;

    /**
     * 钻石数量
     */
    private int num_star;

    private Bitmap star;

    public RatingWidget(Context context) {
        this(context, null);
    }

    public RatingWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatingWidget);
        num_star = a.getInt(R.styleable.RatingWidget_stars, 0);
        starRecourseId = a.getResourceId(R.styleable.RatingWidget_drawable, 0);
    }

    public int getStarRecourseId() {
        return starRecourseId;
    }

    public void setStarRecourseId(int starRecourseId) {
        this.starRecourseId = starRecourseId;
        invalidateStars();
        invalidate();
    }

    public int getNum_star() {
        return num_star;
    }

    public void setNum_star(int num_star) {
        this.num_star = num_star;
        invalidateStars();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int horizontal_padding = getPaddingLeft() + getPaddingRight()+(num_star-1)*STAR_PADDING;
        int vertical_padding = getPaddingTop() + getPaddingBottom();

        int demandWidth;
        int demandHeight;
        if (star == null) {
            if (starRecourseId == 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), starRecourseId, options);
            demandWidth = options.outWidth * num_star + horizontal_padding;
            demandHeight = options.outHeight + vertical_padding;

        } else {
            demandWidth = star.getWidth() * num_star + horizontal_padding;
            demandHeight = star.getHeight() + vertical_padding;
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            width = demandWidth;
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            height = demandHeight;
        }

        setMeasuredDimension(width, height);
        invalidateStars();
    }

    void invalidateStars() {
        if (starRecourseId == 0) {
            star=null;
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), starRecourseId, options);
        int starHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        if (starHeight != 0) {
            float scale = options.outHeight / starHeight;
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) scale;
            star = BitmapFactory.decodeResource(getResources(), starRecourseId, options);
        }
    }

    public void onDraw(Canvas canvas) {
        Logger.d(star+" starRecourseId:"+Integer.toHexString(starRecourseId)+" num_star:"+num_star);
        if (star == null) {
            return;
        }
        int top=getHeight()/2-star.getHeight()/2;
        canvas.save();
        for (int i = 0; i < num_star; i++) {
            canvas.drawBitmap(star, getPaddingLeft(), getPaddingTop()+top, null);
            canvas.translate(star.getWidth()+ STAR_PADDING, 0);
        }
        canvas.restore();
    }


}
