package com.likebamboo.osa.android.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.view.CommonWebView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 普通WebView界面
 */
public class WebViewActivity extends BaseActivity {

    /**
     * 博客URL
     */
    public static final String EXTRA_URL = "extra_url";

    /**
     * actionbar
     */
    private ActionBar mActionBar;

    @InjectView(R.id.webview)
    CommonWebView mWebView = null;

    /**
     * 打开web页面的URL
     */
    private String mUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.inject(this);

        // 初始化actionBar
        initActionBar();

        // 初始化View
        initView();

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
     * 初始化控件
     */
    private void initView() {
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
                return false;
            }
        });
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
