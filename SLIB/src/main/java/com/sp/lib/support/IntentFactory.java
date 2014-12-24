package com.sp.lib.support;


import android.content.Intent;
import android.net.Uri;

public class IntentFactory {
    public static Intent callPhone(String phone){
        Intent intent=new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+phone));
        return intent;
    }
}
