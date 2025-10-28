package com.company.cbf.starter.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 桥梁监测设备MQTT推送协议实体类
 * Topic: bridge_mon/+/standard (标准化实时数据)
 * Topic: bridge_mon/+/lightweight (轻量化实时数据)
 * 
 * @author hzs
 * @date 2023/12/02
 */
@Data
@AllArgsConstructor
public class MqttPubProtocol {
    
    /**
     * 数据标识
     */
    @JsonProperty("data_tag")
    private String dataTag;
    
    /**
     * MQTT数据列表
     */
    @JsonProperty("mqtt_data")
    private List<MqttData> mqttData;
}

