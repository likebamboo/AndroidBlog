package com.likebamboo.osa.android.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.likebamboo.osa.android.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author likebamboo
 * @date 2015/5/13.
 * @desc <pre>描述： Loading(整块区域或者加载更多时loading)
 */
public class LoadingLayout extends LinearLayout {
    /**
     * 正在加载view
     */
    @InjectView(R.id.loading_layout)
    View mLoadingView = null;

    /**
     * Loading 提示TextView
     */
    @InjectView(R.id.loading_tv)
    TextView mLoadingTv = null;

    /**
     * 重试布局
     */
    @InjectView(R.id.loading_fail_layout)
    View mRetryLayout = null;

    /**
     * 错误提示TextView
     */
    @InjectView(R.id.loading_fail_tv)
    TextView mErrorTv = null;

    /**
     * 重试接口
     */
    private IRetryListener mRetryListener = null;

    /**
     * 是否可以重试
     */
    private boolean canRetry = true;

    public interface IRetryListener {
        void onRetry();
    }

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            return;
        }
        ButterKnife.inject(this);
        mRetryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canRetry) {
                    return;
                }
                if (mRetryListener != null) {
                    mRetryListener.onRetry();
                }
            }
        });
    }

    /**
     * 显示/隐藏正在加载中。。。
     *
     * @param show
     * @see [类、类#方法、类#成员]
     */
    public void showLoading(boolean show) {
        showLoading(show, null);
    }

    /**
     * 显示/隐藏正在加载中。。。
     *
     * @param show
     * @param text
     * @see [类、类#方法、类#成员]
     */
    public void showLoading(boolean show, String text) {
        if (show) {
            setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.VISIBLE);
            mRetryLayout.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(text)) {
                mLoadingTv.setText(text);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    /**
     * 显示加载失败信息
     *
     * @param msg
     * @see [类、类#方法、类#成员]
     */
    public void showError(String msg) {
        setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mRetryLayout.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(msg)) {
            mErrorTv.setText(msg);
        }
        canRetry = true;
    }

    /**
     * 显示"空"信息
     *
     * @param msg
     */
    public void showEmpty(String msg) {
        showError(msg);
        // 不允许重试
        canRetry = false;
    }

    /**
     * 设置重试监听器。
     *
     * @param listener
     */
    public void setRetryListener(IRetryListener listener) {
        this.mRetryListener = listener;
    }

}
