package com.finger.activity.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.support.Constant;


public class CommentListActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    RadioGroup      rg;
    FragmentManager manager;
    Fragment        curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);
        manager = getSupportFragmentManager();
        rg = (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);
        showFragment(Constant.COMMENT_GOOD);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_good:
                showFragment(Constant.COMMENT_GOOD);
                break;
            case R.id.rb_mid:
                showFragment(Constant.COMMENT_MID);
                break;
            case R.id.rb_bad:
                showFragment(Constant.COMMENT_BAD);
                break;
        }
    }

    void showFragment(String grade) {
        CommentFragment fragment = (CommentFragment) manager.findFragmentByTag(grade);
        FragmentTransaction ft = manager.beginTransaction();

        if (fragment == null) {
            fragment = CommentFragment.newInstance(1, grade);
            ft.add(R.id.frag_container, fragment, grade);
        }

        ft.show(fragment);

        if (curFragment != null) {
            ft.hide(curFragment);
        }

        ft.commit();
        curFragment = fragment;
    }


}
