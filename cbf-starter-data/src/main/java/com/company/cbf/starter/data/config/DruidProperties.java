package com.company.cbf.starter.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Druid 数据源配置属性
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "cbf.druid")
public class DruidProperties {

    /**
     * 数据库连接URL
     */
    private String url;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码
     */
    private String password;

    /**
     * 数据库驱动类名
     */
    private String driverClassName = "com.mysql.cj.jdbc.Driver";

    /**
     * 初始连接数
     */
    private int initialSize = 5;

    /**
     * 最小空闲连接数
     */
    private int minIdle = 5;

    /**
     * 最大活跃连接数
     */
    private int maxActive = 20;

    /**
     * 获取连接等待超时时间
     */
    private long maxWait = 60000;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private long timeBetweenEvictionRunsMillis = 60000;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private long minEvictableIdleTimeMillis = 300000;

    /**
     * 用来检测连接是否有效的sql
     */
    private String validationQuery = "SELECT 1";

    /**
     * 建议配置为true，不影响性能，并且保证安全性
     */
    private boolean testWhileIdle = true;

    /**
     * 申请连接时执行validationQuery检测连接是否有效
     */
    private boolean testOnBorrow = false;

    /**
     * 归还连接时执行validationQuery检测连接是否有效
     */
    private boolean testOnReturn = false;

    /**
     * 是否缓存preparedStatement
     */
    private boolean poolPreparedStatements = true;

    /**
     * 要启用PSCache，必须配置大于0
     */
    private int maxPoolPreparedStatementPerConnectionSize = 20;

    /**
     * 监控配置
     */
    private Monitor monitor = new Monitor();

    @Data
    public static class Monitor {
        /**
         * 是否启用监控
         */
        private boolean enabled = true;

        /**
         * 监控页面访问路径
         */
        private String statViewServletUrl = "/druid/*";

        /**
         * 监控页面登录用户名
         */
        private String loginUsername = "admin";

        /**
         * 监控页面登录密码
         */
        private String loginPassword = "admin";

        /**
         * 是否允许重置数据
         */
        private String resetEnable = "false";

        /**
         * 监控过滤器URL模式
         */
        private String webStatFilterUrlPatterns = "/*";

        /**
         * 监控过滤器排除路径
         */
        private String webStatFilterExclusions = "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*";
    }
}
