package com.finger.support.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by acer on 2014/9/2.
 */
public class Dimension {
    /**
     * 将value加上单位dp，如dp(2)就是2dp
     * @param value
     * @return
     */
    public static float dp(float value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 将value加上单位dp，如sp(2)就是2sp
     * @param value
     * @return
     */
    public static float sp(float value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,Resources.getSystem().getDisplayMetrics());
    }
}
