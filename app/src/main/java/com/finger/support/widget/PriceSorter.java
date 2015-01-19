package com.finger.support.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.finger.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by acer on 2015/1/19.
 */
public class PriceSorter extends PopupWindow {
    View contentView;

    OnPriceSet set;

    public void setPriceSetWatcher(OnPriceSet set){
        this.set=set;
    }

    public interface OnPriceSet {
        public void onPriceSet(String price);
    }

    public PriceSorter(Context context) {
        super(context);
        setWidth(MATCH_PARENT);
        setHeight(WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(0xffffffff));
        setFocusable(true);

        contentView = View.inflate(context, R.layout.price_area, null);
        contentView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText from = (EditText) contentView.findViewById(R.id.edit_from);
                EditText to = (EditText) contentView.findViewById(R.id.edit_to);

                String price = new StringBuilder()
                        .append(from.getText().toString())
                        .append("_")
                        .append(to.getText().toString())
                        .toString();
                dismiss();
                if (set!=null){
                    set.onPriceSet(price);
                }

            }
        });
        setContentView(contentView);
    }
}
