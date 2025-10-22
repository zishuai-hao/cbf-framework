package com.company.cbf.starter.web.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * CBF Web层自动配置入口
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@AutoConfiguration
@Import({
        GlobalExceptionHandler.class,
        TraceFilterConfiguration.class,
        Knife4jConfiguration.class
})
public class CbfWebAutoConfiguration {
}
