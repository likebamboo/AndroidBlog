package com.likebamboo.osa.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.likebamboo.osa.android.R;

/**
 * 导航基类
 */
public class BaseNavigationActivity extends BaseActivity {

    /**
     * 选中的id
     */
    private static final String EXTRA_SELECTED_ITEM_ID = "EXTRA_SELECTED_ITEM_ID";

    /**
     * 抽屉布局
     */
    protected DrawerLayout drawerLayout;

    /**
     * tabLayout
     */
    protected TabLayout tabLayout = null;

    /**
     * 内容区域
     */
    private FrameLayout contentLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_navigation);
        // 内容区域
        contentLayout = (FrameLayout) findViewById(R.id.real_content);

        // 设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置actionbar的up图标
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // 导航抽屉
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // 导航布局
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        navigationView.setCheckedItem(getIntent().getIntExtra(EXTRA_SELECTED_ITEM_ID, R.id.nav_home));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);
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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 通过layout名称构建视图
     *
     * @param layoutId
     * @see [类、类#方法、类#成员]
     */
    @Override
    public final void setContentView(int layoutId) {
        getLayoutInflater().inflate(layoutId, contentLayout);
    }

    /**
     * 通过view构建视图
     *
     * @param view
     * @see [类、类#方法、类#成员]
     */
    @Override
    public final void setContentView(View view) {
        contentLayout.addView(view);
    }

    /**
     * 通过view构建视图
     *
     * @param view
     * @see [类、类#方法、类#成员]
     */
    @Override
    public final void setContentView(View view, ViewGroup.LayoutParams params) {
        contentLayout.addView(view, params);
    }

    /**
     * 初始化导航抽屉
     *
     * @param navigationView 导航布局
     */
    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent();
                        //
                        intent.putExtra(EXTRA_SELECTED_ITEM_ID, menuItem.getItemId());
                        intent.putExtra(EXTRA_TITLE, menuItem.getTitle());
                        //
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home: // 首页(发现)
                                intent.setClass(BaseNavigationActivity.this, DiscoverActivity.class);
                                break;
                            case R.id.nav_category: // 分类
                                intent.setClass(BaseNavigationActivity.this, CategoryActivity.class);
                                break;
                            case R.id.nav_author: // 作者
                                intent.setClass(BaseNavigationActivity.this, AuthorActivity.class);
                                break;
                            case R.id.nav_recommend: // 推荐
                                intent.setClass(BaseNavigationActivity.this, RecommendActivity.class);
                                break;
                            case R.id.nav_favorite: // 收藏
                                intent.setClass(BaseNavigationActivity.this, FavoriteActivity.class);
                                break;
                            case R.id.nav_settings: // 设置
                                intent.setClass(BaseNavigationActivity.this, RecommendActivity.class);
                                break;
                            default:
                                break;
                        }
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.abc_fade_out);
                        return false;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        super.onBackPressed();
    }
}
