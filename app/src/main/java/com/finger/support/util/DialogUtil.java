package com.finger.support.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.finger.R;

/**
 * Created by acer on 2014/12/17.
 */
public class DialogUtil {
    public  void alert(Context context,String title,String msg){

    }

    public static AlertDialog showNetFail(final Activity context){

        return alert(context,context.getString(R.string.load_failed));
    }
    public static AlertDialog alert(final Activity context,String msg){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.show();
    }
}
