package com.finger.support.widget;

import android.content.Context;
import android.graphics.Point;

import com.finger.R;
import com.sp.lib.util.DisplayUtil;


public class ItemUtil {
    /**
     * 屏幕宽度一半
     */
    public static int halfScreen;
    /**
     * NailItem ArtistItem的大小
     */
    public static int item_size;
    /**
     * NailItem ArtistItem的间距
     */
    public static int item_spacing;

    public static void init(Context context) {
        Point p = new Point();
        DisplayUtil.getScreenSize((android.app.Activity) context, p);
        ItemUtil.halfScreen = p.x / 2;
        ItemUtil.item_spacing = context.getResources().getDimensionPixelSize(R.dimen.item_spacing);
        ItemUtil.item_size = ItemUtil.halfScreen - ItemUtil.item_spacing * 3 / 2;

    }
}
