package com.company.cbf.common.constant;

/**
 * 通用常量
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
public class CommonConstants {

    /**
     * 成功状态码
     */
    public static final Integer SUCCESS_CODE = 200;

    /**
     * 失败状态码
     */
    public static final Integer ERROR_CODE = 500;

    /**
     * 参数错误状态码
     */
    public static final Integer PARAM_ERROR_CODE = 400;

    /**
     * 未授权状态码
     */
    public static final Integer UNAUTHORIZED_CODE = 401;

    /**
     * 禁止访问状态码
     */
    public static final Integer FORBIDDEN_CODE = 403;

    /**
     * 资源不存在状态码
     */
    public static final Integer NOT_FOUND_CODE = 404;

    /**
     * 逻辑删除标记 - 未删除
     */
    public static final Integer NOT_DELETED = 0;

    /**
     * 逻辑删除标记 - 已删除
     */
    public static final Integer DELETED = 1;

    /**
     * 默认分页大小
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final Integer MAX_PAGE_SIZE = 100;

    /**
     * 默认页码
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;

    /**
     * 请求追踪ID请求头名称
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * 用户ID请求头名称
     */
    public static final String USER_ID_HEADER = "X-User-Id";

    /**
     * 租户ID请求头名称
     */
    public static final String TENANT_ID_HEADER = "X-Tenant-Id";
}
