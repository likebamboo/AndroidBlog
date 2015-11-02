package com.likebamboo.osa.android.impl;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.likebamboo.osa.android.ui.view.DynamicHeightImageView;

/**
 * 可变高度图片请求监听器
 * <p/>
 * Created by wentaoli on 2015/10/23.
 */
public class DynamicHeightRequestImpl implements RequestListener<String, GlideDrawable> {
    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        if (!(target instanceof GlideDrawableImageViewTarget) || resource == null) {
            return false;
        }
        ImageView view = ((GlideDrawableImageViewTarget) target).getView();
        if (view instanceof DynamicHeightImageView) {
            ((DynamicHeightImageView) view).setHeightRatio(resource.getMinimumHeight() * 1.0 / resource.getMinimumWidth());
        }
        return false;
    }
}
