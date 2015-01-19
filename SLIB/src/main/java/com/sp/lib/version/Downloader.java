package com.sp.lib.version;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Downloader {
    /**
     * registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
     * 可以监听下载完成
     * <p/>
     * 例如：
     * * <p/>
     * <p/>
     * {@code BroadcastReceiver receiver = new BroadcastReceiver() {
     * e public void onReceive(Context context, Intent intent) {
     * DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
     * DownloadManager.Query query = new DownloadManager.Query();
     * query.setFilterById(prefs.getLong(DL_ID, 0));
     * Cursor c = manager.query(query);
     * if (c.moveToFirst()) {
     * int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
     * switch (status) {
     * case DownloadManager.STATUS_PAUSED:
     * Log.v("down", "STATUS_PAUSED");
     * case DownloadManager.STATUS_PENDING:
     * Log.v("down", "STATUS_PENDING");
     * case DownloadManager.STATUS_RUNNING:
     * //正在下载，不做任何事情
     * Log.v("down", "STATUS_RUNNING");
     * break;
     * case DownloadManager.STATUS_SUCCESSFUL:
     * //完成
     * Log.v("down", "下载完成");
     * break;
     * case DownloadManager.STATUS_FAILED:
     * //清除已下载的内容，重新下载
     * Log.v("down", "STATUS_FAILED");
     * manager.remove(prefs.getLong(DL_ID, 0));
     * prefs.edit().clear().commit();
     * break;
     * }
     * }
     * }
     * };
     * }
     * }
     *
     * @param context
     * @param downloadUrl
     * @param fileName
     * @param notifyCationTitle
     * @param notifyCationMessage
     * @return
     * @Overrid
     */
    public long download(Context context, String downloadUrl, String fileName, String notifyCationTitle, String notifyCationMessage) {
        Uri uri = Uri.parse(downloadUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedOverRoaming(true);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setTitle(notifyCationTitle);
        request.setDescription(notifyCationMessage);

        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!TextUtils.isEmpty(fileName))
            request.setDestinationUri(Uri.fromFile(new File(dir, fileName)));
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        return manager.enqueue(request);
    }



    /**
     * 下载完成接收的广播
     */
   /* public static class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            long download_id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0l);

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(download_id);

            Cursor c = manager.query(query);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        Log.v("down", "STATUS_PAUSED");
                    case DownloadManager.STATUS_PENDING:
                        Log.v("down", "STATUS_PENDING");
                    case DownloadManager.STATUS_RUNNING:
                        //正在下载，不做任何事情
                        Log.v("down", "STATUS_RUNNING");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        //完成
                        Log.v("down", "下载完成");
                        break;
                    case DownloadManager.STATUS_FAILED:
                        //清除已下载的内容，重新下载
                        Log.v("down", "STATUS_FAILED");
                        manager.remove(download_id);
                        break;
                }
            }
        }
    }*/
}
