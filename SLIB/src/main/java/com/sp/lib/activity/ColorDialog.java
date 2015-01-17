package com.sp.lib.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.sp.lib.R;

public class ColorDialog extends Dialog implements NumberPicker.OnValueChangeListener {
    NumberPicker pick_r;
    NumberPicker pick_g;
    NumberPicker pick_b;
    TextView     tv_test;
    PickListener pickListener;

    public PickListener getPickListener() {
        return pickListener;
    }

    public void setPickListener(PickListener pickListener) {
        this.pickListener = pickListener;
    }

    public ColorDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pick_color);

        pick_r = (NumberPicker) findViewById(R.id.pick_r);
        pick_g = (NumberPicker) findViewById(R.id.pick_g);
        pick_b = (NumberPicker) findViewById(R.id.pick_b);
        tv_test = (TextView) findViewById(R.id.tv_test);

        setPicker(pick_r);
        setPicker(pick_g);
        setPicker(pick_b);


        findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickListener != null) {
                    pickListener.onPick(getColor());
                }
                dismiss();
            }
        });
    }

    void setPicker(NumberPicker picker) {
        picker.setDisplayedValues(new String[]{
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "a",
                "b",
                "c",
                "d",
                "e",
                "f"
        });
//        picker.setMaxValue(255);
//        picker.setMinValue(0);
        picker.setFocusable(false);
        picker.setOnValueChangedListener(this);
    }

    public void setColor(int color) {
        pick_r.setValue(Color.red(color));
        pick_g.setValue(Color.green(color));
        pick_b.setValue(Color.blue(color));
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        tv_test.setTextColor(getColor());
    }

    int getColor() {
        return Color.rgb(pick_r.getValue(), pick_g.getValue(), pick_b.getValue());
    }

    public interface PickListener {
        public void onPick(int color);
    }
}
