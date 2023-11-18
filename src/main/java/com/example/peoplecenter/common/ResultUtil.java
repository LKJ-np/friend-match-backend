package com.example.peoplecenter.common;

/**
 * 返回工具类
 */
public class ResultUtil {
    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){

        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code,String message,String description){
        return new BaseResponse<>(code,null,message,description);
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(ErrorCode code,String message,String description){
        return new BaseResponse<>(code.getCode(),null,message,description);
    }

    /**
     * 失败
     * @param code
     * @param description
     * @return
     */
    public static BaseResponse error(ErrorCode code,String description){
        return new BaseResponse<>(code.getCode(),null,code.getMessage(),description);
    }
}
