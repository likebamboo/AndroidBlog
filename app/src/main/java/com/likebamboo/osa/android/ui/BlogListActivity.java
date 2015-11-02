package com.likebamboo.osa.android.ui;

import android.os.Bundle;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.fragments.BlogListFragment;

/**
 * 博客列表界面，为搜索，分类，作者博客服务
 */
public class BlogListActivity extends BaseContentActivity {

    /**
     * 博客列表fragment
     */
    private BlogListFragment mBlogFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mBlogFragment == null) {
            mBlogFragment = new BlogListFragment();
            mBlogFragment.setArguments(getIntent().getExtras());
        }

        getSupportFragmentManager().beginTransaction().add(R.id.real_content, mBlogFragment, "blog_list").commit();
    }
}
