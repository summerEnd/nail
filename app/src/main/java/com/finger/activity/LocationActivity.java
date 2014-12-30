package com.finger.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.finger.R;

public class LocationActivity extends BaseActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_city_activity);
        listView = (ListView) findViewById(R.id.listView);
        View headView=View.inflate(this,R.layout.my_location_head,null);
        listView.addHeaderView(headView);
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_city, new String[]{"常州", "南京", "南京", "南京", "南京", "南京", "南京", "南京", "南京"}));
    }


}
