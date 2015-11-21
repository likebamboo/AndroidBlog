package com.likebamboo.osa.android.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.SparseArray;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wentaoli on 2015/10/22.
 */
public class Favorite extends SugarRecord<Favorite> implements Parcelable {

    public static SparseArray<String> TYPE_NAME_VALUES = new SparseArray<String>();

    static {
        TYPE_NAME_VALUES.put(Type.BLOG, "文章");
        TYPE_NAME_VALUES.put(Type.AUTHOR, "作者");
        TYPE_NAME_VALUES.put(Type.TAG, "标签");
    }

    public static final class Type {
        public static final int BLOG = 0x10;
        public static final int AUTHOR = 0x11;
        public static final int TAG = 0x12;
    }

    /**
     * 关键字
     */
    private String key;
    /**
     * 值（json字符串）
     */
    private String value;
    /**
     * 添加时间
     */
    private String addTime;
    /**
     * 类型
     */
    private int type;

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getAddTime() {
        return addTime;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Site{" +
                "addTime='" + addTime + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
        dest.writeString(this.addTime);
        dest.writeInt(this.type);
    }

    public Favorite() {
    }

    private Favorite(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
        this.addTime = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<Favorite> CREATOR = new Creator<Favorite>() {
        public Favorite createFromParcel(Parcel source) {
            return new Favorite(source);
        }

        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };


    /**
     * 通过key查找收藏
     *
     * @param key
     */
    public static Favorite findByKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        List<Favorite> favorites = find(Favorite.class, " key like ? ", key);
        if (favorites == null || favorites.isEmpty()) {
            return null;
        }
        for (Favorite f : favorites) {
            if (f != null && key.equals(f.getKey())) {
                return f;
            }
        }
        return null;
    }

    /**
     * 删除favorite
     *
     * @param key
     */
    public static void delete(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        deleteAll(Favorite.class, " key like ? ", key);
    }

    /**
     * 分页加载数据
     *
     * @param type     类型
     * @param pageNo   页数， 从0开始
     * @param pageSize 分页大小
     * @param sort     排序
     */
    public static ArrayList<Favorite> listPage(int type, int pageNo, int pageSize, String sort) {
        if (TextUtils.isEmpty(sort)) {
            sort = "id desc";
        }
        pageNo = pageNo > 0 ? pageNo : 1;
        String limit = (pageNo - 1) * pageSize + " , " + pageSize;
        Iterator<Favorite> its = findAsIterator(Favorite.class, " type = " + type, null, null, sort, limit);
        ArrayList<Favorite> result = new ArrayList<Favorite>();
        if (its != null) {
            while (its.hasNext()) {
                result.add(its.next());
            }
        }
        return result;
    }

    /**
     * 获取以保存的数据的类型
     *
     * @return
     */
    public static ArrayList<Integer> getTypes() {
        Iterator<Favorite> its = findAsIterator(Favorite.class, null, null, " type ", null, null);
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if (its != null) {
            while (its.hasNext()) {
                ret.add(its.next().getType());
            }
        }
        return ret;
    }

}
