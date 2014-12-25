package com.finger.activity.other.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.finger.activity.BaseActivity;
import com.finger.R;

public class SuggestionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit: {
                EditText editText= (EditText) findViewById(R.id.edit);
                editText.setText(null);
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.suggestion_done));
                builder.setMessage(getString(R.string.suggestion_dialog_msg));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
                break;
            }
        }
        super.onClick(v);
    }
}
