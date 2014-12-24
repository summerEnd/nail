package com.finger.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.BaseActivity;
import com.finger.R;
import com.finger.activity.other.login.LoginActivity;
import com.finger.activity.artist.my.MyFragment;
import com.finger.activity.artist.order.OrderFragment;
import com.finger.support.Constant;
import com.finger.support.util.ContextUtil;
import com.finger.support.util.Logger;
import com.sp.lib.support.IntentFactory;


public class MainActivity extends BaseActivity {
    /**
     * 当前展示的Fragment在fragments数组中的下标
     */
    int curIndex = 0;
    View clicked_tab;
    boolean sholdReturnHome = false;
    FragmentManager fragmentManager;
    ImageView imageView[] = new ImageView[4];
    TextView tvs[] = new TextView[4];
    /**
     * fragments[0] {@link MainFragment}
     * fragments[1] {@link com.finger.activity.user.order.OrderFragment}
     * fragments[2] {@link com.finger.activity.user.my.MyFragment}
     * fragments[3] {@link com.finger.activity.artist.order.OrderFragment}
     * fragments[4] {@link com.finger.activity.artist.my.MyFragment}
     */
    Fragment[] fragments = new Fragment[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView[0] = (ImageView) findViewById(R.id.r1);
        imageView[1] = (ImageView) findViewById(R.id.r2);
        imageView[2] = (ImageView) findViewById(R.id.r3);
        imageView[3] = (ImageView) findViewById(R.id.r4);

        tvs[0] = (TextView) findViewById(R.id.t1);
        tvs[1] = (TextView) findViewById(R.id.t2);
        tvs[2] = (TextView) findViewById(R.id.t3);
        tvs[3] = (TextView) findViewById(R.id.t4);

        imageView[0].setTag(new int[]{R.drawable.tab_home_01, R.drawable.tab_home_02});
        imageView[1].setTag(new int[]{R.drawable.tab_order_01, R.drawable.tab_order_02});
        imageView[2].setTag(new int[]{R.drawable.tab_my_01, R.drawable.tab_my_02});
        imageView[3].setTag(new int[]{R.drawable.tab_service_01, R.drawable.tab_service_02});

        fragments[0] = new MainFragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.frag_container, fragments[0])
                .commit();
        changeTab(0);
    }

    public void onTabClick(View v) {
        int id = v.getId();
        clicked_tab = v;
        switch (id) {
            case R.id.tab1:
                changeTab(0);
                showFragment(0);
                break;
            case R.id.tab2: {

                if (!checkLogin()) {
                    return;
                }

                changeTab(1);
                String type = getApp().getUser().getType();
                if (Constant.LOGIN_TYPE_ARTIST.equals(type)) {
                    if (fragments[3] == null) {
                        fragments[3] = new OrderFragment();
                    }
                    showFragment(3);
                } else if (Constant.LOGIN_TYPE_USER.equals(type)) {
                    if (fragments[1] == null) {
                        fragments[1] = new com.finger.activity.user.order.OrderFragment();
                    }
                    showFragment(1);
                }
                break;
            }
            case R.id.tab3: {
                if (!checkLogin()) {
                    return;
                }
                changeTab(2);
                String type = getApp().getUser().getType();
                if (Constant.LOGIN_TYPE_ARTIST.equals(type)) {
                    if (fragments[4] == null) {
                        fragments[4] = new MyFragment();
                    }
                    showFragment(4);
                } else if (Constant.LOGIN_TYPE_USER.equals(type)) {
                    if (fragments[2] == null) {
                        fragments[2] = new com.finger.activity.user.my.MyFragment();
                    }
                    showFragment(2);
                }
                break;
            }
            case R.id.tab4: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.alert_msg_call_service));
                builder.setTitle(getString(R.string.call_service));
                builder.setPositiveButton(R.string.call_number, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = IntentFactory.callPhone("12345679");
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;
            }

        }
    }

    boolean checkLogin() {
        if (!getApp().isLogin()) {
            startActivityForResult(new Intent(this, LoginActivity.class), 100);
            return false;
        }
        return true;
    }

    /**
     * 1.如果未登录，跳转到登录页
     * 2.如果登录的是用户就展示fragments[0],fragments[1],fragments[2]，
     * 如果登录的是美甲师则展示fragments[0],fragments[3],fragments[4]。
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tab1:
                break;

            default:
                super.onClick(v);
        }
    }

    void changeTab(int index) {

        for (int i = 0; i < imageView.length; i++) {
            int[] drawables = (int[]) imageView[i].getTag();
            if (index == i) {
                imageView[i].setImageResource(drawables[1]);
                tvs[i].setTextColor(getResources().getColor(R.color.pink));
            } else {
                imageView[i].setImageResource(drawables[0]);
                tvs[i].setTextColor(getResources().getColor(R.color.textColorBlack));
            }
        }
    }

    void showFragment(int index) {
        Logger.i_format("index:%d  cur:%d",index,curIndex);
        if (curIndex == index) {
            return;
        }


        FragmentTransaction ft = fragmentManager.beginTransaction();

        Fragment showFragment = fragments[index];
        Fragment hideFragment = fragments[curIndex];


        if (!showFragment.isAdded()) {
            ft.add(R.id.frag_container, showFragment);
        } else {
            ft.show(showFragment);
        }
        ft.hide(hideFragment).commitAllowingStateLoss();
        curIndex = index;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==100){
            sholdReturnHome =false;
            if (!getApp().isLogin()) {
                returnHome();
            } else {
                onTabClick(clicked_tab);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerRoleChangeBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterRoleChangeBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sholdReturnHome) {
            returnHome();
            sholdReturnHome = false;
        }
    }

    /**
     * 返回首页
     */
    void returnHome() {
        showFragment(0);
        changeTab(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRoleChange(String role) {
        sholdReturnHome = true;
    }

    private long time;

    @Override
    public void onBackPressed() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - time < 2000) {
            super.onBackPressed();
            getApp().onExit();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            time = currentTimeMillis;
            ContextUtil.toast(getString(R.string.click_again_exit));
        }
    }
}
