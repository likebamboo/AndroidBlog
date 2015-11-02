package com.likebamboo.osa.android.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Blog;
import com.likebamboo.osa.android.request.JsonRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.fragments.BlogListFragment;
import com.likebamboo.osa.android.ui.view.CommonWebView;
import com.likebamboo.osa.android.utils.ToastUtil;
import com.likebamboo.osa.android.utils.UrlDetect;

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

    /**
     * webView
     */
    private CommonWebView mWebView = null;

    /**
     * progressDialog
     */
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentId(R.layout.nested_scroll_view);

        // 添加webview
        mWebView = new CommonWebView(this);
        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nsv.addView(mWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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

        // 检查当前博客是否已经被收藏
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 收藏
                mBlogInfo = Blog.findBlogByUrl(mBlogInfo.getUrl());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBlogInfo == null) {
                            // mFavTv.setText(R.string.fa_heart_o, getString(R.string.favorite));
                        } else {
                            // mFavTv.setText(R.string.fa_heart, getString(R.string.unfavorite));
                        }
                    }
                });
            }
        }).start();

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
    }

    /**
     * 收藏数据
     */
    private void doFavorite() {
        if (mBlogInfo != null) {
            // 设置收藏时间
            mBlogInfo.setFavTime(System.currentTimeMillis());
            mBlogInfo.save();
            // 显示提示信息
            ToastUtil.show(getApplicationContext(), R.string.favorite_success);

            // 改变收藏按钮的文字
            // mFavTv.setText(R.string.fa_heart, getString(R.string.unfavorite));

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
        mBlogInfo.delete(mBlogInfo.getUrl());
        // 清空id
        mBlogInfo.setId(null);
        // 显示提示信息
        ToastUtil.show(getApplicationContext(), R.string.unfavorite_success);
        // 改变收藏按钮的文字
        // mFavTv.setText(R.string.fa_heart_o, getString(R.string.favorite));
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
}
