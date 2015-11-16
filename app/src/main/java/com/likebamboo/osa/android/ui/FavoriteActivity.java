package com.likebamboo.osa.android.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Favorite;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.adapter.TabFragmentAdapter;
import com.likebamboo.osa.android.ui.fragments.BlogListFragment;
import com.likebamboo.osa.android.ui.fragments.EndlessListFragment;
import com.likebamboo.osa.android.ui.view.LoadingLayout;

import java.util.ArrayList;

/**
 * 收藏界面
 */
public class FavoriteActivity extends BaseNavigationActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        mViewPager = (ViewPager) findViewById(R.id.discover_viewpager);
        mLoadingLayout = (LoadingLayout) findViewById(R.id.discover_loading);

        getSites();
    }

    /**
     * 获取可用站点
     */
    private void getSites() {
        ArrayList<Integer> types = Favorite.getTypes();
        setUpViewPager(types);
    }

    /**
     * 初始化ViewPager对象
     */
    private void setUpViewPager(ArrayList<Integer> datas) {
        if (datas == null || datas.isEmpty()) {
            mLoadingLayout.showEmpty(getString(R.string.donot_have_any_favorite));
            return;
        }
        if (datas.size() > 1) {
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            tabLayout.setVisibility(View.GONE);
        }
        adapter = new TabFragmentAdapter(getSupportFragmentManager());

        for (int type : datas) {
            Bundle b = new Bundle();
            b.putString(BlogListFragment.EXTRA_REQUEST_URL, RequestUrl.LOCAL_DB);
            switch (type){
                case Favorite.Type.BLOG:
                    BlogListFragment f = new BlogListFragment();
                    f.setArguments(b);
                    adapter.addFragment(f, Favorite.TYPE_NAME_VALUES.get(type));
                    break;
                case Favorite.Type.AUTHOR:
                    break;
                case Favorite.Type.TAG:
                    break;
            }
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
}
