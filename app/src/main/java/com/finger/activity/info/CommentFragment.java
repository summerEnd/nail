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

    /**
     * 设置评论数量的接口。
     * Activity直接实现此接口，fragment在获取到评论数量时会调用setCommentNumber方法
     */
    public interface CommentNumberSetter {
        /**
         * @param good 好评数量
         * @param mid  中评数量
         * @param bad  差评数量
         */
        public void setCommentNumber(int good, int mid, int bad);
    }

    /**
     * @param mid        美甲师id
     * @param product_id 美甲作品id
     * @param grade      好评 中评 差评
     * @return
     */
    public static CommentFragment newInstance(int mid, int product_id, String grade) {
        CommentFragment fragment = new CommentFragment();
        Bundle b = new Bundle();
        b.putInt("product_id", product_id);
        b.putString("grade", grade);
        b.putInt("mid", mid);
        fragment.setArguments(b);
        return fragment;
    }

    /**
     * 美甲作品id
     */
    private int product_id;

    /**
     * 美甲师id
     */
    private int mid;


    /**
     * 好评 中评 差评
     */
    private String grade;

    /**
     * 评论列表数据
     */
    List<CommentBean> beans = new ArrayList<CommentBean>();
    ListController mController;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setListAdapter(new CommentAdapter(beans, getActivity()));
        mController = new ListController(getListView(), this);

        Bundle data = getArguments();
        grade = data.getString("grade");
        product_id = data.getInt("product_id");
        mid = data.getInt("mid");
        getCommentList(1);
    }

    @Override
    public void onLoadMore(AbsListView listView, int nextPage) {
        getCommentList(nextPage);
    }

    /**
     * 获取评论列表
     *
     * @param page 分页展示，第几页
     */
    void getCommentList(int page) {
        RequestParams params = new RequestParams();
        params.put("product_id", product_id);
        params.put("mid", mid);
        params.put("grade", grade);
        params.put("page", page);
        params.put("pagesize", mController.getPageSize());

        FingerHttpClient.post("getCommentList", params, new FingerHttpHandler() {
            @Override
            public void onSuccess(JSONObject o) {
                try {
                    JSONObject data = o.getJSONObject("data");

                    //如果有list
                    if (data.has("list")) {
                        try {
                            JsonUtil.getArray(data.getJSONArray("list"), CommentBean.class, beans);
                        } catch (JSONException e) {

                        }
                    }

                    ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                    if (getActivity() instanceof CommentNumberSetter) {

                        int good = data.has("good_num") ? data.getInt("good_num") : 0;
                        int mid = data.has("normal_num") ? data.getInt("normal_num") : 0;
                        int bad = data.has("bad_num") ? data.getInt("bad_num") : 0;

                        ((CommentNumberSetter) getActivity()).setCommentNumber(good, mid, bad);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 评论列表Adapter
     */
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
