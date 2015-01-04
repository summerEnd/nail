package com.finger.support.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.other.info.NailInfo;
import com.finger.support.entity.NailInfoBean;
import com.finger.support.util.Logger;
import com.sp.lib.util.ImageManager;

/**
 * Created by acer on 2014/12/15.
 */
public class NailItem extends LinearLayout {

    String imageUrl;
    String title;
    String price;
    int drawableId;
    int imageSize;
    TextView tv_title;
    TextView tv_price;
    ImageView iv;
    View contentView;
    NailInfoBean infoBean;

    public NailItem(Context context) {
        super(context);
        init(context, null);
    }

    public NailItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public NailItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    public NailInfoBean getInfoBean() {
        return infoBean;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int width_mode=MeasureSpec.getMode(widthMeasureSpec);
        if (width_mode==MeasureSpec.EXACTLY){
            imageSize=width;
            setMeasuredDimension(width,width);
            int childSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            getChildAt(0).measure(childSpec,childSpec);
        }else{
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }
    }


    /**
     * 设置一个NailInfoBean展示
     *
     * @param infoBean
     */
    public void setInfoBean(NailInfoBean infoBean) {
        this.infoBean = infoBean;
        setPrice(infoBean.price);
        setTitle(infoBean.name);
        ImageManager.loadImage(infoBean.cover, iv);
    }

    public void init(final Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NailItem);
        title = a.getString(R.styleable.NailItem_nail_title);
        price = a.getString(R.styleable.NailItem_nail_price);
        drawableId = a.getResourceId(R.styleable.NailItem_nail_src, 0);
        imageSize = a.getDimensionPixelSize(R.styleable.NailItem_nail_imageSize, 0);
        a.recycle();
        contentView = inflate(context, R.layout.nail_item, null);
        addView(contentView);

        tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        tv_price = (TextView) contentView.findViewById(R.id.tv_price);
        iv = (ImageView) contentView.findViewById(R.id.im_nail);
        if (imageSize != 0) {
            iv.getLayoutParams().width = imageSize;
            iv.getLayoutParams().height = imageSize;
        }

        iv.setImageResource(drawableId);

        setTitle(title);
        setPrice(price);
        contentView.setOnClickListener(defaultListener);
    }

    private OnClickListener defaultListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = 0;
            if (infoBean != null)
                id = infoBean.id;
            Context context = v.getContext();

            context.startActivity(new Intent(context, NailInfo.class).putExtra("id", id));
        }
    };

    public void setExtraOnClickListener(OnClickListener l) {
        if (l != null) {
            contentView.setOnClickListener(l);
        } else {
            contentView.setOnClickListener(defaultListener);
        }
    }

    public View getContentView() {
        return contentView;
    }


    public ImageView getImage() {
        return iv;
    }

    public String getTitle() {
        return title;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
        iv.getLayoutParams().width = imageSize;
        iv.getLayoutParams().height = imageSize;
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


}
