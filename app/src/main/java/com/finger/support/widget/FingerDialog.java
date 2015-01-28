package com.finger.support.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.finger.R;

public class FingerDialog extends Dialog implements View.OnClickListener {
    Listener mListener;
    TextView tv_title;
    TextView tv_message;
    Button   yes;
    Button   no;

    public FingerDialog(Context context, Listener l) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mListener = l;
        setContentView(R.layout.order_confirm_dialog);
        no= (Button) findViewById(R.id.button_no);
        yes= (Button) findViewById(R.id.button_yes);
        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_message= (TextView) findViewById(R.id.tv_message);
    }

    public void setDialogTitle(String title) {
        tv_title.setText(title);
    }

    public void setMessage(String msg){
        tv_message.setText(msg);
    }

    public void setYesText(String text){
        yes.setText(text);
    }

    public void setNoText(String text){
        no.setText(text);
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
