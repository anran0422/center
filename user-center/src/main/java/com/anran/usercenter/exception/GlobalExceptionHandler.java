package com.anran.usercenter.exception;

import com.anran.usercenter.common.BaseResponse;
import com.anran.usercenter.common.ErrorCode;
import com.anran.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * author anran
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 针对什么样的异常，做什么样的处理
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("BusinessException" + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException" + e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(),"");
    }
}
