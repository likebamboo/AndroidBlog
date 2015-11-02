package com.likebamboo.osa.android.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.likebamboo.osa.android.utils.PreferencesUtil;
import com.orm.SugarConfig;
import com.orm.SugarDb;

/**
 * Activity基类
 * Created by likebamboo on 2015/5/11.
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * 标题
     */
    public static final String EXTRA_TITLE = "extra_title";

    /**
     * 是否初始化数据库
     */
    private static boolean initDb = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!initDb) {
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
                            // 居然是个耗时的操作，以下语句执行了3秒钟左右。 可见sugar 效率不高啊，不知道新版是否有改进。
                            int newVersion = (new SugarDb(BaseActivity.this)).getReadableDatabase().getVersion();
                            PreferencesUtil.getInstance(BaseActivity.this).putInt(PreferencesUtil.PREF_DB_VERSION, newVersion);
                            initDb = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                initDb = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra(EXTRA_TITLE)) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                setTitle(getIntent().getStringExtra(EXTRA_TITLE));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("initDb", true);
    }
}
