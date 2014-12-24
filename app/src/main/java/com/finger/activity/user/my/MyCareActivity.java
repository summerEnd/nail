package com.finger.activity.user.my;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.finger.BaseActivity;
import com.finger.R;
import com.finger.support.entity.NailItemBean;

import java.text.DecimalFormat;

public class MyCareActivity extends BaseActivity {
    ListView listView;
    CareAdapter adapter;
    TextView title_delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_care);
        listView = (ListView) findViewById(R.id.listView);
        title_delete = (TextView) findViewById(R.id.tv_title_delete);
        adapter=new CareAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_delete: {
                if (adapter.delete){
                    title_delete.setText(R.string.delete);
                }else {
                    title_delete.setText(R.string.done);
                }
                adapter.showDelete(!adapter.delete);
                break;
            }
        }
        super.onClick(v);
    }

    class CareAdapter extends BaseAdapter {
        LayoutInflater inflater;

        CareAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        private boolean delete;

        public void showDelete(boolean delete) {
            this.delete = delete;
            if (delete) {

            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return 12;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_my_care, null);
                holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (delete) {
                holder.cb.setVisibility(View.VISIBLE);
            } else {
                holder.cb.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
            return convertView;
        }
    }

    class ViewHolder {
        CheckBox cb;
    }
}
