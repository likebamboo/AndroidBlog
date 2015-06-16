package com.likebamboo.osa.android.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * ListView 适配器基类
 *
 * @author wentaoli
 * @version [版本号, 2015年5月12日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    /**
     * 上下文对象
     */
    protected Context mContext = null;

    /**
     * 数据源
     */
    protected ArrayList<T> mDatas = new ArrayList<T>();

    public BaseAdapter(final Context ctx) {
        super();
        this.mContext = ctx;
    }

    public BaseAdapter(final Context ctx, final ArrayList<T> datas) {
        super();
        this.mContext = ctx;
        this.mDatas = datas;
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public ArrayList<T> getDatas() {
        return mDatas;
    }

    /**
     * 添加数据
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
     * 添加数据
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
     * 添加数据
     *
     * @param datas
     */
    public void addData(T datas) {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
        mDatas.add(datas);
    }

    /**
     * 添加数据
     *
     * @param datas
     * @param index
     */
    public void addData(T datas, int index) {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
        mDatas.add(index, datas);
    }

    /**
     * 删除数据
     *
     * @param datas
     */
    public void removeDatas(final ArrayList<T> datas) {
        if (mDatas == null) {
            return;
        }
        mDatas.removeAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 删除数据
     *
     * @param index
     */
    public void removeData(int index) {
        if (mDatas == null || index >= mDatas.size()) {
            return;
        }
        mDatas.remove(index);
    }

    /**
     * 删除数据
     *
     * @param data
     */
    public void removeData(T data) {
        if (mDatas == null || data == null) {
            return;
        }
        mDatas.remove(data);
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

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        int count = mDatas.size();
        return count;
    }

    @Override
    public T getItem(int arg0) {
        if (mDatas == null) {
            return null;
        }
        if (arg0 < 0 || arg0 >= mDatas.size()) {
            return null;
        }
        return mDatas.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }


    @Override
    public abstract View getView(int position, View v, ViewGroup parent);

}
