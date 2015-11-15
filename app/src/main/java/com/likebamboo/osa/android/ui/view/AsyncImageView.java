package com.likebamboo.osa.android.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

/**
 * 图片加载控件
 */
public class AsyncImageView extends ImageView {
    /**
     *
     */
    private DisplayImageOptions options = null;

    public AsyncImageView(Context context) {
        super(context);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageUrl(String url, int defaultImage, ImageLoadingListener listener) {
        if (defaultImage != -1) {
            setImageResource(defaultImage);
        } else {
            setImageBitmap(null);
        }
        if (!TextUtils.isEmpty(url)) {
            try {
                if (options == null) {
                    options = initOptions(defaultImage);
                }
                ImageLoader.getInstance().displayImage(url, this, options, listener);
            } catch (Throwable e) {
                Log.e("AsyncImageView=> ", e + "");
            }
        }
    }

    /**
     * 初始化显示参数
     *
     * @param defaultImage
     */
    public static DisplayImageOptions initOptions(int defaultImage) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565);
        if (defaultImage > 0) {
            builder.showImageOnLoading(defaultImage).showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage);
        }
        return builder.build();
    }

    public void setImageUrl(String url, int defaultImage) {
        setImageUrl(url, defaultImage, null);
    }

    /**
     * 清除缓存
     *
     * @param url
     * @see [类、类#方法、类#成员]
     */
    public static void removeCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.toLowerCase().startsWith("/")) {
            url = "file://" + url;
        }
        MemoryCacheUtils.removeFromCache(url, ImageLoader.getInstance().getMemoryCache());
        DiskCacheUtils.removeFromCache(url, ImageLoader.getInstance().getDiskCache());
    }

}
