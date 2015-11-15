package com.likebamboo.osa.android.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.likebamboo.osa.android.R;

/**
 * 通用webview，包装webview，向外暴露webview部分接口，统一封装了native功能，提供给页面使用
 *
 * @version [版本号, 2015-5-24]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class CommonWebView extends FrameLayout {
    /**
     * 上下文对象
     */
    private Context mContext = null;

    /**
     * webView
     */
    private ObservedWebView mWebview = null;

    /**
     * 进度条
     */
    private ProgressBar mProgressBar = null;

    /**
     * 工具栏
     */
    private WebViewToolBar mToolBar = null;

    /**
     * 状态回调
     */
    private IWebViewStatusListener mListener = null;

    /**
     * webView client
     */
    private WebViewClient mWebViewClient = new WebViewClient() {

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
            if (mListener != null) {
                mListener.onPageStarted(url);
            }
        }

        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
            if (mListener != null) {
                mListener.onPageFinished(url);
            }
            // 标题
            if (!TextUtils.isEmpty(view.getTitle())) {
                if (mListener != null) {
                    mListener.onReceiveTitle(view.getTitle());
                }
            }

            if (mToolBar != null) {
                // 更新工具栏状态
                mToolBar.updateStatus();
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // Android webview访问HTTPS web page忽略验证 .
            handler.proceed(); // 接受所有网站的证书
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url == null) {
                return true;
            }
            if (mListener != null && mListener.shouldOverrideUrl(url)) {
                return true;
            }
            return false;
        }
    };

    /**
     * WebChromeClient
     */
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(GONE);
            } else {
                if (mProgressBar.getVisibility() == GONE) {
                    mProgressBar.setVisibility(VISIBLE);
                }
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (!TextUtils.isEmpty(title)) {
                if (mListener != null) {
                    mListener.onReceiveTitle(view.getTitle());
                }
            }
        }
    };

    public CommonWebView(Context context) {
        this(context, null);
    }

    public CommonWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        initView(context);
    }

    /**
     * 初始化view
     */
    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.common_webview, this, true);
        mWebview = (ObservedWebView) findViewById(R.id.common_webview);
        mProgressBar = (ProgressBar) findViewById(R.id.webview_progress_bar);
        mToolBar = (WebViewToolBar) findViewById(R.id.webview_tool_bar);

        initWebViewSettings();
        mToolBar.attachToWebView(mWebview);
    }

    /**
     * 初始化webview配置
     */
    private void initWebViewSettings() {
        WebSettings webSettings = mWebview.getSettings();
        // 打开h5 localstorage
        webSettings.setDomStorageEnabled(true);
        // 不设置setDatabasePath，html5数据只会保存在内存
        webSettings.setDatabaseEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(PluginState.ON);
        // 禁止window open
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        // 支持viewport meta tag
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(false);
        mWebview.setVerticalScrollBarEnabled(false);
        mWebview.setHorizontalScrollBarEnabled(false);
        // 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        mWebview.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mWebview.setWebChromeClient(mWebChromeClient);
        mWebview.setWebViewClient(mWebViewClient);
    }

    // ==============api method ===========

    /**
     * 加载指定url
     */
    public void loadUrl(String url) {
        mWebview.loadUrl(url);
    }

    /**
     * onpause
     */
    public void onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebview.onPause();
        }
    }

    /**
     * onResume
     */
    public void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebview.onResume();
        }
    }

    /**
     * 滚动监听
     *
     * @param l
     */
    public void setOnScrollChangeListener(ObservedWebView.OnScrollChangedCallback l) {
        mWebview.setOnScrollChangedCallback(l);
    }

    /**
     * 设置toolbar是否显示
     *
     * @param visibility
     */
    public void setToolBarVisibility(int visibility) {
        mToolBar.setVisibility(visibility);
    }

    /**
     * 设置状态监听回调
     *
     * @param mListener
     */
    public void setStatusListener(IWebViewStatusListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 返回
     *
     * @return
     */
    public boolean goBack() {
        if (mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }
        return false;
    }


    public interface IWebViewStatusListener {
        void onPageStarted(String url);

        void onPageFinished(String url);

        void onReceiveTitle(String title);

        boolean shouldOverrideUrl(String url);
    }
}
