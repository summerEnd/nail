package com.finger.activity.info;

import android.os.Bundle;

import com.finger.activity.base.BaseActivity;
import com.finger.R;

/**
 * 美甲作品列表
 */
public class NailListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nail_list);
        NailInfoListFragment nailInfoListFragment = new NailInfoListFragment();
        Bundle data = new Bundle();
        nailInfoListFragment.setArguments(data);
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container, nailInfoListFragment).commit();
    }


}
