package com.finger.support.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.finger.R;
import com.finger.activity.info.HonorInfoActivity;
import com.finger.support.Constant;
import com.finger.entity.ArtistGrade;
import com.finger.support.util.Logger;
import com.sp.lib.util.FileUtil;

import java.util.LinkedList;

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

    private int score;


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
        setOnClickListener(defaultListener);
    }

    private OnClickListener defaultListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            context.startActivity(new Intent(context, HonorInfoActivity.class));
        }
    };

    public int getScore() {
        return score;
    }

    public void setScore(int score) {

        LinkedList<ArtistGrade> grades = (LinkedList<ArtistGrade>) FileUtil.readFile(getContext(), Constant.FILE_ARTIST_GRADE);
        int max = grades.getLast().to;
        score = Math.min(max, score);
        this.score = score;
        int grade = 0;
        for (ArtistGrade artistGrade : grades) {
            if (artistGrade.contains(score)) {
                grade = artistGrade.value;
                break;
            }
        }

        int level = grade / 5 + 1;
        num_star = grade % 5;

        if (grade % 5 == 0) {
            level -= 1;
            num_star = 5;
        }

        switch (level) {
            case 1: {
                starRecourseId = R.drawable.star1;
                break;
            }

            case 2: {
                starRecourseId = R.drawable.star2;
                break;
            }

            case 3: {
                starRecourseId = R.drawable.star3;
                break;
            }

            case 4: {
                starRecourseId = R.drawable.star4;
                break;
            }

            case 5: {
                starRecourseId = R.drawable.star5;
                break;
            }

            default: {
                starRecourseId = R.drawable.star1;
                break;
            }

        }
        invalidateStars();
        requestLayout();
        invalidate();
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

        int starPadding=0;
        if (num_star > 1) {
            starPadding = (num_star - 1) * STAR_PADDING;
        }


        int horizontal_padding = getPaddingLeft() + getPaddingRight() + starPadding;
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
            star = null;
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
        Logger.d(star + " starRecourseId:" + Integer.toHexString(starRecourseId) + " num_star:" + num_star);
        if (star == null) {
            return;
        }
        int top = getHeight() / 2 - star.getHeight() / 2;
        canvas.save();
        for (int i = 0; i < num_star; i++) {
            canvas.drawBitmap(star, getPaddingLeft(), getPaddingTop() + top, null);
            canvas.translate(star.getWidth() + STAR_PADDING, 0);
        }
        canvas.restore();
    }


}
