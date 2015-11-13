package com.likebamboo.osa.android.ui.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.exception.ErrorTrans;
import com.likebamboo.osa.android.exception.OsaException;
import com.likebamboo.osa.android.impl.RecyclerViewOnScroll;
import com.likebamboo.osa.android.interfaces.IOnItemClickListener;
import com.likebamboo.osa.android.request.JsonArrayRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.ui.adapter.BaseRecycleAdapter;
import com.likebamboo.osa.android.ui.view.LoadingLayout;

import org.json.JSONArray;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 分页加载数据Fragment基类
 * Created by likebamboo on 2015/5/14.
 */
public abstract class EndlessListFragment<T extends Parcelable> extends Fragment implements SwipeRefreshLayout.OnRefreshListener, IOnItemClickListener<T> {

    /**
     * pageSize ， 默认值 20
     */
    public static final int PAGE_SIZE = 20;

    /**
     * 参数， pageSize ， 默认值 20
     */
    public static final String PARAM_PAGE_SIZE = "pageSize";

    /**
     * 参数，pageNo
     */
    public static final String PARAM_PAGE_NO = "pageNo";

    /**
     * 缓存的数据
     */
    public static final String EXTRA_SHOULD_CACHE_DATA = "extra_should_cache_data";

    /**
     * 缓存的数据
     */
    private static final String EXTRA_CACHE_DATA = "extra_cache_data";

    /**
     * 页码
     */
    protected int mPageIndex = 0;

    /**
     * 页容量
     */
    protected int mPageSize = PAGE_SIZE;

    /**
     * 是否还有更多
     */
    protected boolean mHasMore = true;

    /**
     * 是否正在加载数据
     */
    protected boolean isLoading = false;

    /**
     * 是否正在下拉刷新数据
     */
    protected boolean isRefreshing = false;


    @InjectView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    /**
     * 适配器
     */
    protected BaseRecycleAdapter mAdapter = null;

    /**
     * loading
     */
    @InjectView(android.R.id.empty)
    protected LoadingLayout mLoadingLayout = null;

    /**
     * 下拉刷新控件
     */
    @InjectView(R.id.swipe_refresh)
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * footer
     */
    protected LoadingLayout mFooterView = null;

    /**
     * 是否需要缓存数据
     */
    private boolean mShouldCache = false;

    /**
     * 滚动事件监听
     */
    protected RecyclerView.OnScrollListener mRecycleScrollListener = new RecyclerViewOnScroll() {
        @Override
        public void onFirstItemVisible(RecyclerView recyclerView) {
            super.onFirstItemVisible(recyclerView);
        }

        @Override
        public void onLastItemVisible(RecyclerView recyclerView) {
            super.onLastItemVisible(recyclerView);
            loadDatas();
        }
    };

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.e(getClass().getName(), "onCreateView-->");
        if (getArguments() != null) {
            mShouldCache = getArguments().getBoolean(EXTRA_SHOULD_CACHE_DATA);
        }
        int layoutId = getLayoutId() > 0 ? getLayoutId() : R.layout.fragment_endless_loading;
        View root = inflater.inflate(layoutId, null);
        ButterKnife.inject(this, root);
        // 添加footer
        mFooterView = (LoadingLayout) inflater.inflate(R.layout.footer_loading_layout, null);
        // 重试
        mLoadingLayout.setRetryListener(new LoadingLayout.IRetryListener() {
            @Override
            public void onRetry() {
                reloadDatas();
            }
        });
        // footerview
        mFooterView.setRetryListener(new LoadingLayout.IRetryListener() {
            @Override
            public void onRetry() {
                mFooterView.showLoading(true);
                reloadDatas();
            }
        });

        // 初始设置其不可见
        mFooterView.setVisibility(View.GONE);

        // 下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // 设置适配器
        mAdapter = initAdapter();
        // 设置listView的footer
        mAdapter.setFooter(mFooterView);
        // 设置监听事件
        mAdapter.setOnItemClickListener(this);

        // 设置布局管理器
        mRecyclerView.setLayoutManager(getLayoutManager());
        // 设置适配器
        mRecyclerView.setAdapter(mAdapter);
        // 设置加载动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置加载更多
        mRecyclerView.addOnScrollListener(mRecycleScrollListener);

        // 如果页面数据有缓存
        if (mShouldCache && savedInstanceState != null) {
            ArrayList<T> data = savedInstanceState.getParcelableArrayList(EXTRA_CACHE_DATA);
            if (data != null && !data.isEmpty()) {
                mLoadingLayout.showLoading(false);
                doOnSuccess(data);
                return root;
            }
        }

        // 清空现有数据
        reset();
        // 加载数据
        loadDatas();

        return root;
    }

    /**
     * 重试
     */
    public void reloadDatas() {
        if (mPageIndex == 0) {
            // 显示加载中的布局
            mLoadingLayout.showLoading(true);
        }
        // 加载数据
        loadDatas();
    }

    /**
     * 网络请求出错时回调
     *
     * @return
     */
    protected final Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                doOnError(ErrorTrans.transToOsaException(error));
            }
        };
    }


    /**
     * 加载数据成功时回调
     *
     * @return
     */
    protected final Response.Listener<JSONArray> responseListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray data) {
                if (getTClass() == null) {
                    doOnError(new OsaException("数据解析出错"));
                    return;
                }
                ArrayList<T> result = (ArrayList<T>) JsonArrayRequest.parseToArrayList(data.toString(), getTClass());
                isLoading = false;
                mLoadingLayout.showLoading(false);
                // 如果是刷新数据
                if (isRefreshing) {
                    // 清空现有数据
                    reset();
                    // 停止刷新
                    stopRefresh();
                }
                doOnSuccess(result);
            }
        };
    }

    /**
     * 获取泛型类型
     *
     * @return
     */
    public Class<?> getTClass() {
        try {
            Type sType = getClass().getGenericSuperclass();
            Type[] generics = ((ParameterizedType) sType).getActualTypeArguments();
            Class<T> mTClass = (Class<T>) (generics[0]);
            return mTClass.newInstance().getClass();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消请求
        RequestManager.cancelAll(getClass().getName());
        isLoading = false;
    }

    /**
     * 加载数据
     */
    protected final void loadDatas() {
        // 如果正在加载数据，或者没有更多数据了，
        if (isLoading) {
            return;
        }
        if (!mHasMore) {
            mFooterView.showEmpty(getString(R.string.has_not_more_data));
            return;
        }
        isLoading = true;
        if (mPageIndex > 0) {
            mFooterView.showLoading(true);
        } else {
            mLoadingLayout.showLoading(true);
        }
        // 加载数据
        RequestParams params = new RequestParams();
        params.add(PARAM_PAGE_NO, (mPageIndex + 1) + "").add(PARAM_PAGE_SIZE, "" + mPageSize);
        loadDatas(params);
    }

    @Override
    public void onRefresh() {
        // 如果正在加载数据，不刷新
        if (isLoading) {
            // stop refresh
            stopRefresh();
            return;
        }
        isRefreshing = true;
        // 加载数据
        RequestParams params = new RequestParams();
        params.add(PARAM_PAGE_NO, 1 + "").add(PARAM_PAGE_SIZE, "" + mPageSize);
        loadDatas(params);
    }

    /**
     * 加载失败，显示错误信息
     *
     * @param error
     */
    protected void doOnError(OsaException error) {
        // 如果加载第一页的数据出错，显示错误信息
        if (mPageIndex == 0) {
            mLoadingLayout.showError(error.getMessage() + "\n" + getString(R.string.retry));
            mFooterView.showLoading(false);
            return;
        }
        mFooterView.showError(error.getMessage() + "\n" + getString(R.string.retry));
    }

    /**
     * 加载成功回调
     *
     * @param data
     */
    protected void doOnSuccess(ArrayList<T> data) {
        if (data == null) {
            showMessage("");
            return;
        }
        // 初始设置其可见
        mFooterView.setVisibility(View.VISIBLE);
        ++mPageIndex;
        if (data.size() < mPageSize) {
            mHasMore = false;
        } else {
            mHasMore = true;
        }
        mAdapter.addDatas(data);
        // 如果数据不足，显示没有更多数据了
        if (data.isEmpty() || data.size() < mPageSize) {
            showMessage("");
        }
    }

    /**
     * 显示服务器返回的信息
     */
    protected void showMessage(String msg) {
        // 如果加载第一页的数据为空
        if (mPageIndex == 0) {
            mLoadingLayout.showEmpty(msg == null ? getString(R.string.can_not_find_data) : msg);
        } else {
            mFooterView.showEmpty(getString(R.string.has_not_more_data));
        }
    }

    /**
     * 重置
     */
    protected void reset() {
        // 清空现有数据
        if (mAdapter != null) {
            mAdapter.clear();
        }
        isLoading = false;
        mHasMore = true;
        mPageIndex = 0;
    }

    /**
     * stopRefresh
     */
    protected void stopRefresh() {
        isRefreshing = false;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    /**
     * 返回布局id
     */
    protected int getLayoutId() {
        return R.layout.fragment_endless_loading;
    }

    /**
     * 获取布局管理器
     *
     * @return
     */
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    /**
     * 滑动到顶部
     */
    public void smoothScrollToTop() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(getClass().getName(), "onSaveInstanceState-->");
        outState.putParcelableArrayList(EXTRA_CACHE_DATA, mAdapter.getDatas());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(getClass().getName(), "onDestroyView-->");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(getClass().getName(), "onCreate-->");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(getClass().getName(), "onPause-->");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(getClass().getName(), "onStart-->");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(getClass().getName(), "onStop-->");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(getClass().getName(), "onResume-->");
    }

    @Override
    public void onItemClick(int position, T item) {

    }

    @Override
    public void onItemLongClick(int position, T item) {

    }

    /**
     * 初始化适配器
     */
    protected abstract BaseRecycleAdapter initAdapter();

    /**
     * 加载数据
     *
     * @param params
     */
    protected abstract void loadDatas(RequestParams params);

}
