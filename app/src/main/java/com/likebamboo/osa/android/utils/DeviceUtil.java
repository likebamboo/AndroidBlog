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
}
