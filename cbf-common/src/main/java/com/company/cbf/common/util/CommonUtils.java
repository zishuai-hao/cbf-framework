package com.company.cbf.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 通用工具类
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
public class CommonUtils {

    /**
     * 生成UUID
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成MD5
     */
    public static String md5(String input) {
        if (StrUtil.isBlank(input)) {
            return null;
        }
        return DigestUtil.md5Hex(input);
    }

    /**
     * 对象转JSON字符串
     */
    public static String toJsonString(Object obj) {
        try {
            return JSONUtil.toJsonStr(obj);
        } catch (Exception e) {
            log.error("对象转JSON失败", e);
            return null;
        }
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        try {
            return JSONUtil.toBean(jsonString, clazz);
        } catch (Exception e) {
            log.error("JSON转对象失败", e);
            return null;
        }
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return StrUtil.isBlank(str);
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return StrUtil.isNotBlank(str);
    }
}
