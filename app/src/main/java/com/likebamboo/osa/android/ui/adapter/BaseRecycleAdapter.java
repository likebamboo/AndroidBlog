package com.likebamboo.osa.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.interfaces.IOnItemClickListener;

import java.util.ArrayList;

/**
 * Recycle Adapter 基类
 * <p/>
 * Created by wentaoli on 2015/5/14.
 */
public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 头部
     */
    protected static final int TYPE_HEADER = 0x1001;
    /**
     * item
     */
    protected static final int TYPE_ITEM = 0x1002;
    /**
     * footer
     */
    protected static final int TYPE_FOOTRE = 0x1003;

    /**
     * 上下文对象
     */
    protected Context mContext = null;

    /**
     * 数据源
     */
    protected ArrayList<T> mDatas = new ArrayList<T>();

    /**
     * header
     */
    private View mHeaderView = null;

    /**
     * footer
     */
    private View mFooterView = null;


    private final TypedValue mTypedValue = new TypedValue();

    /**
     * item 默认背景
     */
    protected int mDefaultBackgroudId = 0;

    public BaseRecycleAdapter(Context mContext) {
        this.mContext = mContext;
        try {
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mDefaultBackgroudId = mTypedValue.resourceId;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected IOnItemClickListener<T> mItemClickListener = null;

    /**
     * 设置回调
     *
     * @param l
     */
    public void setOnItemClickListener(IOnItemClickListener<T> l) {
        this.mItemClickListener = l;
    }

    @Override
    public final int getItemCount() {
        return getItemViewCount() + (mHeaderView == null ? 0 : 1) + (mFooterView == null ? 0 : 1);
    }

    /**
     * 获取中间View的count
     *
     * @return
     */
    public int getItemViewCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    /**
     * 获取position所对应的item的类型
     *
     * @param position
     * @return
     */
    @Override
    public final int getItemViewType(int position) {
        int headerCount = mHeaderView == null ? 0 : 1;
        if (position < headerCount) {
            return TYPE_HEADER;
        }
        if (isItemView(position)) {
            return TYPE_ITEM;
        }
        return TYPE_FOOTRE;
    }

    /**
     * 判断position是否是中间的数据区域的View
     *
     * @param position
     * @return
     */
    public boolean isItemView(int position) {
        if (mDatas == null) {
            return false;
        }
        int headerCount = mHeaderView == null ? 0 : 1;
        if (position < (headerCount + mDatas.size())) {
            return true;
        }
        return false;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // header
        if (viewType == TYPE_HEADER) {
            // footer
            viewGroup.addView(mHeaderView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            return new VHHeader(mHeaderView);
        }

        // item
        if (viewType == TYPE_ITEM) {
            return onCreateViewItemHolder(viewGroup, viewType);
        }
        // footer
        viewGroup.addView(mFooterView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return new VHFooter(mFooterView);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder == null) {
            return;
        }
        if (viewHolder instanceof VHHeader) {
            // do nothing
            return;
        }
        if (viewHolder instanceof VHFooter) {
            // do nothing
            return;
        }
        onBindItemViewHolder(viewHolder, position);
    }

    /**
     * 获取数据
     *
     * @param postion
     * @return
     */
    public T getItem(int postion) {
        if (postion < 0 || postion > getItemCount() || mDatas == null) {
            return null;
        }
        int headerCount = mHeaderView == null ? 0 : 1;
        if (postion < headerCount) {
            return null;
        }
        postion -= headerCount;
        if (postion >= mDatas.size()) {
            return null;
        }
        return mDatas.get(postion);
    }

    /**
     * 添加数据并刷新adapter
     *
     * @param datas
     */
    public void addDatas(final ArrayList<T> datas) {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 添加数据并刷新adapter
     *
     * @param datas
     * @param index
     */
    public void addDatas(final ArrayList<T> datas, int index) {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
        mDatas.addAll(index, datas);
        notifyDataSetChanged();
    }

    /**
     * 获取数据
     *
     * @return
     */
    public ArrayList<T> getDatas() {
        return mDatas;
    }

    /**
     * 清空数据
     */
    public void clear() {
        if (mDatas != null) {
            mDatas.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 添加header
     *
     * @param header
     */
    public void addHeader(View header) {
        mHeaderView = header;
    }

    /**
     * 添加footer
     *
     * @param footer
     */
    public void setFooter(View footer) {
        mFooterView = footer;
    }

    public static class VHHeader extends RecyclerView.ViewHolder {
        public VHHeader(View itemView) {
            super(itemView);
        }
    }

    public static class VHFooter extends RecyclerView.ViewHolder {
        public VHFooter(View itemView) {
            super(itemView);
        }
    }

    /**
     * 创建itemViewHolder
     *
     * @param viewGroup
     * @param i
     * @return
     */
    public abstract RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup viewGroup, int i);

    /**
     * 将ViewHolder绑定到具体的View上
     *
     * @param viewHolder
     * @param i
     * @return
     */
    public abstract void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int i);
}
