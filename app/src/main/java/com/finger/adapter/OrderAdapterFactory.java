package com.finger.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.info.OrderInfoActivity;
import com.finger.activity.main.user.order.CommentOrder;
import com.finger.activity.plan.OrderConfirm;
import com.finger.entity.OrderBean;
import com.finger.entity.OrderListBean;
import com.finger.entity.OrderManager;
import com.finger.support.util.ContextUtil;
import com.finger.support.widget.NailItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.sp.lib.util.ImageManager;

import java.io.Serializable;
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
        TextView  artist_name;
        TextView  create_time;
        TextView  product_name;
        TextView  tv_price;
        TextView  tv_real_pay;
        TextView  refund_state;
        TextView  tv_pay_state;
        ImageView avatar;
        ImageView cover;
        TextView  button1;
        TextView  button2;
        View      btn_layout;
    }

    /**
     * 已下订单
     */
    static class OrderToPay extends OrderAdapter implements View.OnClickListener {


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

                //获取按钮
                holder.btn_layout = convertView.findViewById(R.id.btn_layout);
                holder.button1 = (TextView) holder.btn_layout.findViewById(R.id.button1);
                holder.button2 = (TextView) holder.btn_layout.findViewById(R.id.button2);
                holder.button1.setOnClickListener(onListButtonClick);
                holder.button2.setOnClickListener(onListButtonClick);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);

            ImageManager.loadImage(bean.product_cover, holder.cover);
            holder.tv_pay_state.setText(getStatusByCode(bean.status));

            holder.product_name.setText(bean.product_name);
            holder.create_time.setText(bean.create_time);
            holder.tv_price.setText(context.getString(R.string.price_r_s, bean.order_price));

            setButtonText(bean.status, holder.button1, holder.button2);

            holder.button1.setTag(bean);
            holder.button2.setTag(bean);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            //            context.startActivity(new Intent(context, OrderConfirm.class).putExtra("bean", (Serializable) v.getTag()));
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
                holder.button1 = (TextView) convertView.findViewById(R.id.pay);
                holder.button1.setOnClickListener(this);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);

            ImageManager.loadImage(bean.product_cover, holder.cover);

            holder.product_name.setText(bean.product_name);
            holder.create_time.setText(bean.create_time);
            //订单价格
            holder.tv_price.setText(context.getString(R.string.price_r_s, bean.order_price));
            //实付
            holder.tv_real_pay.setText(context.getString(R.string.s_price, bean.real_pay));
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
                holder.button1 = (TextView) convertView.findViewById(R.id.comment);
                holder.cover = (ImageView) convertView.findViewById(R.id.cover);
                holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
                holder.tv_real_pay = (TextView) convertView.findViewById(R.id.tv_real_pay);

                holder.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, CommentOrder.class).putExtra("bean", (OrderListBean) v.getTag()));
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);
            holder.button1.setTag(bean);
            holder.product_name.setText(bean.product_name);
            holder.tv_price.setText(context.getString(R.string.price_s, bean.order_price));
            holder.create_time.setText(bean.create_time);
            holder.tv_real_pay.setText(context.getString(R.string.s_price, bean.real_pay));
            ImageManager.loadImage(bean.product_cover, holder.cover, options);
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

                holder.cover = (ImageView) convertView.findViewById(R.id.cover);
                holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.tv_real_pay = (TextView) convertView.findViewById(R.id.tv_real_pay);
                holder.refund_state = (TextView) convertView.findViewById(R.id.refund_state);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OrderListBean bean = (OrderListBean) data.get(position);
            holder.product_name.setText(bean.product_name);
            holder.create_time.setText(bean.create_time);
            holder.tv_price.setText(context.getString(R.string.price_s, bean.order_price));
            holder.tv_real_pay.setText(context.getString(R.string.rmb_s, bean.real_pay));
            int status = bean.status;
            if (STATUS_REFUND_OK == bean.status) {
                holder.refund_state.setTextColor(context.getResources().getColor(R.color.textColorGray));
                holder.refund_state.setText(context.getString(R.string.refund_complete));
            } else if (STATUS_REFUND_FAILED == status) {
                holder.refund_state.setTextColor(context.getResources().getColor(R.color.textColorGray));
                holder.refund_state.setText(context.getString(R.string.refund_failed));
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
            holder.tv_real_pay.setText(context.getString(R.string.real_price_s, bean.real_pay));

            ImageManager.loadImage(bean.product_cover, holder.cover);
            ImageManager.loadImage(bean.product_cover, holder.avatar);
            convertView.setTag(holder);
            return convertView;
        }
    }

    public abstract static class OrderAdapter extends BaseAdapter {
        protected DisplayImageOptions options = ContextUtil.getSquareImgOptions();

        protected List           data;
        protected LayoutInflater inflater;
        protected Context        context;


        final int STATUS_NOT_PAY       = 1;//下单成功，未付款
        final int STATUS_WAIT_SERVICE  = 2;//下单成功，已付款	等待美甲
        final int STATUS_CANCEL        = 3;//未付款，订单被取消
        final int STATUS_APPLY_REFUND  = 4;//已付款，申请退款	退款服务	退款通知
        final int STATUS_REFUND_OK     = 5;//退款成功	退款服务	退款通知
        final int STATUS_REFUND_FAILED = 6;//退款失败	退款服务	退款通知
        final int STATUS_WAIT_COMMENT  = 7;//确认支付成功	等待评价
        final int STATUS_COMMENT_OK    = 8;//评价成功

        SparseArray<String> status;
        SparseArray<String> btn_status;


        View.OnClickListener onListButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListBean bean = (OrderListBean) v.getTag();
                switch (bean.status) {
                    case STATUS_NOT_PAY: {
                        break;
                    }
                    case STATUS_WAIT_SERVICE: {
                        break;
                    }
                    case STATUS_CANCEL: {
                        break;
                    }
                    case STATUS_APPLY_REFUND: {
                        break;
                    }
                    case STATUS_REFUND_OK: {
                        break;
                    }
                    case STATUS_REFUND_FAILED: {
                        break;
                    }
                    case STATUS_WAIT_COMMENT: {
                        break;
                    }
                    case STATUS_COMMENT_OK: {
                        break;
                    }
                }
            }
        };

        /**
         * 0未付款，1已经付款，2确认付款，3评价服务，4申请退款，5退款成功，6退款失败
         *
         * @param state
         * @return
         */
        protected String getStatusByCode(int state) {

            if (status == null) {
                status = new SparseArray();
                status.put(STATUS_NOT_PAY, "未付款");
                status.put(STATUS_WAIT_SERVICE, "已付款");
                status.put(STATUS_CANCEL, "订单取消");
                status.put(STATUS_APPLY_REFUND, "正在退款");
                status.put(STATUS_REFUND_OK, "退款成功");
                status.put(STATUS_REFUND_FAILED, "退款失败");
                status.put(STATUS_WAIT_COMMENT, "等待评价");
                status.put(STATUS_COMMENT_OK, "评价成功");
            }

            return status.get(state);
        }

        /**
         * 获取按钮文字,
         * 两个以上的按钮文字用逗号隔开
         *
         * @return
         */
        protected String getButtonStatusByCode(int states) {

            if (btn_status == null) {
                btn_status = new SparseArray();
                btn_status.put(STATUS_NOT_PAY, "付款");
                btn_status.put(STATUS_WAIT_SERVICE, "申请退款,已付款");
                btn_status.put(STATUS_CANCEL, "");
                btn_status.put(STATUS_APPLY_REFUND, "");
                btn_status.put(STATUS_REFUND_OK, "");
                btn_status.put(STATUS_REFUND_FAILED, "");
                btn_status.put(STATUS_WAIT_COMMENT, "领优惠券,等待评价");
                btn_status.put(STATUS_COMMENT_OK, "领优惠券,评价成功");
            }
            return btn_status.get(states);
        }

        /**
         * 设置按钮文字
         */
        protected void setButtonText(int status, TextView... buttons) {
            String str = getButtonStatusByCode(status);
            if (TextUtils.isEmpty(str)) {
                ((View) buttons[0].getParent()).setVisibility(View.GONE);

            } else {
                String[] texts = str.split(",");
                for (int i = 0; i < buttons.length; i++) {
                    buttons[i].setVisibility(View.VISIBLE);
                    if (i < texts.length)
                        buttons[i].setText(texts[i]);
                }
            }
        }


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
