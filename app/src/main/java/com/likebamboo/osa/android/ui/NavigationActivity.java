package com.likebamboo.osa.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.fragments.NavigationDrawerFragment;
import com.likebamboo.osa.android.ui.fragments.SettingsFragment;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;
import com.likebamboo.osa.android.ui.view.blur.BlurBehind;
import com.likebamboo.osa.android.ui.view.blur.OnBlurCompleteListener;

/**
 * drawer导航界面基类
 */
public class NavigationActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * 是否隐藏drawer
     */
    public static final String EXTRA_SHOULD_DISABLE_DRAWER = "extra_should_disable_drawer";

    /**
     * drawer导航fragment
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * 内容区域
     */
    private FrameLayout mContainerView = null;

    private boolean hiddenDrawer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_navigation);
        hiddenDrawer = getIntent().getBooleanExtra(EXTRA_SHOULD_DISABLE_DRAWER, false);

        mContainerView = (FrameLayout) findViewById(R.id.container);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // 关闭DrawerLayout，且不让打开
        if (hiddenDrawer) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout, (hiddenDrawer ? R.drawable.ic_up : 0));
    }

    /**
     * 通过layout名称构建视图
     *
     * @param layoutId
     * @see [类、类#方法、类#成员]
     */
    @Override
    public void setContentView(int layoutId) {
        getLayoutInflater().inflate(layoutId, mContainerView);
    }

    /**
     * 通过view构建视图
     *
     * @param view
     * @see [类、类#方法、类#成员]
     */
    @Override
    public void setContentView(View view) {
        mContainerView.addView(view);
    }

    @Override
    public boolean onNavigationDrawerItemSelected(int position, String text) {
        //update the main content by start Activity
        boolean close = true;
        Intent i = new Intent();
        i.putExtra(EXTRA_TITLE, text);
        switch (position) {
            case 0:// 首页
                i.setClass(this, MainActivity.class);
                break;
            case 1:// 分类
                i.setClass(this, CategoryActivity.class);
                break;
            case 2:// 作者
                i.setClass(this, AuthorActivity.class);
                break;
            case 3:// 收藏
                i.setClass(this, FavoriteActivity.class);
                break;
            case 4:// 设置
                SettingsFragment fragment = SettingsFragment.newInstance();
                fragment.show(getSupportFragmentManager(), "dialog");
                close = false;
                break;
            default:
                break;
        }
        ActivityNavigator.withAnim(i, ActivityNavigator.AnimationMode.DEFAULT).clearTop(i).startActivity(this, i);
        return close;
    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getTitle());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            // 如果不显示drawer，直接finish
            if (hiddenDrawer) {
                finish();
                return true;
            }
            // 如果显示drawer
            if (mNavigationDrawerFragment != null) {
                if (mNavigationDrawerFragment.isDrawerOpen()) {
                    mNavigationDrawerFragment.closeDrawer();
                } else {
                    mNavigationDrawerFragment.openDrawer();
                }
            }
            return true;
        }

        if (id == R.id.action_search) {
            // 如果当前是搜索结果界面
            if (this instanceof SearchResultActivity) {
                finish();
                return true;
            }
            BlurBehind.getInstance().execute(this, new OnBlurCompleteListener() {
                @Override
                public void onBlurComplete() {
                    // 搜索
                    Intent i = new Intent(NavigationActivity.this, SearchActivity.class);
                    ActivityNavigator.withAnim(i, ActivityNavigator.AnimationMode.FADE_IN_OUT).startActivity(NavigationActivity.this, i);
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment != null && mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
