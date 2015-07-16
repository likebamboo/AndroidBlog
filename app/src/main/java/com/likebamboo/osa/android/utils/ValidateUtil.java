package com.likebamboo.osa.android.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 格式验证工具类
 * Created by likebamboo on 2015/7/16.
 */
public class ValidateUtil {

    /**
     * 验证手机格式
     *
     * @param num
     * @return
     */
    public static boolean isPhoneNum(String num) {
        if (TextUtils.isEmpty(num) || num.length() < 11) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(13[0-9]|15[0-9]|14[7|5]|17[0-9]|18[0-9])\\d{8}$");
        // 匹配手机号码
        Matcher matcher = pattern.matcher(num);
        return matcher.matches();
    }

    /**
     * 验证邮箱格式
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }
}
