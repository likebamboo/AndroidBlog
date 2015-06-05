package com.likebamboo.osa.android.ui.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.AuthorList;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;
import com.likebamboo.osa.android.ui.view.CircleImageView;
import com.likebamboo.osa.android.ui.view.blur.BlurDialogFragmentHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 作者信息Fragment
 *
 * @author likebamboo
 */
public class AuthorInfoFragment extends DialogFragment {

    private BlurDialogFragmentHelper mHelper;

    /**
     * 作者
     */
    private AuthorList.Author mAuthor = null;

    @InjectView(R.id.author_avatar_iv)
    CircleImageView mAvatarIv;

    @InjectView(R.id.author_name_tv)
    TextView mNameTv;

    @InjectView(R.id.author_blog_tv)
    TextView mBlogTv;

    @InjectView(R.id.author_github_tv)
    TextView mGithubTv;

    @InjectView(R.id.author_info_tv)
    TextView mInfoTv;

    public AuthorInfoFragment() {
    }

    @SuppressLint("ValidFragment")
    public AuthorInfoFragment(AuthorList.Author author) {
        this.mAuthor = author;
    }

    /**
     * instance
     *
     * @param author
     * @return
     */
    public static AuthorInfoFragment newInstance(AuthorList.Author author) {
        AuthorInfoFragment fragment = new AuthorInfoFragment(author);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new BlurDialogFragmentHelper(this);
        mHelper.onCreate();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_author_info, container, false);

        if (mAuthor == null) {
            return v;
        }
        ButterKnife.inject(this, v);

        // 加载图片
        ImageLoader imageLoader = RequestManager.getImageLoader();
        if (!TextUtils.isEmpty(mAuthor.getAvatar())) {
            imageLoader.get(RequestUrl.BASE_URL + mAuthor.getAvatar(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    mAvatarIv.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mAvatarIv.setImageResource(R.drawable.default_avatar);
                }
            });
        }
        // set info
        mNameTv.setText(mAuthor.getName());
        mBlogTv.setText(mAuthor.getBlog());
        // 链接点击跳转
        mBlogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebView(mAuthor.getBlog());
            }
        });
        mGithubTv.setText(mAuthor.getGithub());
        // 链接点击跳转
        mGithubTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebView(mAuthor.getGithub());
            }
        });
        mInfoTv.setText(mAuthor.getIntroduction());
        return v;
    }

    /**
     * 打开web界面
     *
     * @param url
     */
    private void openWebView(String url) {
        if (getActivity() == null) {
            return;
        }
        // 跳转到Web页面
        ActivityNavigator.openWebView(getActivity(), null, url);
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