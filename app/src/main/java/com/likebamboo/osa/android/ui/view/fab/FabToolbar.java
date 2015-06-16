package com.likebamboo.osa.android.ui.view.fab;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.view.ObservedWebView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

/**
 * fab 对应的工具栏
 */
public class FabToolbar extends RevealFrameLayout {
    /**
     * 内容区
     */
    @InjectView(R.id.container)
    LinearLayout mContainer = null;

    @InjectView(R.id.fabbutton)
    FloatingActionButton mFAB = null;

    /**
     * 屏幕宽度
     */
    private float mScreenWidth = 0;
    /**
     * 动画执行时间
     */
    private int mDuration = 500;

    /**
     * 是否处于动画执行状态
     */
    private boolean isAniming = false;

    public FabToolbar(Context context) {
        this(context, null);
    }

    public FabToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FabToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        if (attrs != null) {
            loadAttributes(attrs);
        }
    }


    /**
     * 初始化
     */
    private void init() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        View v = inflate(getContext(), R.layout.fab_tool_bar, this);
        ButterKnife.inject(this, v);
        mContainer.setVisibility(View.GONE);

        mFAB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 正执行动画
                if (isAniming) {
                    return;
                }

                // 如果是在展示状态
                if (mContainer.getVisibility() == View.VISIBLE) {
                    hideMenu();
                    return;
                }
                showMenu();
            }
        });
    }

    /**
     * 加载布局属性
     *
     * @param attrs
     */
    private void loadAttributes(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FabToolbar, 0, 0);

        try {
            mContainer.setBackgroundColor(a.getColor(R.styleable.FabToolbar_tb_color, getResources().getColor(R.color.bg_drawer)));
            mDuration = a.getInteger(R.styleable.FabToolbar_tb_anim_duration, 500);
        } finally {
            a.recycle();
        }
    }

    /**
     * @param startRadius
     * @param endRadius
     * @param listener
     */
    private void animateCircle(float startRadius, float endRadius, SupportAnimator.AnimatorListener listener) {
        int cX = (mFAB.getLeft() + mFAB.getRight()) / 2;
        int cY = (mFAB.getTop() + mFAB.getBottom()) / 2;
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mContainer, cX, cY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(mDuration);
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.start();
    }

    @Override
    public void addView(@NonNull View child) {
        if (canAddViewToContainer(child)) {
            mContainer.addView(child);
        } else {
            super.addView(child);
        }
    }

    @Override
    public void addView(@NonNull View child, int width, int height) {
        if (canAddViewToContainer(child)) {
            mContainer.addView(child, width, height);
        } else {
            super.addView(child, width, height);
        }
    }

    @Override
    public void addView(@NonNull View child, ViewGroup.LayoutParams params) {
        if (canAddViewToContainer(child)) {
            mContainer.addView(child, params);
        } else {
            super.addView(child, params);
        }
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        if (canAddViewToContainer(child)) {
            mContainer.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

    /**
     * 判断View是否可以添加
     *
     * @param child
     * @return
     */
    private boolean canAddViewToContainer(View child) {
        return mContainer != null && !(child instanceof FloatingActionButton);
    }

    private class ToolbarCollapseListener implements SupportAnimator.AnimatorListener {

        private boolean show = false;

        public ToolbarCollapseListener(boolean show) {
            this.show = show;
        }

        @Override
        public void onAnimationEnd() {
            if (show) {
                mContainer.setVisibility(VISIBLE);
            } else {
                mContainer.setVisibility(GONE);
            }
            isAniming = false;
        }

        @Override
        public void onAnimationStart() {
            isAniming = true;
        }

        @Override
        public void onAnimationCancel() {
        }

        @Override
        public void onAnimationRepeat() {
        }
    }


    //==========api method==================

    /**
     * 显示toolbar
     */
    public void showMenu() {
        mContainer.setVisibility(VISIBLE);
        animateCircle(0, mScreenWidth, new ToolbarCollapseListener(true));
    }

    /**
     * 隐藏toolbar
     */
    public void hideMenu() {
        animateCircle(mScreenWidth, 0, new ToolbarCollapseListener(false));
    }

    /**
     * 设置动画执行事件
     *
     * @param duration
     */
    public void setAnimationDuration(int duration) {
        mDuration = duration;
    }

    /**
     * 将fabtoolbar attach到webView上
     *
     * @param webView
     */
    public void attachTo(ObservedWebView webView) {
        webView.setOnScrollChangedCallback(new DirectionWebViweScrollListener());
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
            if (isAniming) {
                return;
            }
            if (mContainer.getVisibility() == View.VISIBLE) {
                hideMenu();
                return;
            }
            if (mFAB == null) {
                return;
            }
            boolean goingDown = t > mPrevTop;
            boolean changed = Math.abs(t - mPrevTop) > DIRECTION_CHANGE_THRESHOLD;
            if (changed && mUpdated) {
                if (goingDown) {
                    mFAB.hide();
                } else {
                    mFAB.show();
                }
            }
            mPrevTop = t;
            mUpdated = true;
        }
    }
}
