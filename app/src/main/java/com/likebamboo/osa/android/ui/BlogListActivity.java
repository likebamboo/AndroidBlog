package com.likebamboo.osa.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;

import com.etsy.android.grid.StaggeredGridView;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.BlogList;
import com.likebamboo.osa.android.entity.LDialogItem;
import com.likebamboo.osa.android.impl.BaseOnItemClickListener;
import com.likebamboo.osa.android.request.JsonRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.adapter.BlogAdapter;
import com.likebamboo.osa.android.ui.fragments.SimpleListDialog;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;
import com.likebamboo.osa.android.ui.view.FilterFooter;
import com.likebamboo.osa.android.ui.view.fab.DirectionScrollListener;

import java.util.ArrayList;

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

    @InjectView(R.id.list_filter_footer_layout)
    FilterFooter mFilterFooter = null;

    /**
     * 滚动事件监听器
     */
    private DirectionScrollListener mScrollListener = null;

    /**
     * 排序
     */
    protected String mSort = "";

    /**
     * 排序列表数值
     */
    private ArrayList<LDialogItem> mSortDatas = null;

    /**
     * 排序dialog
     */
    private SimpleListDialog<LDialogItem> mSortDialog = null;

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
        ((BlogAdapter) mAdapter).setOnItemClickListener(new BaseOnItemClickListener<BlogList.Blog>() {
            @Override
            public void onItemClick(int postion, BlogList.Blog item) {
                // 进入博客详情界面
                goToBlogDetail(item);
            }
        });
        // 设置加载更多
        mBlogListView.setOnScrollListener(this);

        // 如果有过滤footer
        if (hasFilterFooter()) {
            mFilterFooter.setVisibility(View.VISIBLE);
            // 初始化滚动监听
            mScrollListener = new DirectionScrollListener(mFilterFooter);
            // 设置筛选按钮点击回调
            mFilterFooter.setFilterClickListener(new FilterFooter.IOnFilterClickListener() {
                @Override
                public void onSortClick() {
                    // 显示排序dialog
                    showSortListDialog();
                }

                @Override
                public void onCategoryClick() {
                    // TODO 显示dialog
                }
            });
        } else {
            mFilterFooter.setVisibility(View.GONE);
        }

        // 加载数据
        loadDatas();
    }

    /**
     * 显示排序dialog
     */
    private void showSortListDialog() {
        if (mSortDatas == null) {
            mSortDatas = new ArrayList<>();
            String[] names = getResources().getStringArray(R.array.sort_name_entry);
            String[] values = getResources().getStringArray(R.array.sort_value_entry);
            for (int i = 0; i < names.length; i++) {
                LDialogItem item = new LDialogItem();
                item.setName(names[i]);
                item.setValue(values[i]);
                mSortDatas.add(item);
            }
        }
        for (LDialogItem item : mSortDatas) {
            if (("" + mSort).equals(item.getValue())) {
                item.setSelected(true);
                continue;
            }
            item.setSelected(false);
        }
        if (mSortDialog == null) {
            mSortDialog = new SimpleListDialog<>(this, getString(R.string.sort), mSortDatas);
            mSortDialog.setOnItemClickListener(new SimpleListDialog.OnDialogItemClickListener<LDialogItem>() {
                @Override
                public void onItemClick(LDialogItem obj) {
                    if (obj == null) {
                        return;
                    }
                    // 回到顶部
                    mBlogListView.setSelection(0);
                    // 重置数据
                    reset();
                    // 设置排序规则
                    mSort = obj.getValue();
                    // 重新加载数据
                    loadDatas();
                }
            });
        } else {
            mSortDialog.notifyDataChanged();
        }
        mSortDialog.show(getSupportFragmentManager(), "SortDialog");
    }

    /**
     * 进入博客详情界面
     *
     * @param item
     */
    protected void goToBlogDetail(BlogList.Blog item) {
        if (item == null) {
            return;
        }
        // 进入博客详情界面
        Intent i = new Intent(BlogListActivity.this, BlogActivity.class);
        i.putExtra(BlogActivity.EXTRA_BLOG_URL, item.getUrl());
        ActivityNavigator.startActivity(BlogListActivity.this, i);
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
        ++mPageIndex;
        mAdapter.addDatas(data.getList());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(EXTRA_TITLE)) {
            setTitle(intent.getStringExtra(EXTRA_TITLE));
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /**
     * list底部是否有filter
     *
     * @return
     */
    public boolean hasFilterFooter() {
        return true;
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
