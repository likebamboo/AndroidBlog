package com.likebamboo.osa.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * 作者
 * Created by likebamboo  2015/05/14
 *
 * @author likebamboo
 */
public class AuthorList extends BaseRsp {

    @JsonProperty("result")
    private ArrayList<Author> mList = null;

    public ArrayList<Author> getList() {
        return mList;
    }

    public void setList(ArrayList<Author> list) {
        this.mList = list;
    }

    // 忽略未知属性
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author implements Parcelable {
        /**
         * 名称 *
         */
        private String name = "";

        /**
         * 头像 *
         */
        private String avatar = "";

        /**
         * 简介 *
         */
        private String introduction = "";

        /**
         * 博客地址 *
         */
        private String blog = "";

        /**
         * github 地址 *
         */
        private String github = "";

        /**
         * weibo 地址 *
         */
        private String weibo = "";

        /**
         * twitter 地址 *
         */
        private String twitter = "";

        /**
         * facebook 地址 *
         */
        private String facebook = "";

        /**
         * 添加时间
         */
        private String addTime = "";

        /**
         * 索引
         */
        @JsonProperty("idx")
        private String index = "";

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getBlog() {
            return blog;
        }

        public void setBlog(String blog) {
            this.blog = blog;
        }

        public String getGithub() {
            return github;
        }

        public void setGithub(String github) {
            this.github = github;
        }

        public String getWeibo() {
            return weibo;
        }

        public void setWeibo(String weibo) {
            this.weibo = weibo;
        }

        public String getTwitter() {
            return twitter;
        }

        public void setTwitter(String twitter) {
            this.twitter = twitter;
        }

        public String getFacebook() {
            return facebook;
        }

        public void setFacebook(String facebook) {
            this.facebook = facebook;
        }

        @Override
        public String toString() {
            return "AuthorList [name=" + name + ", avatar=" + avatar + ", introduction=" + introduction + ", blog=" + blog
                    + ", github=" + github + ", weibo=" + weibo + ", twitter=" + twitter + ", facebook=" + facebook + "]";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.avatar);
            dest.writeString(this.introduction);
            dest.writeString(this.blog);
            dest.writeString(this.github);
            dest.writeString(this.weibo);
            dest.writeString(this.twitter);
            dest.writeString(this.facebook);
            dest.writeString(this.addTime);
            dest.writeString(this.index);
        }

        public Author() {
        }

        private Author(Parcel in) {
            this.name = in.readString();
            this.avatar = in.readString();
            this.introduction = in.readString();
            this.blog = in.readString();
            this.github = in.readString();
            this.weibo = in.readString();
            this.twitter = in.readString();
            this.facebook = in.readString();
            this.addTime = in.readString();
            this.index = in.readString();
        }

        public static final Parcelable.Creator<Author> CREATOR = new Parcelable.Creator<Author>() {
            public Author createFromParcel(Parcel source) {
                return new Author(source);
            }

            public Author[] newArray(int size) {
                return new Author[size];
            }
        };
    }
}
