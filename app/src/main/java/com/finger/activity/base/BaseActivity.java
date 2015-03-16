package com.finger.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;


import com.finger.activity.FingerApp;
import com.finger.R;
import com.finger.activity.info.CommentListActivity;
import com.finger.activity.login.LoginActivity;
import com.finger.entity.ArtistRole;
import com.finger.entity.BaseInfo;
import com.finger.entity.CityBean;
import com.finger.entity.RoleBean;
import com.finger.entity.UserRole;
import com.finger.support.Constant;
import com.finger.support.util.ItemUtil;
import com.finger.support.util.Logger;
import com.sp.lib.anim.ActivityAnimator;
import com.sp.lib.util.FileUtil;

import static com.finger.support.util.Logger.i;
import static com.sp.lib.anim.ActivityAnimator.*;

/**
 * @copy
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener {
    /**
     * 是否debug生命周期
    */
    protected boolean            showLifeCircle     = false;
    private   RoleChangeReceiver roleChangeReceiver = null;

    public FingerApp getApp() {
        return (FingerApp) getApplication();
    }

    public String getLoginType() {
        return getApp().getLoginType();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setTitle(getTitle());
    }

    @Override
    public void setTitle(CharSequence title) {

        TextView tv_title = (TextView) findViewById(R.id.title_text);
        if (tv_title != null) {
            tv_title.setText(title);
        } else {
            super.setTitle(title);
        }
    }

    /**
     * 设置美甲师专业沟通守时
     *
     * @param pro    专业 对应TextView 的id为tv_pro
     * @param com    沟通 对应TextView 的id为tv_com
     * @param onTime 守时 对应TextView 的id为tv_on_time
     */
    public void setArtistZGS(float pro, float com, float onTime) {
        TextView tv_pro = (TextView) findViewById(R.id.tv_pro);
        TextView tv_com = (TextView) findViewById(R.id.tv_com);
        TextView tv_on_time = (TextView) findViewById(R.id.tv_on_time);
        tv_pro.setText(String.format("%.1f", pro));
        tv_com.setText(String.format("%.1f", com));
        tv_on_time.setText(String.format("%.1f", onTime));
    }

    private View.OnClickListener onCommentClickListener;


    /**
     * 好评中评差评
     */
    public void setArtistComment(final ArtistRole bean) {

        if (onCommentClickListener == null) {
            onCommentClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseActivity.this, CommentListActivity.class);
                    //这里要判断一下，id只有当自己登录的时候才有值
                    int id = bean.mid;
                    if (id <= 0) {
                        id = bean.id;
                    }
                    intent.putExtra(CommentListActivity.EXTRA_MID, id);
                    switch (v.getId()) {
                        case R.id.tv_good_comment: {
                            intent.putExtra(CommentListActivity.EXTRA_COMMENT_TYPE, Constant.COMMENT_GOOD);
                            break;
                        }
                        case R.id.tv_bad_comment: {
                            intent.putExtra(CommentListActivity.EXTRA_COMMENT_TYPE, Constant.COMMENT_BAD);
                            break;
                        }
                        case R.id.tv_mid_comment: {
                            intent.putExtra(CommentListActivity.EXTRA_COMMENT_TYPE, Constant.COMMENT_MID);
                            break;
                        }
                    }
                    startActivity(intent);
                }
            };
        }
        TextView tv_good_comment = (TextView) findViewById(R.id.tv_good_comment);
        TextView tv_bad_comment = (TextView) findViewById(R.id.tv_bad_comment);
        TextView tv_mid_comment = (TextView) findViewById(R.id.tv_mid_comment);
        tv_good_comment.setText(getString(R.string.d_num, bean.comment_good));
        tv_mid_comment.setText(getString(R.string.d_num, bean.comment_normal));
        tv_bad_comment.setText(getString(R.string.d_num, bean.comment_bad));
        ((View) tv_good_comment.getParent()).setOnClickListener(onCommentClickListener);
        ((View) tv_bad_comment.getParent()).setOnClickListener(onCommentClickListener);
        ((View) tv_mid_comment.getParent()).setOnClickListener(onCommentClickListener);
    }


    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    public void setTitleColor(int textColor) {
        TextView tv_title = (TextView) findViewById(R.id.title_text);
        if (tv_title != null) {
            tv_title.setTextColor(textColor);
        } else {
            super.setTitleColor(textColor);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        debug("onCreate");
        if (ItemUtil.halfScreen == 0) {
            ItemUtil.init(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        debug("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        debug("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        debug("onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        debug("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        debug("onDestroy");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        debug("onRestoreInstanceState");
        //读取基本数据
        RoleBean user = (RoleBean) savedInstanceState.getSerializable("user");
        CityBean city = (CityBean) savedInstanceState.getSerializable("city");
        BaseInfo info = (BaseInfo) savedInstanceState.getSerializable("info");
        getApp().setUser(user);
        getApp().setCurCity(city);
        getApp().setBaseInfo(info);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存基本数据
        RoleBean user = getApp().getUser();
        CityBean city = getApp().getCurCity();
        BaseInfo info = getApp().getBaseInfo();
        outState.putSerializable("user", user);
        outState.putSerializable("city", city);
        outState.putSerializable("info", info);
        FileUtil.saveFile(this,Constant.FILE_ROLE,user);
        debug("onSaveInstanceState");
    }

    void debug(String msg) {
        if (showLifeCircle)
            i(getClass().getSimpleName()+"--->" + msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();

                break;
            //            default:super.onClick(v);
        }
    }

    protected final void registerRoleChangeBroadcast() {
        if (roleChangeReceiver == null) {
            roleChangeReceiver = new RoleChangeReceiver();
        }

        try {
            registerReceiver(roleChangeReceiver, new IntentFilter(FingerApp.ACTION_ROLE_CHANGED));
        } catch (Exception e) {
            Logger.e(e.getLocalizedMessage());
        }

    }

    protected final void unRegisterRoleChangeBroadcast() {
        try {
            unregisterReceiver(roleChangeReceiver);
        } catch (Exception e) {
            Logger.e(e.getLocalizedMessage());
        }
    }

    /**
     * 当前登录的用户发生改变时，会调用这个方法
     *
     * @param role 美甲师：LOGIN_TYPE_ARTIST
     *             用户：LOGIN_TYPE_USER
     *             未登录：LOGIN_TYPE_EMPTY
     */
    protected void onRoleChange(String role) {

    }

    /**
     * 判断是否登录
     *
     * @return
     */
    protected final boolean isLogin() {
        return !(getApp().getUser() instanceof RoleBean.EmptyRole);
    }

    /**
     * 执行登录
     *
     * @return 是否跳转到登录
     */
    protected boolean doLoginIfNeed() {
        boolean isNeeded = !isLogin();
        if (isNeeded) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        return isNeeded;
    }

    private class RoleChangeReceiver extends BroadcastReceiver

    {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRoleChange(intent.getStringExtra("role"));
        }
    }
}
