package com.finger.activity.setting;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.finger.activity.base.BaseActivity;
import com.finger.BuildConfig;
import com.finger.R;
import com.finger.activity.login.UpdatePasswordActivity;
import com.finger.support.net.FingerHttpClient;
import com.finger.support.net.FingerHttpHandler;
import com.loopj.android.http.RequestParams;
import com.sp.lib.Slib;
import com.sp.lib.activity.DEBUGActivity;
import com.sp.lib.version.Downloader;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends BaseActivity {

    DownloadReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (BuildConfig.DEBUG) {
            findViewById(R.id.debug_item).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.debug_item).setVisibility(View.GONE);
        }
        receiver = new DownloadReceiver();
        registerReceiver(receiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.debug_item: {
                startActivity(new Intent(this, DEBUGActivity.class));
                break;
            }

            case R.id.logout: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.confirm_logout));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getApp().setUser(null);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;
            }

            case R.id.service_area: {
                startActivity(new Intent(this, ServiceAreaActivity.class));
                break;
            }

            case R.id.suggestion: {
                startActivity(new Intent(this, SuggestionActivity.class));
                break;
            }

            case R.id.about: {
                startActivity(new Intent(this, About.class));
                break;
            }

            case R.id.clear_cache: {
                Slib.clearCache();
                break;
            }

            case R.id.update_software: {

                RequestParams params = new RequestParams();

                FingerHttpClient.post("getNewestVersion", params, new FingerHttpHandler() {
                    @Override
                    public void onSuccess(JSONObject o) {
                        try {
                            final JSONObject data = o.getJSONObject("data");
                            //远程版本号
                            int serverVersion = data.getInt("version_num");
                            //本地版本号
                            int localVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                            AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

                            if (localVersion < serverVersion) {
                                //发现新版本
                                builder.setTitle(getString(R.string.found_new_version_s, data.getString("version_code")));
                                builder.setMessage(getString(R.string.is_update));
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            String url = data.getString("download_link");
                                            new Downloader().download(SettingActivity.this, url, null, "heihei", "hoho");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                builder.setNegativeButton(R.string.cancel, null);
                            } else {
                                //已经是最新版本
                                builder.setMessage(getString(R.string.aready_newest));
                                builder.setPositiveButton(R.string.yes, null);
                            }
                            builder.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            }

            case R.id.discount_rule: {
                startActivity(new Intent(this, DiscountRuleActivity.class));
                break;
            }

            case R.id.taxi_fee: {
                startActivity(new Intent(this, TaxiFeeActivity.class));
                break;
            }
            case R.id.update_password: {
                startActivity(new Intent(this, UpdatePasswordActivity.class));
                break;
            }

            default:
                super.onClick(v);
        }
    }


    /**
     * 下载完成接收的广播
     */
    public static class DownloadReceiver extends BroadcastReceiver {
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
    }

    ;
}
