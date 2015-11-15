package com.likebamboo.osa.android;

import android.content.Context;

import com.likebamboo.osa.android.request.RequestManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orm.SugarApp;

import java.io.File;

/**
 * Created by likebamboo on 2015/5/10.
 */
public class OsaApplication extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        // 初始化volley请求管理
        RequestManager.init(this);
        // 初始化Imageloader
        initImageLoader(this);
    }


    /**
     * 初始化Imageloader
     *
     * @param context
     */
    private void initImageLoader(Context context) {
        try {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    .diskCache(new UnlimitedDiskCache(new File(getImageDir())))
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .build();
            ImageLoader.getInstance().init(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片缓存目录
     */
    private final String getImageDir() {
        File cache = getExternalCacheDir();
        if (cache == null) {
            cache = getCacheDir();
        }
        if (cache == null) {
            return "/data/data" + File.separator + getPackageName() + File.separator + "cache" + File.separator;
        }
        return cache.getAbsolutePath();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
