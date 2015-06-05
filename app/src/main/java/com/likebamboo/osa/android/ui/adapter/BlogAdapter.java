package com.likebamboo.osa.android.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.BlogList;
import com.likebamboo.osa.android.entity.BlogList.Blog;
import com.likebamboo.osa.android.interfaces.IOnItemClickListener;
import com.likebamboo.osa.android.ui.view.fa.TextAwesome;
import com.likebamboo.osa.android.utils.DateUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wentaoli on 2015/5/12.
 */
public class BlogAdapter extends BaseAdapter<Blog> {

    private IOnItemClickListener mItemClickListener = null;

    /**
     * 设置回调
     *
     * @param l
     */
    public void setOnItemClickListener(IOnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public BlogAdapter(Context ctx) {
        super(ctx);
    }

    public BlogAdapter(Context ctx, ArrayList<BlogList.Blog> datas) {
        super(ctx, datas);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_blog, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        Blog item = getItem(position);
        if (item == null) {
            return view;
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
        /**
         * PS:为什么在Adapter中设置View的OnClick事件，而不是直接使用
         * @see android.widget.AdapterView#setOnItemClickListener(AdapterView.OnItemClickListener)
         * 的方式来实现?
         * 因为用 setOnItemClickListener 的方式 设置 ListView 的item 的background没有效果，不知道什么鬼，
         * 各位大神如果有解决方案，望告知~
         */

        holder.titleTv.setText(item.getTitle());
        holder.abstractsTv.setText(Html.fromHtml(item.getAbstracts().replace("\n", "<br/>")));
        holder.tagTv.setText(R.string.fa_filter, item.getCategorys());
        holder.timeTv.setText(R.string.fa_calendar, DateUtil.parseDate(item.getAddTime()));
        return view;
    }

    public static class ViewHolder {
        @InjectView(R.id.blog_title_tv)
        public TextView titleTv;
        @InjectView(R.id.blog_author_tv)
        public TextView authorTv;
        @InjectView(R.id.blog_abstracts_tv)
        public TextView abstractsTv;
        @InjectView(R.id.blog_time_tv)
        public TextAwesome timeTv;
        @InjectView(R.id.blog_tag_tv)
        public TextAwesome tagTv;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
