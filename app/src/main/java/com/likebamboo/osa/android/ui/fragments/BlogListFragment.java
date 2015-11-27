package com.likebamboo.osa.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Blog;
import com.likebamboo.osa.android.entity.Favorite;
import com.likebamboo.osa.android.exception.OsaException;
import com.likebamboo.osa.android.request.JsonArrayRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.BlogDetailActivity;
import com.likebamboo.osa.android.ui.adapter.BaseRecycleAdapter;
import com.likebamboo.osa.android.ui.adapter.BlogAdapter;
import com.orm.StringUtil;

import java.util.ArrayList;

/**
 * 博客列表Fragment
 */
public class BlogListFragment extends EndlessListFragment<Blog> {

    /**
     * 排序
     */
    public static final String EXTRA_SORT_KEY = "extra_sort_key";

    /**
     * 博客请求url
     */
    public static final String EXTRA_REQUEST_URL = "extra_request_url";

    /**
     * 博客url前缀
     */
    public static final String EXTRA_BLOG_URL_PREFIX = "extra_blog_url_prefix";

    /**
     * 排序
     */
    protected String mSort = "";

    /**
     * 请求URL
     */
    protected String mRequestUrl = "";

    /**
     * 博客URL前缀
     */
    protected String mBlogPrefix = "";

    @Override
    protected BaseRecycleAdapter initAdapter() {
        return new BlogAdapter(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mRequestUrl = getArguments().getString(EXTRA_REQUEST_URL);
            mBlogPrefix = getArguments().getString(EXTRA_BLOG_URL_PREFIX);
        }

        if (TextUtils.isEmpty(mRequestUrl)) {
            mRequestUrl = RequestUrl.BLOG_URL;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 加载数据
     */
    @Override
    protected void loadDatas(RequestParams params) {
        if (TextUtils.isEmpty(mRequestUrl)) {
            mRequestUrl = RequestUrl.BLOG_URL;
        }

        // 如果是本地数据
        if (mRequestUrl.equals(RequestUrl.LOCAL_DB)) {
            loadDatasFromDatabases(params);
            return;
        }


        // 排序
        if (!TextUtils.isEmpty(mSort)) {
            params.add("sort", mSort);
        } else {
            params.remove("sort");
        }

        // 添加其他请求参数
        addParams(params);

        // 添加请求
        RequestManager.addRequest(new JsonArrayRequest(mRequestUrl, params, responseListener(), errorListener()),
                getClass().getName());
    }

    /**
     * 从数据库中读取数据
     */
    protected void loadDatasFromDatabases(RequestParams params) {
        mSort = StringUtil.toSQLName("addTime") + " desc ";
        int pageNo = 0, pageSize = PAGE_SIZE;
        try {
            pageNo = Integer.parseInt(params.get(PARAM_PAGE_NO));
            pageSize = Integer.parseInt(params.get(PARAM_PAGE_SIZE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Favorite> results = Favorite.listPage(Favorite.Type.BLOG, pageNo, pageSize, mSort);
        if (results == null) {
            doOnError(new OsaException(getString(R.string.donot_have_any_favorite)));
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Blog> datas = new ArrayList<Blog>(results.size());
        for (Favorite f : results) {
            try {
                datas.add(mapper.readValue(f.getValue(), Blog.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 如果是刷新数据
        if (isRefreshing) {
            // 清空现有数据
            reset();
            // 停止刷新
            stopRefresh();
        }
        isLoading = false;
        mLoadingLayout.showLoading(false);
        doOnSuccess(datas);
    }

    @Override
    protected void doOnSuccess(ArrayList<Blog> data) {
        if (data != null && !TextUtils.isEmpty(mBlogPrefix)) {
            for (Blog item : data) {
                if (!URLUtil.isHttpUrl(item.getUrl())) {
                    item.setUrl(mBlogPrefix + item.getUrl());
                }
            }
        }
        super.doOnSuccess(data);
    }

    @Override
    public void onItemClick(int position, Blog item) {
        super.onItemClick(position, item);
        if (item == null) {
            return;
        }
        Intent i = new Intent(getActivity(), BlogDetailActivity.class);
        i.putExtra(BlogDetailActivity.EXTRA_BLOG_INFO, item);
        startActivity(i);
    }

    /**
     * 添加其他请求参数
     *
     * @param params
     */
    public void addParams(RequestParams params) {

    }

    /**
     * 获取当前排序方式
     *
     * @return
     */
    public String getSort() {
        return mSort;
    }

    /**
     * 重排序 加载数据
     *
     * @param sort
     */
    public void reloadWithSort(String sort) {
        if (mSort.equals(sort)) {
            return;
        }
        // 如果正在加载数据
        if (isLoading) {
            // 取消加载数据
            RequestManager.cancelAll(getClass().getName());
        }
        mSort = (sort == null ? "" : sort);
        reset();
        loadDatas();
    }
}
