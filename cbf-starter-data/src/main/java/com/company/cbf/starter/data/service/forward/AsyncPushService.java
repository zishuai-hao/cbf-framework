package com.company.cbf.starter.data.service.forward;

import com.company.cbf.starter.data.config.ForwardMqttConfig;
import com.company.cbf.starter.data.entity.MqttPubProtocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author hzs
 * @date 2024/01/07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncPushService {
    private final MqttClient mqttClient;
    private final ForwardMqttConfig mqttConfig;
    private final ObjectMapper objectMapper;

    public void push(MqttPubProtocol data) {
        try {
            final String jsonStr = objectMapper.writeValueAsString(data);
            final String topic = mqttConfig.getLightweightTopic();

            // 推送MQTT消息
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.publish(
                        topic,
                        Buffer.buffer(jsonStr),
                        MqttQoS.AT_MOST_ONCE,
                        false,
                        false,
                        handler -> {
                            if (handler.succeeded()) {
                                log.debug("MQTT消息已发布，dataTag：{}，主题：{}", data.getDataTag(), topic);
                            } else {
                                log.error("MQTT消息发布失败，dataTag：{}，主题：{}", data.getDataTag(), topic, handler.cause());
                            }
                        });
            } else {
                log.warn("MQTT客户端未连接，无法推送消息，dataTag：{}", data.getDataTag());
            }

        } catch (Exception e) {
            log.error("序列化或准备推送MQTT消息失败，dataTag：{}", data.getDataTag(), e);
        }
    }
}
