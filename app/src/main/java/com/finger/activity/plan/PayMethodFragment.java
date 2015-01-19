package com.finger.activity.plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.finger.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PayMethodFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView listView;

    String payMethod;
    CheckedTextView checkedTextView;
    /**
     * 获取支付方式
     * @return
     */
    public String getPayMethod() {
        return payMethod;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_pay_method, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new SimpleAdapter(
                getActivity(),
                createData(),
                R.layout.list_item_pay_method,
                new String[]{"name", "ic"},
                new int[]{R.id.name, R.id.icon}));
    }

    List<Map<String, Object>> createData() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", "支付宝");
            map.put("ic", R.drawable.ic_launcher);
            data.add(map);
        }

        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", "银联支付");
            map.put("ic", R.drawable.ic_launcher);
            data.add(map);
        }
        return data;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView clicked= (CheckedTextView) view.findViewById(R.id.name);
        if (checkedTextView!=null&&checkedTextView==clicked){

        }
        checkedTextView= (CheckedTextView) view.findViewById(R.id.name);
        ((CheckedTextView) view.findViewById(R.id.name)).setChecked(true);
    }
}
