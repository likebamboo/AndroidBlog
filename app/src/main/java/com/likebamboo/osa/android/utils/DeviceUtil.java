package com.likebamboo.osa.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by likebamboo on 2015/5/30.
 */
public class DeviceUtil {

    /**
     * 获取软件版本名称
     *
     * @param ctx
     * @return
     */
    public static String getVersionName(Context ctx) {
        if (ctx == null) {
            return "";
        }

        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取软件版本号
     *
     * @param ctx
     * @return
     */
    public static int getVersionCode(Context ctx) {
        if (ctx == null) {
            return 0;
        }

        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int dip2px(Context context, double dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    public static int px2dip(Context context, double pxValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / m + 0.5f);
    }

}
