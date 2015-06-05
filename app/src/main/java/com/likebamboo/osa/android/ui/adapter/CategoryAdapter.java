package com.likebamboo.osa.android.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.CategoryList;
import com.likebamboo.osa.android.interfaces.IOnItemClickListener;
import com.likebamboo.osa.android.request.RequestManager;
import com.likebamboo.osa.android.request.RequestUrl;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wentaoli on 2015/5/12.
 */
public class CategoryAdapter extends BaseAdapter<CategoryList.Category> {

    private IOnItemClickListener mItemClickListener = null;

    /**
     * 设置回调
     *
     * @param l
     */
    public void setOnItemClickListener(IOnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public CategoryAdapter(Context ctx) {
        super(ctx);
    }

    public CategoryAdapter(Context ctx, ArrayList<CategoryList.Category> datas) {
        super(ctx, datas);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_category, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        CategoryList.Category item = getItem(position);
        if (item == null) {
            return view;
        }

        // 图片
        holder.coverIv.setImageResource(R.drawable.ic_launcher);
        // 加载图片
        ImageLoader imageLoader = RequestManager.getImageLoader();
        holder.coverIv.setHeightRatio(0.8);
        if (!TextUtils.isEmpty(item.getCover())) {
            String url = RequestUrl.BASE_URL + item.getCover();
            holder.coverIv.setTag(url);
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (imageContainer == null || imageContainer.getBitmap() == null) {
                        onErrorResponse(null);
                        return;
                    }
                    if (("" + holder.coverIv.getTag()).equals(imageContainer.getRequestUrl())) {
                        holder.coverIv.setHeightRatio(imageContainer.getBitmap().getHeight() / (double) imageContainer.getBitmap().getWidth());
                        holder.coverIv.setImageBitmap(imageContainer.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    holder.coverIv.setImageResource(R.drawable.ic_launcher);
                }
            });
        }

        view.setId(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = view.getId();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(pos, getItem(pos));
                }
            }
        });
        // set data
        holder.titleTv.setText(item.getName());
        holder.descTv.setText(item.getDescription());

        return view;
    }

    public static class ViewHolder {
        @InjectView(R.id.category_title_tv)
        public TextView titleTv;
        @InjectView(R.id.category_desc_tv)
        public TextView descTv;
        @InjectView(R.id.category_cover_iv)
        public DynamicHeightImageView coverIv;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
