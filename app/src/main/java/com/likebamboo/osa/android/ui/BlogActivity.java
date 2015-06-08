package com.likebamboo.osa.android.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.BlogList;
import com.likebamboo.osa.android.request.JsonRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.fragments.BlogInfoFragment;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;
import com.likebamboo.osa.android.ui.view.CommonWebView;
import com.likebamboo.osa.android.ui.view.LoadingLayout;
import com.likebamboo.osa.android.ui.view.fa.TextAwesome;
import com.likebamboo.osa.android.ui.view.fab.FabToolbar;
import com.likebamboo.osa.android.utils.NetworkUtil;
import com.likebamboo.osa.android.utils.ToastUtil;
import com.likebamboo.osa.android.utils.UrlDetect;

import java.net.URLDecoder;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 博客界面
 */
public class BlogActivity extends BaseActivity {

    /**
     * 博客URL
     */
    public static final String EXTRA_BLOG_URL = "extra_blog_url";

    /**
     * 博客信息
     */
    private BlogList.Blog mBlogInfo = null;

    /**
     * actionbar
     */
    private ActionBar mActionBar;

    @InjectView(R.id.blog_webview)
    CommonWebView mWebView = null;

    @InjectView(R.id.blog_loading_layout)
    LoadingLayout mLoadingLayout = null;

    @InjectView(R.id.fab_tool_bar)
    FabToolbar mFabToolbar = null;

    @InjectView(R.id.blog_favorite_tv)
    TextAwesome mFavTv = null;

    @InjectView(R.id.blog_issue_tv)
    TextAwesome mIssueTv = null;

    @InjectView(R.id.blog_info_tv)
    TextAwesome mInfoTv = null;

    /**
     * 打开web页面的URL
     */
    private String mBlogUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        ButterKnife.inject(this);

        mBlogUrl = getIntent().getStringExtra(EXTRA_BLOG_URL);
        if (TextUtils.isEmpty(mBlogUrl)) {
            // 容错处理
            finish();
            return;
        }

        // 初始化actionBar
        initActionBar();

        // 初始化View
        initView();

        // 添加监听器
        addListener();

        // 开始加载页面
        startLoading(mBlogUrl);

        // 检查当前博客是否已经被收藏
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 收藏
                mBlogInfo = BlogList.Blog.findBlogByUrl(mBlogUrl);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBlogInfo == null) {
                            mFavTv.setText(R.string.fa_heart_o, getString(R.string.favorite));
                        } else {
                            mFavTv.setText(R.string.fa_heart, getString(R.string.unfavorite));
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化WebView
     */
    private void initView() {
        // webView 不显示状态栏
        mWebView.setToolBarVisibility(View.GONE);

        // fab监听mWebView的滚动
        mFabToolbar.attachTo(mWebView.getWebView());

        // 问题
        mIssueTv.setText(R.string.fa_exclamation_circle, getString(R.string.issue));
        mFavTv.setText(R.string.fa_heart_o, getString(R.string.favorite));
        // 博客信息
        mInfoTv.setText(R.string.fa_info, getString(R.string.detail));
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        mWebView.setStatusListener(new CommonWebView.IWebViewStatusListener() {
            @Override
            public void onPageStarted(String url) {
            }

            @Override
            public void onPageFinished(String url) {
            }

            @Override
            public void onReceiveTitle(String title) {
                if (mActionBar != null) {
                    mActionBar.setTitle(title);
                }
            }

            @Override
            public boolean shouldOverrideUrl(String url) {
                // 如果是博客URL, 转到博客界面(BlogActivity)
                String formatUrl = UrlDetect.isBlogUrl(url);
                if (!TextUtils.isEmpty(formatUrl)) {
                    // 进入博客详情界面
                    Intent i = new Intent(BlogActivity.this, BlogActivity.class);
                    i.putExtra(BlogActivity.EXTRA_BLOG_URL, formatUrl);
                    ActivityNavigator.startActivity(BlogActivity.this, i);
                    return true;
                }
                // 如果是 tag 博客界面，
                formatUrl = UrlDetect.isTagBlogUrl(url);
                if (!TextUtils.isEmpty(formatUrl)) {
                    // 进入标签博客列表界面
                    Intent i = new Intent(BlogActivity.this, TagBlogActivity.class);
                    try {
                        String title = URLDecoder.decode(formatUrl, "UTF-8");
                        i.putExtra(EXTRA_TITLE, title);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i.putExtra(TagBlogActivity.EXTRA_TAG_NAME, formatUrl);
                    i.putExtra(NavigationActivity.EXTRA_SHOULD_DISABLE_DRAWER, true);
                    ActivityNavigator.startActivity(BlogActivity.this, i);
                    return true;
                }
                // 如果是其他url
                if (UrlDetect.isValidURL(url)) {
                    // 跳转到Web页面
                    ActivityNavigator.openWebView(BlogActivity.this, null, url);
                    return true;
                }
                return false;
            }
        });

        // 显示信息
        mInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBlogInfo != null) {
                    // 显示博客详情信息
                    showBlogInfo();
                    return;
                }
                // 加载博客信息
                loadBlogInfo(new Response.Listener<BlogList.Blog>() {
                    @Override
                    public void onResponse(BlogList.Blog blog) {
                        mLoadingLayout.showLoading(false);
                        if (blog != null) {
                            mBlogInfo = blog;
                            showBlogInfo();
                        }
                    }
                });
            }
        });

        // 收藏
        mFavTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 如果是未收藏的数据
                if (("" + mFavTv.getText()).contains(getString(R.string.fa_heart_o))) {
                    // 收藏
                    doFavorite();
                    return;
                }
                // 取消收藏
                doUnFavorite();
            }
        });
        // 反馈
        mIssueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 反馈
            }
        });

    }

    /**
     * 收藏数据
     */
    private void doFavorite() {
        if (mBlogInfo != null) {
            // showBlogInfo();
            mBlogInfo.save();
            System.out.println(BlogList.Blog.listAll(BlogList.Blog.class) + "-----");
            // 显示提示信息
            ToastUtil.show(getApplicationContext(), R.string.favorite_success);

            // 改变收藏按钮的文字
            mFavTv.setText(R.string.fa_heart, getString(R.string.unfavorite));
            return;
        }

        // 加载数据
        loadBlogInfo(new Response.Listener<BlogList.Blog>() {
            @Override
            public void onResponse(BlogList.Blog blog) {
                mLoadingLayout.showLoading(false);
                if (blog != null) {
                    mBlogInfo = blog;
                    doFavorite();
                }
            }
        });
    }


    /**
     * 取消收藏数据
     */
    private void doUnFavorite() {
        if (mBlogInfo == null) {
            return;
        }
        mBlogInfo.delete(mBlogInfo.getUrl());
        // 显示提示信息
        ToastUtil.show(getApplicationContext(), R.string.unfavorite_success);

        // 改变收藏按钮的文字
        mFavTv.setText(R.string.fa_heart_o, getString(R.string.favorite));
    }

    /**
     * 加载博客信息
     *
     * @param successListener 获取成功结果回调
     */
    private void loadBlogInfo(Response.Listener<BlogList.Blog> successListener) {
        mLoadingLayout.showLoading(true);
        JsonRequest<BlogList.Blog> request = new JsonRequest<BlogList.Blog>(
                String.format(RequestUrl.BLOG_INFO_URL, mBlogUrl),
                BlogList.Blog.class, successListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mLoadingLayout.showLoading(false);
                // 提示错误信息
                ToastUtil.show(getApplicationContext(), R.string.get_blog_info_error);
            }
        });
        request.setJustResult(true);

        RequestManager.addRequest(request, "getBlogInfo");
    }

    /**
     * 显示博客信息
     */
    private void showBlogInfo() {
        if (isFinishing()) {
            return;
        }
        BlogInfoFragment fragment = BlogInfoFragment.getInstance(mBlogInfo);
        fragment.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * 初始化actionBar布局
     */
    private void initActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_up);
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
        if (!NetworkUtil.isNetworkAvailable(this)) {
            mLoadingLayout.showError(getString(R.string.network_error));
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
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
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
}
