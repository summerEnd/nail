package com.finger.activity.other.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.finger.BaseActivity;
import com.finger.BuildConfig;
import com.finger.R;
import com.finger.activity.other.login.UpdatePasswordActivity;
import com.sp.lib.Slib;
import com.sp.lib.activity.DEBUGActivity;
import com.sp.lib.version.Downloader;

public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (BuildConfig.DEBUG) {
            findViewById(R.id.debug_item).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.debug_item).setVisibility(View.GONE);
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
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.confirm_logout));
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getApp().setUser(null);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;
            }

            case R.id.service_area: {
                startActivity(new Intent(this,ServiceAreaActivity.class));
                break;
            }

            case R.id.suggestion: {
                startActivity(new Intent(this,SuggestionActivity.class));
                break;
            }

            case R.id.about: {
                startActivity(new Intent(this,About.class));
                break;
            }

            case R.id.clear_cache: {
                Slib.clearCache();

                break;
            }

            case R.id.update_software: {
                String url="http://music.baidu.com/data/music/file?link=http://yinyueshiting.baidu.com/data2/music/123448303/34240081176400128.mp3?xcode=7edafdbc53b5131a24018a0b1c6fcc74a9d81da4d88dae02&song_id=34240081";



                new Downloader().download(this,url,null,"heihei","hoho");
                break;
            }


            case R.id.update_password: {
                startActivity(new Intent(this,UpdatePasswordActivity.class));
                break;
            }

            default:
                super.onClick(v);
        }
    }
}
