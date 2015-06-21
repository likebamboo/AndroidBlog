package com.likebamboo.osa.android.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.BlogList;
import com.likebamboo.osa.android.impl.BaseOnItemClickListener;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.ui.adapter.BlogAdapter;
import com.likebamboo.osa.android.ui.adapter.ChoiceAdapter;
import com.likebamboo.osa.android.ui.fragments.BlogInfoFragment;
import com.orm.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 收藏界面
 *
 * @author likebamboo
 * @since 2015-06-08
 */
public class FavoriteActivity extends BlogListActivity {
    /**
     * 收藏或者取消收藏Action。
     */
    public static final String ACTION_FAVORITE_ADD_OR_REMOVE = "com.likebamboo.osa.android.favorite.ACTION_ADD_OR_REMOVE";

    /**
     * 收藏或者取消收藏。
     */
    public static final String EXTRA_FAVORITE_ADD_OR_REMOVE = "extra_favorite_add_or_remove";

    /**
     * 是否处于编辑模式
     */
    private boolean isEditMode = false;

    private BroadcastReceiver mFavReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isFinishing() || intent == null || !ACTION_FAVORITE_ADD_OR_REMOVE.equals(intent.getAction()) || !intent.hasExtra(BlogInfoFragment.EXTRA_BLOG)) {
                return;
            }
            BlogList.Blog blog = null;
            try {
                blog = intent.getParcelableExtra(BlogInfoFragment.EXTRA_BLOG);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (blog == null || !(mAdapter instanceof BlogAdapter)) {
                return;
            }

            boolean add = intent.getBooleanExtra(EXTRA_FAVORITE_ADD_OR_REMOVE, false);
            // 删除收藏
            mAdapter.removeData(blog);
            // 如果是添加收藏
            if (add) {
                // 先移除旧数据, 然后再添加数据
                mAdapter.addData(blog, 0);
            }
            mAdapter.notifyDataSetChanged();

            // 如果数据为空
            if (mAdapter.getCount() <= 0) {
                mLoadingLayout.showEmpty(getString(R.string.donot_have_any_favorite));
            }
        }
    };

    /**
     * 多选监听
     */
    private AbsListView.MultiChoiceModeListener multiChoiceModeListener = null;

    /**
     *
     */
    private Field mChoiceActionMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            registerReceiver(mFavReceiver, new IntentFilter(ACTION_FAVORITE_ADD_OR_REMOVE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 多选模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            initChoiceListener();
        }
    }

    /**
     * 初始化与多选相关
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initChoiceListener() {
        mBlogListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        /**
         * StaggeredGridView的缺陷把我害惨了啊。
         * https://github.com/etsy/AndroidStaggeredGrid/issues/77
         * The StaggeredGridView does not support the following:
         * Item selector drawables
         * Item long press event
         * Scroll bars
         * Row dividers
         * Edge effect
         * Fading edge
         * Overscroll
         */
        // 多选监听回调
        multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean choice) {
                if (mAdapter instanceof ChoiceAdapter) {
                    ((ChoiceAdapter) mAdapter).setItemSelected(position, choice);
                }
                if (mBlogListView.getCheckedItemCount() == 0 && actionMode != null) {
                    actionMode.finish();
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                // actionmode的菜单处理
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.edit, menu);
                setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                isEditMode = true;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_contextual_action_check_all: // 全选 or 取消全选
                        if (!(mAdapter instanceof ChoiceAdapter)) {
                            return false;
                        }
                        int selected = ((ChoiceAdapter) mAdapter).getSelectedCount();
                        // 如果已经全选
                        if (selected == mAdapter.getCount()) { // 取消全选
                            setAllItemCheckState(false);
                        } else { // 全选
                            setAllItemCheckState(true);
                        }
                        break;
                    case R.id.nav_contextual_action_remove: // 删除收藏
                        // 删除选中
                        for (int i = 0; i < mBlogListView.getCount(); i++) {
                            if (mBlogListView.isItemChecked(i)) {
                                // 取消现有选中状态
                                try {
                                    mBlogListView.setItemChecked(i, false);
                                } catch (Exception e) {
                                    //ignore
                                }
                                // 取消现有选中状态
                                ((ChoiceAdapter) mAdapter).setItemSelected(i, false);
                                // 删除收藏的博客
                                BlogList.Blog blog = (BlogList.Blog) mAdapter.getItem(i);
                                if (blog != null) {
                                    blog.delete();
                                }
                            }
                        }
                        // 退出编辑状态
                        actionMode.finish();

                        // 重置
                        reset();
                        // 加载数据
                        loadDatas();
                        break;
                    default:
                        break;
                }
                return true;
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                isEditMode = false;
                setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                // 有可能是按back键或者title back 退出编辑模式的
                if (mBlogListView.getCheckedItemCount() > 0) {
                    setAllItemCheckState(false);
                }
            }
        };

        ((BlogAdapter) mAdapter).setOnItemClickListener(new BaseOnItemClickListener<BlogList.Blog>() {
            @Override
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onItemClick(int position, BlogList.Blog item) {
                if (isEditMode) {
                    flipItemCheckState(position);
                    return;
                }
                // 进入博客详情界面
                goToBlogDetail(item);
            }

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemLongClick(int position, BlogList.Blog item) {
                if (item == null) {
                    return;
                }
                flipItemCheckState(position);
            }
        });

        mBlogListView.setMultiChoiceModeListener(multiChoiceModeListener);
    }

    /**
     * 反转所有项选中的状态
     *
     * @param select
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setAllItemCheckState(boolean select) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            try {
                // StaggeredGridView setItemChecked 莫名报空指针异常。
                mBlogListView.setItemChecked(i, select);
            } catch (NullPointerException e) {
                // ignore
            }
        }
        // 设置adapter选中状态
        ((BlogAdapter) mAdapter).setSelectedAll(select);
    }

    /**
     * 反转某一项选中的状态
     *
     * @param position
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void flipItemCheckState(int position) {
        try {
            boolean checked = mBlogListView.isItemChecked(position);
            try {
                // StaggeredGridView setItemChecked 莫名报空指针异常。
                mBlogListView.setItemChecked(position, !checked);
            } catch (NullPointerException e) {
                // 多选回调
                if (multiChoiceModeListener != null) {
                    //获得对象所有属性
                    if (mChoiceActionMode == null) {
                        mChoiceActionMode = AbsListView.class.getDeclaredField("mChoiceActionMode");
                    }
                    if (mChoiceActionMode != null) {
                        mChoiceActionMode.setAccessible(true);
                        Object value = mChoiceActionMode.get(mBlogListView);
                        if (value instanceof ActionMode) {
                            multiChoiceModeListener.onItemCheckedStateChanged((ActionMode) value, position, position, !checked);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadDatas(RequestParams params) {
        if (isRefreshing) {
            mPageIndex = 0;
        }
        // 加载本地数据
        BlogList list = new BlogList();
        ArrayList<BlogList.Blog> blogs = BlogList.Blog.listPage(mPageIndex, mPageSize, StringUtil.toSQLName("favTime") + " desc ");
        list.setList(blogs);
        if (blogs == null || blogs.isEmpty()) {
            list.setMessage(getString(R.string.donot_have_any_favorite));
            mAdapter.notifyDataSetChanged();
        }
        isLoading = false;
        mLoadingLayout.showLoading(false);
        // 如果是刷新数据
        if (isRefreshing) {
            // 清空现有数据
            reset();
            // 停止刷新
            stopRefresh();
        }
        doOnSuccess(list);
    }

    @Override
    public void addParams(RequestParams params) {
    }

    @Override
    public String getRequestUrl() {
        return "";
    }

    @Override
    public boolean hasFilterFooter() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mFavReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
