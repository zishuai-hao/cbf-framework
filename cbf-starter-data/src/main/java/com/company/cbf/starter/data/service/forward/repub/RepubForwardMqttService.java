package com.company.cbf.starter.data.service.forward.repub;

import com.company.cbf.starter.data.config.ForwardMqttProperties;
import com.company.cbf.starter.data.config.RepubForwardMqttProperties;
import com.company.cbf.starter.data.service.forward.MqttConnectedEvent;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;


/**
 * 用于连接保障
 * @author hzs
 * @date 2023/07/14
 */
@Slf4j
@RequiredArgsConstructor
public class RepubForwardMqttService {

    private final RepubForwardMqttProperties config;
    private final MqttClient repubMqttClient; // MqttClient 现在是注入进来的 Bean
    private final Vertx vertx;

    /**
     * 自动检查重连
     */
    @PostConstruct
    public void init() {
        // Vert.x 周期性任务来检查并触发重连
        vertx.setPeriodic(10000, id -> {
            if (!this.repubMqttClient.isConnected()) {
                log.warn("repubMqttClient 已断线，启动重连尝试...");
                connect(); // 直接调用重连方法
            }
        });
        // 第一次连接
        connect();
    }

    /**
     * 连接逻辑
     */
    private void connect() {
        repubMqttClient
                .connect(config.getPort(), config.getUrl(), s -> {
                    if (s.succeeded()) {
                        log.info("Repub MQTT Client connect success. Broker: {}:{}", config.getUrl(), config.getPort());
                    } else {
                        log.error("Repub MQTT Client connect fail to {}:{}, will retry. Cause: {}", config.getUrl(), config.getPort(), s.cause().getMessage());
                    }
                })
                .exceptionHandler(event -> log.error("repubMqttClient 异常: {}", event.getMessage()));
    }

    /**
     * 服务销毁时断开连接
     */
    @PreDestroy
    public void destroy() {
        if (repubMqttClient.isConnected()) {
            repubMqttClient.disconnect();
            log.info("RepubMqttClient 成功断开连接。");
        }
    }

}
