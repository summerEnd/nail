package com.finger.activity.artist.my;

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.other.setting.SettingActivity;
import com.finger.activity.user.my.MyCareActivity;
import com.finger.activity.user.my.MyCollectionActivity;
import com.finger.activity.user.my.MyDiscountActivity;
import com.finger.support.adapter.NailListAdapter;
import com.finger.support.annotion.Artist;
import com.sp.lib.util.ImageUtil;

@Artist
public class MyFragment extends Fragment implements View.OnClickListener {

    TextView settings;
    ImageView iv_avatar;
    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_center_artist, null);
        ((TextView) v.findViewById(R.id.title_text)).setText(R.string.personal_center);
        gridView = (GridView) v.findViewById(R.id.grid);
        gridView.setAdapter(new NailListAdapter(getActivity()));
        findIds(v);
        setAvatar();
        return v;
    }

    void findIds(View v) {
        settings = (TextView) v.findViewById(R.id.settings);
        v.findViewById(R.id.publish_nail).setOnClickListener(this);
        iv_avatar = (ImageView) v.findViewById(R.id.iv_avatar);
        settings.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);
    }

    void setAvatar() {
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        int radius = getResources().getDimensionPixelSize(R.dimen.avatar_center_size) / 2;
        iv_avatar.setImageBitmap(ImageUtil.roundBitmap(avatar, radius));
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
                case R.id.publish_nail: {
                Intent intent=new Intent(context,PublishNailActivity.class);
                context.startActivity(intent);
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
