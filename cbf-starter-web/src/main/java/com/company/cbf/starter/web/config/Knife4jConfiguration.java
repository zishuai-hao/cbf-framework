package com.company.cbf.starter.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API文档配置
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "cbf.knife4j.enabled", havingValue = "true", matchIfMissing = true)
public class Knife4jConfiguration {

    /**
     * OpenAPI配置
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CBF API文档")
                        .description("CBF 企业级后端框架 API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CBF Framework Team")
                                .email("cbf@company.com")
                                .url("https://github.com/company/cbf-framework"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
