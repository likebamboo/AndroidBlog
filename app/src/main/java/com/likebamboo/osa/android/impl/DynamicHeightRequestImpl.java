package com.likebamboo.osa.android.impl;

import android.graphics.Bitmap;
import android.view.View;

import com.likebamboo.osa.android.ui.view.DynamicHeightImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 可变高度图片请求监听器
 * <p/>
 * Created by wentaoli on 2015/10/23.
 */
public class DynamicHeightRequestImpl implements ImageLoadingListener {

    private DynamicHeightImageView imageView = null;

    public DynamicHeightRequestImpl(DynamicHeightImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (imageView != null) {
            imageView.setHeightRatio(loadedImage.getHeight() * 1.0 / loadedImage.getWidth());
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }
}
