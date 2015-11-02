package com.likebamboo.osa.android.ui;

import android.os.Bundle;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.fragments.AuthorFragment;

/**
 * 作者列表界面
 */
public class AuthorActivity extends BaseNavigationActivity {

    private AuthorFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (fragment == null) {
            fragment = new AuthorFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.real_content, fragment, "author").commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
