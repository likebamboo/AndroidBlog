package com.likebamboo.osa.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.Category;
import com.likebamboo.osa.android.impl.DynamicHeightRequestImpl;
import com.likebamboo.osa.android.request.RequestUrl;
import com.likebamboo.osa.android.ui.view.AsyncImageView;
import com.likebamboo.osa.android.ui.view.DynamicHeightImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wentaoli on 2015/5/12.
 */
public class CategoryAdapter extends BaseRecycleAdapter<Category> {

    public CategoryAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup viewGroup, int viewType) {
        // 创建一个View
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category, viewGroup, false);
        // 创建一个ViewHolder
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder == null || !(viewHolder instanceof ViewHolder)) {
            return;
        }

        final Category item = getItem(position);
        if (item == null) {
            return;
        }

        final ViewHolder holder = (ViewHolder) viewHolder;

        // 加载图片
        ImageLoader.getInstance().displayImage(RequestUrl.BASE_URL + item.getCover(), holder.coverIv,
                AsyncImageView.initOptions(R.color.grey_300),
                new DynamicHeightRequestImpl(holder.coverIv));

        // 点击事件
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
        // set data
        holder.titleTv.setText(item.getName());
        holder.descTv.setText(item.getDescription());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_card_view)
        public View cardView;
        @InjectView(R.id.category_title_tv)
        public TextView titleTv;
        @InjectView(R.id.category_desc_tv)
        public TextView descTv;
        @InjectView(R.id.category_cover_iv)
        public DynamicHeightImageView coverIv;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
