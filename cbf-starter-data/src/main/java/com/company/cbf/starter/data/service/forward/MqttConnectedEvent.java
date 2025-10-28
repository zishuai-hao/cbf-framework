package com.company.cbf.starter.data.service.forward;

import org.springframework.context.ApplicationEvent;

/**
 * MQTT 连接成功事件
 */
public class MqttConnectedEvent extends ApplicationEvent {

    public MqttConnectedEvent(Object source) {
        super(source);
    }
}


