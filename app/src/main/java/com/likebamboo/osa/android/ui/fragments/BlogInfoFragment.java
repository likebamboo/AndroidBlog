package com.likebamboo.osa.android.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.BlogList;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;
import com.likebamboo.osa.android.ui.view.blur.BlurDialogFragmentHelper;

import java.net.URLDecoder;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 博客信息Fragment
 *
 * @author likebamboo
 */
public class BlogInfoFragment extends DialogFragment {
    public static final String EXTRA_BLOG = "extra_blog";

    private BlurDialogFragmentHelper mHelper;

    /**
     * 博客信息
     */
    private BlogList.Blog mBlog = null;

    @InjectView(R.id.blog_title_tv)
    TextView mTitleTv;

    @InjectView(R.id.blog_author_title_tv)
    TextView mAuthorTitleTv;

    @InjectView(R.id.blog_author_tv)
    TextView mAuthorTv;

    @InjectView(R.id.blog_link_tv)
    TextView mUrlTv;

    @InjectView(R.id.blog_time_tv)
    TextView mPostTimeTv;

    @InjectView(R.id.blog_category_tv)
    TextView mCategoryTv;

    // for 翻译的文章
    @InjectView(R.id.blog_o_author_tv)
    TextView mOAuthorTv;

    @InjectView(R.id.blog_o_link_tv)
    TextView mOUrlTv;

    @InjectView(R.id.blog_o_time_tv)
    TextView mOPostTimeTv;

    /**
     * instance
     *
     * @param blog
     * @return
     */
    public static BlogInfoFragment getInstance(BlogList.Blog blog) {
        BlogInfoFragment fragment = new BlogInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_BLOG, blog);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new BlurDialogFragmentHelper(this);
        mHelper.onCreate();
        if (getArguments() != null) {
            mBlog = getArguments().getParcelable(EXTRA_BLOG);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_blog_info, container, false);

        if (mBlog == null) {
            return v;
        }
        ButterKnife.inject(this, v);

        // 标题
        mTitleTv.setText(mBlog.getTitle());
        // 作者
        mAuthorTv.setText(mBlog.getAuthor());
        // 链接
        try {
            final String url = URLDecoder.decode(mBlog.getFromUrl(), "UTF-8");
            mUrlTv.setText(url);
            mUrlTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() == null) {
                        return;
                    }
                    ActivityNavigator.openWebView(getActivity(), null, url);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 发表时间
        mPostTimeTv.setText(mBlog.getPostTime());
        // set info
        mCategoryTv.setText(mBlog.getCategorys());

        // 是否为翻译
        if (mBlog.isTrans()) {
            // 译者
            mAuthorTitleTv.setText(getString(R.string.translator));
            mOAuthorTv.setText(mBlog.getoAuthor());
            mOPostTimeTv.setText(mBlog.getoPostTime());
            try {
                final String oUrl = URLDecoder.decode(mBlog.getoFromUrl(), "UTF-8");
                mOUrlTv.setText(oUrl);
                mOUrlTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getActivity() == null) {
                            return;
                        }
                        ActivityNavigator.openWebView(getActivity(), null, oUrl);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 作者
            mAuthorTitleTv.setText(getString(R.string.author));
            v.findViewById(R.id.blog_o_author_layout).setVisibility(View.GONE);
            v.findViewById(R.id.blog_o_time_layout).setVisibility(View.GONE);
            v.findViewById(R.id.blog_o_link_layout).setVisibility(View.GONE);
        }
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHelper.onActivityCreated();
    }

    @Override
    public void onStart() {
        super.onStart();
        mHelper.onStart();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mHelper.onDismiss();
        super.onDismiss(dialog);
    }
}