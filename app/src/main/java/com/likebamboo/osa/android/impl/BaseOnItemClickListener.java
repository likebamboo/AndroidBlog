package com.likebamboo.osa.android.impl;

import com.likebamboo.osa.android.interfaces.IOnItemClickListener;

/**
 * Created by likebamboo on 2015/6/11.
 */
public class BaseOnItemClickListener<T> implements IOnItemClickListener<T> {

    @Override
    public void onItemClick(int postion, T item) {
        // nothing
    }

    @Override
    public void onItemLongClick(int postion, T item) {
        // nothing
    }
}
