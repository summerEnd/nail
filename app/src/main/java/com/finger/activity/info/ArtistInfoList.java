package com.finger.activity.info;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.finger.activity.base.BaseActivity;
import com.finger.R;
import com.finger.entity.OrderBean;
import com.finger.entity.OrderManager;
import com.finger.api.BaiduAPI;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.finger.support.util.Logger;
import com.finger.support.widget.RatingWidget;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sp.lib.util.DisplayUtil;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 美甲师列表
 */
public class ArtistInfoList extends BaseActivity implements AdapterView.OnItemClickListener, ListController.Callback {
    private ListView       listView;
    private PopupWindow    orderList;
    private int            width;
    /**
     * 距离排序:position
     * 等级排序:score
     * 价格排序:price
     * 例如：价格降序price_desc,等级升序score_asc
     */
    private String         sort;
    private String         ASC_DESC;
    private PopListAdapter popupAdapter;
    private ArtistAdapter  adapter;
    private List<ArtistListBean> beans = new ArrayList<ArtistListBean>();
    private ListController controller;

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getSellerList(nextPage);
    }

    public class ArtistListBean {
        String username;
        String mobile;
        String avatar;
        int    uid;
        int    score;
        String average_price;
        int    order_num;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_artist);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        adapter = new ArtistAdapter(ArtistInfoList.this, beans);
        listView.setAdapter(adapter);
        controller = new ListController(listView, this);
        getSellerList(1);
    }

    /**
     * 获取美甲师详情
     *
     * @param page 从1开始
     */
    void getSellerList(int page) {
        RequestParams params = new RequestParams();

        if (!TextUtils.isEmpty(ASC_DESC)) {
            params.put("sort", sort + "_" + ASC_DESC);
        } else {
            params.put("sort", sort);
        }

        params.put("page", page);
        params.put("pagesize", controller.getPageSize());
        OrderBean bean = OrderManager.getCurrentOrder();
        //如果是预约时查看列表，就取预约的经纬度。否则取用户当前的经纬度
        if (bean == null) {
            if (BaiduAPI.mBDLocation != null) {
                params.put("longitude", BaiduAPI.mBDLocation.getLongitude());
                params.put("latitude", BaiduAPI.mBDLocation.getLatitude());
            }

        } else {
            params.put("longitude", bean.addressSearchBean.longitude);
            params.put("latitude", bean.addressSearchBean.latitude);
        }
        params.put("pagesize", controller.getPageSize());
        FingerHttpClient.post("getSellerList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), ArtistListBean.class, beans);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (orderList == null) {
            orderList = new PopupWindow();
            width = findViewById(R.id.sort_item).getWidth();
            orderList.setWidth(width);
            orderList.setFocusable(true);
            orderList.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            orderList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            View contentView = View.inflate(this, R.layout.order_list, null);
            ListView listView = (ListView) contentView.findViewById(R.id.listView);
            popupAdapter = new PopListAdapter(this);
            listView.setAdapter(popupAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        ASC_DESC = "asc";
                    } else {
                        ASC_DESC = "desc";
                    }
                    beans.clear();
                    adapter.notifyDataSetChanged();
                    getSellerList(1);
                    orderList.dismiss();
                }
            });
            orderList.setContentView(contentView);
            orderList.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
        }


        String name = null;
        switch (v.getId()) {
            case R.id.sort_distance: {
                //按距离只有一种排序方式
                sort = "position_asc";
                beans.clear();
                getSellerList(1);
                break;
            }
            case R.id.sort_price: {
                name = getString(R.string.price);
                sort = "price";
                break;
            }
            case R.id.sort_stars: {
                name = getString(R.string.star_level);
                sort = "score";
                break;
            }
        }
        if (orderList.isShowing()) {
            orderList.dismiss();
        } else if (name != null) {
            openList(v, name);
        }
        super.onClick(v);
    }

    /**
     * 打开选择列表
     *
     * @param v
     * @param str
     */
    void openList(View v, String str) {
        try {
            int[] l = new int[2];
            v.getLocationOnScreen(l);
            orderList.showAsDropDown(v, 0, 0);
            popupAdapter.setName(str);
        } catch (Exception e) {
            Logger.e(e.getLocalizedMessage());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OrderBean orderBean=OrderManager.getCurrentOrder();
        if (orderBean!=null){
            orderBean.nailInfoBean=null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        startActivity(getIntent().setClass(this, ArtistInfo.class).putExtra("id", beans.get(position).uid));
    }

    /**
     * popupWindow的ListAdapter
     */
    class PopListAdapter extends BaseAdapter {
        private Context context;
        private String  name;

        PopListAdapter(Context context) {
            this.context = context;
        }

        public void setName(String name) {
            this.name = name;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return 2;
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
            TextView item;
            if (convertView == null) {
                item = new TextView(context);
                item.setTextSize(15);
                int verticalPadding = (int) DisplayUtil.dp(9, getResources());
                item.setPadding(0, verticalPadding, 0, verticalPadding);
                convertView = item;
            } else {
                item = (TextView) convertView;
            }
            if (position == 0) {
                item.setText(getString(R.string.s_low_to_high, name));
            } else {
                item.setText(getString(R.string.s_high_to_low, name));
            }

            return convertView;
        }
    }

    /**
     * 美甲师列表Adapter
     */
    class ArtistAdapter extends BaseAdapter {
        Context              context;
        List<ArtistListBean> beans;

        ArtistAdapter(Context context, List<ArtistListBean> beans) {
            this.context = context;
            this.beans = beans;
        }

        @Override
        public int getCount() {
            return beans.size();
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
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.artist_list_item, null);
                holder = new ViewHolder();
                holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.tv_order_num = (TextView) convertView.findViewById(R.id.tv_order_num);
                holder.rating = (RatingWidget) convertView.findViewById(R.id.rating);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ArtistListBean bean = beans.get(position);
            holder.tv_name.setText(bean.username);
            //holder.tv_distance.setText(bean.);
            holder.tv_price.setText(getString(R.string.average_price_s, bean.average_price));
            holder.tv_order_num.setText(getString(R.string.order_d_num, bean.order_num));
            holder.rating.setScore(bean.score);
            ImageManager.loadImage(bean.avatar, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    holder.iv_avatar.setImageBitmap(ImageUtil.roundBitmap(loadedImage, context.getResources().getDimensionPixelSize(R.dimen.avatar_size) / 2));
                }
            });
            holder.tv_distance.setText(getString(R.string.distance_s));
            convertView.setTag(holder);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView     tv_name;
        TextView     tv_distance;
        TextView     tv_price;
        TextView     tv_order_num;
        RatingWidget rating;
        ImageView    iv_avatar;
    }
}
