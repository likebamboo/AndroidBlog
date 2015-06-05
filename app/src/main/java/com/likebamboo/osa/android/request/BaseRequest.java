package com.likebamboo.osa.android.request;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by likebamboo on 2015/5/12.
 */
public abstract class BaseRequest<T> extends Request<T> {

    public BaseRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    /**
     * 返回json数据中的result字段
     *
     * @param data
     * @return
     */
    protected String getResult(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        JSONObject json = null;
        try {
            json = new JSONObject(data);
            return json.getString("result");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 拼接URL（仅针对get方法）
     *
     * @param method
     * @param baseUrl
     * @param params
     * @return
     */
    public static String formatUrl(int method, String baseUrl, Map<String, String> params) {
        if (method != Method.GET) {
            return baseUrl;
        }
        return formatUrl(baseUrl, params);
    }

    /**
     * 拼接URL(仅针对get方法)
     *
     * @param baseUrl
     * @param params
     * @return
     */
    public static String formatUrl(String baseUrl, Map<String, String> params) {
        if (TextUtils.isEmpty(baseUrl) || params == null || params.isEmpty()) {
            return baseUrl;
        }
        StringBuilder sb = new StringBuilder(baseUrl);
        if (sb.toString().contains("?")) {
            // 不以问号结尾
            if (!sb.toString().endsWith("?")) {
                sb.append("&");
            }
        } else {// 没有问号，添加问号
            sb.append("?");
        }
        String split = "";
        for (String key : params.keySet()) {
            sb.append(split).append(key).append("=").append(params.get(key));
            split = "&";
        }

        return sb.toString();
    }
}
