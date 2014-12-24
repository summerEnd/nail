package com.finger.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.finger.R;
import com.finger.activity.other.info.NailListActivity;
import com.finger.activity.other.plan.ChooseArtist;
import com.finger.activity.other.plan.PlanActivity;
import com.finger.support.widget.ItemUtil;
import com.finger.support.util.Dimension;
import com.finger.support.widget.ArtistItem;
import com.finger.support.widget.NailItem;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by acer on 2014/12/10.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    RadioGroup dot_group;
    ViewPager switch_banner;
    ArrayList<ImageView> images = new ArrayList<ImageView>();
    int bannerIndex = 0;
    int artist_bottom_height;

    class ArtistBean {
        String title;
        String price;
        String imageUrl;
    }

    class NailBean {
        String title;
        String price;
        String imageUrl;
        String avatar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null);
        dot_group = (RadioGroup) v.findViewById(R.id.dot_group);
        switch_banner = (ViewPager) v.findViewById(R.id.switch_banner);
        v.findViewById(R.id.plan_nail_artist).setOnClickListener(this);
        v.findViewById(R.id.nail_prd).setOnClickListener(this);
        v.findViewById(R.id.choose_nail_artist).setOnClickListener(this);
        addBanner();
        addBanner();
        addBanner();
        switch_banner.setAdapter(new BannerAdapter());
        switch_banner.setOnPageChangeListener(pageChangeListener);
        dot_group.setOnCheckedChangeListener(dotMoveListener);
        startRunBanner();
        setItemSize(v);
        return v;
    }

    void setItemSize(View v) {


        artist_bottom_height = getResources().getDimensionPixelOffset(R.dimen.artist_item_bottom_height);
        setArtistHalfScreen(v, R.id.artist_00);
        setArtistHalfScreen(v, R.id.artist_01);
        setArtistHalfScreen(v, R.id.artist_02);
        setArtistHalfScreen(v, R.id.artist_03);
        setArtistHalfScreen(v, R.id.artist_04);
        setArtistHalfScreen(v, R.id.artist_05);

        setNailSizeHalfScreen(v, R.id.nail_00);
        setNailSizeHalfScreen(v, R.id.nail_01);
        setNailSizeHalfScreen(v, R.id.nail_02);
        setNailSizeHalfScreen(v, R.id.nail_03);
        setNailSizeHalfScreen(v, R.id.nail_04);
        setNailSizeHalfScreen(v, R.id.nail_05);
    }

    void setArtistHalfScreen(View v, int id) {
        ArtistItem item = (ArtistItem) v.findViewById(id);
        item.setImageSize(ItemUtil.item_size);

    }

    void setNailSizeHalfScreen(View v, int id) {
        NailItem item = (NailItem) v.findViewById(id);
        item.setImageSize(ItemUtil.item_size);
        item.setOnClickListener(this);
    }

    void addBanner() {
        Context context = getActivity();
        RadioButton rb = new RadioButton(context);
        rb.setBackgroundResource(R.drawable.home_dot);
        rb.setButtonDrawable(new ColorDrawable(0));
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(dp(4), dp(4));
        lp.setMargins(dp(3), dp(3), dp(3), dp(3));
        rb.setLayoutParams(lp);
        rb.setId(bannerIndex++);
        dot_group.addView(rb);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.banner_test);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        images.add(imageView);
    }

    int dp(float value) {
        return (int) Dimension.dp(value);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            getActivity().setTitle("首页");
        }

    }

    Timer timer;

    void startRunBanner() {
        if (images.size() == 0) return;
        bannerIndex = 0;
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                bannerIndex++;
                bannerIndex %= images.size();
                timeHandler.sendMessage(timeHandler.obtainMessage(0, bannerIndex, 0));

            }
        }, 0, 3000);

    }

    Handler timeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            showBanner(msg.arg1);
            return false;
        }
    });

    RadioGroup.OnCheckedChangeListener dotMoveListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            showBanner(checkedId);
        }
    };

    ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            showBanner(position);
        }
    };

    void showBanner(int index) {
        bannerIndex = index;
        dot_group.check(index);
        switch_banner.setCurrentItem(index);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.plan_nail_artist: {
                scale(v, new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), PlanActivity.class);
                        startActivity(intent);
                    }
                });

                break;
            }
            case R.id.nail_prd: {
                scale(v, new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), NailListActivity.class));
                    }
                });

                break;
            }
            case R.id.choose_nail_artist: {
                scale(v, new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), ChooseArtist.class));
                    }
                });

                break;
            }

            default: {
                if (v instanceof ArtistItem) {

                } else {

                }
            }
        }

    }

    void scale(View v, final Runnable runnable) {
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale_click);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                runnable.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(animation);
    }

    class BannerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(images.get(position));
            return images.get(position);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(images.get(position));
        }
    }
}
