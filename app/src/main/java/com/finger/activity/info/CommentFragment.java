package com.finger.activity.info;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.finger.support.util.JsonUtil;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ListController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价列表
 */
public class CommentFragment extends ListFragment implements ListController.Callback {


    public static CommentFragment newInstance(int product_id, String grade) {
        CommentFragment fragment = new CommentFragment();
        fragment.setGrade(grade);
        fragment.setProduct_id(product_id);
        return fragment;
    }

    private int    product_id;
    private String grade;

    List<CommentBean> data = new ArrayList<CommentBean>();
    ListController mController;

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setListAdapter(new CommentAdapter(data, getActivity()));
        mController = new ListController(getListView(), this);
        getCommentList(1);
    }

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getCommentList(nextPage);
    }

    void getCommentList(int page) {
        RequestParams params = new RequestParams();
        params.put("product_id", product_id);
        params.put("grade", grade);
        params.put("page", page);
        params.put("pagesize", mController.getPageSize());

        FingerHttpClient.post("getCommentList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JsonUtil.getArray(o.getJSONArray("data"), CommentBean.class, data);
                    ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class CommentAdapter extends BaseAdapter {
        List<CommentBean> data;
        private Context context;
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageForEmptyUri(R.drawable.onLoadImage)
                .showImageOnFail(R.drawable.onLoadImage)
                .cacheInMemory(true).cacheOnDisc(true).build();

        CommentAdapter(List<CommentBean> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
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
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_topic, null);
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.userName = (TextView) convertView.findViewById(R.id.userName);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.content = (TextView) convertView.findViewById(R.id.tv_content);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            CommentBean bean = data.get(position);
            ImageManager.loadImage(bean.avatar, holder.avatar, options);
            if (!TextUtils.isEmpty(bean.image)) {
                holder.image.setVisibility(View.VISIBLE);
                ImageManager.loadImage(bean.image, holder.image, options);
            } else {
                holder.image.setVisibility(View.GONE);
            }
            holder.userName.setText(bean.username);
            holder.time.setText(bean.comment_time);
            holder.content.setText(bean.content);
            convertView.setTag(holder);
            return convertView;
        }

    }

    class ViewHolder {
        ImageView avatar;
        ImageView image;
        TextView  content;
        TextView  userName;
        TextView  time;
    }

    public static class CommentBean {

        public String username;
        public String id;
        public String avatar;
        public String uid;
        public String mid;
        public String product_id;
        public String comment_time;
        public String is_delete;
        public String content;
        public String grade;
        public String professional;
        public String talk;
        public String ontime;
        public String image;
    }
}
