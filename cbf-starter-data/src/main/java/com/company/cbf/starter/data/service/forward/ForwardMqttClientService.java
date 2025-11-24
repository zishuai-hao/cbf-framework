package com.company.cbf.starter.data.service.forward;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.company.cbf.starter.data.config.ForwardMqttAutoStartProperties;
import com.company.cbf.starter.data.entity.MqttServerGateway;

import cn.hutool.core.lang.UUID;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于统一管理需要链接Mqtt服务器的客户端
 * 1. 支持多个Mqtt服务器
 * 2. 支持自动连接
 * 3. 支持自动断开连接
 * 4. 支持自动重连
 * 5. 支持自动心跳
 * 6. 支持自动消息发送
 * 7. 支持自动消息接收
 * 8. 支持自动消息订阅
 * 9. 支持自动消息取消订阅
 * 10. 支持自动消息断开连接
 * 11. 支持自动消息连接成功
 * 
 * @author hzs
 * @date 2025/11/15
 */
@Slf4j
@RequiredArgsConstructor
public class ForwardMqttClientService {
    private final ForwardMqttAutoStartProperties config;
    private final Vertx vertx;
    private final Map<Long, MqttClient> mqttClientMap = new ConcurrentHashMap<>();
    private final Map<Long, MqttServerGateway> mqttServerGatewayMap = new ConcurrentHashMap<>();

    public MqttClient getMqttClient(Long id) {
        return mqttClientMap.get(id);
    }

    /**
     * 开启自动启动MqttClient以及自动重连，并且支持手动启动的MqttClient
     */
    @PostConstruct
    public void startAutoMqttClient() {
        if (config.isEnable()) {
            // 启动配置文件中的所有MqttClient
            config.getMqttServerGatewayList().forEach(mqttServerGateway -> {
                MqttClient mqttClient = createMqttClient(mqttServerGateway);
                mqttClientMap.put(mqttServerGateway.getId(), mqttClient);
                mqttServerGatewayMap.put(mqttServerGateway.getId(), mqttServerGateway);
            });
        }

        // 定时检查MqttClient的连接状态，如果未连接，则自动重连
        vertx.setPeriodic(10000, timerId -> {
            mqttClientMap.forEach((gatewayId, mqttClient) -> {
                if (!mqttClient.isConnected()) {
                    MqttServerGateway gateway = mqttServerGatewayMap.get(gatewayId);
                    if (gateway != null) {
                        connect(mqttClient, gateway);
                    }
                }
            });
        });
    }

    /**
     * 创建MqttClient
     * 
     * @param mqttServerGateway Mqtt服务器网关配置
     */
    public MqttClient createMqttClient(MqttServerGateway mqttServerGateway) {
        log.debug("开始配置 MqttClientOptions, mqttServerGateway: {}", mqttServerGateway);

        final MqttClientOptions mqttClientOptions = new MqttClientOptions();
        if (mqttServerGateway.getUsername() != null) {
            mqttClientOptions.setUsername(mqttServerGateway.getUsername());
        }
        if (mqttServerGateway.getPassword() != null) {
            mqttClientOptions.setPassword(mqttServerGateway.getPassword());
        }
        mqttClientOptions.setCleanSession(true);
        mqttClientOptions.setAutoKeepAlive(true);
        // 使用配置中的 client ID 或生成一个
        mqttClientOptions.setClientId(mqttServerGateway.getClientId() != null ? mqttServerGateway.getClientId()
                : "client_data_push_" + UUID.fastUUID().toString());
        mqttClientOptions.setMaxInflightQueue(65535);
        mqttClientOptions.setReconnectInterval(
                mqttServerGateway.getReconnectInterval() != null ? mqttServerGateway.getReconnectInterval()
                        : 10 * 1000);

        log.debug("MqttClientOptions 配置完成, id: {}", mqttServerGateway.getId());
        // 这里只是创建客户端对象，connect 动作留给 Service 去执行
        return MqttClient.create(vertx, mqttClientOptions);
    }

    /**
     * 连接逻辑
     */
    public void connect(MqttClient mqttClient, MqttServerGateway mqttServerGateway) {
        if (mqttClient.isConnected()) {
            log.warn("MqttClient 已连接，无需重复连接, id: {}", mqttServerGateway.getId());
            return;
        }
        mqttClient.connect(mqttServerGateway.getPort(), mqttServerGateway.getUrl(), s -> {
            if (s.succeeded()) {
                log.debug("MqttClient 连接成功, id: {}", mqttServerGateway.getId());
            } else {
                log.error("MqttClient 连接失败, id: {}", mqttServerGateway.getId(), s.cause());
            }
        }).exceptionHandler(event -> log.error("MqttClient 异常: {}", event.getMessage()));
    }

    public MqttClient startWithConnect(MqttServerGateway mqttServerGateway) {
        MqttClient mqttClient = createMqttClient(mqttServerGateway);
        mqttClientMap.put(mqttServerGateway.getId(), mqttClient);
        mqttServerGatewayMap.put(mqttServerGateway.getId(), mqttServerGateway);
        connect(mqttClient, mqttServerGateway);
        return mqttClient;
    }

    /**
     * 发送数据
     * 
     */
    public void sendData(Long mqttId, String data) {
        MqttClient mqttClient = mqttClientMap.get(mqttId);
        MqttServerGateway mqttServerGateway = mqttServerGatewayMap.get(mqttId);
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.publish(mqttServerGateway.getTopic(), Buffer.buffer(data), MqttQoS.AT_MOST_ONCE, false, false, s -> {
                if (s.succeeded()) {
                    log.debug("MqttClient 发送数据成功, mqttId: {}", mqttServerGateway.getId());
                }
            });
        } else {
            log.warn("MqttClient 未连接，无法发送数据, mqttId: {}", mqttId);
        }
    }

    @PreDestroy
    public void destroy() {
        mqttClientMap.values().forEach(mqttClient -> {
            if (mqttClient.isConnected()) {
            mqttClient.disconnect();
            log.info("MqttClient 成功断开连接。");
            }
        });
    }
}
