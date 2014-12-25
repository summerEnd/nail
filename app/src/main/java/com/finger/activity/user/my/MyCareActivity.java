package com.finger.activity.user.my;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.finger.activity.BaseActivity;
import com.finger.R;
import com.finger.support.entity.ArtistItemBean;

import java.util.LinkedList;
import java.util.List;

public class MyCareActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    CareAdapter adapter;
    TextView title_delete;
    List<ArtistItemBean> beans = new LinkedList<ArtistItemBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_care);
        listView = (ListView) findViewById(R.id.listView);
        title_delete = (TextView) findViewById(R.id.tv_title_delete);
        adapter = new CareAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        getData();
    }

    void getData() {
        beans.add(new ArtistItemBean());
        beans.add(new ArtistItemBean());
        beans.add(new ArtistItemBean());
        beans.add(new ArtistItemBean());
        beans.add(new ArtistItemBean());
        beans.add(new ArtistItemBean());
        beans.add(new ArtistItemBean());
        beans.add(new ArtistItemBean());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_delete: {

                String titleText = title_delete.getText().toString();
                if (titleText.equals(getString(R.string.delete))) {
                    title_delete.setText(R.string.done);
                } else {

                    deleteItem();

                    title_delete.setText(R.string.delete);
                }
                adapter.showDelete(!adapter.delete);
                break;
            }
        }
        super.onClick(v);
    }

    private void deleteItem() {
        LinkedList<ArtistItemBean> deletes = new LinkedList<ArtistItemBean>();
        for (ArtistItemBean bean : beans) {
            if (bean.selected) {
                deletes.add(bean);
            }
        }
        for (ArtistItemBean bean : deletes) {
            beans.remove(bean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.delete) {
            ArtistItemBean bean = beans.get(position);
            bean.selected = !bean.selected;
            adapter.notifyDataSetChanged();
        }
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
                for (ArtistItemBean bean : beans) {
                    bean.selected = false;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
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
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ArtistItemBean bean = beans.get(position);
            if (delete) {
                holder.iv_delete.setVisibility(View.VISIBLE);
                if (bean.selected) {
                    holder.iv_delete.setImageResource(R.drawable.attention_checked);
                } else {
                    holder.iv_delete.setImageResource(R.drawable.attention_unchecked);
                }
            } else {
                holder.iv_delete.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
            return convertView;
        }
    }

    class ViewHolder {
        ImageView iv_delete;
    }
}
