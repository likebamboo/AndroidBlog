package com.likebamboo.osa.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by wentaoli on 2015/10/22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Site implements Parcelable {
    /**
     * id : 1
     * name : 泡在网上的日子
     * shortName : jcodecraeer
     * url : http://jcodecraeer.com/
     * addTime : null
     * script :
     */
    private int id;
    private String name;
    @JsonProperty("short_name")
    private String shortName;
    private String url;
    private String addTime;
    private String script;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getUrl() {
        return url;
    }

    public String getAddTime() {
        return addTime;
    }

    public String getScript() {
        return script;
    }

    @Override
    public String toString() {
        return "Site{" +
                "addTime='" + addTime + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", url='" + url + '\'' +
                ", script='" + script + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.shortName);
        dest.writeString(this.url);
        dest.writeString(this.addTime);
        dest.writeString(this.script);
    }

    public Site() {
    }

    private Site(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.shortName = in.readString();
        this.url = in.readString();
        this.addTime = in.readString();
        this.script = in.readString();
    }

    public static final Parcelable.Creator<Site> CREATOR = new Parcelable.Creator<Site>() {
        public Site createFromParcel(Parcel source) {
            return new Site(source);
        }

        public Site[] newArray(int size) {
            return new Site[size];
        }
    };
}
