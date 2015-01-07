package com.finger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.main.user.order.CommentOrder;
import com.finger.entity.OrderBean;
import com.finger.entity.OrderListBean;
import com.finger.entity.OrderManager;
import com.sp.lib.util.ImageManager;

import java.util.List;

/**
 * Created by acer on 2014/12/23.
 */
public class OrderAdapterFactory {


    public enum OrderType {
        ORDER_TO_PAY,//已下订单
        WAIT_SERVICE,//等待服务
        WAIT_COMMENT,//等待评价
        REFUND,//退款
        ORDER_NOTICE,//订单通知
        REFUND_NOTICE//退款通知
    }


    public static OrderAdapter getAdapter(Context context, OrderType type, List<OrderListBean> data) {
        OrderAdapter adapter = null;
        switch (type) {
            case ORDER_TO_PAY: {
                adapter = new OrderToPay(context, data);
                break;
            }
            case WAIT_SERVICE: {
                adapter = new WaitService(context, data);
                break;
            }
            case WAIT_COMMENT: {
                adapter = new WaitComment(context, data);
                break;
            }
            case REFUND: {
                adapter = new Refund(context, data);
                break;
            }
            case ORDER_NOTICE: {
                adapter = new OrderNotice(context, data);
                break;
            }
            case REFUND_NOTICE: {
                adapter = new RefundNotice(context, data);
                break;
            }
        }
        return adapter;
    }

    static class ViewHolder {
        TextView artist_name;
        TextView create_time;
        TextView product_name;
        TextView tv_price;
        TextView tv_real_pay;
        TextView refund_state;
        TextView tv_pay_state;
        ImageView avatar;
        ImageView cover;
        View button1;
    }

    /**
     * 已下订单
     */
    static class OrderToPay extends OrderAdapter implements View.OnClickListener {
        String[] status = new String[]{"未付款", "已经付款", "确认付款", "评价服务", "申请退款", "退款成功", "退款失败"};

        public OrderToPay(Context context, List data) {
            super(context, data);
        }

        @Override
        public int getCount() {
            return data.size();
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
                convertView = inflater.inflate(R.layout.list_item_order_to_pay, null);
                holder.tv_pay_state = (TextView) convertView.findViewById(R.id.tv_pay_state);
                holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.cover = (ImageView) convertView.findViewById(R.id.cover);
                holder.button1 = convertView.findViewById(R.id.pay);
                holder.button1.setOnClickListener(this);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);

            ImageManager.loadImage(bean.product_cover, holder.cover);
            holder.tv_pay_state.setText(status[bean.status]);

            holder.product_name.setText(bean.product_name);
            holder.create_time.setText(bean.create_time);
            holder.tv_price.setText(bean.order_price);

            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            OrderBean order = OrderManager.createOrder();

        }
    }

    /**
     * 等待美甲
     */
    static class WaitService extends OrderAdapter implements View.OnClickListener {


        public WaitService(Context context, List data) {
            super(context, data);
        }

        @Override
        public int getCount() {
            return data.size();
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
                convertView = inflater.inflate(R.layout.list_item_order_wait_service, null);
                holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                holder.tv_real_pay = (TextView) convertView.findViewById(R.id.tv_real_pay);
                holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.cover = (ImageView) convertView.findViewById(R.id.cover);
                holder.button1 = convertView.findViewById(R.id.pay);
                holder.button1.setOnClickListener(this);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);

            ImageManager.loadImage(bean.product_cover, holder.cover);

            holder.product_name.setText(bean.product_name);
            holder.create_time.setText(bean.create_time);
            holder.tv_price.setText(bean.order_price);
            holder.tv_real_pay.setText(bean.order_price);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void onClick(View v) {

        }
    }

    /**
     * 等待评价
     */
    static class WaitComment extends OrderAdapter {

        public WaitComment(Context context, List data) {
            super(context, data);
        }


        @Override
        public int getCount() {
            return data.size();
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
                convertView = inflater.inflate(R.layout.list_item_order_wait_comment, null);
                holder.button1 = convertView.findViewById(R.id.comment);
                holder.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, CommentOrder.class));
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setTag(holder);
            return convertView;
        }
    }

    /**
     * 退款
     */
    static class Refund extends OrderAdapter {


        public Refund(Context context, List data) {
            super(context, data);
        }

        @Override
        public int getCount() {
            return data.size();
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
                convertView = inflater.inflate(R.layout.list_item_order_refund, null);
                holder.refund_state = (TextView) convertView.findViewById(R.id.refund_state);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position % 2 == 1) {
                holder.refund_state.setTextColor(context.getResources().getColor(R.color.textColorGray));
                holder.refund_state.setText(context.getString(R.string.refund_complete));
            } else {
                holder.refund_state.setTextColor(context.getResources().getColor(R.color.textColorPink));
                holder.refund_state.setText(context.getString(R.string.refunding));
            }

            convertView.setTag(holder);
            return convertView;
        }
    }

    /**
     * 退款通知
     */
    static class RefundNotice extends OrderAdapter {


        public RefundNotice(Context context, List data) {
            super(context, data);
        }

        @Override
        public int getCount() {
            return data.size();
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
                convertView = inflater.inflate(R.layout.list_item_refund_notice, null);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setTag(holder);
            return convertView;
        }
    }

    /**
     * 订单通知
     */
    static class OrderNotice extends OrderAdapter {

        public OrderNotice(Context context, List data) {
            super(context, data);
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
                convertView = inflater.inflate(R.layout.list_item_order_notice, null);
                holder.artist_name = (TextView) convertView.findViewById(R.id.artist_name);
                holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
                holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.tv_real_pay = (TextView) convertView.findViewById(R.id.tv_real_pay);
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                holder.cover = (ImageView) convertView.findViewById(R.id.cover);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);
//            holder.artist_name.setText(bean.);
            holder.create_time.setText(bean.create_time);
            holder.product_name.setText(bean.product_name);
            holder.tv_price.setText(context.getString(R.string.price_r_s, bean.order_price));
//            holder.tv_real_pay.setText(context.getString(R.string.real_price_s,));

            ImageManager.loadImage(bean.product_cover, holder.cover);
            ImageManager.loadImage(bean.product_cover, holder.avatar);
            convertView.setTag(holder);
            return convertView;
        }
    }

    public abstract static class OrderAdapter extends BaseAdapter {
        protected List data;
        protected LayoutInflater inflater;
        protected Context context;

        public OrderAdapter(Context context, List data) {
            this.data = data;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        public final List getData() {
            return data;
        }

        public AdapterView.OnItemClickListener getOnItemClickListener() {
            return null;
        }
    }
}
