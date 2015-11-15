package com.likebamboo.osa.android.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Blog;
import com.likebamboo.osa.android.entity.Favorite;
import com.likebamboo.osa.android.request.JsonRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.fragments.BlogListFragment;
import com.likebamboo.osa.android.ui.view.CommonWebView;
import com.likebamboo.osa.android.ui.view.ObservedWebView;
import com.likebamboo.osa.android.utils.ToastUtil;
import com.likebamboo.osa.android.utils.UrlDetect;

import java.net.URLDecoder;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 博客详情 webView 界面
 */
public class BlogDetailActivity extends BaseContentActivity {

    /**
     * 博客URL
     */
    public static final String EXTRA_BLOG_INFO = "extra_blog_info";

    /**
     * 博客信息
     */
    private Blog mBlogInfo = null;

    @InjectView(R.id.fab)
    FloatingActionButton mFab = null;

    /**
     * webView
     */
    @InjectView(R.id.detail_webview)
    CommonWebView mWebView = null;

    /**
     * progressDialog
     */
    private ProgressDialog mProgressDialog = null;

    /**
     * 是否是收藏的数据
     */
    private boolean mIsFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置布局
        setContentId(R.layout.activity_blog_detail);
        ButterKnife.inject(this);

        // 添加webview
        mWebView.setToolBarVisibility(View.GONE);

        // 弃用 NestedScrollView ,因为 NestedScrollView 和 ObservableWebView 共用的时候， ObservableWebView 不能横向滚动(webview里的代码无法全部显示)
        // NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        // nsv.addView(mWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mBlogInfo = getIntent().getParcelableExtra(EXTRA_BLOG_INFO);
        if (mBlogInfo == null || TextUtils.isEmpty(mBlogInfo.getUrl())) {
            // 容错处理
            finish();
            return;
        }

        // 初始化View
        initView();

        // 添加监听器
        addListener();

        // 开始加载页面
        startLoading(mBlogInfo.getUrl());

        if (Favorite.findByKey(mBlogInfo.getUrl()) == null) {
            mFab.setImageResource(R.drawable.ic_favorite_off);
        } else {
            mFab.setImageResource(R.drawable.ic_favorite_on);
            mIsFavorite = true;
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
    }

    /**
     * 初始化WebView
     */
    private void initView() {
        // webView 不显示状态栏
        mWebView.setToolBarVisibility(View.GONE);
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFavorite) {
                    doUnFavorite();
                    return;
                }
                doFavorite();
            }
        });
        mWebView.setStatusListener(new CommonWebView.IWebViewStatusListener() {
            @Override
            public void onPageStarted(String url) {
            }

            @Override
            public void onPageFinished(String url) {
            }

            @Override
            public void onReceiveTitle(String title) {
                if (mToolbar != null) {
                    mToolbar.setTitle(title);
                }
            }

            @Override
            public boolean shouldOverrideUrl(String url) {
                // 如果是博客URL, 转到博客界面(BlogDetailActivity)
                String formatUrl = UrlDetect.isBlogUrl(url);
                if (!TextUtils.isEmpty(formatUrl)) {
                    // 进入博客详情界面
                    mProgressDialog.show();
                    String blogInfoUrl = String.format(RequestUrl.BLOG_INFO_URL, formatUrl.replace(RequestUrl.BLOG_URL + "/", ""));
                    // 请求
                    JsonRequest request = new JsonRequest<Blog>(blogInfoUrl, Blog.class, new Response.Listener<Blog>() {
                        @Override
                        public void onResponse(Blog response) {
                            mProgressDialog.dismiss();
                            if (response == null) {
                                return;
                            }
                            Intent i = new Intent(BlogDetailActivity.this, BlogDetailActivity.class);
                            i.putExtra(BlogDetailActivity.EXTRA_BLOG_INFO, response);
                            startActivity(i);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressDialog.dismiss();
                            ToastUtil.show(getApplicationContext(), R.string.get_blog_info_error);
                        }
                    });

                    RequestManager.addRequest(request);

                    return true;
                }
                // 如果是 tag 博客界面，
                formatUrl = UrlDetect.isTagBlogUrl(url);
                if (!TextUtils.isEmpty(formatUrl)) {
                    // 进入标签博客列表界面
                    Intent i = new Intent(BlogDetailActivity.this, BlogListActivity.class);
                    try {
                        i.putExtra(EXTRA_TITLE, URLDecoder.decode(formatUrl, "utf-8"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i.putExtra(BlogListFragment.EXTRA_REQUEST_URL, String.format(RequestUrl.TAG_BLOG_URL, formatUrl));
                    startActivity(i);
                    return true;
                }
                // 如果是其他url
                if (UrlDetect.isValidURL(url)) {
                    // 跳转到Web页面
                    Intent i = new Intent(BlogDetailActivity.this, WebViewActivity.class);
                    i.putExtra(WebViewActivity.EXTRA_URL, url);
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });

        mWebView.setOnScrollChangeListener(new DirectionWebViweScrollListener());
    }

    /**
     * 收藏数据
     */
    private void doFavorite() {
        if (mBlogInfo != null) {
            Favorite favorite = new Favorite();
            favorite.setAddTime(System.currentTimeMillis() + "");
            favorite.setKey(mBlogInfo.getUrl());
            favorite.setType(Favorite.Type.BLOG);
            favorite.setValue(mBlogInfo.toJsonString());
            // 收藏
            favorite.save();

            mFab.setImageResource(R.drawable.ic_favorite_on);
            mIsFavorite = true;
            // 显示提示信息
            ToastUtil.show(getApplicationContext(), R.string.favorite_success);

            // 发送广播，提示收藏数据了
            sendBroadcastForFavorite(true, mBlogInfo);
            return;
        }
    }


    /**
     * 取消收藏数据
     */
    private void doUnFavorite() {
        if (mBlogInfo == null) {
            return;
        }

        // 取消收藏
        Favorite.delete(mBlogInfo.getUrl());
        mIsFavorite = false;
        mFab.setImageResource(R.drawable.ic_favorite_off);

        // 显示提示信息
        ToastUtil.show(getApplicationContext(), R.string.unfavorite_success);
        sendBroadcastForFavorite(false, mBlogInfo);
    }

    /**
     * 发送收藏广播
     *
     * @param fav  收藏or取消收藏
     * @param blog 博客信息
     */
    private void sendBroadcastForFavorite(boolean fav, final Blog blog) {
        if (blog == null) {
            return;
        }
        // Intent i = new Intent(FavoriteActivity.ACTION_FAVORITE_ADD_OR_REMOVE);
        // i.putExtra(BlogInfoFragment.EXTRA_BLOG, blog);
        // i.putExtra(FavoriteActivity.EXTRA_FAVORITE_ADD_OR_REMOVE, fav);
        // sendBroadcast(i);
    }

    /**
     * 加载url
     *
     * @see [类、类#方法、类#成员]
     */
    private void startLoading(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        url = url.startsWith("/") ? url.substring(1) : url;
        if (!url.contains(RequestUrl.BASE_URL)) {
            url = String.format(RequestUrl.BLOG_VIEW_URL, url);
        }
        // 开始loading web页面
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.goBack()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }


    /**
     * 滚动监听
     */
    public class DirectionWebViweScrollListener implements ObservedWebView.OnScrollChangedCallback {

        private static final int DIRECTION_CHANGE_THRESHOLD = 8;
        private int mPrevTop;
        private boolean mUpdated;

        @Override
        public void onScroll(int l, int t) {
            if (mFab == null) {
                return;
            }
            boolean goingDown = t > mPrevTop;
            boolean changed = Math.abs(t - mPrevTop) > DIRECTION_CHANGE_THRESHOLD;
            if (changed && mUpdated) {
                if (goingDown) {
                    mFab.hide();
                } else {
                    mFab.show();
                }
            }
            mPrevTop = t;
            mUpdated = true;
        }
    }
}