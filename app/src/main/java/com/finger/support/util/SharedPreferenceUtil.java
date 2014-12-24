package com.finger.support.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by acer on 2014/12/8.
 */
public class SharedPreferenceUtil {
    public static final void get(Context context){
        SharedPreferences sp=context.getSharedPreferences("",Context.MODE_PRIVATE);
    }
}
