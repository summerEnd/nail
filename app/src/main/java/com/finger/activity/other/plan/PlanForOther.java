package com.finger.activity.other.plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.finger.R;
import com.finger.support.entity.OrderBean;
import com.finger.support.entity.OrderManager;
import com.finger.support.widget.EditItem;

import static com.finger.activity.other.plan.PlanActivity.PlanFragment;

/**
 * Created by acer on 2014/12/18.
 */
public class PlanForOther extends PlanFragment implements View.OnClickListener {
    EditItem edit_plan_time;
    EditItem edit_address;
    EditItem edit_mobile;
    EditItem edit_contact;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plan_for_other, null);
        v.findViewById(R.id.choose_nail_artist).setOnClickListener(this);
        edit_plan_time = (EditItem) v.findViewById(R.id.item_plan_time_for_other);
        edit_contact = (EditItem) v.findViewById(R.id.edit_contact);
        edit_mobile = (EditItem) v.findViewById(R.id.edit_mobile);
        edit_address = (EditItem) v.findViewById(R.id.edit_address);

        edit_mobile.getTextView().setInputType(InputType.TYPE_CLASS_PHONE);

        edit_plan_time.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.choose_nail_artist: {
                OrderBean bean = OrderManager.getCurrentOrder();
                if (bean == null) {
                    bean = OrderManager.createOrder();
                }
                bean.address = edit_address.getContent();
                bean.planTime = edit_plan_time.getContent();
                bean.contact = edit_contact.getContent();
                bean.mobile = edit_mobile.getContent();
                bean.time_block=time_block;
                bean.book_date=book_date;
                submit();
                break;
            }
            case R.id.item_plan_time_for_other: {
                ((PlanActivity) getActivity()).getPlanTime(new Schedule.Callback() {
                    @Override
                    public void onSelect(String book_date, int time_block) {
                        planTime = getPlanTimeStr(book_date, time_block);
                        edit_plan_time.setContent(planTime);
                    }
                });
                break;
            }

        }

    }
}
