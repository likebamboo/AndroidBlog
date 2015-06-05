package com.likebamboo.osa.android.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;
import com.likebamboo.osa.android.utils.DeviceUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 关于界面
 *
 * @author likebamboo
 */
public class AboutActivity extends BaseActivity {

    @InjectView(R.id.app_version_tv)
    TextView mVersionTv;

    @InjectView(R.id.app_desc_tv)
    TextView mDescTv;

    @InjectView(R.id.app_detail_tv)
    TextView mDetailTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_up);

        mVersionTv.setText(getString(R.string.version, DeviceUtil.getVersionName(this)));
        mDescTv.setMovementMethod(LinkMovementMethod.getInstance());
        mDescTv.setText(Html.fromHtml(getString(R.string.about_app)));

        mDetailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityNavigator.openWebView(AboutActivity.this, null, RequestUrl.ABOUT_APP_URL);
            }
        });
    }

}
