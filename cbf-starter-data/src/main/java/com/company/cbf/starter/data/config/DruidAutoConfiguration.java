package com.company.cbf.starter.data.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Druid 数据源自动配置
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@EnableConfigurationProperties(DruidProperties.class)
public class DruidAutoConfiguration {

    /**
     * Druid 数据源配置
     */
    @Bean
    @ConditionalOnMissingBean
    public DataSource druidDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = new DruidDataSource();
        
        // 基本配置
        dataSource.setUrl(druidProperties.getUrl());
        dataSource.setUsername(druidProperties.getUsername());
        dataSource.setPassword(druidProperties.getPassword());
        dataSource.setDriverClassName(druidProperties.getDriverClassName());
        
        // 连接池配置
        dataSource.setInitialSize(druidProperties.getInitialSize());
        dataSource.setMinIdle(druidProperties.getMinIdle());
        dataSource.setMaxActive(druidProperties.getMaxActive());
        dataSource.setMaxWait(druidProperties.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(druidProperties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(druidProperties.getMinEvictableIdleTimeMillis());
        dataSource.setValidationQuery(druidProperties.getValidationQuery());
        dataSource.setTestWhileIdle(druidProperties.isTestWhileIdle());
        dataSource.setTestOnBorrow(druidProperties.isTestOnBorrow());
        dataSource.setTestOnReturn(druidProperties.isTestOnReturn());
        
        // 监控配置
        dataSource.setPoolPreparedStatements(druidProperties.isPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(druidProperties.getMaxPoolPreparedStatementPerConnectionSize());
        
        try {
            dataSource.init();
            log.info("Druid 数据源初始化成功");
        } catch (Exception e) {
            log.error("Druid 数据源初始化失败", e);
            throw new RuntimeException("Druid 数据源初始化失败", e);
        }
        
        return dataSource;
    }

//    /**
//     * Druid 监控页面配置
//     */
//    @Bean
//    @ConditionalOnProperty(name = "cbf.druid.monitor.enabled", havingValue = "true", matchIfMissing = true)
//    public ServletRegistrationBean<StatViewServlet> druidStatViewServlet(DruidProperties druidProperties) {
//        DruidProperties.Monitor monitor = druidProperties.getMonitor();
//
//        ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(
//                new StatViewServlet(), monitor.getStatViewServletUrl());
//
//        // 添加初始化参数
//        registrationBean.addInitParameter("loginUsername", monitor.getLoginUsername());
//        registrationBean.addInitParameter("loginPassword", monitor.getLoginPassword());
//        registrationBean.addInitParameter("resetEnable", monitor.getResetEnable());
//
//        return registrationBean;
//    }

//    /**
//     * Druid 监控过滤器配置
//     */
//    @Bean
//    @ConditionalOnProperty(name = "cbf.druid.monitor.enabled", havingValue = "true", matchIfMissing = true)
//    public FilterRegistrationBean<WebStatFilter> druidWebStatFilter(DruidProperties druidProperties) {
//        DruidProperties.Monitor monitor = druidProperties.getMonitor();
//
//        FilterRegistrationBean<WebStatFilter> registrationBean = new FilterRegistrationBean<>(new WebStatFilter());
//
//        // 添加过滤规则
//        registrationBean.addUrlPatterns(monitor.getWebStatFilterUrlPatterns());
//        registrationBean.addInitParameter("exclusions", monitor.getWebStatFilterExclusions());
//
//        return registrationBean;
//    }
}
