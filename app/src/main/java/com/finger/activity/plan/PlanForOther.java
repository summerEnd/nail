package com.finger.activity.plan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.finger.R;
import com.finger.activity.FingerApp;
import com.finger.activity.login.LoginActivity;
import com.finger.entity.AddressSearchBean;
import com.finger.entity.OrderBean;
import com.finger.entity.OrderManager;
import com.finger.support.widget.EditItem;

import static android.app.Activity.RESULT_OK;
import static com.finger.activity.plan.PlanActivity.PlanFragment;

/**
 * Created by acer on 2014/12/18.
 */
public class PlanForOther extends PlanFragment implements View.OnClickListener {
    EditItem          edit_plan_time;
    EditItem          edit_address;
    EditItem          edit_mobile;
    EditItem          edit_contact;
    AddressSearchBean addressSearchBean;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plan_for_other, null);
        v.findViewById(R.id.choose_nail_artist).setOnClickListener(this);
        edit_plan_time = (EditItem) v.findViewById(R.id.item_plan_time_for_other);
        edit_contact = (EditItem) v.findViewById(R.id.edit_contact);
        edit_mobile = (EditItem) v.findViewById(R.id.edit_mobile);
        edit_address = (EditItem) v.findViewById(R.id.edit_address);
        edit_mobile.getTextView().setInputType(InputType.TYPE_CLASS_PHONE);

        edit_address.setOnClickListener(this);
        edit_plan_time.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.edit_address: {
                startActivityForResult(new Intent(getActivity(), SearchAddress.class), 101);
                break;
            }

            case R.id.choose_nail_artist: {

                if (FingerApp.getInstance().isLogin()) {
                    OrderBean bean = OrderManager.getCurrentOrder();
                    if (bean == null) {
                        bean = OrderManager.createOrder();
                    }
                    bean.address = edit_address.getContent();
                    bean.planTime = planTime;
                    bean.contact = edit_contact.getContent();
                    bean.mobile = edit_mobile.getContent();
                    bean.time_block = time_block;
                    bean.book_date = book_date;
                    bean.addressSearchBean=addressSearchBean;
                    bean.type = TYPE_FOR_ME;
                    submit();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }
        addressSearchBean = (AddressSearchBean) data.getSerializableExtra("bean");
        String address = addressSearchBean == null ? "" : addressSearchBean.address + addressSearchBean.name;

        edit_address.setContent(address);

    }
}
