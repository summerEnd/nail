package com.sp.lib.activity;

import com.loopj.android.http.RequestParams;

public class NetworkInfo {
    public String        url;
    public RequestParams params;
    public boolean showLog    = true;
    public boolean showDialog = true;

    public static class Builder {
        private NetworkInfo networkInfo;

        private Builder() {
            networkInfo = new NetworkInfo();
        }

        public Builder withUrl(String url) {
            networkInfo.url = url;
            return this;
        }

        public Builder withParams(RequestParams params) {
            networkInfo.params = params;
            return this;
        }

        public Builder withShowLog(boolean showLog) {
            networkInfo.showLog = showLog;
            return this;
        }

        public Builder withShowDialog(boolean showDialog) {
            networkInfo.showDialog = showDialog;
            return this;
        }

        public NetworkInfo build() {
            return networkInfo;
        }
    }
}
