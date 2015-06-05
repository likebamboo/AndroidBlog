
package com.likebamboo.osa.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author likebamboo
 * @date 2015年5月17日
 */
public class NetworkUtil {

    /**
     * Returns whether the network is available
     *
     * @param context Context
     * @return 网络是否可用
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNetworkAvailable(Context context) {
        return getConnectedNetworkInfo(context) != null;
    }

    /**
     * 获取网络类型
     *
     * @param context Context
     * @return 网络类型
     * @see [类、类#方法、类#成员]
     */
    public static int getNetworkType(Context context) {
        NetworkInfo networkInfo = getConnectedNetworkInfo(context);
        if (networkInfo != null) {
            return networkInfo.getType();
        }

        return -1;
    }

    public static NetworkInfo getConnectedNetworkInfo(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                Log.e("network", "couldn't get connectivity manager");
            } else {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null) {
                    return info;
                }
            }
        } catch (Exception e) {
            Log.e("network", e.toString(), e);
        }
        return null;
    }

    /**
     * 判断网络是不是手机网络，非wifi
     *
     * @param context Context
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public static boolean isMobileNetwork(Context context) {
        return (ConnectivityManager.TYPE_MOBILE == getNetworkType(context));
    }

    /**
     * 判断网络是不是wifi
     *
     * @param context Context
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public static boolean isWifiNetwork(Context context) {
        return (ConnectivityManager.TYPE_WIFI == getNetworkType(context));
    }

}
