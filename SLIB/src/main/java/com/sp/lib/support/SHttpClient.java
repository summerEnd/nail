package com.sp.lib.support;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SHttpClient {

    private static final String TAG = "SLOG";
    private static ProgressDialogCreator mDialogCreator;
    private static Dialog mDialog;
    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.setTimeout(10000);
    }

    /**
     * an interface to create a dialog which is show as progress
     */
    public interface ProgressDialogCreator {
        public Dialog onCreateDialog();
    }

    private static class Utf8JsonHandler extends JsonHttpResponseHandler {
        private WebJsonHttpHandler handler;
        private boolean showLog;


        private Utf8JsonHandler(WebJsonHttpHandler handler, boolean showLog) {
            super("UTF-8");
            this.handler = handler;
            this.showLog = showLog;
        }

        public void setHandler(WebJsonHttpHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            sendSuccess(response, null);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            sendSuccess(null, response);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            super.onSuccess(statusCode, headers, responseString);
            try {
                sendSuccess(new JSONObject(responseString), null);
            } catch (JSONException e) {
                e.printStackTrace();
                sendSuccess(null, null);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.i(TAG, "Throwable:" + throwable);
            sendFail("net error:" + statusCode);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.i(TAG, "JSONObject:" + errorResponse);
            sendFail("data error" + statusCode);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.i(TAG, "JSONArray:" + errorResponse);
            sendFail("data error" + statusCode);
        }

        void sendSuccess(JSONObject object, JSONArray array) {
            if (handler == null) {
                return;
            }

            try {
                if (showLog)
                    Log.i(TAG, "sendSuccess:" + (object == null ? null : object.toString(6)));
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            handler.onSuccess(object, array);
        }

        void sendFail(String msg) {
            if (handler == null) {
                return;
            }
            Log.i(TAG, "sendFail:" + msg);
            handler.onFail(msg);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (mDialog != null && handler.showDialog) mDialog.dismiss();
        }
    }

    /**
     * create a progress Dialog,to show when doing net work
     *
     * @param dialogCreator
     */
    public static void setDialogCreator(ProgressDialogCreator dialogCreator) {
        SHttpClient.mDialogCreator = dialogCreator;
        if (mDialogCreator == null) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            mDialog = null;
        }
    }


    public static void post(String url, RequestParams params, WebJsonHttpHandler handler, boolean showLog) {
        if (handler.showDialog) createDialog();
        post(url, params, new Utf8JsonHandler(handler,showLog), showLog);

    }

    public static void post(String url, RequestParams params, ResponseHandlerInterface handlerInterface, boolean showLog) {

        if (showLog) {
            Log.i("SHTTP", params.toString());
            String[] strings = params.toString().split("&");
            StringBuilder builder = new StringBuilder();
            builder.append(url + "{\n");
            for (int i = 0; i < strings.length; i++) {
                builder.append(strings[i] + "\n");
            }
            builder.append("}");
            Log.i("SHTTP", builder.toString());
        }

        client.post(url, params, handlerInterface);
    }

    private static void createDialog() {
        if (mDialogCreator == null) return;
        if (mDialog == null) {
            mDialog = mDialogCreator.onCreateDialog();
        }
        mDialog.show();
    }
    public static boolean isConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null&& info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.v("error",e.toString());
        }
        return false;
    }
}
