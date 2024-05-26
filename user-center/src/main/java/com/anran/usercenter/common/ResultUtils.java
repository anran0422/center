package com.anran.usercenter.common;

/**
 * 返回工具类
 */
public class ResultUtils {
    /**
     * 成功
     * @param data 数据
     * @return 返回泛型
     * @param <T> 泛型
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data, "success");
    }

    /**
     * 失败
     * @param errorCode 错误码对象
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
//        return new BaseResponse<>(errorCode.getCode(),null, errorCode.getMessage(),errorCode.getDescription());
        return new BaseResponse<>(errorCode);
    }

    /**
     *
     * @param errorCode
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description){
        return new BaseResponse<>(errorCode.getCode(), message, description);
    }

    /**
     *
     * @param errorCode
     * @param description
     * @return
     */

    public static BaseResponse error(ErrorCode errorCode, String description){
        return new BaseResponse<>(errorCode.getCode(),null, errorCode.getMessage(), description);
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code, String message, String description){
        return new BaseResponse<>(code,null,message, description);
    }

}
