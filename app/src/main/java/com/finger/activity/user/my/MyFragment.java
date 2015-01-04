package com.finger.activity.user.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.FingerApp;
import com.finger.R;
import com.finger.activity.other.common.ChangeMyData;
import com.finger.activity.other.common.MyCareActivity;
import com.finger.activity.other.common.MyCollectionActivity;
import com.finger.activity.other.setting.SettingActivity;
import com.finger.support.annotion.User;
import com.finger.support.entity.RoleBean;
import com.finger.support.entity.UserRole;
import com.sp.lib.util.ImageManager;

@User
public class MyFragment extends Fragment implements View.OnClickListener {

    TextView my_attention;
    TextView my_collection;
    TextView settings;
    TextView my_discount_card;
    TextView tv_nick_name;
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
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setUserInfo();
        }
    }

    void setUserInfo(){
        RoleBean role= ((FingerApp) getActivity().getApplication()).getUser();
        tv_nick_name.setText(role.username);
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
                    Intent intent = new Intent(context, MyCareActivity.class);
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
