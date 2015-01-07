package com.finger.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.finger.R;
import com.finger.activity.MainActivity;

import java.util.ArrayList;


public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    ViewPager pager;
    ArrayList<View> views = new ArrayList<View>();
    private float mSavedOffset = 0;
    private boolean scrollToEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);

        pager = new ViewPager(this);
        views.add(getImage(R.drawable.guide1));
        views.add(getImage(R.drawable.guide2));

        View guide3 = getLayoutInflater().inflate(R.layout.guide3, null);
        guide3.findViewById(R.id.guide_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        views.add(guide3);
        pager.setAdapter(new GuideAdapter());
        pager.setOnPageChangeListener(this);
        setContentView(pager);
    }


    /**
     * 获取每个导航页
     *
     * @param id 图片的资源ID
     * @return
     */
    private ImageView getImage(int id) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(id);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == views.size() - 1) {
            float delta = mSavedOffset - positionOffset;
            scrollToEnd = (delta == 0);
        }
        mSavedOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {

    }


    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE && pager.getCurrentItem() == pager.getChildCount() - 1 && scrollToEnd) {

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i).getId() == checkedId) {
                pager.setCurrentItem(i);
                break;
            }

        }
    }


    class GuideAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }
}
