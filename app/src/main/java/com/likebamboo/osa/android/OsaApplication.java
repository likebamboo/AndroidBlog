package com.likebamboo.osa.android;

import com.likebamboo.osa.android.request.RequestManager;
import com.orm.SugarApp;

/**
 * Created by likebamboo on 2015/5/10.
 */
public class OsaApplication extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        // 初始化volley请求管理
        RequestManager.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
