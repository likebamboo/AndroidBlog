package com.likebamboo.osa.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.likebamboo.osa.android.entity.Blog;
import com.likebamboo.osa.android.request.JsonArrayRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.BlogDetailActivity;
import com.likebamboo.osa.android.ui.adapter.BaseRecycleAdapter;
import com.likebamboo.osa.android.ui.adapter.BlogAdapter;

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
    protected  String mBlogPrefix = "";

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

        // 排序
        if (!TextUtils.isEmpty(mSort)) {
            params.add("sort", mSort);
        }

        // 添加其他请求参数
        addParams(params);

        // 添加请求
        RequestManager.addRequest(new JsonArrayRequest(mRequestUrl, params, responseListener(), errorListener()),
                getClass().getName());
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
}
