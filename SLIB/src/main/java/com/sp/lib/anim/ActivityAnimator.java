package com.sp.lib.anim;

import android.app.Activity;

import com.sp.lib.R;

public class ActivityAnimator {

    /**
     * 淡入
     */
    public static final int IN_FADE         = android.R.anim.fade_in;
    /**
     * 淡出
     */
    public static final int OUT_FADE        = android.R.anim.fade_out;
    /**
     * 左滑入
     */
    public static final int IN_SLIDE_LEFT   = R.anim.slide_in_left;
    /**
     * 右滑入
     */
    public static final int IN_SLIDE_RIGHT  = R.anim.slide_in_right;
    /**
     * 右滑出
     */
    public static final int OUT_SLIDE_RIGHT = R.anim.slide_out_right;
    /**
     * 左滑出
     */
    public static final int OUT_SLIDE_LEFT  = R.anim.slide_out_left;
    /**
     * 上滑入
     */
    public static final int IN_SLIDE_UP     = R.anim.slide_up_in;
    /**
     * 下滑出
     */
    public static final int OUT_SLIDE_DOWN  = R.anim.slide_down_out;
    /**
     * 静止
     */
    public static final int NO_ANIMATION    = R.anim.stand_still;


    public static void override(Activity activity, int in, int out) {
        activity.overridePendingTransition(in, out);
    }

}
