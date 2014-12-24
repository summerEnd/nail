package com.sp.lib.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;

/**
 * Created by acer on 2014/12/16.
 */
public class DisplayUtil {
    /**
     * @return
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void getScreenSize(Activity activity,Point p) {
        int width;
        int height;
        Display display = activity.getWindow().getWindowManager().getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(p);
        } else {
            width = display.getWidth();
            height=display.getHeight();
            p.x=width;
            p.y=height;
        }
    }

    public static float dp(float value,Resources res){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,res.getDisplayMetrics());
    }
}
