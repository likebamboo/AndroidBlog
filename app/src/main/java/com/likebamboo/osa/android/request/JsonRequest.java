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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * JSON请求
 *
 * @param <T>
 */
public class JsonRequest<T> extends BaseRequest<T> {
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final Class<T> mClazz;
    private final Listener<T> mListener;
    protected final Map<String, String> mParams;

    /**
     * 是否仅仅解析返回数据中 result 字段的数据
     */
    private boolean justResult = true;

    public JsonRequest(String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        this(url, clazz, null, listener, errorListener);
    }

    public JsonRequest(String url, Class<T> clazz, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, formatUrl(url, params), errorListener);
        this.mClazz = clazz;
        this.mParams = params;
        this.mListener = listener;
    }

    public JsonRequest(int method, String url, Class<T> clazz, Map<String, String> params, Listener<T> listener, ErrorListener errorListener) {
        super(method, formatUrl(method, url, params), errorListener);
        this.mClazz = clazz;
        this.mParams = params;
        this.mListener = listener;
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return mParams != null ? mParams : super.getParams();
    }

    public boolean isJustResult() {
        return justResult;
    }

    public void setJustResult(boolean justResult) {
        this.justResult = justResult;
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener == null) {
            return;
        }
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (TextUtils.isEmpty(json)) {
                return Response.error(new VolleyError("返回数据为空"));
            }
            if (justResult) {
                json = getResult(json);
            }
            return Response.success(mapper.readValue(json, mClazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (JsonParseException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }
}
