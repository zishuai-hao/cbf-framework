package com.company.cbf.starter.cache.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * CBF 缓存自动配置入口
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@AutoConfiguration
@Import({
        RedisConfiguration.class
})
public class CbfCacheAutoConfiguration {
}
