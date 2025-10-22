package com.company.cbf.starter.data.config;

//import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.company.cbf.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 自动配置
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class MybatisPlusAutoConfiguration {

    /**
     * 分页插件
     */
//    @Bean
//    @ConditionalOnMissingBean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        // 分页插件
//        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
//        paginationInnerInterceptor.setMaxLimit(CommonConstants.MAX_PAGE_SIZE);
//        interceptor.addInnerInterceptor(paginationInnerInterceptor);
//        return interceptor;
//    }

    /**
     * 字段自动填充处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler metaObjectHandler() {
        return new CustomMetaObjectHandler();
    }

    /**
     * 自定义字段自动填充处理器
     */
    public static class CustomMetaObjectHandler implements MetaObjectHandler {

        @Override
        public void insertFill(MetaObject metaObject) {
            log.debug("开始插入填充...");
            
            // 填充创建时间
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            // 填充更新时间
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            // 填充逻辑删除标记
            this.strictInsertFill(metaObject, "deleted", Integer.class, CommonConstants.NOT_DELETED);
            
            // 填充创建人ID（从上下文获取）
            Long currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                this.strictInsertFill(metaObject, "createBy", Long.class, currentUserId);
                this.strictInsertFill(metaObject, "updateBy", Long.class, currentUserId);
            }
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            log.debug("开始更新填充...");
            
            // 填充更新时间
            this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            
            // 填充更新人ID（从上下文获取）
            Long currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                this.strictUpdateFill(metaObject, "updateBy", Long.class, currentUserId);
            }
        }

        /**
         * 获取当前用户ID
         * 这里可以从Security上下文、ThreadLocal等地方获取
         * 具体实现根据业务系统的认证方式而定
         */
        private Long getCurrentUserId() {
            // TODO: 实现获取当前用户ID的逻辑
            // 可以从SecurityContext、ThreadLocal、请求头等地方获取
            return null;
        }
    }
}
