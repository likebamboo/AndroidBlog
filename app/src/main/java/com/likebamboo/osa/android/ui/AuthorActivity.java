package com.likebamboo.osa.android.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.AuthorList;
import com.likebamboo.osa.android.impl.BaseOnItemClickListener;
import com.likebamboo.osa.android.request.JsonRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.adapter.AuthorAdapter;
import com.likebamboo.osa.android.ui.fragments.AuthorInfoFragment;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;
import com.likebamboo.osa.android.ui.view.fastscroll.FastScroller;

import butterknife.InjectView;

/**
 * 作者界面
 */
public class AuthorActivity extends EndlessActivity<AuthorList> {

    @InjectView(R.id.recycler_view)
    RecyclerView mAuthorList = null;

    /**
     * 适配器
     */
    private AuthorAdapter mAuthorAdapter = null;

    /**
     * 布局管理器
     */
    private LinearLayoutManager mLayoutManager;

    /**
     * 加载更多，状态记录
     */
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_author;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化
        initView();
        addListener();

        // 加载数据
        loadDatas();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        // 初始化适配器
        mAuthorAdapter = new AuthorAdapter(this);
        // 添加footer
        mAuthorAdapter.setFooter(mFooterView);
        // 设置适配器
        mAuthorList.setAdapter(mAuthorAdapter);

        // recyclerView布局管理器
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAuthorList.setLayoutManager(mLayoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // fastScroller
            FastScroller fastScroller = (FastScroller) findViewById(R.id.fastscroller);
            fastScroller.setRecyclerView(mAuthorList);
        } else {
            findViewById(R.id.fastscroller).setVisibility(View.GONE);
        }
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        // item Onclick
        mAuthorAdapter.setOnItemClickListener(new BaseOnItemClickListener<AuthorList.Author>() {
            @Override
            public void onItemClick(int postion, AuthorList.Author item) {
                if (item == null) {
                    return;
                }
                // 开始搜索
                Intent i = new Intent(AuthorActivity.this, AuthorBlogActivity.class);
                // 搜索关键字
                i.putExtra(AuthorBlogActivity.EXTRA_AUTHOR_NAME, item.getName());
                // 设置不显示抽屉导航
                i.putExtra(NavigationActivity.EXTRA_SHOULD_DISABLE_DRAWER, true);
                // 设置标题
                i.putExtra(EXTRA_TITLE, item.getName());
                ActivityNavigator.withAnim(i, ActivityNavigator.AnimationMode.DEFAULT).startActivity(AuthorActivity.this, i);
            }
        });

        // item info click
        mAuthorAdapter.setOnInfoClickListner(new AuthorAdapter.IOnAuthorInfoClickListener() {
            @Override
            public void onAuthorInfoClick(int position, AuthorList.Author author) {
                if (author == null) {
                    return;
                }
                AuthorInfoFragment fragment = AuthorInfoFragment.newInstance(author);
                fragment.show(getSupportFragmentManager(), "dialog");
            }

            @Override
            public void onAuthorBlogLinkClick(int position, String blogLink) {
                if (TextUtils.isEmpty(blogLink)) {
                    return;
                }
                // 跳转到Web页面
                ActivityNavigator.openWebView(AuthorActivity.this, null, blogLink);
            }
        });

        // 滚动加载更多
        mAuthorList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                // 加载更多
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loadDatas();
                }
            }
        });
    }

    @Override
    protected void loadDatas(RequestParams params) {
        // 加载数据
        RequestManager.addRequest(new JsonRequest<AuthorList>(RequestUrl.AUTHOR_URL, AuthorList.class, params, responseListener(), errorListener()),
                this.getClass().getName());
    }

    @Override
    protected void doOnSuccess(AuthorList data) {
        if (data == null || data.getList() == null || data.getList().isEmpty()) {
            showMessage(data);
            return;
        }
        ++mPageIndex;
        if (data.getList().size() < mPageSize) {
            mHasMore = false;
        } else {
            mHasMore = true;
        }
        mAuthorAdapter.addDatas(data.getList());
    }

    @Override
    protected void reset() {
        super.reset();
        if (mAuthorAdapter != null) {
            mAuthorAdapter.clear();
        }
    }
}
