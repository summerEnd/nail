package com.sp.lib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sp.lib.util.FileUtil;
import com.sp.lib.R;
import com.sp.lib.exception.ExceptionHandler;

import java.util.LinkedList;

import static com.sp.lib.exception.ExceptionHandler.ErrorLog;

/**
 * Created by acer on 2014/12/5.
 */
public class DEBUGActivity extends Activity {
    LinkedList<ErrorLog> logs;
    TextView tv_logs;
    TextView tv_time;
    TextView tv_page;
    int curIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        tv_page = (TextView) findViewById(R.id.tv_page);
        tv_logs = (TextView) findViewById(R.id.tv_logs);
        tv_time = (TextView) findViewById(R.id.tv_time);
        logs = ExceptionHandler.getErrors();
        showLog(curIndex);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FileUtil.saveFile(this, "debugs", logs);
    }

    public void onDelete(View v) {
        if (logs != null && curIndex >= 0 && curIndex < logs.size()) {
            logs.remove(curIndex);
            curIndex--;
            showLog(curIndex);
        }
    }

    public void onNext(View v) {
        if (logs != null) {
            curIndex++;
            showLog(curIndex);
        }
    }

    public void onPrev(View v) {
        if (logs != null) {
            curIndex--;
            showLog(curIndex);
        }
    }

    public void onSend(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, tv_logs.getText().toString());
        startActivity(Intent.createChooser(intent, "SEND"));
    }

    public void showLog(int index) {
        if (logs == null || logs.size() == 0) {
            tv_logs.setText(null);
            tv_time.setText(null);
            return;
        }
        if (index < 0) {
            index += logs.size();
        }

        curIndex = index % logs.size();
        tv_logs.setText(logs.get(curIndex).msg);
        tv_time.setText(logs.get(curIndex).time);
        tv_page.setText(String.format("%d/%d", curIndex + 1, logs.size()));
    }

}
