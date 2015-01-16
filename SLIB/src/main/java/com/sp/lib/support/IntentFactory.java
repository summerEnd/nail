package com.sp.lib.support;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentFactory {
    /**
     * 打电话
     *
     * @param phone
     * @return
     */
    public static Intent callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        return intent;
    }

    public static Intent share( String title, String content) throws ActivityNotFoundException {
        Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
        intent.setType("text/plain"); // 分享发送的数据类型
//        intent.setPackage(context.getPackageName());
        intent.putExtra(Intent.EXTRA_SUBJECT, title); // 分享的主题
        intent.putExtra(Intent.EXTRA_TEXT, content); // 分享的内容
        return intent;
    }
}
