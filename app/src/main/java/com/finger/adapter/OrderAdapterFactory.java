package com.finger.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.finger.activity.MainActivity;
import com.finger.activity.main.user.order.ApplyRefund;
import com.finger.activity.main.user.order.CommentOrder;
import com.finger.entity.OrderListBean;
import com.finger.support.util.ContextUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.sp.lib.support.IntentFactory;
import com.sp.lib.util.ClickFullScreen;
import com.sp.lib.util.ImageManager;
import com.sp.lib.util.ImageUtil;

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
                adapter = new RefundNotice(context, data);
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

            //如果是未付款，就隐藏"实付"
            if (bean.status != STATUS_NOT_PAY) {
                holder.real_pay_layout.setVisibility(View.VISIBLE);
                holder.tv_real_pay.setText(mContext.getString(R.string.s_price, bean.real_pay));
            } else {
                holder.real_pay_layout.setVisibility(View.GONE);
            }

            holder.tv_pay_state.setText(getStatusByCode(bean.status));
            holder.tv_pay_state.setTextColor(getColorByCode(bean.status));

            holder.product_name.setText(bean.product_name);
            holder.create_time.setText(bean.create_time);
            holder.tv_price.setText(mContext.getString(R.string.price_r_s, bean.order_price));

            setButtonStatus(bean, holder.button1, holder.button2);

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
            options = new DisplayImageOptions.Builder()
                    .displayer(new BitmapDisplayer() {
                        @Override
                        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
                            imageAware.setImageBitmap(ImageUtil.roundBitmap(bitmap, context.getResources().getDimensionPixelSize(R.dimen.avatar_size) / 2));
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

        SparseArray<String> status;
        SparseArray<String> btn_status;


        View.OnClickListener onListButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                OrderListBean bean = (OrderListBean) v.getTag();
                switch (bean.status) {
                    case STATUS_NOT_PAY: {
                        //按钮：付款
                        ContextUtil.toast("付款");
                        break;
                    }
                    case STATUS_WAIT_SERVICE: {
                        //按钮：申请退款,确认付款
                        if (v.getId() == R.id.button1) {
                            v.getContext().startActivity(
                                    new Intent(v.getContext(), ApplyRefund.class)
                                            .putExtra("id", bean.id));
                        } else {
                            ContextUtil.toast("确认付款");
                        }
                        break;
                    }
                    //case STATUS_CANCEL: {
                    //
                    //    break;
                    //}
                    //
                    //case STATUS_APPLY_REFUND: {
                    //
                    //    break;
                    //}

                    //case STATUS_REFUND_OK: {
                    //
                    //    break;
                    //}
                    //
                    //case STATUS_REFUND_FAILED: {
                    //
                    //    break;
                    //}

                    case STATUS_WAIT_COMMENT: {
                        //按钮：领优惠券,等待评价
                        if (v.getId() == R.id.button1) {
                            getCoupon(v);
                        } else {
                            mContext.startActivity(new Intent(mContext, CommentOrder.class).putExtra("bean", (Serializable) v.getTag()));
                        }
                        break;
                    }

                    case STATUS_COMMENT_OK: {
                        //按钮：领优惠券
                        getCoupon(v);
                        break;
                    }
                }
            }
        };

        /**
         * 获取优惠券对话框
         *
         * @param v
         */
        void getCoupon(final View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(R.string.get_coupon)
                    .setMessage(R.string.share_coupon_notice)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity activity = (MainActivity) mContext;
                            OrderListBean bean = (OrderListBean) v.getTag();
                            activity.setGetCouponOrderId(bean.id);
                            activity.startActivityForResult(IntentFactory.share(mContext.getString(R.string.app_name), "要你用你就用，别乌拉！"), MainActivity.REQUEST_SHARE);
                        }
                    })
                    .show();

        }

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
        protected void setButtonStatus(OrderListBean bean, TextView... buttons) {
            int status = bean.status;
            String str = getButtonStatusByCode(status);
            //Logger.i_format("status:%d str:%s status_str:%s", status, str, getStatusByCode(status));
            if (TextUtils.isEmpty(str)) {
                ((View) buttons[0].getParent()).setVisibility(View.INVISIBLE);

            } else {
                ((View) buttons[0].getParent()).setVisibility(View.VISIBLE);
                String[] texts = str.split(",");
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
            holder.tv_price.setText(mContext.getString(R.string.price_r_s, bean.order_price));
            //实付
            holder.tv_real_pay.setText(mContext.getString(R.string.s_price, bean.real_pay));
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
        protected DisplayImageOptions options = ContextUtil.getSquareImgOptions();

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
                holder.button1 = (TextView) convertView.findViewById(R.id.button1);
                holder.cover = (ImageView) convertView.findViewById(R.id.cover);
                holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.create_time = (TextView) convertView.findViewById(R.id.create_time);
                holder.tv_real_pay = (TextView) convertView.findViewById(R.id.tv_real_pay);

                holder.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, CommentOrder.class).putExtra("bean", (OrderListBean) v.getTag()));
                    }
                });
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OrderListBean bean = (OrderListBean) data.get(position);
            holder.button1.setTag(bean);
            holder.product_name.setText(bean.product_name);
            holder.tv_price.setText(mContext.getString(R.string.price_s, bean.order_price));
            holder.create_time.setText(bean.create_time);
            holder.tv_real_pay.setText(mContext.getString(R.string.s_price, bean.real_pay));
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
            holder.tv_price.setText(mContext.getString(R.string.price_s, bean.order_price));
            holder.tv_real_pay.setText(mContext.getString(R.string.rmb_s, bean.real_pay));
            int status = bean.status;
            if (STATUS_REFUND_OK == bean.status) {
                holder.refund_state.setTextColor(mContext.getResources().getColor(R.color.textColorGray));
                holder.refund_state.setText(mContext.getString(R.string.refund_complete));
            } else if (STATUS_REFUND_FAILED == status) {
                holder.refund_state.setTextColor(mContext.getResources().getColor(R.color.textColorGray));
                holder.refund_state.setText(mContext.getString(R.string.refund_failed));
            } else {
                holder.refund_state.setTextColor(mContext.getResources().getColor(R.color.textColorPink));
                holder.refund_state.setText(mContext.getString(R.string.refunding));
            }

            convertView.setTag(holder);
            return convertView;
        }
    }
}
