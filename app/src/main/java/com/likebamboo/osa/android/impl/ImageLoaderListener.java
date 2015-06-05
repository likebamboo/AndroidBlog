package com.likebamboo.osa.android.impl;

import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * volley 图片下载回调
 * Created by wentaoli on 2015/6/4.
 */
public class ImageLoaderListener implements ImageLoader.ImageListener {

    private ImageView imageView = null;

    private int defaultResId = 0;

    public ImageLoaderListener(final ImageView imageView, final String imageUrl, final int defaultResId) {
        this.defaultResId = defaultResId;
        this.imageView = imageView;
        if (imageView != null) {
            imageView.setTag(imageUrl);
            imageView.setImageResource(defaultResId);
        }
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
        if (imageContainer == null || imageContainer.getBitmap() == null) {
            return;
        }
        if (("" + imageView.getTag()).equals(imageContainer.getRequestUrl())) {
            imageView.setImageBitmap(imageContainer.getBitmap());
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        imageView.setImageResource(defaultResId);
    }

}
