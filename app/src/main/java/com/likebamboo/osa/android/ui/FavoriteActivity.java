package com.likebamboo.osa.android.ui;

import android.os.Bundle;

import com.likebamboo.osa.android.entity.BlogList;
import com.likebamboo.osa.android.request.RequestParams;
import com.orm.StringUtil;

import java.util.ArrayList;

/**
 * 收藏界面
 *
 * @author likebamboo
 * @since 2015-06-08
 */
public class FavoriteActivity extends BlogListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void loadDatas(RequestParams params) {
        // 加载本地数据
        BlogList list = new BlogList();
        ArrayList<BlogList.Blog> blogs = BlogList.Blog.listPage(mPageIndex, mPageSize, StringUtil.toSQLName("favTime") + " desc ");
        list.setList(blogs);
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
}
