package com.likebamboo.osa.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Author;
import com.likebamboo.osa.android.request.JsonArrayRequest;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestParams;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.AuthorDetailActivity;
import com.likebamboo.osa.android.ui.adapter.AuthorAdapter;
import com.likebamboo.osa.android.ui.adapter.BaseRecycleAdapter;
import com.likebamboo.osa.android.ui.view.fastscroll.FastScroller;

/**
 * 作者列表Fragment
 */
public class AuthorFragment extends EndlessListFragment<Author> {

    @Override
    protected BaseRecycleAdapter initAdapter() {
        return new AuthorAdapter(getActivity());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_author;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // fastScroller
        FastScroller fastScroller = (FastScroller) view.findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(mRecyclerView);
        return view;
    }

    /**
     * 加载数据
     */
    @Override
    protected void loadDatas(RequestParams params) {
        // 加载数据
        RequestManager.addRequest(new JsonArrayRequest(RequestUrl.AUTHOR_URL, params, responseListener(), errorListener()),
                getClass().getName());
    }

    @Override
    public void onItemClick(int position, Author item) {
        if (item == null) {
            return;
        }

        Intent i = new Intent(getActivity(), AuthorDetailActivity.class);
        i.putExtra(AuthorDetailActivity.EXTRA_AUTHOR_INFO, item);
        startActivity(i);
    }
}
