package com.sp.lib.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
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

    /**
     * 删除
     * @param v
     */
    public void onDelete(View v) {
        if (logs != null && curIndex >= 0 && curIndex < logs.size()) {
            logs.remove(curIndex);
            curIndex--;
            showLog(curIndex);
        }
    }

    /**
     * 下一个
     * @param v
     */
    public void onNext(View v) {
        if (logs != null) {
            curIndex++;
            showLog(curIndex);
        }
    }

    /**
     * 上一个
     * @param v
     */
    public void onPrev(View v) {
        if (logs != null) {
            curIndex--;
            showLog(curIndex);
        }
    }

    /**
     * 清空
     * @param v
     */
    public void onClear(View v){
        logs.clear();
        showLog(0);
    }

    public void onColor(View v){
        SharedPreferences sp=getSharedPreferences("debug_color",MODE_PRIVATE);
        ColorDialog dialog=new ColorDialog(this);
        dialog.setColor(sp.getInt("color",0));
        dialog.setPickListener(new ColorDialog.PickListener() {
            @Override
            public void onPick(int color) {
                tv_logs.setTextColor(color);
            }
        });
        dialog.show();
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
            tv_page.setText(null);
            return;
        }

        if (index < 0) {
            index += logs.size();
        }

        if (index>=logs.size()){
            index-=logs.size();
        }
        curIndex=index;

        tv_logs.setText(logs.get(index).msg);
        tv_time.setText(logs.get(index).time);
        tv_page.setText(String.format("%d/%d", index + 1, logs.size()));
    }

}
