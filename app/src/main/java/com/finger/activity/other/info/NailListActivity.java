package com.finger.activity.other.info;

import android.os.Bundle;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.activity.other.plan.NailItemListFragment;


public class NailListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nail_list);
        NailItemListFragment nailItemListFragment = new NailItemListFragment();
        Bundle data=new Bundle();
        nailItemListFragment.setArguments(data);
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, nailItemListFragment).commit();
    }


}
