package com.finger.activity.other.info;


import android.os.Bundle;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.activity.other.plan.NailItemListFragment;

public class ArtistInfo extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);
        NailItemListFragment item = new NailItemListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, item).commit();
    }
}
