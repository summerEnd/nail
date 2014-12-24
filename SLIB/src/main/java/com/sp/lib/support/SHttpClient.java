package com.sp.lib.support;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

public class SHttpClient {

    private static ProgressDialogCreator mDialogCreator;
    private static Dialog mDialog;
    private static Utf8JsonHandler utf8JsonHandler = new Utf8JsonHandler();
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

        public Utf8JsonHandler() {
            super("UTF-8");
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
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            sendFail("net error:" + statusCode);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            sendFail("data error");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            sendFail("data error");
        }

        void sendSuccess(JSONObject object, JSONArray array) {
            if (handler == null) {
                return;
            }

            if (object!=null){
                Log.i("SLOG",object+"");
            }else{
                Log.i("SLOG",array+"");
            }

            handler.onSuccess(object, array);
        }

        void sendFail(String msg) {
            if (handler == null) {
                return;
            }
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
    }


    public static void post(String url, RequestParams params, WebJsonHttpHandler handler) {
        utf8JsonHandler.setHandler(handler);
        if (handler.showDialog) createDialog();
        post(url, params, utf8JsonHandler);

    }

    public static void post(String url, RequestParams params, ResponseHandlerInterface handlerInterface) {

        client.post(url, params, handlerInterface);
    }

    private static void createDialog() {
        if (mDialogCreator == null) return;
        mDialog = mDialogCreator.onCreateDialog();
        mDialog.show();
    }
}
