package com.company.cbf.starter.data.service.forward;

import com.company.cbf.starter.data.config.ForwardMqttProperties;
import com.company.cbf.starter.data.config.RepubForwardMqttProperties;
import com.company.cbf.starter.data.entity.MqttData;
import com.company.cbf.starter.data.entity.MqttPubProtocol;
import com.company.cbf.starter.data.service.forward.repub.ReDevice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author hzs
 * @date 2024/01/07
 */
@Slf4j
@RequiredArgsConstructor
public class AsyncPushService implements ApplicationContextAware {
    private final MqttClient mqttClient;
    private final MqttClient repubClient;
    private final ForwardMqttProperties mqttConfig;
    private final RepubForwardMqttProperties repubMqttConfig;
    private final ObjectMapper objectMapper;

    private HashMap<String, List<ReDevice>> repubMap;

    public void push(MqttPubProtocol data) {
        try {
            // 发送原始Mqtt消息
            final String jsonStr = objectMapper.writeValueAsString(data);
            final String topic = getTopic();
            String clientName = mqttConfig.getClientId();
            sendMqttMessage(data, topic, mqttClient, jsonStr, clientName);

            sendRepubMqttMessage(data);

        } catch (Exception e) {
            log.error("序列化或准备推送MQTT消息失败，dataTag：{}", data.getDataTag(), e);
        }
    }

    private void sendRepubMqttMessage(MqttPubProtocol data) throws JsonProcessingException {
        // 复用推送MQTT消息
        if (repubClient != null && repubClient.isConnected()) {
            // 修改数据标签和设备ID
            final ArrayList<MqttData> repubMqttDataList = new ArrayList<>();
            // 循环处理每个设备数据
            for (MqttData sourceDevice : data.getMqttDataList()) {
                // 查找对应的复用设备ID列表
                List<ReDevice> reDevices = repubMap.get(sourceDevice.getDeviceId());
                if (reDevices != null && !reDevices.isEmpty()) {
                    // 为每个复用设备ID创建新的MqttData对象
                    for (ReDevice reDevice : reDevices) {
                        final MqttData repubMqttData = new MqttData();
                        // 使用ReDevice 的 设备ID 与设备类型
                        repubMqttData.setDeviceId(reDevice.getDeviceId());
                        repubMqttData.setDeviceType(reDevice.getDeviceType().name());
                        // 使用sourceDevice的数据和采样时间
                        repubMqttData.setValue(sourceDevice.getValue());
                        repubMqttData.setSampleTime(sourceDevice.getSampleTime());

                        repubMqttDataList.add(repubMqttData);
                    }
                }
            }

            // 创建复用的MqttPubProtocol对象
            final MqttPubProtocol repubMqttPubProtocol = new MqttPubProtocol(repubMqttConfig.getDataTag(), repubMqttDataList);
            final String repubJsonStr = objectMapper.writeValueAsString(repubMqttPubProtocol);
            String reTopic = getReTopic();
            sendMqttMessage(repubMqttPubProtocol, reTopic, repubClient, repubJsonStr, repubMqttConfig.getClientId());
        }
    }

    private static void sendMqttMessage(MqttPubProtocol data, String topic, MqttClient client, String jsonStr, String clientName) {
        // 推送MQTT消息
        if (client != null && client.isConnected()) {
            client.publish(
                    topic,
                    Buffer.buffer(jsonStr),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false,
                    handler -> {
                        if (handler.succeeded()) {
                            log.debug("MQTT[{}] 消息已发布，dataTag：{}，主题：{}", clientName, data.getDataTag(), topic);
                        } else {
                            log.error("MQTT[{}] 消息发布失败，dataTag：{}，主题：{}", clientName, data.getDataTag(), topic, handler.cause());
                        }
                    });
        } else {
            log.warn("MQTT[{}] 客户端未连接，无法推送消息，dataTag：{}", clientName, data.getDataTag());
        }
    }

    private String getTopic() {
        String topic; // 先声明变量，但不初始化
        if (mqttConfig.isUseStandardTopic()) { // 判断 boolean 表达式
            topic = mqttConfig.getStandardTopic(); // 如果为 true，赋值标准主题
        } else {
            topic = mqttConfig.getLightweightTopic(); // 如果为 false，赋值轻量级主题
        }
        return topic;
    }

    private String getReTopic() {
        String topic; // 先声明变量，但不初始化
        if (repubMqttConfig.isUseStandardTopic()) { // 判断 boolean 表达式
            topic = repubMqttConfig.getStandardTopic(); // 如果为 true，赋值标准主题
        } else {
            topic = repubMqttConfig.getLightweightTopic(); // 如果为 false，赋值轻量级主题
        }
        return topic;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        repubMap = (HashMap<String, List<ReDevice>>) applicationContext.getBean("deviceIdMap", HashMap.class);
    }
}
