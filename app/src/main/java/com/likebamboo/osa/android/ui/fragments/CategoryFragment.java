package com.likebamboo.osa.android.ui.fragments;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.likebamboo.osa.android.entity.Category;
import com.likebamboo.osa.android.request.JsonArrayRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.BaseActivity;
import com.likebamboo.osa.android.ui.BlogListActivity;
import com.likebamboo.osa.android.ui.adapter.BaseRecycleAdapter;
import com.likebamboo.osa.android.ui.adapter.CategoryAdapter;

/**
 * 分类列表Fragment
 */
public class CategoryFragment extends EndlessListFragment<Category> {

    @Override
    protected BaseRecycleAdapter initAdapter() {
        return new CategoryAdapter(getActivity());
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    /**
     * 加载数据
     */
    @Override
    protected void loadDatas(RequestParams params) {
        // 加载数据
        RequestManager.addRequest(new JsonArrayRequest(RequestUrl.CATEGORY_URL, params, responseListener(), errorListener()),
                getClass().getName());
    }

    @Override
    public void onItemClick(int position, Category item) {
        super.onItemClick(position, item);
        if (item == null) {
            return;
        }
        Intent i = new Intent(getActivity(), BlogListActivity.class);
        i.putExtra(BlogListFragment.EXTRA_REQUEST_URL, String.format(RequestUrl.CATEGORY_BLOG_URL, item.getId()));
        i.putExtra(BaseActivity.EXTRA_TITLE, item.getName());
        startActivity(i);

    }
}
