package com.finger.activity.artist.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finger.R;
import com.finger.support.annotion.Artist;

@Artist
public class OrderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_artist_order, null);
        ((TextView) v.findViewById(R.id.title_text)).setText("订单");
        return v;
    }
}
