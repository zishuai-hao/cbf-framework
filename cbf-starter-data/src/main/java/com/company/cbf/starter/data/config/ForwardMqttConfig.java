package com.company.cbf.starter.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 主题规范
 * - *标准化实时数据: bridge_mon/+/standard
 * - *轻量化实时数据: bridge_mon/+/lightweight
 * @author hzs
 * @date 2023/12/08
 */
@Data
@ConfigurationProperties(prefix = "mqtt-config")
public class ForwardMqttConfig {
    private String url;
    private Integer port;
    private String topic;
    private String username;
    private String password;
    private String dataTag;
    private boolean enable;
    private String clientId; // 新增，允许配置 client ID
    private Integer reconnectInterval; // 新增，允许配置重连间隔


    public String getStandardTopic() {
        return "bridge_mon/" + dataTag + "/standard";
    }

    public String getLightweightTopic() {
        return "bridge_mon/" + dataTag + "/lightweight";
    }

}