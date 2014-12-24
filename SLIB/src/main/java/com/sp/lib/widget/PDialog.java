package com.sp.lib.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;

import com.sp.lib.R;

/**
 * Created by acer on 2014/12/1.
 */
public class PDialog extends AlertDialog {

    float defaultDim;
    public PDialog(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_window);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        defaultDim=lp.dimAmount;
        getWindow().getAttributes().dimAmount=0f;
        setCancelable(false );
    }

    @Override
    protected void onStop() {
        super.onStop();
        getWindow().getAttributes().dimAmount=defaultDim;
    }
}
