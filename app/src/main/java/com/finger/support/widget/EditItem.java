package com.finger.support.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finger.R;

public class EditItem extends LinearLayout {
    View contentView;
    TextView tv;
    EditText edit;
    ImageView ic;
    int drawableId;
    boolean editable;

    public EditItem(Context context) {
        this(context, null);
    }

    public EditItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        contentView = inflate(context, R.layout.edit_item, null);
        edit = (EditText) contentView.findViewById(R.id.edit);
        ic = (ImageView) contentView.findViewById(R.id.ic);
        tv = (TextView) contentView.findViewById(R.id.tv);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditItem);
        String text = a.getString(R.styleable.EditItem_edit_text);
        String hint = a.getString(R.styleable.EditItem_edit_hint);
        editable = a.getBoolean(R.styleable.EditItem_edit_editable, true);
        drawableId = a.getResourceId(R.styleable.EditItem_edit_drawable, 0);

        if (editable) {
            edit.setHint(hint);
            edit.setText(text);
            edit.setVisibility(VISIBLE);
            tv.setVisibility(GONE);
        } else {
            tv.setHint(hint);
            tv.setText(text);
            tv.setVisibility(VISIBLE);
            edit.setVisibility(GONE);
        }

        ic.setImageResource(drawableId);
        setOrientation(VERTICAL);
        addView(contentView);
    }

    void init(Context context) {

    }

//    @Override
//    public void setOnClickListener(OnClickListener l) {
//        contentView.setOnClickListener(l);
//    }

    public TextView getTextView() {
        if (editable) return edit;
        else return tv;
    }

    public String getContent() {
        if (editable) return edit.getText().toString();
        else return edit.getText().toString();
    }
}
