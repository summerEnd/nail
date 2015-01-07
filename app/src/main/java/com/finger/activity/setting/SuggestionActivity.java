package com.finger.activity.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

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
                final EditText editText= (EditText) findViewById(R.id.edit);
                RequestParams params=new RequestParams();
                params.put("content",editText.getText().toString());
                FingerHttpClient.post("postSuggest", params, new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject o) {
                        editText.setText(null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(SuggestionActivity.this);
                        builder.setTitle(getString(R.string.suggestion_done));
                        builder.setMessage(getString(R.string.suggestion_dialog_msg));
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                        builder.show();
                    }
                });


                break;
            }
        }
        super.onClick(v);
    }
}
