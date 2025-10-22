package com.company.cbf.starter.web.config;

import com.company.cbf.common.constant.CommonConstants;
import com.company.cbf.common.util.CommonUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 请求追踪过滤器
 *
 * @author CBF Framework Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class TraceFilterConfiguration {

    /**
     * 请求追踪过滤器
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(1)
    public Filter traceFilter() {
        return new TraceFilter();
    }

    /**
     * 请求追踪过滤器实现
     */
    public static class TraceFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            
            // 获取或生成TraceId
            String traceId = request.getHeader(CommonConstants.TRACE_ID_HEADER);
            if (CommonUtils.isEmpty(traceId)) {
                traceId = CommonUtils.generateUuid();
            }
            
            // 设置TraceId到响应头
            response.setHeader(CommonConstants.TRACE_ID_HEADER, traceId);
            
            // 设置TraceId到MDC，用于日志追踪
            try {
                org.slf4j.MDC.put("traceId", traceId);
                log.debug("请求开始: {} {}", request.getMethod(), request.getRequestURI());
                
                filterChain.doFilter(request, response);
                
                log.debug("请求结束: {} {} - {}", request.getMethod(), request.getRequestURI(), response.getStatus());
            } finally {
                // 清理MDC
                org.slf4j.MDC.remove("traceId");
            }
        }
    }
}
