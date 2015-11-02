package com.likebamboo.osa.android.utils;

import android.text.TextUtils;
import android.webkit.URLUtil;

import com.likebamboo.osa.android.request.RequestUrl;

/**
 * URL 检测
 * Created by wentaoli on 2015/5/27.
 */
public class UrlDetect {

    /**
     * 是否为正确的url
     *
     * @param url
     * @return
     */
    public static boolean isValidURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return URLUtil.isValidUrl(url);
    }

    /**
     * 是否为本站URL
     *
     * @param url
     */
    public static boolean isOurselvesURL(String url) {
        if (!isValidURL(url)) {
            return false;
        }
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        if (url.startsWith(RequestUrl.BASE_URL)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是博客链接
     *
     * @param url
     * @return
     */
    public static String isBlogUrl(String url) {
        if (!isOurselvesURL(url)) {
            return "";
        }
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        // 查看该链接是否以博客url开头
        if (!url.startsWith(RequestUrl.BLOG_URL)) {
            return "";
        }
        // 去掉链接前面的内容
        url = url.substring((RequestUrl.BLOG_URL + "/").length());
        // 如果处理后的 url 不含"/"，或者只有最后一个字符是"/"，说明是blog链接
        if (!url.contains("/") || url.indexOf("/") == url.length() - 1) {
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            return url;
        }
        return "";
    }


    /**
     * 是否是标签blog列表链接
     *
     * @param url
     * @return
     */
    public static String isTagBlogUrl(String url) {
        if (!isOurselvesURL(url)) {
            return "";
        }
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        // 查看该链接是否以博客url开头
        if (!url.startsWith(RequestUrl.BLOG_URL)) {
            return "";
        }
        // 去掉链接前面的内容
        url = url.substring((RequestUrl.BLOG_URL + "/").length());
        // 如果 url 不是以 "tag/" 开头
        if (!url.toLowerCase().startsWith("tag/")) {
            return "";
        }
        // 截取 "tag/" 后边的内容
        url = url.substring(4);
        // 如果处理后的 url 不含"/"，或者只有最后一个字符是"/"，说明是 tag blog链接
        if (!url.contains("/") || url.indexOf("/") == url.length() - 1) {
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            return url;
        }
        return "";
    }

}
