package com.likebamboo.osa.android.ui;

import android.os.Bundle;
import android.util.Log;

import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.utils.PreferencesUtil;
import com.orm.SugarConfig;
import com.orm.SugarDb;

/**
 * 主界面
 */
public class MainActivity extends BlogListActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 本次数据库版本
        int dbVersion = SugarConfig.getDatabaseVersion(this);
        // 保存的数据库版本
        int savedDbVersion = PreferencesUtil.getInstance(this).getInt(PreferencesUtil.PREF_DB_VERSION, 0);
        if (dbVersion > savedDbVersion) { // 如果数据库有升级。
            //
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing()) {
                        return;
                    }
                    try {
                        Log.e(TAG, System.currentTimeMillis() + "====start");
                        // 居然是个耗时的操作，以下语句执行了3秒钟左右。 可见sugar 效率不高啊，不知道新版是否有改进。
                        int newVersion = (new SugarDb(MainActivity.this)).getReadableDatabase().getVersion();
                        PreferencesUtil.getInstance(MainActivity.this).putInt(PreferencesUtil.PREF_DB_VERSION, newVersion);
                        Log.e(TAG, System.currentTimeMillis() + "====end");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void addParams(RequestParams params) {
    }

    @Override
    public String getRequestUrl() {
        return RequestUrl.BLOG_URL;
    }
}
