package com.likebamboo.osa.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 类别
 * Created by likebamboo 2015/05/14
 *
 * @author likebamboo
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category implements Parcelable {
    /**
     * 类别id
     */
    private int id = 0;

    /**
     * 类别名称
     */
    private String name = "";

    /**
     * 类别描述
     */
    private String description = "";

    /**
     * 添加时间
     */
    @JsonProperty("add_time")
    private String addTime = "";

    /**
     * 封面图
     */
    private String cover = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "Category [name=" + name + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.addTime);
        dest.writeString(this.cover);
    }

    public Category() {
    }

    private Category(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.addTime = in.readString();
        this.cover = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

}
