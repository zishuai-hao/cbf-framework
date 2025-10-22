package com.company.cbf.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @param <T> 响应数据类型
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求追踪ID
     */
    private String traceId;

    /**
     * 时间戳
     */
    private Long timestamp;

    public CommonResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public CommonResponse(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public CommonResponse(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(200, "操作成功");
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> CommonResponse<T> success(String message) {
        return new CommonResponse<>(200, message);
    }

    /**
     * 成功响应（自定义消息和数据）
     */
    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> CommonResponse<T> error() {
        return new CommonResponse<>(500, "操作失败");
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(500, message);
    }

    /**
     * 失败响应（自定义码和消息）
     */
    public static <T> CommonResponse<T> error(Integer code, String message) {
        return new CommonResponse<>(code, message);
    }

    /**
     * 失败响应（自定义码、消息和数据）
     */
    public static <T> CommonResponse<T> error(Integer code, String message, T data) {
        return new CommonResponse<>(code, message, data);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}
