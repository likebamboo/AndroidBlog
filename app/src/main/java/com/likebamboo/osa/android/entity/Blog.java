package com.likebamboo.osa.android.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 博客基本信息
 *
 * @author likebamboo
 */
// 忽略未知属性
@JsonIgnoreProperties(ignoreUnknown = true)
public class Blog extends SugarRecord<Blog> implements Parcelable {

    /**
     * id
     */
    private long _id = 0L;

    /**
     * 标题
     */
    private String title = "";

    /**
     * 摘要
     */
    private String abstracts = "";

    /**
     * 类别
     */
    private String categorys = "";

    /**
     * 作者名称
     */
    private String author = "";

    /**
     * URL
     */
    private String url = "";

    /**
     * 博客发布时间,
     */
    private String postTime = "";

    private boolean isTrans = false;

    /**
     * 博客原作者（针对翻译）
     */
    private String oAuthor = "";

    /**
     * 源博客发布时间
     */
    private String oPostTime = "";

    /**
     * 博客来源URL
     */
    private String fromUrl = "";

    /**
     * 原文来源URL(针对翻译)
     */
    private String oFromUrl = "";

    /**
     * 博客添加时间
     */
    private String addTime = "";

    /**
     * 收藏时间。
     */
    private long favTime = 0L;

    /**
     * 封面图
     */
    private String cover = "";

    /**
     * 浏览量
     */
    private String scans = "";

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategorys() {
        return categorys;
    }

    public void setCategorys(String categorys) {
        this.categorys = categorys;
    }

    public long getBlogId() {
        return _id;
    }

    public void setBlogId(long _id) {
        this._id = _id;
    }

    @Override
    public void setId(Long id) {
        if (id != null) {
            setBlogId(id);
        } else {
            super.setId(id);
        }
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getScans() {
        return scans;
    }

    public void setScans(String scans) {
        this.scans = scans;
    }

    public boolean isTrans() {
        return isTrans;
    }

    public void setIsTrans(boolean isTrans) {
        this.isTrans = isTrans;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getoAuthor() {
        return oAuthor;
    }

    public void setoAuthor(String oAuthor) {
        this.oAuthor = oAuthor;
    }

    public String getoPostTime() {
        return oPostTime;
    }

    public void setoPostTime(String oPostTime) {
        this.oPostTime = oPostTime;
    }

    public String getoFromUrl() {
        return oFromUrl;
    }

    public void setoFromUrl(String oFromUrl) {
        this.oFromUrl = oFromUrl;
    }

    public String getFromUrl() {
        return fromUrl;
    }

    public void setFromUrl(String fromUrl) {
        this.fromUrl = fromUrl;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public long getFavTime() {
        return favTime;
    }

    public void setFavTime(long favTime) {
        this.favTime = favTime;
    }

    /**
     * 通过url查找博客
     *
     * @param url
     */
    public static Blog findBlogByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        List<Blog> blogs = find(Blog.class, " url like ? ", url);
        if (blogs == null || blogs.isEmpty()) {
            return null;
        }
        for (Blog b : blogs) {
            if (b != null && url.equals(b.getUrl())) {
                return b;
            }
        }
        return null;
    }

    /**
     * 删除标签
     *
     * @param url
     */
    public static void delete(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        deleteAll(Blog.class, " url like ? ", url);
    }

    /**
     * 分页加载数据
     *
     * @param pageNo   页数， 从0开始
     * @param pageSize 分页大小
     * @param sort     排序
     */
    public static ArrayList<Blog> listPage(int pageNo, int pageSize, String sort) {
        if (TextUtils.isEmpty(sort)) {
            sort = "id desc";
        }
        String limit = (pageNo) * pageSize + " , " + pageSize;
        Iterator<Blog> its = findAsIterator(Blog.class, null, null, null, sort, limit);
        ArrayList<Blog> result = new ArrayList<>();
        if (its != null) {
            while (its.hasNext()) {
                result.add(its.next());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", abstracts='" + abstracts + '\'' +
                ", categorys='" + categorys + '\'' +
                ", author='" + author + '\'' +
                ", url='" + url + '\'' +
                ", postTime='" + postTime + '\'' +
                ", isTrans=" + isTrans +
                ", oAuthor='" + oAuthor + '\'' +
                ", oPostTime='" + oPostTime + '\'' +
                ", fromUrl='" + fromUrl + '\'' +
                ", oFromUrl='" + oFromUrl + '\'' +
                ", addTime='" + addTime + '\'' +
                ", favTime=" + favTime +
                ", cover='" + cover + '\'' +
                ", scans='" + scans + '\'' +
                '}';
    }


    public Blog() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this._id);
        dest.writeString(this.title);
        dest.writeString(this.abstracts);
        dest.writeString(this.categorys);
        dest.writeString(this.author);
        dest.writeString(this.url);
        dest.writeString(this.postTime);
        dest.writeByte(isTrans ? (byte) 1 : (byte) 0);
        dest.writeString(this.oAuthor);
        dest.writeString(this.oPostTime);
        dest.writeString(this.fromUrl);
        dest.writeString(this.oFromUrl);
        dest.writeString(this.addTime);
        dest.writeLong(this.favTime);
        dest.writeString(this.cover);
        dest.writeString(this.scans);
    }

    private Blog(Parcel in) {
        this._id = in.readLong();
        this.title = in.readString();
        this.abstracts = in.readString();
        this.categorys = in.readString();
        this.author = in.readString();
        this.url = in.readString();
        this.postTime = in.readString();
        this.isTrans = in.readByte() != 0;
        this.oAuthor = in.readString();
        this.oPostTime = in.readString();
        this.fromUrl = in.readString();
        this.oFromUrl = in.readString();
        this.addTime = in.readString();
        this.favTime = in.readLong();
        this.cover = in.readString();
        this.scans = in.readString();
    }

    public static final Creator<Blog> CREATOR = new Creator<Blog>() {
        public Blog createFromParcel(Parcel source) {
            return new Blog(source);
        }

        public Blog[] newArray(int size) {
            return new Blog[size];
        }
    };
}
