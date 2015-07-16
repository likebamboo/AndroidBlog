package com.likebamboo.osa.android.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.BaseRsp;
import com.likebamboo.osa.android.entity.Feedback;
import com.likebamboo.osa.android.entity.IssueList;
import com.likebamboo.osa.android.request.JsonRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.view.LoadingLayout;
import com.likebamboo.osa.android.ui.view.TagGroup;
import com.likebamboo.osa.android.ui.view.blur.BlurDialogFragmentHelper;
import com.likebamboo.osa.android.utils.ToastUtil;
import com.likebamboo.osa.android.utils.ValidateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 反馈Fragment
 *
 * @author likebamboo
 */
public class FeedbackFragment extends DialogFragment {
    /**
     * 博客id
     */
    public static final String EXTRA_BLOG_ID = "extra_blog_id";

    /**
     * 博客标题
     */
    public static final String EXTRA_BLOG_TITLE = "extra_blog_title";

    /**
     * TAG
     */
    public static final String TAG = "feedback";

    private BlurDialogFragmentHelper mHelper;

    /**
     * 反馈信息
     */
    private Feedback mFeedback = null;

    /**
     * 博客标题
     */
    private String mBlogTitle = "";

    @InjectView(R.id.feed_title_tv)
    TextView mTitleTv;

    @InjectView(R.id.feed_contact_et)
    EditText mContactEt;

    @InjectView(R.id.feed_issues_tags)
    TagGroup mIssuesTag;

    @InjectView(R.id.feed_desc_et)
    EditText mDescEt;

    @InjectView(R.id.feed_submit_tv)
    TextView mSubmitTv;

    @InjectView(R.id.loading_layout)
    LoadingLayout mLoadingLayout;

    /**
     *
     */
    ObjectMapper mapper = null;

    /**
     * instance
     *
     * @param blogId
     * @return
     */
    public static FeedbackFragment getInstance(long blogId, String blogTitle) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_BLOG_ID, blogId);
        bundle.putString(EXTRA_BLOG_TITLE, blogTitle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new BlurDialogFragmentHelper(this);
        mHelper.setDismissOnTouch(false);
        mHelper.onCreate();
        mFeedback = new Feedback();
        mFeedback.setIssues(new ArrayList<String>());
        if (getArguments() != null) {
            mFeedback.setBlogId(getArguments().getLong(EXTRA_BLOG_ID));
            mBlogTitle = getArguments().getString(EXTRA_BLOG_TITLE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.inject(this, v);

        // 标题
        mTitleTv.setText(mBlogTitle);
        // 加载tag
        loadingIssues(mIssuesTag);

        // 提交
        mSubmitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                // 验证联系方式
                if (TextUtils.isEmpty(mContactEt.getText().toString().trim())) {
                    ToastUtil.show(getActivity().getApplicationContext(), getString(R.string.please_input_contact));
                    return;
                }

                // 验证联系方式
                if (!validateContact(mContactEt.getText().toString().trim())) {
                    ToastUtil.show(getActivity().getApplicationContext(), getString(R.string.please_input_contact_correct));
                    return;
                }

                // 问题
                if (mFeedback.getIssues() == null || mFeedback.getIssues().isEmpty()) {
                    ToastUtil.show(getActivity().getApplicationContext(), getString(R.string.please_select_issues));
                    return;
                }

                // 问题详情
                if (TextUtils.isEmpty(mDescEt.getText().toString().trim())) {
                    ToastUtil.show(getActivity().getApplicationContext(), getString(R.string.please_write_detail));
                    return;
                }

                // 置为不可点击
                mSubmitTv.setEnabled(false);

                //
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                mFeedback.setAddTime(sdf.format(new Date()));
                mFeedback.setContact(mContactEt.getText().toString().trim());
                mFeedback.setDescription(mDescEt.getText().toString().trim());

                doFeedback();
            }
        });

        return v;
    }

    /**
     *
     */
    private void doFeedback() {
        if (mFeedback == null) {
            return;
        }
        mLoadingLayout.showLoading(true);
        RequestParams params = new RequestParams();

        // Convert object to JSON string
        try {
            if (mapper == null) {
                mapper = new ObjectMapper();
            }
            String json = mapper.writeValueAsString(mFeedback);
            params.add("feedback", json);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 服务器端有BUG，post请求返回了cache-age，导致有缓存。
        // 客户端做兼容方案，
        JsonRequest request = new JsonRequest<BaseRsp>(Request.Method.POST,
                String.format(RequestUrl.FEEDBACK_SAVE_URL, "" + (new Random()).nextInt()), BaseRsp.class, params,
                new Response.Listener<BaseRsp>() {
                    @Override
                    public void onResponse(BaseRsp resp) {
                        mSubmitTv.setEnabled(true);
                        if (getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }
                        mLoadingLayout.showLoading(false);
                        if (resp != null && resp.getErrorCode() == 0) {
                            // 请求成功
                            ToastUtil.show(getActivity().getApplicationContext(), getString(R.string.feedback_commit_success));
                            dismiss();
                            return;
                        }
                        if (resp != null) {
                            // 请求失败
                            ToastUtil.show(getActivity().getApplicationContext(), resp.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mSubmitTv.setEnabled(true);
                        if (volleyError == null || getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }
                        mLoadingLayout.showLoading(false);
                        // 请求失败
                        ToastUtil.show(getActivity().getApplicationContext(), volleyError.getMessage());
                    }
                });
        RequestManager.addRequest(request, TAG);
    }

    /**
     * 验证联系方式格式
     *
     * @param info
     * @return
     */
    private boolean validateContact(String info) {
        if (TextUtils.isEmpty(info)) {
            return false;
        }

        return ValidateUtil.isEmail(info) || ValidateUtil.isPhoneNum(info);
    }

    /**
     * 加载issues列表
     */
    private void loadingIssues(final TagGroup tagGroup) {
        JsonRequest request = new JsonRequest<IssueList>(RequestUrl.ISSUES_LIST_URL, IssueList.class, new Response.Listener<IssueList>() {
            @Override
            public void onResponse(IssueList issueList) {
                if (issueList == null || getActivity() == null) {
                    return;
                }
                ArrayList<IssueList.Issue> issues = issueList.getList();
                if (issues != null && !issues.isEmpty()) {
                    ArrayList<String> tags = new ArrayList<String>();
                    for (IssueList.Issue issue : issues) {
                        tags.add(issue.getName());
                    }
                    tagGroup.setTags(tags);
                    // 设置tag单击事件
                    tagGroup.setOnTagClickListener(new TagGroup.IOnTagClickListener() {
                        @Override
                        public void onTagClick(String tag) {
                            boolean selected = tagGroup.isTagSelected(tag);
                            if (selected) {
                                mFeedback.getIssues().remove(tag);
                            } else {
                                mFeedback.getIssues().add(tag);
                            }
                            tagGroup.setTagSelected(tag, !selected);
                        }

                        @Override
                        public void onTagLongClick(String tag) {

                        }
                    });
                }
            }
        }, null);
        RequestManager.addRequest(request, "issues");
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
        RequestManager.cancelAll("issues");
        RequestManager.cancelAll(TAG);
    }
}