package com.company.cbf.starter.data.config;

import com.company.cbf.starter.data.service.forward.ForwardMqttClientService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.company.cbf.starter.data.entity.MqttServerGateway;

import java.util.List;

/**
 * Mqtt服务器自动启动配置
 * @author hzs
 * @date 2023/12/08
 */
@Data
@ConfigurationProperties(prefix = "mqtt-auto-config")
public class ForwardMqttAutoStartProperties {
    /**
     * 是否启用Mqtt服务器自动启动
     */
    private boolean enable;

    /**
     * 排除的Mqtt服务器ID
     */
    private List<Long> excludeMqttId;

    /**
     * 支持多个Mqtt服务器，以实现自动连接，
     * 手动连接参考{@link ForwardMqttClientService#startWithConnect(MqttServerGateway)}
     */
    private List<MqttServerGateway> mqttServerGatewayList;
}