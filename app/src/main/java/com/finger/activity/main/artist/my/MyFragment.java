package com.finger.activity.main.artist.my;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.FingerApp;
import com.finger.activity.base.BaseActivity;
import com.finger.activity.info.Artist;
import com.finger.activity.main.AttentionList;
import com.finger.activity.main.MyCollectionActivity;
import com.finger.activity.main.user.my.MyDiscountActivity;
import com.finger.activity.setting.SettingActivity;
import com.finger.adapter.NailListAdapter;
import com.finger.entity.ArtistRole;
import com.finger.entity.NailInfoBean;
import com.finger.entity.RoleBean;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.widget.RatingWidget;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;

@Artist
public class MyFragment extends Fragment implements View.OnClickListener, ListController.Callback {
    ArtistRole   role;
    RatingWidget rating;
    TextView     settings;
    ImageView    iv_avatar;
    GridView     gridView;
    TextView     tv_on_time;
    TextView     tv_com;
    TextView     tv_pro;
    TextView     tv_nick_name;
    TextView     tv_good_comment;
    TextView     tv_mid_comment;
    TextView     tv_bad_comment;
    LinkedList<NailInfoBean> beans = new LinkedList<NailInfoBean>();
    ListController  controller;
    NailListAdapter adapter;
    //没有发布作品通知
    boolean has_no_product_noticed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_center_artist, null);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.personal_center);

        tv_on_time = (TextView) v.findViewById(R.id.tv_on_time);
        tv_com = (TextView) v.findViewById(R.id.tv_com);
        tv_pro = (TextView) v.findViewById(R.id.tv_pro);
        tv_nick_name = (TextView) v.findViewById(R.id.tv_nick_name);

        tv_good_comment = (TextView) v.findViewById(R.id.tv_good_comment);
        tv_mid_comment = (TextView) v.findViewById(R.id.tv_mid_comment);
        tv_bad_comment = (TextView) v.findViewById(R.id.tv_bad_comment);

        rating = (RatingWidget) v.findViewById(R.id.rating);
        gridView = (GridView) v.findViewById(R.id.grid);


        adapter = new NailListAdapter(getActivity(), beans);
        gridView.setAdapter(adapter);
        controller = new ListController(gridView, this);
        findIds(v);
        initialUserData();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initialUserData();
    }

    /**
     * 初始化用户数据
     */
    void initialUserData() {
        RoleBean bean = FingerApp.getInstance().getUser();
        if (bean instanceof ArtistRole) {
            role = (ArtistRole) bean;
            displayArtistInfo(role);
            beans.clear();
            adapter.notifyDataSetChanged();
            getProductList(1);
        }
    }

    /**
     * 展示美甲师信息
     *
     * @param bean
     */
    void displayArtistInfo(ArtistRole bean) {
        //设置头像
        ImageManager.loadImage(bean.avatar, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage == null) {
                    iv_avatar.setImageResource(R.drawable.default_user);
                    return;
                }
                int radius = getResources().getDimensionPixelSize(R.dimen.avatar_center_size) / 2;
                iv_avatar.setImageBitmap(ImageUtil.roundBitmap(loadedImage, radius));
            }
        });

        setArtistZGS(bean.professional, bean.talk, bean.on_time);
        rating.setScore(bean.score);
        tv_nick_name.setText(bean.username);
    }

    void getProductList(int page) {
        FingerApp app = ((BaseActivity) getActivity()).getApp();
        ArtistRole role = (ArtistRole) app.getUser();
        RequestParams params = new RequestParams();
        JSONObject condition = new JSONObject();
        try {
            condition.put("mid", role.id);// (美甲师id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            params.put("condition", URLEncoder.encode(condition.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        params.put("page", page);
        params.put("pagesize", controller.getPageSize());
        FingerHttpClient.post("getProductList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {

                    JSONObject data = o.getJSONObject("data");
                    JsonUtil.getArray(data.getJSONArray("normal"), NailInfoBean.class, beans);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    //提示还没有发布作品
                    if (beans.size() == 0 && !has_no_product_noticed) {
                        new AlertDialog.Builder(getActivity()).setTitle(R.string.warn)
                                .setMessage(getString(R.string.product_null))
                                .setPositiveButton(getString(R.string.publish_now), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(getActivity(), PublishNailActivity.class));
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .show();
                    }
                    has_no_product_noticed = true;
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置专业沟通守时
     *
     * @param pro    专业 对应TextView 的id为tv_pro
     * @param com    沟通 对应TextView 的id为tv_com
     * @param onTime 守时 对应TextView 的id为tv_on_time
     */
    public void setArtistZGS(float pro, float com, float onTime) {
        tv_pro.setText(String.format("%.1f", pro));
        tv_com.setText(String.format("%.1f", com));
        tv_on_time.setText(String.format("%.1f", onTime));
    }


    void findIds(View v) {
        settings = (TextView) v.findViewById(R.id.settings);
        v.findViewById(R.id.title_manage).setOnClickListener(this);
        iv_avatar = (ImageView) v.findViewById(R.id.iv_avatar);
        settings.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            role = (ArtistRole) FingerApp.getInstance().getUser();
            displayArtistInfo(role);
        }
    }

    @Override
    public void onClick(View v) {
        {
            Context context = v.getContext();
            switch (v.getId()) {
                case R.id.settings: {
                    Intent intent = new Intent(context, SettingActivity.class);
                    context.startActivity(intent);
                    break;
                }
                case R.id.my_attention: {
                    Intent intent = new Intent(context, AttentionList.class);
                    context.startActivity(intent);
                    break;
                }
                case R.id.my_collection: {
                    Intent intent = new Intent(context, MyCollectionActivity.class);
                    context.startActivity(intent);
                    break;
                }
                case R.id.my_discount_card: {
                    Intent intent = new Intent(context, MyDiscountActivity.class);
                    context.startActivity(intent);
                    break;
                }
                case R.id.title_manage: {
                    Intent intent = new Intent(context, ProductManage.class);
                    context.startActivity(intent);
                    break;
                }
                case R.id.published_nail: {
           /*     Intent intent=new Intent(context,MyDiscountActivity.class);
                context.startActivity(intent);*/
                    break;
                }
                case R.id.iv_avatar: {
                    Intent intent = new Intent(context, MyInfoActivity.class);
                    context.startActivity(intent);
                    break;
                }
                case R.id.commit: {

                    break;
                }
            }
        }
    }

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getProductList(nextPage);
    }
}
