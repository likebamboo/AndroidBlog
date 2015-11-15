package com.likebamboo.osa.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Blog;
import com.likebamboo.osa.android.impl.DynamicHeightRequestImpl;
import com.likebamboo.osa.android.ui.view.AsyncImageView;
import com.likebamboo.osa.android.ui.view.DynamicHeightImageView;
import com.likebamboo.osa.android.ui.view.fa.TextAwesome;
import com.likebamboo.osa.android.utils.DateUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wentaoli on 2015/5/12.
 */
public class BlogAdapter extends BaseRecycleAdapter<Blog> {


    public BlogAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup viewGroup, int viewType) {
        // 创建一个View
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blog, viewGroup, false);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder == null || !(viewHolder instanceof ViewHolder)) {
            return;
        }

        final Blog item = getItem(position);
        if (item == null) {
            return;
        }

        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.titleTv.setText(item.getTitle());
        // 如果没有封面图
        if (TextUtils.isEmpty(item.getCover())) {
            holder.coverIv.setVisibility(View.GONE);
            holder.abstractsTv.setVisibility(View.VISIBLE);
            holder.abstractsTv.setText(Html.fromHtml(item.getAbstracts().replace("\n", "<br/>")));
        } else {
            holder.coverIv.setVisibility(View.VISIBLE);
            holder.abstractsTv.setVisibility(View.GONE);
            // 加载图片
            ImageLoader.getInstance().displayImage(item.getCover(), holder.coverIv,
                    AsyncImageView.initOptions(R.color.grey_300),
                    new DynamicHeightRequestImpl(holder.coverIv));
        }

        holder.tagTv.setText(R.string.fa_filter, item.getCategorys());
        String time = TextUtils.isEmpty(item.getAddTime()) ? item.getPostTime() : item.getAddTime();
        holder.timeTv.setText(R.string.fa_calendar, DateUtil.parseDate(time));

        holder.cardView.setTag(position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener == null) {
                    return;
                }
                try {
                    int pos = Integer.parseInt(view.getTag() + "");
                    mItemClickListener.onItemClick(pos, getItem(pos));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_card_view)
        public View cardView;
        @InjectView(R.id.blog_cover_iv)
        public DynamicHeightImageView coverIv;
        @InjectView(R.id.blog_title_tv)
        public TextView titleTv;
        @InjectView(R.id.blog_abstracts_tv)
        public TextView abstractsTv;
        @InjectView(R.id.blog_time_tv)
        public TextAwesome timeTv;
        @InjectView(R.id.blog_tag_tv)
        public TextAwesome tagTv;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
