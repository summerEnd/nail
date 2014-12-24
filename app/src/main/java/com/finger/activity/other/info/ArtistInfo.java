package com.finger.activity.other.info;


import android.os.Bundle;

import com.finger.BaseActivity;
import com.finger.R;
import com.finger.activity.other.plan.NailItemListFragment;

public class ArtistInfo extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container,new NailItemListFragment()).commit();
    }
}
