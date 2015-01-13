package com.finger.activity.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.finger.R;
import com.finger.activity.base.BaseActivity;
import com.finger.entity.OrderListBean;
import com.finger.support.Constant;
import com.finger.support.util.ContextUtil;


public class CommentListActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, CommentFragment.CommentNumberSetter {
    RadioGroup      rg;
    FragmentManager manager;
    Fragment        curFragment;
    /**
     * Intent参数，评价类型 好评 中评 差评 参照 com.finger.support.Constant COMMENT_GOOD，COMMENT_MID，COMMENT_BAD
     */
    public static final String EXTRA_COMMENT_TYPE = "comment_type";
    /**
     * Intent参数,评价的产品id
     */
    public static final String EXTRA_PRODUCT_ID   = "comment_id";
    /**
     * Intent参数,美甲师id
     */
    public static final String EXTRA_MID          = "comment_mid";
    int product_id;//作品id
    int mid;//作品id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);
        manager = getSupportFragmentManager();
        rg = (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);

        product_id = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        mid = getIntent().getIntExtra(EXTRA_MID, -1);

        String type = getIntent().getStringExtra(EXTRA_COMMENT_TYPE);
        if (Constant.COMMENT_BAD.equals(type)) {
            rg.check(R.id.rb_bad);
        } else if (Constant.COMMENT_MID.equals(type)) {
            rg.check(R.id.rb_mid);
        } else {
            rg.check(R.id.rb_good);
        }

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

    /**
     * 根据grade显示fragment
     *
     * @param grade
     */
    void showFragment(String grade) {
        CommentFragment fragment = (CommentFragment) manager.findFragmentByTag(grade);
        FragmentTransaction ft = manager.beginTransaction();

        if (fragment == null) {
            fragment = CommentFragment.newInstance(mid, product_id, grade);
            ft.add(R.id.frag_container, fragment, grade);
        }

        ft.show(fragment);

        if (curFragment != null) {
            ft.hide(curFragment);
        }

        ft.commit();
        curFragment = fragment;
    }


    @Override
    public void setCommentNumber(int good, int mid, int bad) {
        ((RadioButton) rg.getChildAt(0)).setText(String.format("好评(%d)", good));
        ((RadioButton) rg.getChildAt(1)).setText(String.format("中评(%d)", mid));
        ((RadioButton) rg.getChildAt(2)).setText(String.format("差评(%d)", bad));
    }
}
