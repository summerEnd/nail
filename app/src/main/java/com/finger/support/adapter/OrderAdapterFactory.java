package com.finger.support.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.user.order.ApplyRefund;
import com.finger.activity.user.order.CommentOrder;

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

    public static OrderAdapter getAdapter(Context context, OrderType type, List data) {
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
        TextView tv1;
        TextView tv2;
        Button button1;
    }

    /**
     * 已下订单
     */
    static class OrderToPay extends OrderAdapter {


        public OrderToPay(Context context, List data) {
            super(context, data);
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
                convertView = inflater.inflate(R.layout.list_item_order_to_pay, null);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setTag(holder);
            return convertView;
        }
    }

    /**
     * 等待美甲
     */
    static class WaitService extends OrderAdapter {


        public WaitService(Context context, List data) {
            super(context, data);
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
                convertView = inflater.inflate(R.layout.list_item_order_wait_service, null);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setTag(holder);
            return convertView;
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
                convertView = inflater.inflate(R.layout.list_item_order_wait_comment, null);
                holder.button1 = (Button) convertView.findViewById(R.id.comment);
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
                convertView = inflater.inflate(R.layout.list_item_order_refund, null);
                holder.tv1 = (TextView) convertView.findViewById(R.id.refund_state);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position%2==1){
                holder.tv1.setTextColor(context.getResources().getColor(R.color.textColorGray));
                holder.tv1.setText(context.getString(R.string.refund_complete));
            }else{
                holder.tv1.setTextColor(context.getResources().getColor(R.color.textColorPink));
                holder.tv1.setText(context.getString(R.string.refunding));
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
                convertView = inflater.inflate(R.layout.list_item_refund_notice, null);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.setTag(holder);
            return convertView;
        }
    }

    /**
     * 退款通知
     */
    static class OrderNotice extends OrderAdapter {

        public OrderNotice(Context context, List data) {
            super(context, data);
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
                convertView = inflater.inflate(R.layout.list_item_order_notice, null);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
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
