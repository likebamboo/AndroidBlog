package com.likebamboo.osa.android.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.likebamboo.osa.android.R;

/**
 * 普通内容界面基类
 */
public class BaseContentActivity extends BaseActivity {

    /**
     * toolbar
     */
    protected Toolbar mToolbar;

    /**
     * 内容区
     */
    protected FrameLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // 初始化元素
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        contentLayout = (FrameLayout) findViewById(R.id.real_content);

        // 设置toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 设置内容区
     *
     * @param resid
     */
    public final void setContentId(int resid) {
        getLayoutInflater().inflate(resid, contentLayout);
    }

    /**
     * 设置内容区
     *
     * @param view
     */
    public final void setContentLayout(View view) {
        contentLayout.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }
}
