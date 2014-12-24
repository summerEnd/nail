package com.sp.lib.version;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Downloader {

    public void download(Context context, String downloadUrl, String fileName, String notifyCationTitle, String notifyCationMessage) {
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
        manager.enqueue(request);
    }
}
