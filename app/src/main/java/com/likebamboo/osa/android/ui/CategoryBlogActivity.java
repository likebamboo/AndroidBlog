package com.likebamboo.osa.android.ui;

import android.os.Bundle;

import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;

/**
 * 分类博客列表界面
 */
public class CategoryBlogActivity extends BlogListActivity {

    /**
     * 类别id
     */
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";

    /**
     * 博客类别
     */
    private int mCategoryId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent() != null && getIntent().hasExtra(EXTRA_CATEGORY_ID)) {
            mCategoryId = getIntent().getIntExtra(EXTRA_CATEGORY_ID, 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void addParams(RequestParams params) {
    }

    @Override
    public String getRequestUrl() {
        // 改为搜索url
        return String.format(RequestUrl.CATEGORY_BLOG_URL, mCategoryId + "");
    }
}
