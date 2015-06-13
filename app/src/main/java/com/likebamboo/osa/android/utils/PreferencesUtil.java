
package com.likebamboo.osa.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * sharedPreferences工具类
 *
 * @author likebamboo
 * @version [版本号, 2015年6月8日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class PreferencesUtil {

    /**
     * 当前数据库版本
     */
    public static final String PREF_DB_VERSION = "pref_db_version";

    private static String PREF_NAME = "osa_simple_data";

    private static PreferencesUtil mInstance = null;

    private SharedPreferences mSettings;

    private SharedPreferences.Editor mEditor;

    public static PreferencesUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PreferencesUtil(context.getApplicationContext());
        }
        return mInstance;
    }

    public boolean contains(String key) {
        return mSettings.contains(key);
    }

    public String getString(String key) {
        return mSettings.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return mSettings.getString(key, defValue);
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public void clear() {
        mEditor.clear();
        mEditor.commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSettings.getBoolean(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public int getInt(String key, int defValue) {
        return mSettings.getInt(key, defValue);
    }

    public void putInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public long getLong(String key, long defValue) {
        return mSettings.getLong(key, defValue);
    }

    public void putLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    private PreferencesUtil(Context c) {
        mSettings = c.getSharedPreferences(PREF_NAME, 0);
        mEditor = mSettings.edit();
    }
}
