package com.likebamboo.osa.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.AuthorList;
import com.likebamboo.osa.android.impl.ImageLoaderListener;
import com.likebamboo.osa.android.interfaces.IOnItemClickListener;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.view.CircleImageView;
import com.likebamboo.osa.android.ui.view.fa.TextAwesome;
import com.likebamboo.osa.android.ui.view.fastscroll.BubbleTextGetter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wentaoli on 2015/5/14.
 */
public class AuthorAdapter extends BaseRecycleAdapter<AuthorList.Author> implements BubbleTextGetter {
    private IOnItemClickListener mItemClickListener = null;
    private IOnAuthorInfoClickListener mInfoClickListener = null;

    /**
     * 设置回调
     *
     * @param l
     */
    public void setOnInfoClickListner(IOnAuthorInfoClickListener l) {
        this.mInfoClickListener = l;
    }

    /**
     * 设置回调
     *
     * @param l
     */
    public void setOnItemClickListener(IOnItemClickListener l) {
        this.mItemClickListener = l;
    }

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

        AuthorList.Author item = getItem(position);
        if (item == null) {
            return;
        }

        final ItemViewHolder holder = (ItemViewHolder) viewHolder;

        holder.avatarIv.setImageResource(R.drawable.default_avatar);
        if (!TextUtils.isEmpty(item.getAvatar())) {
            String url = RequestUrl.BASE_URL + item.getAvatar();
            // 加载图片
            ImageLoader imageLoader = RequestManager.getImageLoader();
            imageLoader.get(url, new ImageLoaderListener(holder.avatarIv, url, R.drawable.default_avatar));
        }
        // 作者名称
        holder.nameTv.setText(item.getName());

        // 博客
        holder.blogTv.setText(R.string.fa_link, item.getBlog());
        holder.blogTv.setTag(position);
        holder.blogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInfoClickListener == null) {
                    return;
                }
                Object obj = view.getTag();
                try {
                    int pos = Integer.parseInt(obj + "");
                    AuthorList.Author item = getItem(pos);
                    if (item == null) {
                        return;
                    }
                    mInfoClickListener.onAuthorBlogLinkClick(pos, item.getBlog());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // item点击事件
        holder.rootView.setTag(position);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener == null) {
                    return;
                }
                Object obj = view.getTag();
                try {
                    int pos = Integer.parseInt(obj + "");
                    mItemClickListener.onItemClick(pos, getItem(pos));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.infoTv.setTag(position);
        holder.infoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInfoClickListener == null) {
                    return;
                }
                Object obj = view.getTag();
                try {
                    int pos = Integer.parseInt(obj + "");
                    mInfoClickListener.onAuthorInfoClick(pos, getItem(pos));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        AuthorList.Author item = getItem(pos);
        if (item != null) {
            return item.getIndex();
        }
        return "#";
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.author_item_layout)
        public View rootView;
        @InjectView(R.id.author_blog_tv)
        public TextAwesome blogTv;
        @InjectView(R.id.author_name_tv)
        public TextView nameTv;
        @InjectView(R.id.author_info_tv)
        public TextView infoTv;
        @InjectView(R.id.author_avatar_iv)
        public CircleImageView avatarIv;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public interface IOnAuthorInfoClickListener {
        /**
         * 点击作者信息
         *
         * @param position
         * @param author
         */
        void onAuthorInfoClick(int position, AuthorList.Author author);

        /**
         * 作者博客链接点击事件
         *
         * @param position
         * @param blogLink
         */
        void onAuthorBlogLinkClick(int position, String blogLink);
    }
}
