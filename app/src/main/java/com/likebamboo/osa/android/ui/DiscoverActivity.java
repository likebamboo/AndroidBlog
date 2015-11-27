package com.likebamboo.osa.android.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.LDialogItem;
import com.likebamboo.osa.android.entity.Site;
import com.likebamboo.osa.android.request.JsonArrayRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.adapter.TabFragmentAdapter;
import com.likebamboo.osa.android.ui.fragments.BlogListFragment;
import com.likebamboo.osa.android.ui.fragments.EndlessListFragment;
import com.likebamboo.osa.android.ui.fragments.SimpleListDialog;
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

    /**
     * adapter
     */
    private TabFragmentAdapter adapter = null;

    /**
     * 排序列表数值
     */
    private ArrayList<LDialogItem> mSortDatas = null;

    /**
     * 排序dialog
     */
    private SimpleListDialog<LDialogItem> mSortDialog = null;

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

        adapter = new TabFragmentAdapter(getSupportFragmentManager());
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

        // tablayout 监听
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (mViewPager == null) {
                    return;
                }
                mViewPager.setCurrentItem(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (adapter == null) {
                    return;
                }
                // 滚动到顶部
                if (adapter.getItem(pos) instanceof EndlessListFragment) {
                    ((EndlessListFragment) adapter.getItem(pos)).smoothScrollToTop();
                }
            }
        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_sort: // 排序
                showSortListDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示排序dialog
     */
    private void showSortListDialog() {
        final Fragment fragment = adapter.getItem(mViewPager.getCurrentItem());
        if (!(fragment instanceof BlogListFragment)) {
            return;
        }

        if (mSortDatas == null) {
            mSortDatas = new ArrayList<>();
            String[] names = getResources().getStringArray(R.array.sort_name_entry);
            String[] values = getResources().getStringArray(R.array.sort_value_entry);
            for (int i = 0; i < names.length; i++) {
                LDialogItem item = new LDialogItem();
                item.setName(names[i]);
                item.setValue(values[i]);
                if (i == 0) {
                    item.setSelected(true);
                }
                mSortDatas.add(item);
            }
        }
        for (LDialogItem item : mSortDatas) {
            if ((((BlogListFragment) fragment).getSort()).equals(item.getValue())) {
                item.setSelected(true);
                continue;
            }
            item.setSelected(false);
        }
        if (mSortDialog == null) {
            mSortDialog = SimpleListDialog.getInstance(getString(R.string.sort), mSortDatas);
            mSortDialog.setOnItemClickListener(new SimpleListDialog.OnDialogItemClickListener<LDialogItem>() {
                @Override
                public void onItemClick(LDialogItem obj) {
                    if (obj == null) {
                        return;
                    }
                    final Fragment fragment = adapter.getItem(mViewPager.getCurrentItem());
                    if (!(fragment instanceof BlogListFragment)) {
                        return;
                    }
                    ((BlogListFragment) fragment).reloadWithSort(obj.getValue());
                }
            });
        } else {
            mSortDialog.notifyDataChanged();
        }
        mSortDialog.show(getSupportFragmentManager(), "SortDialog");
    }

}
