package com.finger.activity.main;

import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.FingerApp;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.info.ArtistInfoList;
import com.finger.activity.info.NailListActivity;
import com.finger.activity.plan.PlanActivity;
import com.finger.activity.setting.BaseInfoActivity;
import com.finger.entity.AdsBean;
import com.finger.entity.ArtistRole;
import com.finger.entity.BaseInfo;
import com.finger.entity.CityBean;
import com.finger.entity.HotTagBean;
import com.finger.support.Constant;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.Dimension;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.ArtistItem;
import com.finger.support.widget.SearchWindow;
import com.loopj.android.http.RequestParams;
import com.sp.lib.anim.ActivityAnimator;
import com.sp.lib.util.FileUtil;
import com.sp.lib.util.ImageManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainFragment extends Fragment implements View.OnClickListener {
    RadioGroup dot_group;
    ViewPager  switch_banner;
    ArrayList<ImageView> images      = new ArrayList<ImageView>();
    int                  bannerIndex = 0;
    View     layout;
    TextView title_city;

    ArrayList<AdsBean> ads = new ArrayList<AdsBean>();

    /**
     * 调接口获取首页数据
     */
    void requestIndexData() {
        RequestParams params = new RequestParams();
        FingerHttpClient.post("getIndex", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    //解析json
                    ArrayList<HotTagBean> tags = new ArrayList<HotTagBean>();

                    ArrayList<CityBean> cities = new ArrayList<CityBean>();
                    JSONObject data = o.getJSONObject("data");
                    JSONArray json_cityList = data.getJSONArray("city_list");
                    JSONArray json_tags = data.getJSONArray("hot_tags");
                    JSONArray json_ads = data.getJSONArray("ads_list");
                    JsonUtil.getArray(json_cityList, CityBean.class, cities);
                    JsonUtil.getArray(json_tags, HotTagBean.class, tags);
                    JsonUtil.getArray(json_ads, AdsBean.class, ads);

                    //将获取的城市列表写入文件
                    FileUtil.saveFile(getActivity(), Constant.FILE_CITIES, cities);
                    //将热门标签写入文件
                    FileUtil.saveFile(getActivity(), Constant.FILE_TAGS, tags);

                    //设置定位城市
                    setLocationCity(cities);

                    //讲banner添加
                    for (AdsBean bean : ads) {
                        addBanner(bean);
                    }
                    switch_banner.setAdapter(new BannerAdapter());
                    switch_banner.setOnPageChangeListener(pageChangeListener);
                    dot_group.setOnCheckedChangeListener(dotMoveListener);
                    //运行banner
                    startRunBanner();
                    getBaseInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 设置定位城市，如果定位失败就从城市列表中取一个
     */
    void setLocationCity(List<CityBean> cities) {
        FingerApp app = FingerApp.getInstance();
        String city_code;
        if (TextUtils.isEmpty(app.getCurCity().city_code)) {
            //没有默认取南京
            city_code = "315";

        } else {
            city_code = app.getCurCity().city_code;
        }
        CityBean curCity = getCityByCode(city_code);
        FingerApp.getInstance().setCurCity(curCity);
        setCityIfExists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, null);
        layout = v;
        dot_group = (RadioGroup) v.findViewById(R.id.dot_group);
        switch_banner = (ViewPager) v.findViewById(R.id.switch_banner);
        v.findViewById(R.id.plan_nail_artist).setOnClickListener(this);
        v.findViewById(R.id.nail_prd).setOnClickListener(this);
        v.findViewById(R.id.choose_nail_artist).setOnClickListener(this);
        v.findViewById(R.id.hot_things).setOnClickListener(this);
        v.findViewById(R.id.title_search).setOnClickListener(this);
        title_city = (TextView) v.findViewById(R.id.title_city);
        title_city.setOnClickListener(this);
        requestIndexData();
        return v;
    }

    /**
     * 获取基本信息
     */
    void getBaseInfo() {
        BaseInfo.reload(getActivity(), new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setCityIfExists();
    }


    /**
     * 设置城市
     */
    void setCityIfExists() {
        FingerApp app = ((BaseActivity) getActivity()).getApp();
        CityBean cityBean = app.getCurCity();
        //如果城市名不为空，代表定位成功
        if (!TextUtils.isEmpty(cityBean.name)) {
            title_city.setText(cityBean.name);
        }
    }

    /**
     * 将城市code转化为CityBean
     */
    CityBean getCityByCode(String cityCode) {
        ArrayList<CityBean> cities = (ArrayList<CityBean>) FileUtil.readFile(getActivity(), Constant.FILE_CITIES);

        if (cities == null) {
            return null;
        }

        CityBean mCity = null;
        for (CityBean bean : cities) {
            if (bean.city_code.equals(cityCode)) {
                mCity = bean;
                break;
            }
        }
        return mCity;
    }

    /**
     * 添加banner
     */
    void addBanner(AdsBean bean) {
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
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setTag(bean);
        imageView.setOnClickListener(mBannerClicker);
        ImageManager.loadImage(bean.cover, imageView);
        images.add(imageView);
    }

    /**
     * 广告条(banner)点击事件
     */
    View.OnClickListener mBannerClicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AdsBean bean = (AdsBean) v.getTag();
            startActivity(new Intent(getActivity(), AdsInfoActivity.class)
                            .putExtra(AdsInfoActivity.EXTRA_URL, bean.content)
            );
            ActivityAnimator.override(getActivity(), ActivityAnimator.IN_SLIDE_UP, ActivityAnimator.NO_ANIMATION);
        }
    };


    /**
     * 加dp单位
     *
     * @param value
     * @return
     */
    int dp(float value) {
        return (int) Dimension.dp(value);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
        }
    }

    Timer timer;

    /**
     * 让banner跑起来
     */
    void startRunBanner() {
        if (images.size() == 0)
            return;
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
                //美甲师不能预约
                if (FingerApp.getInstance().getUser() instanceof ArtistRole) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.warn);
                    builder.setMessage(getString(R.string.artist_cannot_plan));
                    builder.setPositiveButton(getString(R.string.know), null);
                    builder.show();
                    return;
                }

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
                        startActivity(new Intent(getActivity(), ArtistInfoList.class));
                    }
                });

                break;
            }
            case R.id.hot_things: {
                scale(v, new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), NewHotActivity.class));
                    }
                });

                break;
            }
            case R.id.title_search: {
                final View anchor = v;


                scale(v, new Runnable() {
                    @Override
                    public void run() {
                        SearchWindow searchWindow = new SearchWindow(getActivity());
                        searchWindow.show(anchor);
                    }
                });

                break;
            }
            case R.id.title_city: {

                scale(v, new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), LocationActivity.class));
                        ActivityAnimator.override(getActivity(), ActivityAnimator.IN_SLIDE_UP, ActivityAnimator.NO_ANIMATION);
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

    /**
     * 点击缩放
     *
     * @param v
     * @param runnable
     */
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
