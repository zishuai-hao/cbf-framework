package com.company.cbf.starter.data.config;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author hzs
 * @date 2025/10/28
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(ForwardMqttConfig.class) // 注册并加载 ForwardMqttConfig 类，使其成为 Spring Bean
@ConditionalOnProperty(prefix = "mqtt-config", name = "enable", havingValue = "true") // 只有当配置文件中存在 "mqtt-config.enable=true" 或其他条件时才激活
public class MqttAutoConfiguration {

    // 1. 提供 Vertx 实例作为 Bean
    @Bean
    @ConditionalOnMissingBean // 允许用户提供自己的 Vertx Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    // 2. 配置并创建 MqttClient 实例作为 Bean
    @Bean
    @ConditionalOnMissingBean // 允许用户提供自己的 MqttClient Bean
    public MqttClient mqttClient(Vertx vertx, ForwardMqttConfig config) {
        log.info("开始配置 MqttClientOptions...");

        final MqttClientOptions mqttClientOptions = new MqttClientOptions();

        if (config.getUsername() != null) {
            mqttClientOptions.setUsername(config.getUsername());
        }
        if (config.getPassword() != null) {
            mqttClientOptions.setPassword(config.getPassword());
        }

        mqttClientOptions.setCleanSession(true);
        mqttClientOptions.setAutoKeepAlive(true);
        // 使用配置中的 client ID 或生成一个
        mqttClientOptions.setClientId(config.getClientId() != null ? config.getClientId() : "client_data_push_" + System.currentTimeMillis());
        mqttClientOptions.setMaxInflightQueue(65535);
        // 重连间隔应该在配置中设置
        mqttClientOptions.setReconnectInterval(config.getReconnectInterval() != null ? config.getReconnectInterval() : 10 * 1000);

        log.info("MqttClientOptions 配置完成。");
        // 这里只是创建客户端对象，connect 动作留给 Service 去执行
        return MqttClient.create(vertx, mqttClientOptions);
    }
}
