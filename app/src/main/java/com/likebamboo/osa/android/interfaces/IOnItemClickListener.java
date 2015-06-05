package com.likebamboo.osa.android.interfaces;

/**
 * Created by wentaoli on 2015/5/14.
 */
public interface IOnItemClickListener<T> {

    /**
     * listView 点击事件回调
     *
     * @param postion
     * @param item
     */
    void onItemClick(int postion, T item);
}
