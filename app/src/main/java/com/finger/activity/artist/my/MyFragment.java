package com.finger.activity.artist.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.other.setting.SettingActivity;
import com.finger.activity.user.my.MyCareActivity;
import com.finger.activity.user.my.MyCollectionActivity;
import com.finger.activity.user.my.MyDiscountActivity;
import com.finger.support.annotion.Artist;

@Artist
public class MyFragment extends Fragment implements View.OnClickListener {

    TextView my_attention;
    TextView my_collection;
    TextView settings;
    TextView my_discount_card;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_center_artist, null);
        ((TextView) v.findViewById(R.id.title_text)).setText("我的");
        findIds(v);
        return v;
    }

    void findIds(View v) {
        my_attention = (TextView) v.findViewById(R.id.my_attention);
        my_collection = (TextView) v.findViewById(R.id.my_collection);
        settings = (TextView) v.findViewById(R.id.settings);
        my_discount_card = (TextView) v.findViewById(R.id.my_discount_card);
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
                    Intent intent=new Intent(context,MyCareActivity.class);
                    context.startActivity(intent);
                    break;
                }
                case R.id.my_collection: {
                    Intent intent=new Intent(context,MyCollectionActivity.class);
                    context.startActivity(intent);
                    break;
                }
                case R.id.my_discount_card: {
                    Intent intent = new Intent(context, MyDiscountActivity.class);
                    context.startActivity(intent);
                    break;
                }
                case R.id.publish_nail: {
//                Intent intent=new Intent(context,MyDiscountActivity.class);
//                context.startActivity(intent);
                    break;
                }
                case R.id.published_nail: {
           /*     Intent intent=new Intent(context,MyDiscountActivity.class);
                context.startActivity(intent);*/
                    break;
                }
            }
        }
    }

}
