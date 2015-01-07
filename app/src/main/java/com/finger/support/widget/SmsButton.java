package com.finger.support.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.finger.R;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 发送短信的按钮
 */
public class SmsButton extends Button {

    int remainTime;
    CharSequence mText;

    public SmsButton(Context context) {
        this(context, null);
    }

    public SmsButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmsButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean performClick() {
        setEnabled(false);
        mText = getText();
        startTimer();
        return super.performClick();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(timer!=null){
            timer.cancel();
            timer = null;
        }

    }

    Timer timer;

    void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                remainTime--;
                handler.sendEmptyMessage(remainTime);
            }
        }, 0, 1000);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int time = msg.what;
            if (time <= 0) {
                setText(mText);
            } else {
                setText(getContext().getString(R.string.remain_time_d, time));
            }
            return false;
        }
    });
}
