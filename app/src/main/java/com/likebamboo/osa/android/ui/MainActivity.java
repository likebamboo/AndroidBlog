package com.likebamboo.osa.android.ui;

import android.os.Bundle;

import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;

/**
 * 主界面
 */
public class MainActivity extends BlogListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void addParams(RequestParams params) {
    }

    @Override
    public String getRequestUrl() {
        return RequestUrl.BLOG_URL;
    }
}
