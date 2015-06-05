package com.likebamboo.osa.android.ui;

import android.os.Bundle;
import android.text.TextUtils;

import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;

import java.net.URLEncoder;

/**
 * 作者博客列表界面
 */
public class AuthorBlogActivity extends BlogListActivity {

    /**
     * 作者名称
     */
    public static final String EXTRA_AUTHOR_NAME = "extra_author_name";

    /**
     * 作者名称
     */
    private String mAuthorName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_AUTHOR_NAME)) {
            mAuthorName = getIntent().getStringExtra(EXTRA_AUTHOR_NAME);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void addParams(RequestParams params) {
    }

    @Override
    public String getRequestUrl() {
        if (!TextUtils.isEmpty(mAuthorName)) {
            try {
                mAuthorName = URLEncoder.encode(mAuthorName, "UTF-8");
            } catch (Exception e) {
            }
        }
        // 改为搜索url
        return String.format(RequestUrl.AUTHOR_BLOG_URL, mAuthorName + "");
    }
}
