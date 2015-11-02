package com.likebamboo.osa.android.request;

/**
 * Created by wentaoli on 2015/5/12.
 */
public class RequestUrl {

    public static final boolean isTest = false;

    /**
     * BaseUrl
     */
    public static final String BASE_URL = isTest ? "http://192.168.45.38:8080/osa-mobile/" : "http://120.24.93.248/";

    /**
     * BLOG,博客
     */
    public static final String BLOG_URL = BASE_URL + "blog";

    /**
     * BLOG搜索,博客搜索
     */
    public static final String BLOG_SEARCH_URL = BLOG_URL + "/s";

    /**
     * category,分类
     */
    public static final String CATEGORY_URL = BASE_URL + "category";

    /**
     * autor,作者
     */
    public static final String AUTHOR_URL = BASE_URL + "author";

    /**
     * 分类 BLOG, 参数： 类别id
     */
    public static final String CATEGORY_BLOG_URL = BLOG_URL + "/category/%s";

    /**
     * 作者的 BLOG, 参数：作者名称 （why名称，不是id？ 具体看代码）
     */
    public static final String AUTHOR_BLOG_URL = BLOG_URL + "/author/%s/";

    /**
     * BLOG detail展示URL
     */
    public static final String BLOG_VIEW_URL = BLOG_URL + "/%s";

    /**
     * BLOG detail info
     */
    public static final String BLOG_INFO_URL = BLOG_URL + "/info/%s";

    /**
     * 标签 BLOG, 参数： 标签名称
     */
    public static final String TAG_BLOG_URL = BLOG_URL + "/tag/%s/";

    /**
     * issues
     */
    public static final String ISSUES_LIST_URL = BASE_URL + "issue/";

    /**
     * feedback.save
     */
    public static final String FEEDBACK_SAVE_URL = BASE_URL + "feedback/save?random=%s";

    /**
     * 关于我
     */
    public static final String ABOUT_ME_URL = "http://likebamboo.com/about.html";

    /**
     * issues
     */
    public static final String ISSUES_URL = "https://github.com/likebamboo/AndroidBlog/issues";

    /**
     * 关于app
     */
    public static final String ABOUT_APP_URL = "https://github.com/likebamboo/AndroidBlog";

    /**
     * site,其他站点
     */
    public static final String SITE_URL = BASE_URL + "site";

    /**
     * 其他站点博客列表
     */
    public static final String SITE_BLOG_LIST_URL = BASE_URL + "spider/%s/article";

}
