package com.likebamboo.osa.android.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Author;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.fragments.BlogListFragment;

import java.net.URLEncoder;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者信息界面
 */
public class AuthorDetailActivity extends BaseActivity implements View.OnClickListener {

    /**
     *
     */
    public static final String EXTRA_AUTHOR_INFO = "extra_author_info";


    @InjectView(R.id.backdrop)
    ImageView mBackdropIv;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @InjectView(R.id.author_introduction_tv)
    TextView mIntroductionTv;

    @InjectView(R.id.author_info_tv)
    TextView mInfoTv;
    @InjectView(R.id.author_home_page)
    TextView mHomePage;
    @InjectView(R.id.author_github)
    TextView mGithub;
    @InjectView(R.id.author_blog)
    TextView mBlogRecommend;

    /**
     * 作者信息
     */
    private Author mAuthor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);

        if (getIntent() == null || !getIntent().hasExtra(EXTRA_AUTHOR_INFO)) {
            finish();
            return;
        }

        ButterKnife.inject(this);

        // 设置toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuthor = getIntent().getParcelableExtra(EXTRA_AUTHOR_INFO);
        if (mAuthor == null) {
            finish();
            return;
        }

        // 加载并显示数据
        loadShow();
    }

    /**
     * 显示数据
     */
    private void loadShow() {
        mCollapsingToolbar.setTitle(mAuthor.getName());

        Glide.with(this).load(RequestUrl.BASE_URL + mAuthor.getAvatar()).placeholder(R.color.grey_300).centerCrop().into(mBackdropIv);
        mIntroductionTv.setAutoLinkMask(Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
        mIntroductionTv.setText(Html.fromHtml(mAuthor.getIntroduction().replace("\n", "<br/>")));

        mInfoTv.setAutoLinkMask(Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.home_page)).append(" : ").append(mAuthor.getBlog());
        sb.append("\n").append(getString(R.string.github)).append(" : ").append(mAuthor.getGithub());
        mInfoTv.setText(Html.fromHtml(sb.toString().replace("\n", "<br/>")));

        // 个人主页
        if (!TextUtils.isEmpty(mAuthor.getBlog())) {
            mHomePage.setOnClickListener(this);
        } else {
            mHomePage.setVisibility(View.GONE);
        }

        // github
        if (!TextUtils.isEmpty(mAuthor.getGithub())) {
            mGithub.setOnClickListener(this);
        } else {
            mGithub.setVisibility(View.GONE);
        }
        // recommend blog
        mBlogRecommend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        Intent i = new Intent();
        switch (view.getId()) {
            case R.id.author_home_page:// 主页
                // 跳转到Web页面
                i.setClass(AuthorDetailActivity.this, WebViewActivity.class);
                i.putExtra(WebViewActivity.EXTRA_URL, mAuthor.getBlog());
                break;

            case R.id.author_github:// github
                i.setClass(AuthorDetailActivity.this, WebViewActivity.class);
                // 跳转到Web页面
                i.putExtra(WebViewActivity.EXTRA_URL, mAuthor.getGithub());
                break;

            case R.id.author_blog: // 他的博客
                i.setClass(this, BlogListActivity.class);
                String url = "";
                try {
                    url = String.format(RequestUrl.AUTHOR_BLOG_URL, URLEncoder.encode(mAuthor.getName(), "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i.putExtra(EXTRA_TITLE, mAuthor.getName());
                i.putExtra(BlogListFragment.EXTRA_REQUEST_URL, url);
                break;
        }
        ComponentName cn = i.resolveActivity(getPackageManager());
        if (cn != null) {
            startActivity(i);
        }
    }
}
