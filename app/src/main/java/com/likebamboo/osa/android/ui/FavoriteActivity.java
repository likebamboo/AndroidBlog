package com.likebamboo.osa.android.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.BlogList;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.ui.adapter.BlogAdapter;
import com.likebamboo.osa.android.ui.fragments.BlogInfoFragment;
import com.orm.StringUtil;

import java.util.ArrayList;

/**
 * 收藏界面
 *
 * @author likebamboo
 * @since 2015-06-08
 */
public class FavoriteActivity extends BlogListActivity {
    /**
     * 收藏或者取消收藏Action。
     */
    public static final String ACTION_FAVORITE_ADD_OR_REMOVE = "com.likebamboo.osa.android.favorite.ACTION_ADD_OR_REMOVE";

    /**
     * 收藏或者取消收藏。
     */
    public static final String EXTRA_FAVORITE_ADD_OR_REMOVE = "extra_favorite_add_or_remove";

    private BroadcastReceiver mFavReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isFinishing() || intent == null || !ACTION_FAVORITE_ADD_OR_REMOVE.equals(intent.getAction()) || !intent.hasExtra(BlogInfoFragment.EXTRA_BLOG)) {
                return;
            }
            BlogList.Blog blog = null;
            try {
                blog = intent.getParcelableExtra(BlogInfoFragment.EXTRA_BLOG);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (blog == null || !(mAdapter instanceof BlogAdapter)) {
                return;
            }

            boolean add = intent.getBooleanExtra(EXTRA_FAVORITE_ADD_OR_REMOVE, false);
            // 删除收藏
            mAdapter.removeData(blog);
            // 如果是添加收藏
            if (add) {
                // 先移除旧数据, 然后再添加数据
                mAdapter.addData(blog, 0);
            }
            mAdapter.notifyDataSetChanged();

            // 如果数据为空
            if (mAdapter.getCount() <= 0) {
                mLoadingLayout.showEmpty(getString(R.string.donot_have_any_favorite));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            registerReceiver(mFavReceiver, new IntentFilter(ACTION_FAVORITE_ADD_OR_REMOVE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadDatas(RequestParams params) {
        // 加载本地数据
        BlogList list = new BlogList();
        ArrayList<BlogList.Blog> blogs = BlogList.Blog.listPage(mPageIndex, mPageSize, StringUtil.toSQLName("favTime") + " desc ");
        list.setList(blogs);
        if (blogs == null || blogs.isEmpty()) {
            list.setMessage(getString(R.string.donot_have_any_favorite));
        }
        isLoading = false;
        mLoadingLayout.showLoading(false);
        doOnSuccess(list);
    }

    @Override
    public void addParams(RequestParams params) {
    }

    @Override
    public String getRequestUrl() {
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mFavReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
