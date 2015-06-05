package com.likebamboo.osa.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by wentaoli on 2015/5/12.
 */
public class BlogList extends BaseRsp {

    @JsonProperty("result")
    private ArrayList<Blog> mList = null;

    public ArrayList<Blog> getList() {
        return mList;
    }

    public void setList(ArrayList<Blog> list) {
        this.mList = list;
    }

    /**
     * 博客基本信息
     *
     * @author likebamboo
     */
    // 忽略未知属性
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Blog implements Parcelable {

        /**
         * id
         */
        private long id = 0L;

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

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
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

        @Override
        public String toString() {
            return "Blog {" +
                    "abstracts='" + abstracts + '\'' +
                    ", id=" + id +
                    ", title='" + title + '\'' +
                    ", categorys='" + categorys + '\'' +
                    ", authorName='" + author + '\'' +
                    ", url='" + url + '\'' +
                    ", postTime='" + postTime + '\'' +
                    ", isTrans=" + isTrans +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
            dest.writeString(this.title);
            dest.writeString(this.abstracts);
            dest.writeString(this.categorys);
            dest.writeString(this.author);
            dest.writeString(this.url);
            dest.writeString(this.postTime);
            dest.writeByte(isTrans ? (byte) 1 : (byte) 0);
            dest.writeString(oAuthor);
            dest.writeString(oPostTime);
            dest.writeString(fromUrl);
            dest.writeString(oFromUrl);
            dest.writeString(addTime);
        }

        public Blog() {
        }

        private Blog(Parcel in) {
            this.id = in.readLong();
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
        }

        public static final Parcelable.Creator<Blog> CREATOR = new Parcelable.Creator<Blog>() {
            public Blog createFromParcel(Parcel source) {
                return new Blog(source);
            }

            public Blog[] newArray(int size) {
                return new Blog[size];
            }
        };
    }

}
