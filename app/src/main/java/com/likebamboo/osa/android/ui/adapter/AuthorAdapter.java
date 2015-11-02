package com.likebamboo.osa.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Author;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.view.fa.TextAwesome;
import com.likebamboo.osa.android.ui.view.fastscroll.BubbleTextGetter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wentaoli on 2015/5/14.
 */
public class AuthorAdapter extends BaseRecycleAdapter<Author> implements BubbleTextGetter {

    public AuthorAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup viewGroup, int viewType) {
        // 创建一个View
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_author, viewGroup, false);
        // 创建一个ViewHolder
        ItemViewHolder holder = new ItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder == null || !(viewHolder instanceof ItemViewHolder)) {
            return;
        }

        Author item = getItem(position);
        if (item == null) {
            return;
        }

        final ItemViewHolder holder = (ItemViewHolder) viewHolder;

        // holder.avatarIv.setImageResource(R.drawable.default_avatar);
        if (!TextUtils.isEmpty(item.getAvatar())) {
            String url = RequestUrl.BASE_URL + item.getAvatar();
            // 加载图片
            Glide.with(mContext).load(url).into(holder.avatarIv);
        } else {
            holder.avatarIv.setImageResource(R.color.grey_300);
        }
        // 作者名称
        holder.nameTv.setText(item.getName());

        // 博客
        holder.linkTv.setText(R.string.fa_link, item.getBlog());
        holder.linkTv.setTag(position);

        // item点击事件
        holder.rootView.setTag(position);
        holder.rootView.setBackgroundResource(mDefaultBackgroudId);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public String getTextToShowInBubble(int pos) {
        Author item = getItem(pos);
        if (item != null) {
            return item.getIndex();
        }
        return "#";
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.author_item_layout)
        public View rootView;
        @InjectView(R.id.author_link_tv)
        public TextAwesome linkTv;
        @InjectView(R.id.author_name_tv)
        public TextView nameTv;
        @InjectView(R.id.author_avatar_iv)
        public ImageView avatarIv;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
