package com.finger.support.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finger.R;
import com.finger.support.util.Logger;

public class ArtistItem extends LinearLayout {

    String imageUrl;
    String avatarUrl;
    String title;
    String price;
    String name;

    int drawableId;
    int srcId;
    int imageSize;
    int stars;
    TextView tv_title;
    TextView tv_price;
    TextView tv_name;
    ImageView iv;
    ImageView avatar;
    RatingWidget rating;

    public ArtistItem(Context context) {
        super(context);
        init(context, null);
    }

    public ArtistItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public ArtistItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArtistItem);
        title = a.getString(R.styleable.ArtistItem_artist_title);
        price = a.getString(R.styleable.ArtistItem_artist_price);
        name = a.getString(R.styleable.ArtistItem_artist_name);
        srcId = a.getResourceId(R.styleable.ArtistItem_artist_src, 0);
        drawableId = a.getResourceId(R.styleable.ArtistItem_artist_rating_drawable, 0);
        imageSize = a.getDimensionPixelSize(R.styleable.ArtistItem_artist_imageSize, 0);
        stars = a.getInt(R.styleable.ArtistItem_artist_rating_stars, 0);
        a.recycle();

        View v = inflate(context, R.layout.artist_item, null);
        addView(v);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tv_title = (TextView) v.findViewById(R.id.tv_title);
        tv_price = (TextView) v.findViewById(R.id.tv_price);
        tv_name = (TextView) v.findViewById(R.id.tv_name);

        iv = (ImageView) v.findViewById(R.id.im_nail);
        avatar = (ImageView) v.findViewById(R.id.avatar);

        rating = (RatingWidget) v.findViewById(R.id.rating);

        if (imageSize != 0) {
            iv.getLayoutParams().width = imageSize;
            iv.getLayoutParams().height = imageSize;
        }

        iv.setImageResource(srcId);

        setTitle(title);
        setPrice(price);
        setName(name);
        rating.setNum_star(stars);
        rating.setStarRecourseId(drawableId);
        Logger.i_format("title:%s  price:%s drawable:%s", title, price, Integer.toHexString(drawableId));
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        throw new UnsupportedOperationException("setOnClickListener is not support,artist item has a default OnClickListener");
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
        if (iv != null) {
            iv.getLayoutParams().width = imageSize;
            iv.getLayoutParams().height = imageSize;
        }
    }

    public RatingWidget getRating() {
        return rating;
    }

    public void setRating(RatingWidget rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (tv_title != null) {
            tv_title.setText(title);
        }
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        if (tv_price != null) {
            tv_price.setText(price);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (tv_name != null) {
            tv_name.setText(name);
        }
    }
}
