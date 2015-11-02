/*
 * Created by Storm Zhang, Feb 11, 2014.
 */

package com.likebamboo.osa.android.request;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * JSON Array 请求
 */
public class JsonArrayRequest extends BaseRequest<JSONArray> {
    private final Listener<JSONArray> mListener;
    protected final Map<String, String> mParams;

    public JsonArrayRequest(String url, Listener<JSONArray> listener, ErrorListener errorListener) {
        this(url, null, listener, errorListener);
    }

    public JsonArrayRequest(String url, Map<String, String> params, Listener<JSONArray> listener, ErrorListener errorListener) {
        super(Method.GET, formatUrl(url, params), errorListener);
        this.mParams = params;
        this.mListener = listener;
    }

    public JsonArrayRequest(int method, String url, Map<String, String> params, Listener<JSONArray> listener, ErrorListener errorListener) {
        super(method, formatUrl(method, url, params), errorListener);
        this.mParams = params;
        this.mListener = listener;
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return mParams != null ? mParams : super.getParams();
    }

    @Override
    protected void deliverResponse(JSONArray response) {
        if (mListener == null) {
            return;
        }
        mListener.onResponse(response);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (TextUtils.isEmpty(json)) {
                return Response.error(new VolleyError("返回数据为空"));
            }
            json = getResult(json);
            return Response.success(new JSONArray(json), HttpHeaderParser.parseCacheHeaders(response));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 将数据解析为ArrayList对象
     *
     * @param <T>
     * @param jsonStr json字符串
     * @param clazz   类型
     * @return
     */
    public static <T> ArrayList<T> parseToArrayList(String jsonStr, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
        try {
            return (ArrayList<T>) mapper.readValue(jsonStr, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
