package com.likebamboo.osa.android.exception;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;


/**
 * Created by wentaoli on 2015/5/14.
 */
public class ErrorTrans {

    public static OsaException transToOsaException(Throwable a) {
        if (a == null) {
            return new OsaException("未知错误");
        }
        if (a instanceof ParseError) {
            return new OsaException("数据解析错误", a);
        }
        if (a instanceof TimeoutError) {
            return new OsaException("请求超时", a);
        }
        if (a instanceof ServerError) {
            return new OsaException("服务器错误", a);
        }
        if (a instanceof AuthFailureError) {
            return new OsaException("请求认证错误", a);
        }
        if (a instanceof NoConnectionError) {
            return new OsaException("网络未连接，请检查网络状态", a);
        }
        if (a instanceof NetworkError) {
            return new OsaException("网络连接异常", a);
        }
        return new OsaException("未知错误");
    }

}
