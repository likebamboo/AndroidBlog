package com.likebamboo.osa.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.etsy.android.grid.StaggeredGridView;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.BlogList;
import com.likebamboo.osa.android.interfaces.IOnItemClickListener;
import com.likebamboo.osa.android.request.JsonRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.adapter.BlogAdapter;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;

import butterknife.InjectView;

/**
 * 博客界面
 */
public abstract class BlogListActivity extends EndlessActivity<BlogList> {

    /**
     * 排序
     */
    public static final String EXTRA_SORT_KEY = "extra_sort_key";

    @InjectView(R.id.list_view)
    StaggeredGridView mBlogListView;

    /**
     * 排序
     */
    protected String mSort = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_blog_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(EXTRA_TITLE)) {
            setTitle(getIntent().getStringExtra(EXTRA_TITLE));
        } else {
            setTitle(R.string.app_name);
        }

        if (getIntent().hasExtra(EXTRA_SORT_KEY)) {
            mSort = getIntent().getStringExtra(EXTRA_SORT_KEY);
        }

        // 设置空态页面
        mBlogListView.setEmptyView(mLoadingLayout);
        // 添加footer
        mBlogListView.addFooterView(mFooterView);

        // 设置适配器
        mAdapter = new BlogAdapter(this);
        mBlogListView.setAdapter(mAdapter);
        ((BlogAdapter) mAdapter).setOnItemClickListener(new IOnItemClickListener<BlogList.Blog>() {
            @Override
            public void onItemClick(int postion, BlogList.Blog item) {
                if (item == null) {
                    return;
                }
                // 进入博客详情界面
                Intent i = new Intent(BlogListActivity.this, BlogActivity.class);
                i.putExtra(BlogActivity.EXTRA_BLOG_URL, item.getUrl());
                ActivityNavigator.startActivity(BlogListActivity.this, i);
            }
        });
        // 设置加载更多
        mBlogListView.setOnScrollListener(this);

        // 加载数据
        loadDatas();
    }

    /**
     * 加载数据
     */
    @Override
    protected void loadDatas(RequestParams params) {
        String url = getRequestUrl();
        if (TextUtils.isEmpty(url)) {
            url = RequestUrl.BLOG_URL;
        }

        // 排序
        if (!TextUtils.isEmpty(mSort)) {
            params.add("sort", mSort);
        }

        // 添加其他请求参数
        addParams(params);

        RequestManager.addRequest(new JsonRequest<BlogList>(url, BlogList.class, params, responseListener(), errorListener()),
                getClass().getName());
    }

    @Override
    protected void doOnSuccess(BlogList data) {
        if (data == null || data.getList() == null) {
            showMessage(data);
            return;
        }
        ++mPageIndex;
        if (data.getList().size() < mPageSize) {
            mHasMore = false;
        } else {
            mHasMore = true;
        }
        // 如果数据为空，显示没有更多数据了
        if (data.getList().isEmpty()) {
            showMessage(data);
            return;
        }
        mAdapter.addDatas(data.getList());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(EXTRA_TITLE)) {
            setTitle(intent.getStringExtra(EXTRA_TITLE));
        }
    }

    /**
     * 获取请求的URL
     *
     * @return
     */
    public abstract String getRequestUrl();

    /**
     * 添加其他请求参数
     *
     * @param params
     */
    public abstract void addParams(RequestParams params);
}
