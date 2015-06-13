package com.likebamboo.osa.android.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by likebamboo on 2015/5/19.
 */
public class TagList extends BaseRsp {

    @JsonProperty("result")
    private ArrayList<Tag> mList = null;

    public ArrayList<Tag> getList() {
        return mList;
    }

    public void setList(ArrayList<Tag> list) {
        this.mList = list;
    }

    /**
     * 标签
     *
     * @author likebamboo
     */
    // 忽略未知属性
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tag extends SugarRecord<Tag> implements Parcelable {

        /**
         * id
         */
        @JsonProperty("id")
        private long _id = 0L;

        /**
         * 标题
         */
        private String name = "";

        /**
         * 摘要
         */
        private String desc = "";

        /**
         * 添加时间
         */
        @JsonIgnoreProperties
        private long addTime = 0;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getTagId() {
            return _id;
        }

        public void setTagId(long id) {
            this._id = id;
        }

        public long getAddTime() {
            return addTime;
        }

        public void setAddTime(long addTime) {
            this.addTime = addTime;
        }

        /**
         * 删除标签
         *
         * @param name
         */
        public static void delete(String name) {
            if (TextUtils.isEmpty(name)) {
                return;
            }
            deleteAll(Tag.class, " name like ? ", name);
        }

        /**
         * 查找标签
         *
         * @param name
         */
        public static Tag findTagByName(String name) {
            if (TextUtils.isEmpty(name)) {
                return null;
            }
            List<Tag> tags = find(Tag.class, " name like ? ", name);
            if (tags == null || tags.isEmpty()) {
                return null;
            }
            for (Tag t : tags) {
                if (t != null && name.equals(t.getName())) {
                    return t;
                }
            }
            return null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
            dest.writeString(this.name);
            dest.writeString(this.desc);
            dest.writeLong(this.addTime);
        }

        public Tag() {
        }

        private Tag(Parcel in) {
            this.id = in.readLong();
            this.name = in.readString();
            this.desc = in.readString();
            this.addTime = in.readLong();
        }

        public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
            public Tag createFromParcel(Parcel source) {
                return new Tag(source);
            }

            public Tag[] newArray(int size) {
                return new Tag[size];
            }
        };
    }

}
