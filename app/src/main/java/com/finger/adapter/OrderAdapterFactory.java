package com.finger.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.finger.R;
import com.finger.activity.base.OrderListCallback;
import com.finger.entity.OrderListBean;
import com.finger.support.util.ContextUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.sp.lib.support.ShareWindow;
import com.sp.lib.util.ClickFullScreen;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

import java.util.List;


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
                adapter = new OrderToPay(context, data);
                break;
            }
            case WAIT_COMMENT: {
                adapter = new OrderToPay(context, data);
                break;
            }
            case REFUND: {
                adapter = new OrderToPay(context, data);
                break;
            }
            case ORDER_NOTICE: {
                adapter = new OrderNotice(context, data);
                break;
            }
            case REFUND_NOTICE: {
                adapter = new OrderNotice(context, data);
                break;
            }
        }
        return adapter;
    }

    static class ViewHolder {
        //美甲师姓名
        TextView  artist_name;
        //用户名
        TextView  username;
        //订单时间
        TextView  create_time;
        //作品名称
        TextView  product_name;
        //作品价格
        TextView  tv_price;
        //实际价格
        TextView  tv_real_pay;
        //退款状态
        TextView  refund_state;
        //付款状态
        TextView  tv_pay_state;
        //头像
        ImageView avatar;
        //作品封面
        ImageView cover;
        TextView  button1;
        TextView  button2;
        //按钮
        View      btn_layout;
        //实付
        View      real_pay_layout;
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

                //实付
                holder.real_pay_layout = holder.btn_layout.findViewById(R.id.real_pay_layout);
                holder.tv_real_pay = (TextView) holder.real_pay_layout.findViewById(R.id.tv_real_pay);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);

            ImageManager.loadImage(bean.product_cover, holder.cover);

            holder.tv_pay_state.setText(getStatusByCode(bean.status));
            holder.tv_pay_state.setTextColor(getColorByCode(bean.status));

            holder.product_name.setText(bean.product_name);
            holder.create_time.setText(bean.create_time);
            holder.tv_price.setText(mContext.getString(R.string.price_r_s, bean.order_price));

            int button_number = setButtonStatus(bean, holder.button1, holder.button2);
            if (button_number > 0) {
                holder.real_pay_layout.setVisibility(View.VISIBLE);
                holder.tv_real_pay.setText(mContext.getString(R.string.s_price, bean.real_pay));
            } else {
                holder.real_pay_layout.setVisibility(View.GONE);
            }
            //如果是等待评价或者评价成功，就设置按钮状态
            if (bean.status == STATUS_WAIT_COMMENT || bean.status == STATUS_COMMENT_OK) {
                //优惠券按钮
                setHasGetCoupon(holder.button1, bean.get_coupon);
                //评价按钮
                holder.button2.setVisibility(bean.status == STATUS_WAIT_COMMENT ? View.VISIBLE : View.GONE);
            }

            convertView.setTag(holder);
            return convertView;
        }

        /**
         * 设置是否领取优惠券
         *
         * @param couponButton
         * @param get_coupon
         */
        void setHasGetCoupon(View couponButton, String get_coupon) {
            //1 已领取 0未领取
            if ("1".equals(get_coupon)) {
                couponButton.setVisibility(View.GONE);
            } else {
                couponButton.setVisibility(View.VISIBLE);
            }
        }

    }


    /**
     * 退款通知
     */
    static class RefundNotice extends OrderAdapter implements View.OnClickListener {
        protected DisplayImageOptions options;


        public RefundNotice(Context context, List data) {
            super(context, data);
            options = new DisplayImageOptions.Builder()
                    .displayer(new BitmapDisplayer() {
                        @Override
                        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
                            imageAware.setImageBitmap(ImageUtil.roundBitmap(bitmap, mContext.getResources().getDimensionPixelSize(R.dimen.avatar_size) / 2));
                        }
                    })
                    .build();
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
                holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
                holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.button1 = (TextView) convertView.findViewById(R.id.button1);
                holder.tv_real_pay = (TextView) convertView.findViewById(R.id.tv_real_pay);
                holder.username = (TextView) convertView.findViewById(R.id.username);
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                holder.cover = (ImageView) convertView.findViewById(R.id.cover);
                holder.cover.setOnClickListener(this);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);
            holder.username.setText(bean.username);
            holder.create_time.setText(bean.create_time);
            holder.product_name.setText(bean.product_name);
            holder.tv_price.setText(mContext.getString(R.string.price_r_s, bean.order_price));
            holder.tv_real_pay.setText(mContext.getString(R.string.s_price, bean.real_pay));
            holder.button1.setText(getStatusByCode(bean.status));
            holder.button1.setTextColor(getColorByCode(bean.status));

            ImageManager.loadImage(bean.product_cover, holder.cover);
            ImageManager.loadImage(bean.avatar, holder.avatar, options);
            holder.cover.setTag(bean.product_cover);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            ClickFullScreen window = new ClickFullScreen(v.getContext());
            window.setNetWorkImage(v.getTag().toString());
            window.showFor(v);
        }
    }

    /**
     * 订单通知
     */
    static class OrderNotice extends OrderAdapter implements View.OnClickListener {

        DisplayImageOptions options;

        public OrderNotice(final Context context, List data) {
            super(context, data);
            options = ContextUtil.getAvatarOptions();
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
                holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
                holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.tv_real_pay = (TextView) convertView.findViewById(R.id.tv_real_pay);
                holder.username = (TextView) convertView.findViewById(R.id.username);
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                holder.cover = (ImageView) convertView.findViewById(R.id.cover);
                holder.tv_pay_state = (TextView) convertView.findViewById(R.id.tv_state);
                holder.cover.setOnClickListener(this);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);
            holder.username.setText(bean.username);
            holder.create_time.setText(bean.create_time);
            holder.product_name.setText(bean.product_name);
            holder.tv_price.setText(mContext.getString(R.string.price_r_s, bean.order_price));
            holder.tv_real_pay.setText(mContext.getString(R.string.s_price, bean.real_pay));
            holder.tv_pay_state.setTextColor(getArtistColorByCode(bean.status));
            holder.tv_pay_state.setText(getArtistStatusByCode(bean.status));
            ImageManager.loadImage(bean.product_cover, holder.cover);
            ImageManager.loadImage(bean.avatar, holder.avatar, options);
            holder.cover.setTag(bean.product_cover);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            ClickFullScreen window = new ClickFullScreen(v.getContext());
            window.setNetWorkImage(v.getTag().toString());
            window.showFor(v);
        }
    }

    public abstract static class OrderAdapter extends BaseAdapter {

        protected List           data;
        protected LayoutInflater inflater;
        protected Context        mContext;


        final int STATUS_NOT_PAY       = 1;//下单成功，未付款
        final int STATUS_WAIT_SERVICE  = 2;//下单成功，已付款	等待美甲
        final int STATUS_CANCEL        = 3;//未付款，订单被取消
        final int STATUS_APPLY_REFUND  = 4;//已付款，申请退款	退款服务	退款通知
        final int STATUS_REFUND_OK     = 5;//退款成功	退款服务	退款通知
        final int STATUS_REFUND_FAILED = 6;//退款失败	退款服务	退款通知
        final int STATUS_WAIT_COMMENT  = 7;//确认支付成功	等待评价
        final int STATUS_COMMENT_OK    = 8;//评价成功
        ShareWindow         window;
        //普通用户状态
        SparseArray<String> status;
        //美甲师状态
        SparseArray<String> artistStatus;
        SparseArray<String> btn_status;
        OrderListCallback   callback;

        /**
         * 所有订单上的按钮点击事件都在这里处理
         */
        View.OnClickListener onListButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (callback == null) {
                    return;
                }

                OrderListBean bean = (OrderListBean) v.getTag();
                switch (bean.status) {
                    case STATUS_NOT_PAY: {
                        callback.onPay(bean);
                        break;
                    }
                    case STATUS_WAIT_SERVICE: {
                        //按钮：申请退款,确认付款
                        if (v.getId() == R.id.button1) {
                            callback.onApplyRefund(bean);
                        } else {
                            callback.onConfirmPay(bean);
                        }
                        break;
                    }


                    case STATUS_WAIT_COMMENT: {
                        //按钮：领优惠券,等待评价
                        if (v.getId() == R.id.button1) {
                            callback.onGetCoupon(bean);
                        } else {
                            callback.onComment(bean);
                        }
                        break;
                    }

                    case STATUS_COMMENT_OK: {
                        //按钮：领优惠券
                        callback.onGetCoupon(bean);
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

        protected String getArtistStatusByCode(int state) {
            if (artistStatus == null) {
                artistStatus = new SparseArray();
                artistStatus.put(STATUS_NOT_PAY, "未付款");
                artistStatus.put(STATUS_WAIT_SERVICE, "等待服务");
                artistStatus.put(STATUS_CANCEL, "订单取消");
                artistStatus.put(STATUS_APPLY_REFUND, "正在退款");
                artistStatus.put(STATUS_REFUND_OK, "退款成功");
                artistStatus.put(STATUS_REFUND_FAILED, "退款失败");
                artistStatus.put(STATUS_WAIT_COMMENT, "等待评价");
                artistStatus.put(STATUS_COMMENT_OK, "已评价");
            }

            return artistStatus.get(state);
        }

        protected int getArtistColorByCode(int status){
            int color;
            if (status==STATUS_WAIT_SERVICE){
                color=0xffe5376b;
            }else{
                color=0xff808080;
            }
            return color;
        }

        protected int getColorByCode(int status) {
            int color;

            if (status == STATUS_REFUND_FAILED) {
                color = 0xfffa1010;
            } else if (status == STATUS_APPLY_REFUND) {
                color = 0xffe5376b;
            } else if (TextUtils.isEmpty(getButtonStatusByCode(status))) {
                color = 0xff808080;
            } else {
                color = 0xff3a3a3a;
            }

            return color;
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
                btn_status.put(STATUS_WAIT_SERVICE, "申请退款,确认付款");
                btn_status.put(STATUS_CANCEL, "");
                btn_status.put(STATUS_APPLY_REFUND, "");
                btn_status.put(STATUS_REFUND_OK, "");
                btn_status.put(STATUS_REFUND_FAILED, "");
                btn_status.put(STATUS_WAIT_COMMENT, "领优惠券,评价订单");
                btn_status.put(STATUS_COMMENT_OK, "领优惠券");
            }
            return btn_status.get(states);
        }

        /**
         * 设置按钮文字
         */
        protected int setButtonStatus(OrderListBean bean, TextView... buttons) {
            int button_number = 0;
            int status = bean.status;
            String str = getButtonStatusByCode(status);
            //Logger.i_format("status:%d str:%s status_str:%s", status, str, getStatusByCode(status));
            if (TextUtils.isEmpty(str)) {
                ((View) buttons[0].getParent()).setVisibility(View.INVISIBLE);

            } else {
                ((View) buttons[0].getParent()).setVisibility(View.VISIBLE);
                String[] texts = str.split(",");
                button_number = texts.length;
                //遍历按钮数组
                for (int i = 0; i < buttons.length; i++) {
                    //给安妮添加tag
                    buttons[i].setTag(bean);

                    //根据按钮文字数组长度setVisibility
                    if (i < texts.length) {

                        buttons[i].setVisibility(View.VISIBLE);
                        buttons[i].setText(texts[i]);
                    } else {
                        buttons[i].setVisibility(View.GONE);
                    }
                }
            }
            return button_number;
        }

        /**
         * 设置订单列表回调
         *
         * @param callback
         */

        public void setCallback(OrderListCallback callback) {
            this.callback = callback;
        }

        public OrderAdapter(Context context, List data) {
            this.data = data;
            this.mContext = context;
            inflater = LayoutInflater.from(context);
        }

        public final List getData() {
            return data;
        }
    }


}
