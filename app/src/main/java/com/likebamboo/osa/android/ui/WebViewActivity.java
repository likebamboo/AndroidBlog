package com.likebamboo.osa.android.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.view.CommonWebView;

/**
 * 普通WebView界面
 */
public class WebViewActivity extends BaseContentActivity {

    /**
     * URL
     */
    public static final String EXTRA_URL = "extra_url";

    private CommonWebView mWebView = null;

    /**
     * 打开web页面的URL
     */
    private String mUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加webview
        mWebView = new CommonWebView(this);
        mWebView.setToolBarVisibility(View.GONE);
        // 设置布局
        setContentLayout(mWebView);
        // 弃用 NestedScrollView ,因为 NestedScrollView 和 WebView 共用的时候， WebView 不能横向滚动(webview里的代码无法全部显示)
        // NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        // nsv.addView(mWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // 添加监听器
        addListener();

        mUrl = getIntent().getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(mUrl)) {
            // 容错处理
            finish();
            return;
        }
        // 开始加载页面
        startLoading(mUrl);
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
                return false;
            }
        });
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
        overridePendingTransition(0, R.anim.abc_fade_out);
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
