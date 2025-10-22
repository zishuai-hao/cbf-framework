package com.company.cbf.starter.web.config;

import com.company.cbf.common.constant.CommonConstants;
import com.company.cbf.common.exception.BusinessException;
import com.company.cbf.common.model.CommonResponse;
import com.company.cbf.common.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return CommonResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理 - @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常: {}", message);
        return CommonResponse.error(CommonConstants.PARAM_ERROR_CODE, message);
    }

    /**
     * 参数校验异常处理 - @RequestParam
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常: {}", message);
        return CommonResponse.error(CommonConstants.PARAM_ERROR_CODE, message);
    }

    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定异常: {}", message);
        return CommonResponse.error(CommonConstants.PARAM_ERROR_CODE, message);
    }

    /**
     * 运行时异常处理
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return CommonResponse.error(CommonConstants.ERROR_CODE, "系统内部错误");
    }

    /**
     * 其他异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return CommonResponse.error(CommonConstants.ERROR_CODE, "系统异常");
    }

    /**
     * 获取请求追踪ID
     */
    private String getTraceId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader(CommonConstants.TRACE_ID_HEADER);
            }
        } catch (Exception e) {
            log.warn("获取TraceId失败", e);
        }
        return null;
    }
}
