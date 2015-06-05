package com.likebamboo.osa.android.exception;

/**
 * Created by wentaoli on 2015/5/14.
 */
public class OsaException extends Exception {
    public OsaException() {
    }

    public OsaException(String detailMessage) {
        super(detailMessage);
    }

    public OsaException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public OsaException(Throwable throwable) {
        super(throwable);
    }
}
