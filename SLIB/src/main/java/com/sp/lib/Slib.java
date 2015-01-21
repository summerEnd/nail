package com.sp.lib;

import android.content.Context;

import com.sp.lib.exception.ExceptionHandler;
import com.sp.lib.util.FileUtil;
import com.sp.lib.util.ImageManager;

/**
 * Created by acer on 2014/12/8.
 */
public class Slib {

    public static final void initialize(Context context) {
        String packageName;
        packageName = context.getPackageName();
        ImageManager.init(context, packageName);
        ExceptionHandler.init(context);

    }

    public static final void clearCache() {
        try {
            ImageManager.clear();
            ExceptionHandler.clear();

        } catch (Exception e) {

        }
    }

}
