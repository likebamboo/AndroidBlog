
package com.likebamboo.osa.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by wentaoli on 2015/7/16.
 */
public class IssueList extends BaseRsp {

    @JsonProperty("result")
    private ArrayList<Issue> mList = null;

    public ArrayList<Issue> getList() {
        return mList;
    }

    public void setList(ArrayList<Issue> list) {
        this.mList = list;
    }

    /**
     * 问题表
     *
     * @author likebamboo
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Issue implements Parcelable {

        /**
         * 名称
         */
        private String name = "";

        /**
         * 描述
         */
        private String description = "";

        /**
         * 添加时间
         */
        private String addTime = "";

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

        @Override
        public String toString() {
            return "issue [name=" + name + "]";
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
        }

        public Issue() {
        }

        private Issue(Parcel in) {
            this.name = in.readString();
            this.description = in.readString();
            this.addTime = in.readString();
        }

        public static final Parcelable.Creator<Issue> CREATOR = new Parcelable.Creator<Issue>() {
            public Issue createFromParcel(Parcel source) {
                return new Issue(source);
            }

            public Issue[] newArray(int size) {
                return new Issue[size];
            }
        };
    }
}
