package com.finger.activity.other.plan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.finger.BaseActivity;
import com.finger.R;
import com.finger.activity.other.info.ArtistInfo;
import com.finger.support.widget.RatingWidget;

public class ChooseArtist extends BaseActivity implements AdapterView.OnItemClickListener{
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_artist);
        listView= (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArtistAdapter(this));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(this, ArtistInfo.class));
    }

    class ArtistAdapter extends BaseAdapter{
        Context context;

        ArtistAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View holder;
            if (convertView==null){
                convertView= LayoutInflater.from(context).inflate(R.layout.artist_list_item,null);
            }else{
                holder= (View) convertView.getTag();
            }
            return convertView;
        }
    }
    static class ViewHolder{
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
        RatingWidget ratingWidget;
        ImageView iv;
    }
}
