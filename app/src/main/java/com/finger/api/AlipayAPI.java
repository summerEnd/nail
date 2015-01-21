package com.finger.api;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AlipayAPI {

    public static final String PARTNER = "2088811275152442";

    public static final String SELLER = "wjdzsw188@163.com";

    public static final String RSA_PRIVATE =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL8QSJ0C25QLHm1b" +
                    "ahAcHO7MgfHldVb7NzUtkIG3fI6FncilbmXWz+oL+MYePs9AoyScsOtwR09nZ/rz" +
                    "5mHOp/hf4ZaLmihASaRgUR/xyHix3WoeJybhA1NSpP/pcUalbpgNsYjOhahqDdBe" +
                    "uCYSuWApBEI88ajs1gCcISt2fk2dAgMBAAECgYEAivvUZmqiwdFIw/IAeFGK9mbL" +
                    "i+QHdEtvwH4xpTqNH7uwqDk20lvtiGpHAA8WT3rMciCNTeax6N/mspVjG/jRE8aV" +
                    "BOr7ZPeKee/wjwMBY8fuP8Rb37p2t/KgTLcbkeFW3+ebc2iSHi5vXoYPQXIVrnfZ" +
                    "7KCdoV0rOfzRDgUN8b0CQQD6yyQH50UJu0c42td89+rS3vm6NLaUsamVipxcr6Hi" +
                    "SmOEATniSbxqycNvcr+O04+v/8j9maiEjYd8Y2iDKCr3AkEAwwe0R6EUyHyPXxH6" +
                    "h8ovxbZROxxZsx+mmy4UGYhYvwjl3FGt9v4YD0gE5MILUkzsrDOPUfyZsVOvR4QY" +
                    "SeTzCwJAU+DjQR1xcqrHTFWtIqfMSxC2VzfQJPUyscg1Oa6oJwYYOIssb+mXcePf" +
                    "UIQBW2SYtxWGhIMC4KpxOQIKb2tcywJAJ7sfD+SR3lH5xy1bc2ROHSIKJFefMm2F" +
                    "PGHDuHvdUHWYliyRmxqifiJ21L9vHQIMyPvr+5DRIp3gvFn9tLgOhwJAAJvDAMts" +
                    "k2ArKWQe+P302mbm4JVgyaXqKC4T5f7ipgTHqS4Ikqc4zWZMv1Y8NVYlyL8LBty8" +
                    "6qi/RGm2hf3b0A==";

    //    public static final String RSA_PUBLIC =
    //            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/EEidAtuUCx5tW2oQHBzuzIHx" +
    //                    "5XVW+zc1LZCBt3yOhZ3IpW5l1s/qC/jGHj7PQKMknLDrcEdPZ2f68+Zhzqf4X+GW" +
    //                    "i5ooQEmkYFEf8ch4sd1qHicm4QNTUqT/6XFGpW6YDbGIzoWoag3QXrgmErlgKQRC" +
    //                    "PPGo7NYAnCErdn5NnQIDAQAB";

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Activity mActivity;

    public AlipayAPI(Activity activity) {
        this.mActivity = activity;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    Result resultObj = new Result((String) msg.obj);
                    String resultStatus = resultObj.resultStatus;

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(mActivity, "支付成功",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(mActivity, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(mActivity, "支付失败",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(mActivity, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void start(
            String subject,
            String body,
            //支付的价格
            String price) {
        String orderInfo = getOrderInfo(subject, body, price);
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask payTask = new PayTask(mActivity);
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(mActivity);
        String version = payTask.getVersion();
        Toast.makeText(mActivity, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String subject, String body, String price) {
        // 合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + 0.01 + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://pnail.ywswl.com/Home/Alipay/notify_url.html"
                + "\"";

        // 接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 获取外部订单号
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
