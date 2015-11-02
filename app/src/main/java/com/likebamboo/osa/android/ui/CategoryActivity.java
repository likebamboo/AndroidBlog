package com.likebamboo.osa.android.ui;

import android.os.Bundle;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.fragments.CategoryFragment;

/**
 * 分类列表界面
 */
public class CategoryActivity extends BaseNavigationActivity {

    private CategoryFragment categoryFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (categoryFragment == null) {
            categoryFragment = new CategoryFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.real_content, categoryFragment, "category").commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
