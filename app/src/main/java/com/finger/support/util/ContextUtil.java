package com.finger.support.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.finger.BuildConfig;
import com.finger.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.sp.lib.util.ImageUtil;


import java.util.Random;


/**
 * Created by acer on 2014/11/12.
 */
public class ContextUtil {
    private static Context context;

    public static final void init(Context context) {
        ContextUtil.context = context;
    }

    public static final String getString(int id) {
        return context.getString(id);
    }


    public static int getVersion() {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 设备id。如果device获取为空，测从preference取出. 如果preference为空就随机生成一个，并保存到preference.
     *
     * @return
     */
    public static String getImei() {
        String MIEI = "miei";

        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String miei = manager.getDeviceId();

        if (miei != null && miei.length() == 14) {
            miei += "1";
        } else if (miei == null) {
            SharedPreferences sp = context.getSharedPreferences(MIEI, Context.MODE_PRIVATE);
            miei = sp.getString(MIEI, null);
        }
        if (miei == null) {
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 15; i++) {
                sb.append(random.nextInt(10));
            }
            miei = sb.toString();
            SharedPreferences sp = context.getSharedPreferences(MIEI, Context.MODE_PRIVATE);
            sp.edit().putString(MIEI, miei);
        }
        return miei;
    }

    public static final void toast(Object o) {
        if (null == o)
            return;
        Toast toast = new Toast(context);
        View v = View.inflate(context, R.layout.toast_view, null);
        toast.setView(v);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        TextView tv = (TextView) v.findViewById(R.id.toast);
        tv.setText(String.valueOf(o));
        toast.show();
    }

    public static final void toast(int resId) {
        toast(getString(resId));
    }

    public static final void toast_debug(Object o) {
        if (BuildConfig.DEBUG) {
            toast(o);
        }
    }

    public static final Context getContext() {
        return context;
    }

    /**
     * get square image options.
     *
     * @return
     */
    public static DisplayImageOptions getSquareImgOptions() {

        DisplayImageOptions squareImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .cacheInMemory(true).cacheOnDisc(true).build();
        return squareImageOptions;
    }

    public static DisplayImageOptions getAvatarOptions() {
        return new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageForEmptyUri(R.drawable.default_user)
                .showImageOnFail(R.drawable.default_user)
                .showImageOnLoading(R.drawable.default_user)
                .displayer(new BitmapDisplayer() {
                    @Override
                    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {

                        if (!bitmap.isRecycled()) {
                            imageAware.setImageBitmap(ImageUtil.roundBitmap(bitmap, context.getResources().getDimensionPixelSize(R.dimen.avatar_size) / 2));
                        }else{
                            Logger.d("------------->");
                        }
                    }
                })
                .cacheInMemory(true).cacheOnDisc(true).build();
    }
}
