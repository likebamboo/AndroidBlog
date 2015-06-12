package com.likebamboo.osa.android.interfaces;

/**
 * Created by wentaoli on 2015/5/14.
 */
public interface IOnItemClickListener<T> {

    /**
     * listView 点击事件回调
     *
     * @param position
     * @param item
     */
    void onItemClick(int position, T item);

    /**
     * listView 长按事件回调
     *
     * @param position
     * @param item
     */
    void onItemLongClick(int position, T item);
}
