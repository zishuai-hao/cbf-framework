package com.company.cbf.starter.data.config;

import com.company.cbf.starter.data.service.forward.BufferForwardMqttClientAdapter;
import com.company.cbf.starter.data.service.forward.DeviceInfoManager;
import com.company.cbf.starter.data.service.forward.ForwardMqttClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author hzs
 * @date 2025/10/28
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties({ForwardMqttAutoStartProperties.class})
// 注册并加载 ForwardMqttConfig 类，使其成为 Spring Bean
public class MqttAutoConfiguration {

    // 1. 提供 Vertx 实例作为 Bean
    @Bean
    @ConditionalOnMissingBean // 允许用户提供自己的 Vertx Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    /**
     * 设备信息管理器
     * @return DeviceInfoManager
     */
    @Bean
    @ConditionalOnMissingBean
    public DeviceInfoManager deviceInfoManager() {
        return new DeviceInfoManager();
    }

    /**
     * 用于统一管理需要链接Mqtt服务器的客户端
     * @param vertx Vertx实例
     * @param config 配置文件
     * @return ForwardMqttClientService
     */
    @Bean
    @ConditionalOnMissingBean
    public ForwardMqttClientService forwardMqttClientService(Vertx vertx, ForwardMqttAutoStartProperties config) {
        return new ForwardMqttClientService(config, vertx);
    }
    

    /**
     * 缓冲转发MQTT客户端适配器
     * @param forwardMqttClientService 用于统一管理需要链接Mqtt服务器的客户端
     * @param config 配置文件
     * @param vertx Vertx实例
     * @param objectMapper ObjectMapper实例
     * @param deviceInfoManager 设备信息管理器
     * @return BufferForwardMqttClientAdapter
     */
    @Bean
    @ConditionalOnMissingBean
    public BufferForwardMqttClientAdapter bufferForwardMqttClientAdapter(ForwardMqttClientService forwardMqttClientService, ForwardMqttAutoStartProperties config, Vertx vertx, ObjectMapper objectMapper, DeviceInfoManager deviceInfoManager) {
        return new BufferForwardMqttClientAdapter(vertx, forwardMqttClientService, config, objectMapper, deviceInfoManager);
    }
}
