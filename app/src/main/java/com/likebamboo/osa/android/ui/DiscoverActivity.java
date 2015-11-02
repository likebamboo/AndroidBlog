package com.likebamboo.osa.android.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Site;
import com.likebamboo.osa.android.request.JsonArrayRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.adapter.DiscoverFragmentAdapter;
import com.likebamboo.osa.android.ui.fragments.BlogListFragment;
import com.likebamboo.osa.android.ui.fragments.EndlessListFragment;
import com.likebamboo.osa.android.ui.view.LoadingLayout;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * 发现界面
 */
public class DiscoverActivity extends BaseNavigationActivity {

    /**
     * loading界面
     */
    private LoadingLayout mLoadingLayout = null;

    /**
     * viewPager
     */
    private ViewPager mViewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        mLoadingLayout = (LoadingLayout) findViewById(R.id.discover_loading);
        mViewPager = (ViewPager) findViewById(R.id.discover_viewpager);

        getSites();
    }

    /**
     * 获取可用站点
     */
    private void getSites() {
        mLoadingLayout.showLoading(true, getString(R.string.loading));
        RequestManager.addRequest(new JsonArrayRequest(RequestUrl.SITE_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                mLoadingLayout.showLoading(false);
                if (isFinishing()) {
                    return;
                }
                if (response == null) {
                    showError(getString(R.string.network_error));
                    return;
                }
                // 解析数据成ArrayList
                ArrayList<Site> result = JsonArrayRequest.parseToArrayList(response.toString(), Site.class);
                if (result == null || result.isEmpty()) {
                    showError(getString(R.string.network_error));
                    return;
                }
                // 加载成功
                setUpViewPager(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError(error.getMessage());
            }
        }), getClass().getName());
    }

    /**
     * 初始化ViewPager对象
     */
    private void setUpViewPager(ArrayList<Site> datas) {
        tabLayout.setVisibility(View.VISIBLE);

        DiscoverFragmentAdapter adapter = new DiscoverFragmentAdapter(getSupportFragmentManager());
        for (int i = 0; i < datas.size(); i++) {
            Site site = datas.get(i);
            BlogListFragment f = new BlogListFragment();
            Bundle b = new Bundle();
            b.putString(BlogListFragment.EXTRA_REQUEST_URL, String.format(RequestUrl.SITE_BLOG_LIST_URL, site.getShortName()));
            b.putString(BlogListFragment.EXTRA_BLOG_URL_PREFIX, String.format(RequestUrl.SITE_BLOG_LIST_URL, site.getShortName()) + "/");
            if (i == 0) {
                b.putBoolean(EndlessListFragment.EXTRA_SHOULD_CACHE_DATA, true);
            }
            f.setArguments(b);
            adapter.addFragment(f, site.getName());
        }
        mViewPager.setAdapter(adapter);

        // 当Tab超过3个时候，改成可以滚动的形式
        if (datas.size() > 3) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            // tab <=3 个，以填充的形式展示TAB
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        //
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 显示错误信息
     *
     * @param msg
     */
    private void showError(String msg) {
        mLoadingLayout.showError(msg);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestManager.cancelAll(getClass().getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
