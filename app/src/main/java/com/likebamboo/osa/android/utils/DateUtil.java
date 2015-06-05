package com.likebamboo.osa.android.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class DateUtil {

    /**
     * 格式化服务器端给的时间字符串
     *
     * @param timeStr
     * @return
     */
    public static String parseDate(String timeStr) {
        return parseDate(timeStr, "yyyy-MM-dd");
    }

    /**
     * 将时间字符串转为本地显示的时间形式
     *
     * @param timeStr
     * @param pattern
     * @return
     */
    public static String parseDate(String timeStr, String pattern) {
        if (TextUtils.isEmpty(timeStr) || TextUtils.isEmpty(pattern)) {
            return "";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            // 将字符串转为日期
            Date d = sdf.parse(timeStr);
            return parseDate(d.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeStr;
    }

    /**
     * 将时间戳转为本地显示的时间形式
     *
     * @param timeStamp
     * @return
     */
    public static String parseDate(long timeStamp) {
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = null;
        try {
            // 将字符串转为日期
            Date d = new Date(timeStamp);
            Date dd = new Date();
            // 如果是同一天
            if ((now - timeStamp < ONE_DAY) && (d.getDate() == dd.getDate())) {
                // sdf = new SimpleDateFormat("HH:mm");
                return "今天";
            } else if (dd.getYear() != d.getYear()) {// 如果是不同年份
                sdf = new SimpleDateFormat("yyyy-MM-dd");
            } else {
                sdf = new SimpleDateFormat("MM-dd");
            }
            return sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final long ONE_DAY = 24 * 60 * 60 * 1000L;
}
