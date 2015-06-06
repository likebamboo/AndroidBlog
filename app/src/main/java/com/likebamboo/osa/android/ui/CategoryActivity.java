package com.likebamboo.osa.android.ui;

import android.content.Intent;
import android.os.Bundle;

import com.etsy.android.grid.StaggeredGridView;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.CategoryList;
import com.likebamboo.osa.android.interfaces.IOnItemClickListener;
import com.likebamboo.osa.android.request.JsonRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.adapter.CategoryAdapter;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;

import butterknife.InjectView;

/**
 * 分类界面
 */
public class CategoryActivity extends EndlessActivity<CategoryList> {

    @InjectView(R.id.list_view)
    StaggeredGridView mListView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_category;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置空态页面
        mListView.setEmptyView(mLoadingLayout);
        // 添加footer
        mListView.addFooterView(mFooterView);

        // 设置适配器
        mAdapter = new CategoryAdapter(this);
        mListView.setAdapter(mAdapter);
        ((CategoryAdapter) mAdapter).setOnItemClickListener(new IOnItemClickListener<CategoryList.Category>() {
            @Override
            public void onItemClick(int postion, CategoryList.Category item) {
                if (item == null) {
                    return;
                }
                // 开始搜索
                Intent i = new Intent(CategoryActivity.this, CategoryBlogActivity.class);
                // 搜索关键字
                i.putExtra(CategoryBlogActivity.EXTRA_CATEGORY_ID, item.getId());
                // 设置不显示抽屉导航
                i.putExtra(NavigationActivity.EXTRA_SHOULD_DISABLE_DRAWER, true);
                // 设置标题
                i.putExtra(EXTRA_TITLE, item.getName());
                ActivityNavigator.withAnim(i, ActivityNavigator.AnimationMode.DEFAULT).startActivity(CategoryActivity.this, i);
            }
        });
        // 设置加载更多
        mListView.setOnScrollListener(this);

        // 加载数据
        loadDatas();
    }

    /**
     * 加载数据
     */
    @Override
    protected void loadDatas(RequestParams params) {
        // 加载数据
        RequestManager.addRequest(new JsonRequest<CategoryList>(RequestUrl.CATEGORY_URL, CategoryList.class, params, responseListener(), errorListener()),
                this.getClass().getName());
    }

    @Override
    protected void doOnSuccess(CategoryList data) {
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

}
