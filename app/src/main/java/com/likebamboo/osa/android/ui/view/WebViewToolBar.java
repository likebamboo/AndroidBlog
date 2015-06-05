package com.likebamboo.osa.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.likebamboo.osa.android.R;

/**
 * WebView 操作栏封装，提供 back ,refresh ,forward 功能
 * 
 * @author likebamboo
 * @version [版本号, 2015年5月20日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class WebViewToolBar extends RelativeLayout implements View.OnClickListener
{

    /**
     * 后退
     */
    private View mWebviewGoBack;

    /**
     * 刷新
     */
    private View mWebviewRefresh;

    /**
     * 前进
     */
    private View mWebviewGoForward;

    /**
     * 目标WebView
     */
    private WebView mTargetView = null;

    public WebViewToolBar(Context context)
    {
        this(context, null);
    }

    public WebViewToolBar(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public WebViewToolBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.webview_toolbar, this, true);
        if (!isInEditMode())
        {
            // 初始化
            initView();
            // 添加监听器
            addListener();
        }
    }

    /**
     * 初始化界面元素
     * 
     * @see [类、类#方法、类#成员]
     */
    private void initView()
    {
        mWebviewGoBack = findViewById(R.id.webviewGoBack);
        mWebviewGoBack.setEnabled(false);
        mWebviewRefresh = findViewById(R.id.webviewRefresh);
        mWebviewRefresh.setEnabled(false);
        mWebviewGoForward = findViewById(R.id.webviewGoForward);
        mWebviewGoForward.setEnabled(false);
    }

    /**
     * 添加监听器
     * 
     * @see [类、类#方法、类#成员]
     */
    private void addListener()
    {
        mWebviewGoBack.setOnClickListener(this);
        mWebviewRefresh.setOnClickListener(this);
        mWebviewGoForward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == null || mTargetView == null)
        {
            return;
        }
        switch (v.getId())
        {
            case R.id.webviewGoBack:// 后退
                mTargetView.goBack();
                break;
            case R.id.webviewRefresh:// 刷新
                mTargetView.reload();
                break;
            case R.id.webviewGoForward:// 向前
                mTargetView.goForward();
                break;
            default:
                break;
        }
    }

    /**
     * 将本控件绑定到具体的webView 上
     * 
     * @see [类、类#方法、类#成员]
     */
    public void attachToWebView(WebView target)
    {
        mTargetView = target;
    }

    /**
     * 更新状态
     * 
     * @see [类、类#方法、类#成员]
     */
    public void updateStatus()
    {
        if (mTargetView == null)
        {
            return;
        }
        mWebviewGoBack.setEnabled(mTargetView.canGoBack());
        mWebviewRefresh.setEnabled(true);
        mWebviewGoForward.setEnabled(mTargetView.canGoForward());
    }

}
