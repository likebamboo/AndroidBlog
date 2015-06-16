package com.likebamboo.osa.android.ui.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.view.fab.FloatingView;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * listView底部过滤器布局
 * <p/>
 * Created by likebamboo on 2015/6/15.
 */
public class FilterFooter extends FrameLayout implements FloatingView {

    /**
     * 加速器
     */
    private Interpolator mInterpolator = new AccelerateInterpolator();
    /**
     * 是否隐藏
     */
    private boolean mHidden = false;

    /**
     * 隐藏时候的Y值
     */
    private float mYHidden = -1;
    /**
     * 显示时候的Y值
     */
    private float mYDisplayed = -1;

    @InjectView(R.id.filter_category_tv)
    TextView mCategoryTv = null;

    @InjectView(R.id.filter_sort_tv)
    TextView mSortTv = null;

    /**
     * 点击事件回调
     */
    private IOnFilterClickListener mFilterClickListener = null;

    public FilterFooter(Context context) {
        this(context, null);
    }

    public FilterFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            mYHidden = size.y;
        } else {
            mYHidden = display.getHeight();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            return;
        }
        ButterKnife.inject(this);
        mCategoryTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFilterClickListener == null) {
                    return;
                }
                mFilterClickListener.onCategoryClick();
            }
        });
        mSortTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFilterClickListener == null) {
                    return;
                }
                mFilterClickListener.onSortClick();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // Perform the default behavior
        super.onLayout(changed, left, top, right, bottom);
        // Store the FAB button's displayed Y position if we are not already aware of it
        if (mYDisplayed == -1) {
            mYDisplayed = ViewHelper.getY(this);
        }
    }

    @Override
    public void hide() {
        hide(true);
    }

    @Override
    public void show() {
        hide(false);
    }

    /**
     * 设置点击事件回调
     *
     * @param l
     */
    public void setFilterClickListener(IOnFilterClickListener l) {
        this.mFilterClickListener = l;
    }

    private void hide(boolean hide) {
        // If the hidden state is being updated
        if (mHidden != hide) {
            // Store the new hidden state
            mHidden = hide;

            // Animate the FAB to it's new Y position
            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "y", mHidden ? mYHidden : mYDisplayed).setDuration(500);
            animator.setInterpolator(mInterpolator);
            animator.start();
        }
    }

    /**
     * 点击事件回调
     */
    public interface IOnFilterClickListener {
        /**
         * 点击排序
         */
        void onSortClick();

        /**
         * 点击分类
         */
        void onCategoryClick();
    }
}
