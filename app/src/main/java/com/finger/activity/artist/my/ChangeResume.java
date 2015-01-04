package com.finger.activity.artist.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.finger.R;
import com.finger.activity.BaseActivity;
import com.sp.lib.widget.AddImageItem;

/**
 * 修改简历
 */
public class ChangeResume extends BaseActivity {
    public static final String EXTRA_SHORT_TEXT = "short_text";
    public static final String EXTRA_SHORT_IMAGE = "short_image";
    AddImageItem imageItem;
    EditText edit_resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_resume);
        imageItem = (AddImageItem) findViewById(R.id.add_image);
        edit_resume = (EditText) findViewById(R.id.edit_resume);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                String resume = edit_resume.getText().toString();
                break;
        }
        super.onClick(v);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageItem.onActivityResult(requestCode, resultCode, data);
    }
}
