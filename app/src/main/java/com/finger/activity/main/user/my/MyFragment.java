package com.finger.activity.main.user.my;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.FingerApp;
import com.finger.activity.info.User;
import com.finger.activity.main.AttentionList;
import com.finger.activity.main.MyCollectionActivity;
import com.finger.activity.setting.ChangeMyData;
import com.finger.activity.setting.SettingActivity;
import com.finger.entity.RoleBean;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

@User
public class MyFragment extends Fragment implements View.OnClickListener {

    TextView  my_attention;
    TextView  my_collection;
    TextView  settings;
    TextView  my_discount_card;
    TextView  tv_nick_name;
    ImageView avatar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_center_user, null);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.personal_center);
        findIds(v);
        settings.setOnClickListener(this);
        my_discount_card.setOnClickListener(this);
        v.findViewById(R.id.my_attention).setOnClickListener(this);
        v.findViewById(R.id.my_collection).setOnClickListener(this);
        avatar = (ImageView) v.findViewById(R.id.iv_avatar);
        tv_nick_name = (TextView) v.findViewById(R.id.tv_nick_name);
        avatar.setOnClickListener(this);
        setUserInfo();
        return v;
    }

    void findIds(View v) {
        my_attention = (TextView) v.findViewById(R.id.my_attention);
        my_collection = (TextView) v.findViewById(R.id.my_collection);
        settings = (TextView) v.findViewById(R.id.settings);
        my_discount_card = (TextView) v.findViewById(R.id.my_discount_card);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserInfo();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setUserInfo();
        }
    }

    /**
     * 设置用户信息
     */
    void setUserInfo() {
        RoleBean role = FingerApp.getInstance().getUser();
        tv_nick_name.setText(role.username);
        loadAvatar(role.avatar);
    }

    /**
     * 加载头像
     *
     * @param uri
     */
    void loadAvatar(String uri) {
        ImageManager.loadImage(uri, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage == null) {
                    loadedImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_user);
                }
                setImage(loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.default_user);
                setImage(avatar);
            }

            void setImage(Bitmap bitmap) {
                int radius = getResources().getDimensionPixelSize(R.dimen.avatar_center_size) / 2;
                avatar.setImageBitmap(ImageUtil.roundBitmap(bitmap, radius));
            }
        });
    }

    @Override
    public void onClick(View v) {
        {
            Context context = v.getContext();
            switch (v.getId()) {
                case R.id.iv_avatar: {
                    startActivity(new Intent(getActivity(), ChangeMyData.class));
                    break;
                }
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

            }
        }
    }

}
