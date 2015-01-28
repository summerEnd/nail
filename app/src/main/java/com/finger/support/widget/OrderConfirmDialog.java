package com.finger.support.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;

import com.finger.R;

public class OrderConfirmDialog extends Dialog implements View.OnClickListener {
    Listener mListener;

    public OrderConfirmDialog(Context context, Listener l) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mListener = l;
        setContentView(R.layout.order_confirm_dialog);
        findViewById(R.id.button_no).setOnClickListener(this);
        findViewById(R.id.button_yes).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_no:
                if (mListener != null)
                    mListener.onDialogNoPressed(this);
                break;
            case R.id.button_yes:
                if (mListener != null)
                    mListener.onDialogYesPressed(this);
                break;
        }
    }

    public interface Listener {
        public void onDialogYesPressed(DialogInterface dialog);

        public void onDialogNoPressed(DialogInterface dialog);
    }
}
