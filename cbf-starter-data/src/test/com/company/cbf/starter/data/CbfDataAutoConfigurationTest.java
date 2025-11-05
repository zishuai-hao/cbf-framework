package com.company.cbf.starter.data;

import com.company.cbf.starter.data.config.CbfDataAutoConfiguration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class CbfDataAutoConfigurationTest {

    // 1. 定义 ApplicationContextRunner
    // withUserConfiguration() 用于注册您的自定义测试配置（如果需要）
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            // 关键点：注册要测试的自动配置类本身
            .withUserConfiguration(CbfDataAutoConfiguration.class)
            .withPropertyValues(
                    // 关键：提供 DataSource 所需的最小配置，以便创建 DataSource 和 SqlSessionFactory
                    // 确保 DruidAutoConfiguration 的 @ConditionalOnProperty(prefix = "spring.datasource", name = "url") 满足
                    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL", // 使用 H2 内存数据库并模拟 MySQL 模式
                    "spring.datasource.driver-class-name=org.h2.Driver",
                    "spring.datasource.username=sa",
                    "spring.datasource.password=password");

    @Test
    @DisplayName("测试 CbfDataAutoConfiguration 是否成功加载并导入所有配置")
    void shouldLoadAndImportAllConfigurations() {
        this.runner.run(context -> {
            // 断言 1: CbfDataAutoConfiguration 自身已成功加载
            assertThat(context).hasSingleBean(CbfDataAutoConfiguration.class);

            // 断言 2: 验证 MybatisPlusAutoConfiguration 是否生效 (通过检查它定义的 Bean)
            // 假设 MybatisPlusAutoConfiguration 最终会定义一个 SqlSessionFactory Bean
            // 注意：您可能需要根据您的实际依赖和 AutoConfig 类名进行调整
            assertThat(context).as("MybatisPlusAutoConfiguration 应该成功加载 SqlSessionFactory Bean")
                    .hasSingleBean(org.apache.ibatis.session.SqlSessionFactory.class);

            // 断言 3: 验证 DruidAutoConfiguration 是否生效 (通过检查它定义的 Bean)
            // 假设 DruidAutoConfiguration 最终会暴露一个 DataSource Bean
            // 您可能需要根据实际使用的类名进行调整
            assertThat(context).as("DruidAutoConfiguration 应该成功加载 DataSource Bean")
                    .hasSingleBean(javax.sql.DataSource.class);

            // 如果您还有其他 Bean，继续在这里添加断言
        });
    }

    @Test
    @DisplayName("测试自动配置类中的条件是否正确跳过加载")
    void shouldNotLoadIfConditionalFails() {
        // 假设 CbfDataAutoConfiguration 上有 @ConditionalOnClass(MissingDependency.class)
        // 且该依赖没有在 classpath 中
        this.runner
                .withUserConfiguration(EmptyTestConfiguration.class) // 使用一个空的配置类确保上下文启动
                .run(context -> {
                    // 在这个场景下，由于条件不满足，自动配置类应该没有被加载
                    assertThat(context).as("CbfDataAutoConfiguration 应该因为条件不满足而被跳过加载")
                            .doesNotHaveBean(CbfDataAutoConfiguration.class);
                });
    }

    // 一个空的配置类，有时用于启动一个干净的上下文
    @Configuration
    static class EmptyTestConfiguration {
    }
}