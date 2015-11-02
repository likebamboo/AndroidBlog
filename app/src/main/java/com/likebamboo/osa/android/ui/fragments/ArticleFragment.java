package com.likebamboo.osa.android.ui.fragments;

import android.text.TextUtils;

import com.likebamboo.osa.android.entity.Blog;
import com.likebamboo.osa.android.request.JsonArrayRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.adapter.BaseRecycleAdapter;
import com.likebamboo.osa.android.ui.adapter.BlogAdapter;
import com.likebamboo.osa.android.utils.ToastUtil;

/**
 * 其他站点文章Fragment
 */
public class ArticleFragment extends EndlessListFragment<Blog> {

    /**
     * 排序
     */
    public static final String EXTRA_SORT_KEY = "extra_sort_key";

    /**
     * 排序
     */
    protected String mSort = "";

    @Override
    protected BaseRecycleAdapter initAdapter() {
        return new BlogAdapter(getActivity());
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

        // 添加请求
        RequestManager.addRequest(new JsonArrayRequest(url, params, responseListener(), errorListener()),
                getClass().getName());
    }

    @Override
    public void onItemClick(int position, Blog item) {
        super.onItemClick(position, item);
        ToastUtil.show(getActivity(), item.getTitle());
    }

    /**
     * 获取请求的URL
     *
     * @return
     */
    public String getRequestUrl() {
        return RequestUrl.BLOG_URL;
    }

    /**
     * 添加其他请求参数
     *
     * @param params
     */
    public void addParams(RequestParams params) {

    }
}
