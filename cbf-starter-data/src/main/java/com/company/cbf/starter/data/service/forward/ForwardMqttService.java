package com.company.cbf.starter.data.service.forward;

import com.company.cbf.starter.data.config.ForwardMqttConfig;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


/**
 * @author hzs
 * @date 2023/07/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ForwardMqttService {

    private final ForwardMqttConfig config;
    private final MqttClient mqttClient; // MqttClient 现在是注入进来的 Bean
    private final Vertx vertx;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 自动检查重连
     */
    @PostConstruct
    public void init() {
        // Vert.x 周期性任务来检查并触发重连
        vertx.setPeriodic(10000, id -> {
            if (!this.mqttClient.isConnected()) {
                log.warn("MqttClient 已断线，启动重连尝试...");
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
        mqttClient
                .connect(config.getPort(), config.getUrl(), s -> {
                    if (s.succeeded()) {
                        log.info("MQTT Client connect success. Broker: {}:{}", config.getUrl(), config.getPort());
                         applicationEventPublisher.publishEvent(new MqttConnectedEvent(this));
                    } else {
                        log.error("Client connect fail to {}:{}, will retry. Cause: {}", config.getUrl(), config.getPort(), s.cause().getMessage());
                    }
                })
                .exceptionHandler(event -> log.error("MqttClient 异常: {}", event.getMessage()));
    }

    /**
     * 服务销毁时断开连接
     */
    @PreDestroy
    public void destroy() {
        if (mqttClient.isConnected()) {
            mqttClient.disconnect();
            log.info("MqttClient 成功断开连接。");
        }
    }

    // TODO 暂时暴露MqttClient，后续可封装对外方法
//    // 封装 MqttClient 的对外方法，防止直接访问
//    public void publish(String topic, String payload) {
//        if (mqttClient.isConnected()) {
//            // ... 实际的发布逻辑
//        } else {
//            log.warn("MqttClient 未连接，发布失败。");
//        }
//    }


}
