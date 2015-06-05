package com.likebamboo.osa.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 可监听滚动的WebView
 * <p/>
 * Created by wentaoli on 2015/5/22.
 */
public class ObservedWebView extends WebView {
    private OnScrollChangedCallback mOnScrollChangedCallback;

    public ObservedWebView(final Context context) {
        super(context);
    }

    public ObservedWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservedWebView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l, t);
        }
    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public interface OnScrollChangedCallback {
        void onScroll(int l, int t);
    }
}
