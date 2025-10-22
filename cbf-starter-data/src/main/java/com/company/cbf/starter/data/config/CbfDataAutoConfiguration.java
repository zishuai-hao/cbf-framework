package com.company.cbf.starter.data.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * CBF 数据访问自动配置入口
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@AutoConfiguration
@Import({
        MybatisPlusAutoConfiguration.class,
        DruidAutoConfiguration.class
})
public class CbfDataAutoConfiguration {
}
