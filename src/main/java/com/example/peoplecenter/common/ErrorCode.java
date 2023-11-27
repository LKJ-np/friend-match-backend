package com.example.peoplecenter.common;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * 枚举
 * 自定义错误码
 */
public enum ErrorCode {

    SUCCESS(0,"ok",""),
    PARAM_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求参数为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(401001,"无权限",""),
    FORBIDDEN(403001,"禁止操作",""),
    SYSTEM_ERROR(50000,"系统内部异常","");

    private final int code;
    private final String message;
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
