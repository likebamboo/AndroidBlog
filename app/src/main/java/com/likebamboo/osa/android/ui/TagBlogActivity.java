package com.likebamboo.osa.android.ui;

import android.os.Bundle;

import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;

/**
 * 标签博客列表界面
 */
public class TagBlogActivity extends BlogListActivity {

    /**
     * 类别id
     */
    public static final String EXTRA_TAG_NAME = "extra_tag_name";

    /**
     * tag标签名称
     */
    private String mTagName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_TAG_NAME)) {
            mTagName = getIntent().getStringExtra(EXTRA_TAG_NAME);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void addParams(RequestParams params) {
    }

    @Override
    public String getRequestUrl() {
        // 改为搜索url
        return String.format(RequestUrl.TAG_BLOG_URL, mTagName);
    }
}
