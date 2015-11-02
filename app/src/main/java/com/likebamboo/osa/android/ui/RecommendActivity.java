package com.likebamboo.osa.android.ui;

import android.os.Bundle;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.fragments.BlogListFragment;

/**
 * 推荐界面
 */
public class RecommendActivity extends BaseNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(R.id.real_content, new BlogListFragment(), "blog").commit();
    }
}
