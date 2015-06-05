package com.likebamboo.osa.android.entity;

/**
 * Created by wentaoli on 2015/5/12.
 */
public class BaseRsp {
    /**
     * 返回信息
     */
    private String message = "";

    /**
     * 错误码
     */
    private int errorCode = -1;

    @Override
    public String toString() {
        return "BaseRsp{" +
                "errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}';
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
